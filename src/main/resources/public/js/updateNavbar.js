// Adapted from https://www.w3schools.com/js/js_cookies.asp
function usernameExists() {
    var cookieStrings = decodeURIComponent(document.cookie).split(';');
    for(var i = 0; i <cookieStrings.length; i++) {
        var c = cookieStrings[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf("username=") == 0) {
            return true;
        }
    }
    return false;
}

if (usernameExists()) {
    document.getElementById("submit").innerHTML = "Sign Out";
}