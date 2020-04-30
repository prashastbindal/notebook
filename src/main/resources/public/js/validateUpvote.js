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
        return true;
    }
}

var dataelement = document.getElementById("data");
var courseid = dataelement.dataset.courseid;
var noteid = dataelement.dataset.noteid;

var upvotes = parseInt(dataelement.dataset.upvotes);
var creator = dataelement.dataset.creator;
var notename = dataelement.dataset.notename;

var canUpvote = true;

gapiPromise.then(() => {

    var auth2 = gapi.auth2.getAuthInstance();
    var username = auth2.currentUser.get().getBasicProfile().getName();

    $.ajax({
        url: "/courses/" + courseid + "/notes/" + noteid + "/checkUpvote",
        method: "POST",
        data: {
            noteId: noteid,
            courseId: courseid,
            username: username
        },
        success: function(data, status, xhr) {
            console.log("success");
            if (data == "false") {
                canUpvote = false;
                upvoteButton.innerHTML = "Downvote: " + upvotes;
            }
        }
    });

});

var upvoteButton = document.getElementById("upvote");
upvoteButton.addEventListener("click", function() {
    if (validateUpvote()) {

        var auth2 = gapi.auth2.getAuthInstance();
        var username = auth2.currentUser.get().getBasicProfile().getName();

        $.ajax({
            url: "/courses/" + courseid + "/notes/" + noteid + "/upvote",
            method: "POST",
            data: {
                noteId: noteid,
                courseId: courseid,
                username: username
            }
        });

        if (canUpvote) {
            canUpvote = false;
            upvotes += 1;
            upvoteButton.innerHTML = "Downvote: " + upvotes;
            $("a[data-noteid='" + noteid +"']", parent.document)[0].innerHTML = notename + " by " + creator + "<br>Upvotes: " + upvotes;
        } else {
            canUpvote = true;
            upvotes -= 1;
            upvoteButton.innerHTML = "Upvote: " + upvotes;
            $("a[data-noteid='" + noteid +"']", parent.document)[0].innerHTML = notename + " by " + creator + "<br>Upvotes: " + upvotes;

        }
    }
});
