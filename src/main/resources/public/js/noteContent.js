function insertNoteCss() {
    var cssLinks = " \
    <link href='/css/note-content.css' rel='stylesheet'> \
    <link href='https://fonts.googleapis.com/css2?family=Inconsolata&family=Roboto&display=swap' rel='stylesheet'> \
    <link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/katex@0.11.1/dist/katex.min.css' integrity='sha384-zB1R0rpPzHqg7Kpt0Aljp8JPLqbXI3bhnPWROx27a9N0Ll6ZP/+DiW/UqRcLbRjq' crossorigin='anonymous'> \
    <script defer src='https://cdn.jsdelivr.net/npm/katex@0.11.1/dist/katex.min.js' integrity='sha384-y23I5Q6l+B6vatafAwxRu/0oK/79VlbSz7Q9aiSZUvyWYIYsd+qj+o24G5ZU2zJz' crossorigin='anonymous'></script> \
    <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/highlight.js/10.0.0/styles/monokai.min.css'> \
    <link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/editor.md@1/css/editormd.min.css'> \
    ";

    var frame = document.getElementById("note-content");
    frame.contentDocument.head.innerHTML += cssLinks;
}
