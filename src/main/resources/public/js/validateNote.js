function validateNote() {
    var auth2 = gapi.auth2.getAuthInstance();
    if (!auth2.isSignedIn.get()) {
        auth2.signIn();
        //right now this won't update the navbar but I don't care, that's something for tomorrow
        return false;
    } else {
        var username = auth2.currentUser.get().getBasicProfile().getName();
        document.getElementById("username-field").value = username;
        return true;
    }
}