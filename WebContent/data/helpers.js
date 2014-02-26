/* YAGO2s Demo SVG dynamic interface helper methods */
/* Author: Joanna Biega */

/* Decode input/output theme information */
function extractValues(themesString) {
	if (themesString) {
		return themesString.split(";");
	}
	return new Array();
}


function highlightDesired(desiredNodes) {
	//highlight the desired nodes
	desiredNodes.fadeTo("fast", 1);
	
	//then we make all other nodes transparent
	$("svg").find("*").not(':has(*)') //DOM leaves
					  .not($(desiredNodes))
					  .fadeTo("fast", 0.02);
}
