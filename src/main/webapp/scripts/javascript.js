function display_div(id) {
  document.getElementById(id).style.display = 'block';
}

function hide_div(id) {
  document.getElementById(id).style.display = 'none';
}

/**
 * TODO: Use angularjs? Function to update file label
 * @param {type} ontName
 * @returns {undefined}
 */
function refreshFileUpload(uploadFilename, labelToUpdate) {
  var path = document.getElementById(uploadFilename).value;
  var fileName = path.match(/[^\/\\]+$/);
  $('#' + labelToUpdate).html(fileName);
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

