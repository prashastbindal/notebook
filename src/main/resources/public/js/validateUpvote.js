function validateUpvote() {
    var auth2 = gapi.auth2.getAuthInstance();
    if (!auth2.isSignedIn.get()) {
        auth2.signIn().then(() => {
            parent.document.getElementById("signin-btn").style.display = "none";
            parent.document.getElementById("signout-btn").style.display = "inline";
        });
        return false;
    } else {
        var username = auth2.currentUser.get().getBasicProfile().getName();
        console.log("Name is " + username);
        var courseId = document.getElementById("courseId").getAttribute("value");
        var noteId = document.getElementById("noteId").getAttribute("value");
        var cookieKey = username + "_" + courseId + "_" + noteId;
        document.getElementById("usernameUpvote").setAttribute("value", cookieKey);
        return true;
    }
}