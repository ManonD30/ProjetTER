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
