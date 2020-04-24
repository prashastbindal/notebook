function validateNote() {
    var auth2 = gapi.auth2.getAuthInstance();
    if (!auth2.isSignedIn.get()) {
        auth2.signIn().then(() => {
            document.getElementById("signin-btn").style.display = "none";
            document.getElementById("signout-btn").style.display = "inline";
        });
        return false;
    } else {
        var username = auth2.currentUser.get().getBasicProfile().getName();
        document.getElementById("username-field").value = username;
        return true;
    }
}