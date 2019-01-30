package com.internousdev.anemone.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.anemone.dao.CartInfoDAO;
import com.internousdev.anemone.dto.CartInfoDTO;
import com.internousdev.anemone.util.CommonUtility;
import com.opensymphony.xwork2.ActionSupport;

public class DeleteCartAction extends ActionSupport implements SessionAware{

	private Collection<String> checkList;
	private Map<String, Object> session;

	public String execute() {

		String result = ERROR;
		String userId = null;
		CartInfoDAO cartInfoDAO = new CartInfoDAO();
		int count = 0;

//		タイムアウト処理
		if(!session.containsKey("mCategoryList")) {
			return "sessionError";
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

//		チェックされたデータを1件ずつループし、削除処理・件数カウント
		for(String productId : checkList) {
			count += cartInfoDAO.delete(productId, userId);
		}

//		削除成功、カートの一覧を再取得
		if(count > 0) {
			List<CartInfoDTO> cartInfoDtoList = new ArrayList<CartInfoDTO>();

			cartInfoDtoList = cartInfoDAO.getCartInfoDtoList(userId);

//			後続のnull値判定のため、データが無い場合nullを代入
			Iterator<CartInfoDTO> iterator = cartInfoDtoList.iterator();
			if(!(iterator.hasNext())) {
				cartInfoDtoList = null;
			}
			session.put("cartInfoDTOList", cartInfoDtoList);

			int totalPrice = cartInfoDAO.getTotalPrice(userId);
			session.put("totalPrice", totalPrice);

			result = SUCCESS;
		}
		return result;
	}

	public Collection<String> getCheckList() {
		return checkList;
	}

	public void setCheckList(Collection<String> checkList) {
		this.checkList = checkList;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
