$.getScript("codemirror/lib/codemirror.js");
$.getScript("codemirror/mode/javascript/javascript.js");

var myCodeMirror = CodeMirror.fromTextArea(document.getElementById("codepad"), {
    lineNumbers: true
});
