<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="./css/style.css">
<link rel="stylesheet" href="./css/cart.css">
<title>カート</title>
<%-- チェックがついている場合のみ、削除ボタンを動作 --%>
<script>
	function checkValue(check) {
		var checkList = document.getElementsByClassName("checkList");
		var checkFlag = 0;
		for (var i = 0; i < checkList.length; i++) {
			if (checkFlag == 0) {
				if (checkList[i].checked) {
					checkFlag = 1;
					break;
				}
			}
		}
		if (checkFlag == 1) {
			document.getElementById('delete_btn').disabled = "";
		} else {
			document.getElementById('delete_btn').disabled = "true";
		}
	}
</script>

</head>
<body>
	<s:include value="header.jsp" />
	<div id="contents">
		<h1>カート画面</h1>

		<%-- カート情報がある場合のみ表示 --%>
		<s:if test="#session.cartInfoDTOList != null">
			<s:form id="form" action="SettlementConfirmAction">
				<table class="horizontal-list-table">
					<thead>
						<tr>
							<th><s:label value="削除対象" /></th>
							<th><s:label value="商品名" /></th>
							<th><s:label value="商品名ふりがな" /></th>
							<th><s:label value="商品画像" /></th>
							<th><s:label value="値段" /></th>
							<th><s:label value="発売会社名" /></th>
							<th><s:label value="発売年月日" /></th>
							<th><s:label value="購入個数" /></th>
							<th><s:label value="合計金額" /></th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="#session.cartInfoDTOList">
							<tr>
								<%-- チェックボックスにチェックを入れた商品IDを受け渡す --%>
								<td><s:checkbox name="checkList" class="checkList" value="checked" fieldValue="%{productId}" onchange="checkValue(this)" /></td>
								<td class="cart_product"><s:property value="productName" /></td>
								<td class="cart_product"><s:property value="productNameKana" /></td>
								<td><img class="item-image-box-50" src='<s:property value="imageFilePath"/>/<s:property value="imageFileName"/>'/></td>
								<td><s:property value="price" />円</td>
								<td><s:property value="releaseCompany" /></td>
								<td><s:property value="releaseDate" /></td>
								<td><s:property value="productCount" /></td>
								<td><s:property value="subtotal" />円</td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
				<h2 class="total_price">
					<s:label value="カート合計金額 :" />
					<s:property value="#session.totalPrice" />円
				</h2>
				<br>
				<div class="submit_btn_flex">
						<s:submit value="決済" class="submit_btn" />
						<%-- チェックを付けた場合のみ削除ボタンを動作 --%>
						<s:submit value="削除" id="delete_btn" class="submit_btn" onclick="this.form.action='DeleteCartAction';" disabled="true" />
				</div>

			</s:form>
		</s:if>

		<s:else>
			<div class="message message_nomal">カート情報がありません。</div>
		</s:else>
	</div>
	<div id="footer">
		<s:include value="footer.jsp"/>
	</div>
</body>
</html>