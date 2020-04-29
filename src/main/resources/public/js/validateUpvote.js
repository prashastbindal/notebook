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
        document.getElementById("usernameUpvote").value = cookieKey;
        return true;
    }
}

var upvoteButton = document.getElementById("upvote");
upvoteButton.addEventListener("click", function() {
    if (validateUpvote()) {

        var dataelement = document.getElementById("data");
        var courseid = dataelement.dataset.courseid;
        var noteid = dataelement.dataset.noteid;

        var auth2 = gapi.auth2.getAuthInstance();
        var username = auth2.currentUser.get().getBasicProfile().getName();

        $.ajax({
            url: "/courses/" + courseid + "/notes/" + noteid + "/upvote",
            method: "POST",
            data: {
                noteId: noteid,
                courseId: courseid,
                usernameUpvote: username + "_" + courseid + "_" + noteid
            }
        });

        var upvotes = parseInt(dataelement.dataset.upvotes);
        var creator = dataelement.dataset.creator;
        var notename = dataelement.dataset.notename;

        upvotes += 1;
        upvoteButton.innerHTML = "Upvote: " + upvotes;
        $("a[data-noteid='" + noteid +"']", parent.document)[0].innerHTML = notename + " by " + creator + "<br>Upvotes: " + upvotes;
    }
});
