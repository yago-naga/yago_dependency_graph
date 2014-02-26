/* YAGO2s Demo SVG dynamic interface */
/* Author: Joanna Biega */


/* Highlights the chosen extractor and its all input themes */
function highlightInput(elem, isInputHighlighted, svgLeafNodes) {
	//get extractor information
	var ellipse = elem.find("ellipse");
	var extractorOpacity = ellipse.css("opacity");
	
	var extractorName = ellipse.attr("extractor-id");
	
	var inputThemes = ellipse.attr("input-themes");
	inputThemes = extractValues(inputThemes);
	
	//if an extractor is highlighted and user clicks it again
	// we remove the highlight
	if (isInputHighlighted && extractorOpacity == 1) {
		$("svg").find("*").fadeTo("fast", 1);
	}
	//else either user highlights a first extractor
	//or wants to change the highlight to another extractor
	else {
		var desiredNodes = svgLeafNodes
								   .filter(function(index) {
								   	var themeName = $(this).attr("theme-name");
								   	var extrName = $(this).attr("extractor-id");
								   	
								   	//Do not highlight data files
								   	if ($(this).attr("data-file") == "true") {
								   		return false;
								   	}

								   	//all nodes related to the extractor input themes
									if ($.inArray(themeName, inputThemes) != -1) {
										var nodeName = $(this).prop("nodeName");
										//we choose a node if: 
										//- it is not a line
										//- it is a line and the line leads to the chosen extractor
										if (nodeName != 'line' || nodeName == 'line' && extrName == extractorName) {
											return true;
										}
									}
									
									//all nodes related to the chosen extractor
									if (extrName == extractorName) {
										return true;
									}
									return false;
								});
		//highlight the desired nodes
		highlightDesired(desiredNodes);
	}
}

/* Highlight the chosen theme and the parent extractor */
function highlightParent(elem, isParentHighlighted, svgLeafNodes) {
	//get theme information
	var rectangle = elem.find("rect");
	var themeName = rectangle.attr("theme-name");
	var themeOpacity = rectangle.css('opacity');
	
	//if a theme is highlighted and user clicks it again
	// we remove the highlight
	if (isParentHighlighted && themeOpacity == 1) {
		$("svg").find("*").fadeTo("fast", 1);
	}
	//else either user highlights a first theme
	//or wants to change the highlight to another theme
	else {
		var desiredNodes = svgLeafNodes
								   .filter(function(index) {
									   	var thName = $(this).attr("theme-name");
									   	var nodeName = $(this).prop("nodeName");
									   	
										var outputThemes = $(this).attr("output-themes");
										outputThemes = extractValues(outputThemes);
										
										//nodes related to this theme or to the extractors that use this theme as input
										if ((themeName == thName && nodeName != 'line') || $.inArray(themeName, outputThemes) != -1) {
											return true;
										}
										return false;
									});
		//highlight the desired nodes
		highlightDesired(desiredNodes);
	}
}

/* Highlight the chosen theme and all extractors that use it as input */
function highlightTheme(elem, isThemeHighlighted, svgLeafNodes) {
	//get theme information
	var rectangle = elem.find("rect");
	var themeName = rectangle.attr("theme-name");
	var themeOpacity = rectangle.css('opacity');
	
	//if a theme is highlighted and user clicks it again
	// we remove the highlight
	if (isThemeHighlighted && themeOpacity == 1) {
		$("svg").find("*").fadeTo("fast", 1);
	}
	//else either user highlights a first theme
	//or wants to change the highlight to another theme
	else {
		var desiredNodes = svgLeafNodes
								   .filter(function(index) {
									   	var thName = $(this).attr("theme-name");
									   	
										var inputThemes = $(this).attr("input-themes");
										inputThemes = extractValues(inputThemes);
										
										//nodes related to this theme or to the extractors that use this theme as input
										if (themeName == thName || $.inArray(themeName, inputThemes) != -1) {
											return true;
										}
										return false;
									});
		//highlight the desired nodes
		highlightDesired(desiredNodes);
	}
}


/* Highlight the whole subgraph dependent on the chosen extractor */
function highlightExtractor(elem, isExtractorHighlighted, svgLeafNodes) {
	
	var ellipse = elem.find("ellipse");
	var extractorOpacity = ellipse.css('opacity');
	
	var dependencyId = ellipse.attr("dependency-id");
	
	//if an extractor is highlighted and user clicks it again
	// we remove the highlight
	if (isExtractorHighlighted && extractorOpacity == 1) {
		$("svg").find("*").fadeTo("fast", 1);
	}
	
	//else either user highlights a first extractor
	//or wants to change the highlight to another extractor
	else {
		
		var desiredNodes = svgLeafNodes
								   .filter(function(index) {
									   	var dependencies = $(this).attr("dependencies");
									   	dependencies = extractValues(dependencies);
									   	
									   	return $.inArray(dependencyId, dependencies) != -1;
									});
		
		//highlight the desired nodes
		highlightDesired(desiredNodes);
	}
}


