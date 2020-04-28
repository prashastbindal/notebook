var dropZone = document.getElementById('drop-zone');
var selectForm = document.getElementById("file-field");
var submitForm = document.getElementById("submit-button");

var filetypeSelect = document.getElementById("filetype-field");

var editor;

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

    editor = editormd("markdown", {
        path: "https://cdn.jsdelivr.net/npm/editor.md@1.5.0/lib/",
        onload : function() {
            $('#markdown-container').hide();
        },
        saveHTMLToTextarea : true
    });
    editormd.loadScript("https://cdn.jsdelivr.net/npm/editor.md@1/languages/en.min", function(){
        editor.lang = editormd.defaults.lang;
        editor.recreate();
    })
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
        $('#markdown-container').hide();
    } else if (filetypeSelect.value == "html") {
        $('#texteditor-container').show();
        $('#file-area').hide();
        $('#markdown-container').hide();
    } else if (filetypeSelect.value == "md") {
        $('#texteditor-container').hide();
        $('#file-area').hide();
        $('#markdown-container').show();
    }
});
$('#note-form').submit(function() {
    var title = document.getElementById('title-field').value;
    if (title == null || title == '') {
        e.preventDefault();
        alert("Please supply a title for your note.");
    }

    if (filetypeSelect.value == "html") {
        document.getElementById("textarea").innerHTML = quill.root.innerHTML;
    } else if (filetypeSelect.value == "md") {
        document.getElementById("textarea").innerHTML = editor.getHTML();
        filetypeSelect.value = "html";
    } else if (filetypeSelect.value == "pdf") {
        var name = document.getElementById('name');
        if (name == null || name == '') {
            e.preventDefault();
            alert("Uploaded file is empty or invalid, please try again.");
            window.location.reload();
        }
    }
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