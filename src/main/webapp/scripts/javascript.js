
/**
 * To display the submit button if password and confirmation are equals
 * @param {type} id
 * @returns {undefined}
 */
function display_div(id) {
  document.getElementById(id).style.display = 'block';
}

/**
 * To hide the submit button whilef password and confirmation are not equals
 * @param {type} id
 * @returns {undefined}
 */
function hide_div(id) {
  document.getElementById(id).style.display = 'none';
}

/**
 * Function to update file label. Use angularjs? 
 * @param {type} uploadFilename
 * @param {type} labelToUpdate
 * @returns {undefined}
 */
function refreshFileUpload(uploadFilename, labelToUpdate) {
  var path = document.getElementById(uploadFilename).value;
  var fileName = path.match(/[^\/\\]+$/);
  $('#' + labelToUpdate).html(fileName);
}
