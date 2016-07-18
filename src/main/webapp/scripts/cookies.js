/*create a new random key this key will be used to
 give a unique name to temporary files*/
function createCookie(days) {
	if (days) {
		var date = new Date();
		date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
		var expires = "; expires=" + date.toGMTString();
	} else {
		var expires = "";
	}
	var key = Math.floor(Math.random() * 1000001);
	document.cookie = "key=" + key + expires + "; path=/";
}

function readCookie() {
	var nameEQ = "key=";
	var ca = document.cookie.split(';');
	for ( var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ')
			c = c.substring(1, c.length);
		if (c.indexOf(nameEQ) == 0)
			return c.substring(nameEQ.length, c.length);
	}
	return null;
}

function eraseCookie() {
	createCookie("value", "", -1);
}