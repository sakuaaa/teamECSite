package com.internousdev.anemone.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.anemone.dao.CartInfoDAO;
import com.internousdev.anemone.dto.CartInfoDTO;
import com.internousdev.anemone.util.CommonUtility;
import com.opensymphony.xwork2.ActionSupport;

public class AddCartAction extends ActionSupport implements SessionAware {

	private Map<String, Object> session;
	private String productCount;

	public String execute() {

		String result = ERROR;
		String userId = null;
		String tempUserId = null;
		CartInfoDAO cartInfoDao = new CartInfoDAO();
		List<CartInfoDTO> cartInfoDtoList = new ArrayList<CartInfoDTO>();
		int intProductCount = 0;

//		タイムアウト処理
		if(!session.containsKey("mCategoryList")) {
			return "sessionError";
		}

//		購入個数の数値変換エラーチェック
		try{
			intProductCount = Integer.parseInt(productCount);
		}catch(NumberFormatException e) {
			return ERROR;
		}

//		購入個数が0～5個以外の場合エラー
		if(intProductCount <= 0 || intProductCount >= 6) {
			return ERROR;
		}

//		ログインIDがある場合、userIdに代入
		if(session.containsKey("loginId")) {
			userId = String.valueOf(session.get("loginId"));

//		仮ユーザーIDがある場合、userIdに代入
		}else if (session.containsKey("tempUserId")) {
			userId = String.valueOf(session.get("tempUserId"));

//		どちらも無い場合、仮ユーザーIDを取得し、userIdに代入
		} else {
			CommonUtility commonUtility = new CommonUtility();
			userId = commonUtility.getRamdomValue();
			session.put("tempUserId", userId);
		}

//		仮ユーザーIDをtempUserIdに取得
		tempUserId = String.valueOf(session.get("tempUserId"));

		int productId = Integer.parseInt(session.get("productId").toString());		//商品ID
		int productPrice = Integer.parseInt(session.get("productPrice").toString());	//金額
		int productTotal = productPrice * intProductCount;				//金額 * 個数

//		追加前の総額と追加商品の合計金額がint型の最大値を超える場合は追加せずエラー
		try {
			Math.addExact(cartInfoDao.getTotalPrice(userId), productTotal);
		} catch(ArithmeticException e) {
			return ERROR;
		}

//		1商品の合計金額がint型の最大値を超える場合は追加せずエラー
		List<CartInfoDTO> cartInfoDtoList_check = cartInfoDao.getCartInfoDtoList(userId);
		Iterator<CartInfoDTO> iterator_check = cartInfoDtoList_check.iterator();

		if(iterator_check.hasNext()) {
//			商品IDごとに桁あふれチェック
			for(CartInfoDTO dto : cartInfoDtoList_check){
				if(dto.getProductId() == productId) {
					try {
//						追加前の商品の金額 + 追加される商品の金額
						Math.addExact(dto.getSubtotal() , productTotal);
					} catch(ArithmeticException e) {
						return ERROR;
					}
				}
			}
		}

//		カート情報TBLに追加
		int count = cartInfoDao.regist(	userId,tempUserId,productId,intProductCount,productPrice);

//		登録できた場合
		if(count > 0) { result=SUCCESS; }

//		対象ユーザーのカートの一覧情報を取得
		cartInfoDtoList = cartInfoDao.getCartInfoDtoList(userId);

//		後続のnull値判定のため、データが無い場合nullを代入
		Iterator<CartInfoDTO> iterator = cartInfoDtoList.iterator();
		if(!(iterator.hasNext())) {
			cartInfoDtoList = null;
		}

		session.put("cartInfoDTOList", cartInfoDtoList);

//		対象のユーザーIDのカート総額を取得
		int totalPrice = cartInfoDao.getTotalPrice(userId);
		session.put("totalPrice", totalPrice);

		return result;
	}

	public String getProductCount() {
		return productCount;
	}

	public void setProductCount(String productCount) {
		this.productCount = productCount;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