$(document).ready(function(){
	
	var svgLeafNodes = $("svg").find("*").not(':has(*)');
	
	var isInputHighlighted = false;
	var isThemeHighlighted = false;
	var isExtractorHighlighted = false;
	var isParentHighlighted = false;
	
	INPUT = 'input';
	THEME = 'theme';
	EXTRACTOR = 'extractor';
	PARENT = 'parent';
	
	function highlightStateChange(highlightType) {
		if (highlightType == INPUT) {
			isInputHighlighted = !isInputHighlighted;
			isThemeHighlighted = false;
			isExtractorHighlighted = false;
			isParentHighlighted = false;
			
		} else if (highlightType == PARENT) {
			isInputHighlighted = false;
			isThemeHighlighted = false;
			isExtractorHighlighted = false;
			isParentHighlighted = !isParentHighlighted;
			
		} else if (highlightType == THEME) {
			isInputHighlighted = false;
			isThemeHighlighted = !isThemeHighlighted;
			isExtractorHighlighted = false;
			isParentHighlighted = false;
			
		} else if (highlightType == EXTRACTOR) {
			isInputHighlighted = false;
			isThemeHighlighted = false;
			isExtractorHighlighted = !isExtractorHighlighted;
			isParentHighlighted = false;
			
		}
	}
	
	// MENU BOXES for themes
	//rectangles that either don't have specified data-file attribute
	//or have the data-file attribute set to value other than false
	$("g").has("rect[data-file!='false']").contextMenu('context-menu-rect', {
		'Show the parent extractor': {
	        click: function(element){
	            highlightParent(element, isParentHighlighted, svgLeafNodes);
	            highlightStateChange(PARENT);
	        },
	    },
		'Show dependent extractors': {
	        click: function(element){
	            highlightTheme(element, isThemeHighlighted, svgLeafNodes);
	            highlightStateChange(THEME);
	        },
	    },
		'Preview': {
	        click: function(element){
	        	var themeName = element.find("rect").attr("theme-name");
	        	var new_href = 'http://' + window.location.host + '/yago2sdemo/Preview?themeName=' + themeName;
	        	window.location = new_href;
	        },
	    },
		'Edit': {
	        click: function(element){
	        	var themeName = element.find("rect").attr("theme-name");
	        	$.ajax({
    			  url: '/yago2sdemo/Edit?themeName=' + themeName
    			}).done(function(response) {
    			}).fail(function() { 
    				alert("Theme file could not be edited."); 
    			});
	        },
	    },
	    'Reset': {
	        click: function(element){
	        	var themeName = element.find("rect").attr("theme-name");
	        	var resetType = element.find("rect").attr("data-file") == 'true' ? 'data' : 'theme';
	        	$.ajax({
    			  url: '/yago2sdemo/Reset?themeName=' + themeName + '&resetType=' + resetType
    			}).done(function(response) {
    				var msg = $("div#response", response).text();
    				alert(msg);
    			}).fail(function() { 
    				alert("Theme file could not be reset."); 
    			});
	        },
	    },
	  },
	  {
	    leftClick: true
	  }
	);
	
	// MENU BOXES for extractors
	$("g").has("ellipse").contextMenu('context-menu-ellipse', {
		'Show input themes': {
	        click: function(element) {
	            highlightInput(element, isInputHighlighted, svgLeafNodes);
	            highlightStateChange(INPUT);
	        },
	    },
	    'Show dependent extractors': {
	        click: function(element) {
	            highlightExtractor(element, isExtractorHighlighted, svgLeafNodes);
	            highlightStateChange(EXTRACTOR);
	        },
	    },
	    'Run extractor': {
	    	click: function(element) {
	    		var extractorName = element.find('ellipse').attr('extractor-name');
	    		$.ajax({
    			  url: '/yago2sdemo/Run?extractorName=' + extractorName
    			}).done(function(response) {
    				var msg = $("div#response", response).text();
    				alert(msg);
    			}).fail(function() { 
    				alert("Extractor could not be called."); 
    			});
	    	}
	    }
	  },
	  {
	    leftClick: true
	  }
	);
	
	// "Run the entire system" button
	$("#run-extraction").click(function() {
		$.ajax({
		  url: '/yago2sdemo/Run'
		}).done(function(response) {
			var msg = $("div#response", response).text();
			alert(msg);
		}).fail(function() { 
			alert("Extractors could not be called."); 
		});
	});
	
	$("#reset-files").click(function(){
		$.ajax({
		  url: '/yago2sdemo/Reset?resetType=all'
		}).done(function(response) {
			var msg = $("div#response", response).text();
			alert(msg);
		}).fail(function() { 
			alert("Files could not be reset."); 
		});
	});
	
	// AJAX progress bars
	$("body").on({
	    ajaxStart: function() { 
	        $(this).addClass("loading"); 
	    },
	    ajaxStop: function() { 
	        $(this).removeClass("loading"); 
	    }    
	});
	
 });