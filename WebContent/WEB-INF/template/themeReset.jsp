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

div.controll {
	margin: auto;
	max-width: 60em;
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
	margin-top: 2em;
	margin-bottom: 2em;
	margin-left: auto;
}

div.centered-response {
	margin: auto;
	max-width: 60em;
	text-align: center;
}

</style>

</head>
<body>
	<div class=logo>
		<a href='http://yago-knowledge.org'> <img width='100%'
			src='/yago2sdemo/data/yago_logo_rgb_scaled.png'></a>
	</div>
	
	<hr/>
	<div class="controll input">
		<a href='/yago2sdemo/Browser'>Back to the main page</a>
	</div>
	<hr/>
	
	<div class="results">
		<div id="response">${response}</div>
	</div>
</body>
</html>