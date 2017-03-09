<%@page import="java.sql.SQLException"%>
<%@page import="jdbc.connection.ConnectionProvider"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<%
		try (Connection conn = ConnectionProvider.getConnection()) {
			out.println("db 연결 성공");
		} catch(SQLException e) {
			out.println("컨넥션 연결 실패:" + e.getMessage());
			application.log("컨넥션 연결 실패", e);
		}
	%>
</body>
</html>