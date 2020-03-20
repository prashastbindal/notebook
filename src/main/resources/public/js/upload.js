+ function($) {
    'use strict';


    var dropZone = document.getElementById('drop-zone');
    var uploadForm = document.getElementById('note-form');
    var selectForm = document.getElementById("file-field")
    var uploadFiles;

    var startUpload = function(files, title, creator) {
        var formData = new FormData();
        formData.append('title', title);
        formData.append('creator', creator);
        // can only upload 1 file at a time right now, can change in future
        formData.append('file', files[0]);
        // for now until we have validation
        formData.append('filetype', 'pdf');

        $.ajax({
            url: "../notes",
            data: formData,
            type: 'POST',
            processData: false,
            contentType: false})
    }

    var addFileName = function(name) {
        // Create a new div so we know upload was successful
        var newDiv = document.createElement("div");
        newDiv.className = "js-upload-finished text-center";
        newDiv.appendChild(document.createTextNode(name));

        var currentDiv = document.getElementById("div-next");
        var parent = document.getElementById("div-upload")
        parent.insertBefore(newDiv, currentDiv);

    }

    uploadForm.addEventListener('submit', function(e) {
        e.preventDefault()
        // If we select a file, overwrite any dragged ones for now.
        if (document.getElementById('file-field').files.length != 0) {
            uploadFiles = document.getElementById('file-field').files;
        }
        var title = document.getElementById('title-field').value;
        var creator = document.getElementById('creator-field').value;

        startUpload(uploadFiles, title, creator)
    })

    selectForm.addEventListener('change', function(e) {
        e.preventDefault();
        addFileName(document.getElementById('file-field').files[0].name);
    })



    dropZone.ondrop = function(e) {
        e.preventDefault();
        this.className = 'upload-drop-zone';
        addFileName(e.dataTransfer.files[0].name);
        uploadFiles = e.dataTransfer.files;
    };

    dropZone.ondragover = function() {
        this.className = 'upload-drop-zone drop';
        return false;
    };

    dropZone.ondragleave = function() {
        this.className = 'upload-drop-zone';
        return false;
    };

}(jQuery);