function display_div(id) {
	document.getElementById(id).style.display = 'block';
}

function hide_div(id) {
	document.getElementById(id).style.display = 'none';
}

// display download button if at least one checkbox is checked
function seeDownloadButton() {

	var atLeastOneChecked = false;
	var i = 0;

	while (document.getElementById(i)) {
		if (document.getElementById(i).checked) {
			atLeastOneChecked = true;
			break;
		}
		i++;
	}

	if (atLeastOneChecked == true) {
		hide_div('toHideWhenDownload');
		display_div('seeMappings');
		display_div('download');
	} else {
		alert("Please check at least one alignment.");
		return false;
	}
}

// display mappings tab and hide download button
function seeMappings() {
	hide_div('download');
	hide_div('seeMappings');
	display_div('toHideWhenDownload');
}

function refreshTab() {
	// get threshold value
	var threshold = document.getElementById("seuilDynamic").value;
	// get table
	var table = document.getElementById("table");
	// get lines
	tableLines = table.rows;
	// get number of lines
	tableLength = tableLines.length;
        
        // update threshold display text
        document.getElementById("threshold_display").innerHTML = threshold;

	for ( var i = 0; i < tableLength; i++) { // for each line
		// get columns
		var tableColumns = tableLines[i].cells;

		if (tableColumns[4].innerHTML < threshold) {// if score < threshold
			// get checkbox
			var checkbox = document.getElementById(i);
			// uncheck checkbox
			checkbox.checked = false;
			// hide line
			tableLines[i].style.display = 'none';

		} else { // if score >= threshold
			if (tableLines[i].style.display == 'none') { // if line hided
				tableLines[i].style.display = 'table-row'; // show line
				var checkbox = document.getElementById(i); // get checkbox
				checkbox.checked = true; // check checkbox
			}
		}
	}

	refreshLineNumber();
}

// refresh line numbers in table
function refreshLineNumber() {
	// get table
	var table = document.getElementById("table");
	// get lines
	tableLines = table.rows;
	// get number of lines
	tableLength = tableLines.length;

	numLine = 1;
	for ( var i = 0; i < tableLength; i++) { // for each line
		if (tableLines[i].style.display != 'none') { // if line is displayed
			// refresh numLine
			tableLines[i].cells[0].innerHTML = numLine;
			// refresh color
			if (numLine % 2 == 0) {
				tableLines[i].className = "grey";
			} else {
				tableLines[i].className = "white";
			}
			numLine++; // next numLine
		}
	}
}