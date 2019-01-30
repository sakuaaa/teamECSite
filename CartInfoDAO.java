package com.internousdev.anemone.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.internousdev.anemone.dto.CartInfoDTO;
import com.internousdev.anemone.util.DBConnector;

public class CartInfoDAO {

//	カート画面の一覧出力で使用
	public List<CartInfoDTO> getCartInfoDtoList(String loginId) {

		DBConnector db = new DBConnector();
		Connection con = db.getConnection();

		List<CartInfoDTO> cartInfoDtoList = new ArrayList<CartInfoDTO>();

//		カート情報と商品情報テーブルからデータ取得
		String sql="select"
		+ " ci.id as id,"					//カート：ID
		+ " ci.user_id as user_id,"				//カート：ユーザーID
		+ " ci.temp_user_id as temp_user_id,"			//カート：仮ユーザーID
		+ " ci.product_id as product_id,"			//カート：商品ID
		+ " sum(ci.product_count) as product_count,"		//商品IDごとにカートの購入個数をSUM
		+ " pi.price as price,"					//商品情報：価格
		+ " pi.regist_date as regist_date,"			//商品情報：登録日
		+ " pi.update_date as update_date,"			//商品情報：更新日
		+ " pi.product_name as product_name,"			//商品情報：商品名
		+ " pi.product_name_kana as product_name_kana,"		//商品情報：商品かな
		+ " pi.product_description as product_description,"	//商品情報：商品詳細
		+ " pi.category_id as category_id,"			//商品情報：カテゴリID
		+ " pi.image_file_path as image_file_path, "		//商品情報：画像ファイルパス
		+ " pi.image_file_name as image_file_name, "		//商品情報：画像ファイル名
		+ " pi.release_date as release_date,"			//商品情報：発売年月
		+ " pi.release_company as release_company,"		//商品情報：発売会社
		+ " pi.status as status,"				//商品情報：ステータス
		+ " (sum(ci.product_count) * ci.price) as subtotal,"	//カート：購入個数*金額
		+ " max(ci.regist_date) as regist_date "		//更新日のMAX値を取得
		+ " FROM cart_info as ci"				//カート情報TBL
		+ " LEFT JOIN product_info as pi"			//商品情報TBL
		+ " ON ci.product_id = pi.product_id"			//ID一致
		+ " WHERE ci.user_id = ?"				//ユーザーID
		+ " group by product_id"				//カートの商品IDでグループ化
		+ " ORDER BY regist_date DESC";				//カート登録日の降順

		try {

			PreparedStatement ps = con.prepareStatement(sql);

			ps.setString(1, loginId);

			ResultSet resultSet = ps.executeQuery();

			while(resultSet.next()) {
				CartInfoDTO cartInfoDTO = new CartInfoDTO();
				cartInfoDTO.setId(resultSet.getInt("id"));
				cartInfoDTO.setUserId(resultSet.getString("user_id"));
				cartInfoDTO.setTempUserId(resultSet.getString("temp_user_id"));
				cartInfoDTO.setProductId(resultSet.getInt("product_id"));
				cartInfoDTO.setProductCount(resultSet.getInt("product_count"));
				cartInfoDTO.setPrice(resultSet.getInt("price"));
				cartInfoDTO.setRegistDate(resultSet.getDate("regist_date"));
				cartInfoDTO.setUpdateDate(resultSet.getDate("update_date"));
				cartInfoDTO.setProductName(resultSet.getString("product_name"));
				cartInfoDTO.setProductNameKana(resultSet.getString("product_name_kana"));
				cartInfoDTO.setProductDescription(resultSet.getString("product_description"));
				cartInfoDTO.setCategoryId(resultSet.getInt("category_id"));
				cartInfoDTO.setImageFilePath(resultSet.getString("image_file_path"));
				cartInfoDTO.setImageFileName(resultSet.getString("image_file_name"));
				cartInfoDTO.setReleaseDate(resultSet.getDate("release_date"));
				cartInfoDTO.setReleaseCompany(resultSet.getString("release_company"));
				cartInfoDTO.setStatus(resultSet.getString("status"));
				cartInfoDTO.setSubtotal(resultSet.getInt("subtotal"));

				cartInfoDtoList.add(cartInfoDTO);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try{
				con.close();
			}catch ( SQLException e) {
				e.printStackTrace();
			}
		}
		return cartInfoDtoList;
	}

//	対象ユーザーIDのカート総額を取得
	public int getTotalPrice(String userId) {

		int totalPrice = 0;
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();

//		カート情報TBLからuserIdをキーに個数*金額の合計値を取得
		String sql = "select sum(product_count * price) as total_price from cart_info where user_id=? group by user_id";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();

			if(rs.next()) {
				totalPrice = rs.getInt("total_price");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try{
				con.close();
			}catch ( SQLException e) {
				e.printStackTrace();
			}
		}
		return totalPrice;
	}

//	カート商品追加処理
	public int regist(String userId, String tempUserId, int productId, int productCount, int price) {

		DBConnector db = new DBConnector();
		Connection con = db.getConnection();

		int count = 0;

//		ユーザーID、仮ユーザーID、商品ID、個数、価格、登録日、更新日
		String sql = "insert into cart_info(user_id, temp_user_id, product_id, product_count, price, regist_date, update_date) values (?, ?, ?, ?, ?, now(), now())";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, tempUserId);
			ps.setInt(3, productId);
			ps.setInt(4, productCount);
			ps.setInt(5, price);
			count = ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try{
				con.close();
			}catch ( SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

//	カート画面で選択された商品の削除処理
	public int delete(String productId, String userId) {

		DBConnector db = new DBConnector();
		Connection con = db.getConnection();

		int count = 0;

		String sql = "delete from cart_info where product_id=? and user_id=?";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, productId);
			ps.setString(2, userId);

			count = ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try{
				con.close();
			}catch ( SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

//	ユーザーのカート情報をすべて削除
	public int deleteAll(String userId) {

		DBConnector db = new DBConnector();
		Connection con = db.getConnection();

		int count = 0;

		String sql = "delete from cart_info where user_id=?";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);

			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try{
				con.close();
			}catch ( SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

//	仮ユーザーIDでカート追加後、ログインした場合
//	正規のログインIDに更新、件数を返す
	public int linkToLoginId(String tempUserId, String loginId) {

		DBConnector db = new DBConnector();
		Connection con = db.getConnection();

		int count = 0;

//		仮ユーザーIDでカート追加後、ログインした場合は、仮ユーザーIDを初期化し、ログインIDを更新
		String sql = "update cart_info set user_id=?, temp_user_id = null, update_date = now() where temp_user_id=?";

		try {
			PreparedStatement ps = con.prepareStatement(sql);

			ps.setString(1, loginId);
			ps.setString(2, tempUserId);

			count = ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try{
				con.close();
			}catch ( SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
}
