var dropZone = document.getElementById('drop-zone');
var uploadForm = document.getElementById('note-form');
var selectForm = document.getElementById("file-field")
var uploadFiles;

var filetypeSelect = document.getElementById("filetype-field");

$(document).ready(function() {

    var fonts = ['arial', 'roboto', 'inconsolata'];
    var Font = Quill.import('formats/font');
    Font.whitelist = fonts;
    Quill.register(Font, true);

    var quill = new Quill('#texteditor', {
        theme: 'snow',
        modules: {
            'toolbar': [
                [{ 'font': fonts }, { 'size': [] }],
                [ 'bold', 'italic', 'underline', 'strike' ],
                [{ 'color': [] }, { 'background': [] }],
                [{ 'script': 'super' }, { 'script': 'sub' }],
                [{ 'header': '1' }, { 'header': '2' }, 'blockquote', 'code-block' ],
                [{ 'list': 'ordered' }, { 'list': 'bullet'}, { 'indent': '-1' }, { 'indent': '+1' }],
                [ {'direction': 'rtl'}, { 'align': [] }],
                [ 'link', 'video', 'formula' ],
                [ 'clean' ]
            ],
            formula: true,
            syntax: true,
        },
    });
    loadFonts();
    $('#texteditor-container').hide();
});

function loadFonts() {
    window.WebFontConfig = {
        google: { families: [ 'Inconsolata::latin', 'Roboto+Slab::latin' ] }
    };
    (function() {
        var wf = document.createElement('script');
        wf.src = 'https://ajax.googleapis.com/ajax/libs/webfont/1/webfont.js';
        wf.type = 'text/javascript';
        wf.async = 'true';
        var s = document.getElementsByTagName('script')[0];
        s.parentNode.insertBefore(wf, s);
    })();
}

filetypeSelect.addEventListener("change", function() {
    if (filetypeSelect.value == "pdf") {
        $('#texteditor-container').hide();
        $('#file-area').show();
    } else {
        $('#texteditor-container').show();
        $('#file-area').hide();
    }
});
$('#note-form').submit(function() {
    document.getElementById("textarea").innerHTML = quill.root.innerHTML;
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