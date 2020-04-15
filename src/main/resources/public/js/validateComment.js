function validateComment() {
    var comment = document.getElementById("comment-field").value;
    if (comment.length < 1) {
        alert("Comment cannot be empty!");
        return false;
    } else if (comment.length > 1000) {
        alert("Comment too long!");
        return false;
    }

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