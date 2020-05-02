// allows for comment replies by setting the hidden reply id to the id of whatever parentID is
// parentID is usually grabbed via the reply button on the soon-to-be-parent comment
function setReplyId(parentID) {
    document.getElementById("reply-id-field").value = parentID;
    document.getElementById("comment-field").focus();
}