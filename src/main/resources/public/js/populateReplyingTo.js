var parentsList = document.getElementsByClassName("replying-to");
var i;
for (i = 0; i < parentsList.length; i++) {
    if (parentsList[i].innerText === "0") {
        parentsList[i].parentElement.removeChild(parentsList[i]);
        i--;
    } else {
        parentsList[i].innerText = "@" + getCreatorNameById(parentsList[i].innerText);
    }
}

function getCreatorNameById(id) {
    return document.getElementById("creator-of-comment-" + id).innerText;
}