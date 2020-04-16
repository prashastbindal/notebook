function updateNavbar() {
    gapi.load('auth2', function() {
        auth2 = gapi.auth2.init();
        auth2.then(function () {
            if (auth2.isSignedIn.get()) {
                document.getElementById("signin-btn").style.display = "none";
                document.getElementById("signout-btn").style.display = "inline";
            } else {
                document.getElementById("signin-btn").style.display = "inline";
                document.getElementById("signout-btn").style.display = "none";
            }
        })
    });
}

updateNavbar();