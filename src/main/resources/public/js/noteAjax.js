var notes;

$(function(){
    $(".note-select").click(function(e) {
        $('#note-list .active').removeClass('active');
        $(this).addClass('active');

        e.preventDefault();
        $("#note-frame").attr("src", "".concat("/courses/", e.target.dataset.courseid, "/notes-preview/", e.target.dataset.noteid));
    });
});

$('#search-form').submit(function(ev) {
    ev.preventDefault();
    // ajax stuff...
});

function parse(str) {
    var args = [].slice.call(arguments, 1),
        i = 0;

    return str.replace(/%s/g, () => args[i++]);
}
var noteItemFormat = '<a data-courseId=%s data-noteId=%s class="note-select list-group-item' +
    ' list-group-item-action">%s by %s %s  Upvotes: %s </a>'

function checkSignin(courseId) {
    var auth2 = gapi.auth2.getAuthInstance();
    if (!auth2.isSignedIn.get()) {
        auth2.signIn();
        //right now this won't update the navbar but I don't care, that's something for tomorrow
    } else {
        window.location.href = "/courses/" + courseId + "/addNote";
        return;
    }
}

function searcher(courseId, searchKey, newUrl) {
    console.log("search button clicked")
    console.log(searchKey)
    $.ajax({
        type: "GET",
        url: newUrl,
        contentType: "application/json",
        success: function (data, status, jqXHR) {
            $("#note-list").empty();

            notes = data
            data.map(function(note){
                $("#note-list").append(parse(noteItemFormat, note.courseId.toString(), note.id.toString(),
                    note.title.toString(), note.creator.toString(), note.date.toString(), note.upvotes.toString()))
            });

            $(".note-select").click(function(e) {
                $('#note-list .active').removeClass('active');
                $(this).addClass('active');

                e.preventDefault();
                $("#note-frame").attr("src", "".concat("/courses/", e.target.dataset.courseid, "/notes-preview/", e.target.dataset.noteid));
            });
        },

        error: function (jqXHR, status) {
            // error handler
            console.log(jqXHR);
            alert("Empty or invalid search input");
        }
    });

}

function updateNoteView(notes){
    $("#note-list").empty();

    notes.map(function(note){
        $("#note-list").append(parse(noteItemFormat, note.courseId.toString(), note.id.toString(),
            note.title.toString(), note.creator.toString(), note.date.toString(), note.upvotes.toString()))
    });

    $(".note-select").click(function(e) {
        $('#note-list .active').removeClass('active');
        $(this).addClass('active');

        e.preventDefault();
        $("#note-frame").attr("src", "".concat("/courses/", e.target.dataset.courseid, "/notes-preview/", e.target.dataset.noteid));
    });
}

function queryAllNotes(courseId, callback=null, callback_args=null){
    //after ajax call, the callback function is executed with arguments : args...., notes
    $.ajax({
        type: "GET",
        url: "/courses/" + courseId.toString() + "/notes/json",
        contentType: "application/json",
        success: function (data, status, jqXHR) {
            notes = data;
            if(callback!=null){
               callback(...callback_args);
            }
            updateNoteView(notes)

        },

        error: function (jqXHR, status) {
            // error handler
            console.log(jqXHR);
            alert('fail' + status.code);
        }
    });

}

function sorter(sortBy, is_ascending){
    var order;
    notes.sort(function(a, b) {
        if (sortBy == "title"){
            order = a.title.toUpperCase().localeCompare(b.title.toUpperCase());
        }else if(sortBy == "date"){
            // check year is greater
            if(a.date.substring(6).toUpperCase().localeCompare(b.date.substring(6).toUpperCase()) == 0){
                order = a.date.toUpperCase().localeCompare(b.date.toUpperCase());
            }else{
                order = a.date.substring(6).toUpperCase().localeCompare(b.date.substring(6).toUpperCase())
            }
        }else if(sortBy == "upvotes"){
            order = a.upvotes -  b.upvotes;
        }else{
            alert(sortBy + ' sort option not supported ! ')
            throw "Unsupported sorting option : " + sortBy
        }
        if(!is_ascending){
            order = -1 * order;
        }

        return order

    });
}

//function queryAllNotes(courseId){
//
//    $.ajax({
//            type: "GET",
//            url: "/courses/" + courseId.toString() + "/notes/json",
//            contentType: "application/json",
//            success: function (data, status, jqXHR) {
//                notes = data;
//
//                $("#note-list").empty();
//
//                notes.map(function(note){
//                    $("#note-list").append(parse(noteItemFormat, note.courseId.toString(), note.id.toString(),
//                        note.title.toString(), note.creator.toString(), note.date.toString(), note.upvotes.toString()))
//                });
//
//                $(".note-select").click(function(e) {
//                    $('#note-list .active').removeClass('active');
//                    $(this).addClass('active');
//
//                    e.preventDefault();
//                    $("#note-frame").attr("src", "".concat("/courses/", e.target.dataset.courseid, "/notes-preview/", e.target.dataset.noteid));
//                });
//
//            },
//
//            error: function (jqXHR, status) {
//                // error handler
//                console.log(jqXHR);
//                alert('fail' + status.code);
//            }
//        });
//
//
//}

function sortcallback(sortBy, courseId){
    console.log("sorting by " + sortBy);

    var is_ascending = true;
    if(sortBy == "upvotes"){
        is_ascending = false;
    }

    if(notes == null){
        queryAllNotes(courseId, sorter, [sortBy, is_ascending]);
    }else{
        sorter(sortBy, is_ascending);
        updateNoteView(notes);
    }



};

function searchcallback(searchBy, courseId) {
    var searchKey = $("#searchbar").val();
    var url = "";
    if (searchBy == "title") {
        url = "/courses/" + courseId.toString() + "/notes/searchName/"+ searchKey;
    } else if (searchBy == "body") {
        url = "/courses/" + courseId.toString() + "/notes/searchContent/"+ searchKey;
    } else if (searchBy == "creator") {
        url = "/courses/" + courseId.toString() + "/notes/searchCreator/"+ searchKey;
    } else if (searchBy == "date") {
        searchKey = searchKey.replace("/", "").replace("/", "");
        url = "/courses/" + courseId.toString() + "/notes/searchDate/"+ searchKey;
    }
    searcher(courseId, searchKey, url);
};

$("#menu-toggle").click(function(e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
});

