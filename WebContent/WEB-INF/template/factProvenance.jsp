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

/* Progress bar on AJAX load */
.progress-bar {
    display:    none;
    position:   fixed;
    z-index:    1000;
    top:        0;
    left:       0;
    height:     100%;
    width:      100%;
    background: rgba( 255, 255, 255, .8 ) 
                url('data/ajax-loader.gif') 
                50% 50% 
                no-repeat;
}

body.loading {
    overflow: hidden;   
}

body.loading .progress-bar {
    display: block;
}
</style>

<script src="data/helpers.js"></script>
<script>
	$(document).ready(function() {
		var themes = extractThemes("${provenance}");
		
		var desiredNodes = $("svg").find("*").not(':has(*)') // get all leaf nodes
								   .filter(function(index) {
									   	var thName = $(this).attr("theme-name");
									   	
										var inputThemes = $(this).attr("input-themes");
										inputThemes = extractThemes(inputThemes);
										
										var outputThemes = $(this).attr("output-themes");
										outputThemes = extractThemes(outputThemes);
										
										//nodes related to this theme or to the extractors that use this theme as input
										if ($.inArray(thName, themes) != -1) {
											return true;
										}
										
										for (var i = 0; i < inputThemes.length; i++) {
											if ($.inArray(inputThemes[i], themes) != -1) {
												return true;
											}
										}
										
										for (var i = 0; i < outputThemes.length; i++) {
											if ($.inArray(outputThemes[i], themes) != -1) {
												return true;
											}
										}
										return false;
									});
		
		//highlight the desired nodes
		highlightDesired(desiredNodes);
	});
</script>

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
		<div>${svgImage}</div>
	</div>
	
	<div class="progress-bar"></div>
</body>
</html>