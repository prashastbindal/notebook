var dropZone = document.getElementById('drop-zone');
var uploadForm = document.getElementById('note-form');
var selectForm = document.getElementById("file-field")
var uploadFiles;

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
    // Create a new div so we know drag-drop was successful
    var newDiv = document.createElement("div");
    newDiv.className = "js-upload-finished text-center";
    newDiv.appendChild(document.createTextNode(name));

    var currentDiv = document.getElementById("div-next");
    var parent = document.getElementById("div-upload")
    parent.insertBefore(newDiv, currentDiv);
}

selectForm.addEventListener('change', function(e) {
    addFileName(document.getElementById('file-field').files[0].name);
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