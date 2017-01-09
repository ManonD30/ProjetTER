
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
 * To check if password and confirmation elements contain the same String
 * @returns {Boolean}
 */
function checkPassword() {
  //Store the password field objects into variables ...
  var password = document.getElementById('password');
  var confirmation = document.getElementById('confirmation');
  //Store the Confimation Message Object ...
  var message = document.getElementById('message');
  //Set the colors we will be using ...
  var goodColor = "#66cc66";
  var badColor = "#ff6666";
  //Compare the values in the password field 
  //and the confirmation field
  if (password.value == confirmation.value) {
    //The passwords match. 
    //Set the color to the good color and inform
    //the user that they have entered the correct password 
    message.style.color = goodColor;
    message.innerHTML = "Passwords match!";
    display_div("submitSignup");
    return true;
  } else {
    //The passwords do not match.
    //Set the color to the bad color and
    //notify the user.
    message.style.color = badColor;
    message.innerHTML = "Passwords do not match!";
    hide_div("submitSignup");
    return false;
  }
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
