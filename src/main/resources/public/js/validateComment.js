function validateComment() {
    var comment = document.getElementById("comment-field").value;
    if (comment.length < 1) {
        alert("Comment cannot be empty!");
        return false;
    } else if (comment.length > 1000) {
        alert("Comment too long!");
        return false;
    }

    var user = document.getElementById("creator-field").value;
    if (user.length < 1) {
        alert("Username cannot be empty!");
        return false;
    } else if (user.length > 30) {
        alert("Username too long!");
        return false;
    }
    return true;
}