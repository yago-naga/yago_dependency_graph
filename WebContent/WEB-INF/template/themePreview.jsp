<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta content="text/html; charset=utf-8" http-equiv="Content-Type">
<title>YAGO Demo: Inside YAGO2s
	city</title>	
<script src="/yago2sdemo/data/jquery-1.8.2.js"></script>
<script src="/yago2sdemo/data/jquery-ui-1.9.0.js"></script>
<script src="/yago2sdemo/data/jquery.contextMenu.js"></script>

<link href="/yago2sdemo/data/jquery-ui.css" rel="stylesheet">

<style type='text/css'>
@media screen , paper {
	body {
		
	}
}

body {
	font-family: "Century Gothic", sans-serif;
	text-align: justify;
}

div.results {
	margin: auto;
	max-width: 90%;
}

div.logo {
	width: 7%;
	border: none;
	margin: 0em 0em 1em 4em;
	padding: 0em;
	text-align: center;
}

div.head {
	text-align: center;
	font-size: 150%;
	margin-top: 1em;
	margin-bottom: 1em;
}

div.input {
	margin: 0em 0em 1em 4em;
}

div.centered-response {
	margin: auto;
	max-width: 60em;
	text-align: center;
}

table#facts {
	text-align: center;
	max-width: 60em;
	width: 100%;
	margin: auto;
	border-collapse: collapse;
}

table#facts td {
	padding: 5px;
	s
}

table#facts th {
	padding: 5px;
	border-bottom: 1px solid;
	border-spacing: 0px;
	border-color: #696969;
}

table#facts tr {
	padding: 5px;
	border-bottom: 1px solid;
	border-spacing: 0px;
	border-color: #C0C0C0;
}

</style>

</head>
<body>
	<div class=logo>
		<a href='http://yago-knowledge.org'> <img width='100%'
			src='data/yago_logo_rgb_scaled.png'></a>
	</div>
	
	<div class="input">
		<a href='/yago2sdemo/Browser'>Back to the main page</a>
	</div>
	<hr/>
	
	<div class="results">
		<div id="response">
			<h3>Theme: <c:out value="${themeName}" /></h3>
			<c:if test="${facts != null}">
			<table id="facts">
			<thead>
			<tr>
				<th></th>
				<th>ID</th>
				<th>Arg1</th>
				<th>Relation</th>
				<th>Arg2</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${facts}" var="entry">
				<tr>
					<td>
						<c:if test="${entry.getId() != null}">
							<a href="/yago2sdemo/Provenance?factId=<c:out value="${entry.getId()}" /> ">Provenance</a>
						</c:if>
			    		<c:if test="${entry.getId() == null}">-</c:if>
					</td>
			    	<td>
			    		<c:if test="${entry.getId() != null}"><c:out value="${entry.getId()}" /></c:if>
			    		<c:if test="${entry.getId() == null}">-</c:if>
			    	</td>
			    	<td><c:out value="${entry.getArg(1)}" /></td>
			    	<td><c:out value="${entry.getRelation()}" /></td>
			    	<td><c:out value="${entry.getArg(2)}" /></td>
			    <tr>
			</c:forEach>
			</tbody>
			</table>
			</c:if>
			<c:if test="${facts == null}">
				<div class="centered-response">
					${response}
				</div>
			</c:if>
		</div>
	</div>
</body>
</html>