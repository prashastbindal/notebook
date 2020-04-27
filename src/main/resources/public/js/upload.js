var dropZone = document.getElementById('drop-zone');
var selectForm = document.getElementById("file-field");
var submitForm = document.getElementById("submit-button");

var filetypeSelect = document.getElementById("filetype-field");

filetypeSelect.addEventListener("change", function() {
    if (filetypeSelect.value == "pdf") {
        $('#texteditor').trumbowyg('destroy');
        $('#texteditor').hide();
        $('#file-area').show();
    } else {
        $('#texteditor').show();
        $('#texteditor').trumbowyg({
            resetCss: true,
            removeformatPasted: true
        });
        $('#file-area').hide();
    }
});
$('#note-form').submit(function() {
    document.getElementById("textarea").innerHTML = $('#texteditor').trumbowyg('html');
});

var addFileName = function(name) {
    // Create a new div so we know upload was successful
    var newDiv = document.createElement("div");
    newDiv.className = "js-upload-finished text-center";
    newDiv.id = "name";
    newDiv.appendChild(document.createTextNode(name));

    var currentDiv = document.getElementById("div-next");
    var parent = document.getElementById("div-upload");
    parent.insertBefore(newDiv, currentDiv.nextSibling);
}

selectForm.addEventListener('change', function(e) {
    // replace name if there is one already
    var name = document.getElementById('name');
    if (name != null && name != '') {
        var element = document. getElementById('name');
        element.parentNode.removeChild(element);
    }
    addFileName(document.getElementById('file-field').files[0].name);
});

submitForm.addEventListener('click', function(e) {
    var name = document.getElementById('textarea');
    var title = document.getElementById('title-field').value;
    if (name == null || name == '') {
        e.preventDefault();
        alert("Uploaded file is empty or invalid, please try again.");
        window.location.reload();
    }
    if (title == null || title == '') {
        e.preventDefault();
        alert("Please supply a title for your note.");
    }
});

dropZone.ondrop = function(e) {
    e.preventDefault();
    this.className = 'upload-drop-zone';
    addFileName(e.dataTransfer.files[0].name);
    selectForm.files = e.dataTransfer.files;
};

dropZone.ondragover = function() {
    this.className = 'upload-drop-zone drop';
    return false;
};

dropZone.ondragleave = function() {
    this.className = 'upload-drop-zone';
    return false;
};