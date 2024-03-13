<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>     
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Spring MVC01</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
</head>
<body>
 
<div class="container">
  <h2>Spring MVC01</h2>
  <div class="panel panel-default">
    <div class="panel-heading" onClick="location.href='boardList.do'">BOARD</div>
    <div class="panel-body">Panel Content</div>
    <div class="panel-footer"></div>
  </div>
  <c:if test="${log eq null }">
    <div onClick="location.href='loginForm.do'"> 로그인 </div>
 	 <div onClick="location.href='joinForm.do'"> 회원가입 </div>
  </c:if>
    <c:if test="${log ne null }">
      <div onClick="location.href='logoutPro.do'"> 로그아웃 </div>
  </c:if>
</div>

</body>
</html>