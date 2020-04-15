function validateUpvote() {
    var auth2 = gapi.auth2.getAuthInstance();
    var username = auth2.currentUser.get().getBasicProfile().getName();
    console.log("Name is " + username);
    var courseId = document.getElementById("courseId").getAttribute("value");
    var noteId = document.getElementById("noteId").getAttribute("value");
    var cookieKey = username + "_" + courseId + "_" + noteId;
    document.getElementById("usernameUpvote").setAttribute("value", cookieKey);
    if (!auth2.isSignedIn.get()) {
        auth2.signIn();
        //right now this won't update the navbar but I don't care, that's something for tomorrow
        return false;
    } else {
        return true;
    }
}