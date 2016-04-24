// code to instantiate Code Mirror interface for stub code
var pythonArea = $("#pythonStub")[0]; 
var pythonEditor = CodeMirror.fromTextArea(pythonArea, 
	{lineNumbers: true, 
	 lineWrapping: false,
	 autoRefresh: true,
	 mode: "python"});
pythonEditor.setSize('100%', 400);
pythonEditor.refresh();

var rubyArea = $("#rubyStub")[0]; 
var rubyEditor = CodeMirror.fromTextArea(rubyArea, 
	{lineNumbers: true, 
	 lineWrapping: false,
	 autoRefresh: true,
	 mode: "ruby"});
rubyEditor.setSize('100%', 400);
rubyEditor.refresh();

var jsArea = $("#jsStub")[0]; 
var jsEditor = CodeMirror.fromTextArea(jsArea, {lineNumbers: true, 
	 lineWrapping: false,
	 autoRefresh: true,
	 mode: "javascript"});
jsEditor.setSize('100%', 400);
jsEditor.refresh();

var javaArea = $("#javaStub")[0]; 
var javaEditor = CodeMirror.fromTextArea(javaArea, {lineNumbers: true, 
	 lineWrapping: false,
	 autoRefresh: true,
	 mode: "text/x-java"});
javaEditor.setSize('100%', 400);
javaEditor.refresh();

// When "Add a new category" is a selected under Challenge Category
$("#newCategoryDiv").hide();
$("#newCategoryDivTitle").hide();
$("#challengeSelect").change(function() {
	if ($(this).val() == "Add a new category") {
		$("#newCategoryDiv").show();
		$("#newCategoryDivTitle").show();
	} else {
		$("#newCategoryDiv").hide();
		$("#newCategoryDivTitle").hide();
	}
});

// whenever user enters text into name, check to see if the name is already taken
$("#name").on('input', function(){
	var name = $("#name")[0];
	var nameValue = JSON.stringify(name.value); // stringify?

	var postParameters = {
	    textValue:nameValue,
    };

    $.post("/namecheck", postParameters, function(responseJSON){
    	responseObject = JSON.parse(responseJSON);
        var exists = responseObject.exists;

        if (exists) {
        	document.getElementById("nameMessage").innerHTML = "This name is already taken.";
        } else {
        	document.getElementById("nameMessage").innerHTML = "";
        }
    });
});

// whenever user enters text into new category, check to see if the category is already taken
$("#newCategory").on('input', function(){
	var category = $("#newCategory")[0];
	var categoryValue = JSON.stringify(category.value); // stringify?

	var postParameters = {
	    textValue:categoryValue,
    };

    $.post("/categorycheck", postParameters, function(responseJSON){
    	responseObject = JSON.parse(responseJSON);
        var exists = responseObject.exists;

        if (exists) {
        	document.getElementById("newCategoryMessage").innerHTML = "This category already exists.";
        } else {
        	document.getElementById("newCategoryMessage").innerHTML = "";
        }
    });
});

// loads all the current categories into the dropdown
$(window).load(function() { 
	console.log(5);
	var postParameters = {};

	$.post("/getallcategories", postParameters, function(responseJSON){
    	responseObject = JSON.parse(responseJSON);
        var categories = responseObject.categories;

        var dropdown = $("#challengeSelect");
        dropdown.find('option').remove().end();

        for (var i = 0; i < categories.length; i++) {
        	var category = categories[i];
        	dropdown.append($("<option></option>")
        					.attr("value", category)
        					.text(category));
        }

        dropdown.append($("<option></option>")
        					.attr("value", "Add a new category")
        					.text("Add a new category"));
    });
});

$("#submit").click(function() {
	if ($("#challengeSelect").val() == "Add a new category") {
		var cat = $("#newCategory")[0].value;
	} else {
		var cat = $("#challengeSelect :selected").text();
	}

	var postParameters = {
	    category: JSON.stringify(cat),
	    name: JSON.stringify($("#name")[0].value),
	    description: JSON.stringify($("#description").val()),
	    javaInput: JSON.stringify($("#javaInput").val()),
	    javaOutput: JSON.stringify($("#javaOutput").val()),
	    javaStub: JSON.stringify(javaEditor.getValue()),
	    pythonInput: JSON.stringify($("#pythonInput").val()),
	    pythonOutput: JSON.stringify($("#pythonOutput").val()),
	    pythonStub: JSON.stringify(pythonEditor.getValue()),
	    rubyInput: JSON.stringify($("#rubyInput").val()),
	    rubyOutput: JSON.stringify($("#rubyOutput").val()),
	    rubyStub: JSON.stringify(rubyEditor.getValue()),
	    jsInput: JSON.stringify($("#jsInput").val()),
	    jsOutput: JSON.stringify($("#jsOutput").val()),
	    jsStub: JSON.stringify(jsEditor.getValue())
    };

    $.post("/admin_add/results", postParameters, function(responseJSON){
    	location.reload();
    	$("#description").val("");
    	$("#name")[0].value = "";
    	$("#javaInput").val("");
    	$("#javaOutput").val("");
    	$("#pythonInput").val("");
    	$("#pythonOutput").val("");
    	$("#rubyInput").val("");
    	$("#rubyOutput").val("");
    	$("#jsInput").val("");
    	$("#jsOutput").val("");
    });
});






