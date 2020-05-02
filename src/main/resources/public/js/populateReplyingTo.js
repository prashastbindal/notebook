//formats every comment to look like a reply or top-level comment
var parentsList = document.getElementsByClassName("replying-to");
var i;
for (i = 0; i < parentsList.length; i++) {
    // get rid of this element if comment isn't a reply
    if (parentsList[i].innerText === "0") {
        parentsList[i].parentElement.removeChild(parentsList[i]);
        i--;
    // set the text to be "@parent_username" if it is, signifying it's replying to parent
    } else {
        parentsList[i].innerText = "@" + getCreatorNameById(parentsList[i].innerText);
    }
}

function getCreatorNameById(id) {
    return document.getElementById("creator-of-comment-" + id).innerText;
}