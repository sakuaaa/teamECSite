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

public class CartAction extends ActionSupport implements SessionAware {

	private Map<String, Object> session;

	public String execute() {

		String userId = null;

//		タイムアウト処理
		if(!session.containsKey("mCategoryList")) {
			return "sessionError";
		}

		CartInfoDAO cartInfoDAO = new CartInfoDAO();
		List<CartInfoDTO> cartInfoDTOList = new ArrayList<CartInfoDTO>();

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

//		カートの一覧情報を取得
		cartInfoDTOList = cartInfoDAO.getCartInfoDtoList(userId);

//		後続のnull値判定のため、データが無い場合nullを代入
		Iterator<CartInfoDTO> iterator = cartInfoDTOList.iterator();
		if(!(iterator.hasNext())) {
			cartInfoDTOList = null;
		}

		session.put("cartInfoDTOList", cartInfoDTOList);

//		カート一覧の合計金額を取得
		int totalPrice = cartInfoDAO.getTotalPrice(userId);
		session.put("totalPrice", totalPrice);

		return SUCCESS;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
