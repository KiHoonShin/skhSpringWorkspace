<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
	<link rel="stylesheet" type="text/css" href="${cp}/resources/css/normal.css" />
</head>
<body>
	<h1>회원가입</h1>	
	<form action="joinPro.do" method="post">
		<table>
			<tr>
				<td>아이디</td>			
				<td><input type="text" name="memID" ></td>
			</tr>
			<tr>
				<td>패스워드</td>
				<td><input type="password" name="memPassword" ></td>
			</tr>
			<tr>
				<td>이름</td>
				<td><input type="text" name="memName" ></td>
			</tr>
			<tr>
				<td>나이</td>
				<td><input type="text" name="memAge" ></td>
			</tr>
			<tr>
				<td>성별</td>
				<td><input type="text" name="memGender" ></td>
			</tr>
			<tr>
				<td>이메일</td>
				<td><input type="text" name="memEmail" ></td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="submit" value="회원가입" >
					<input type="reset" value="다시작성" >
				</td>
			</tr>
		</table>			
	</form>
	<p></p>
	<a href="${cp}/">메인으로</a>
</body>
</html>