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

// detect if name is empty or not
$("#name").on('input', function(){
	var name = $("#name")[0];
	var nameValue = name.value;
	var len = nameValue.length;

	if (len === 0) {
		document.getElementById("nameError").innerHTML = "This field is empty.";
	} else {
		document.getElementById("nameError").innerHTML = "";
	}
});

// detect if description is empty or not
$("#description").on('input', function(){
	var description = $("#description");
	var descriptionValue = description.val();
	var len = descriptionValue.length;

	if (len === 0) {
		document.getElementById("descriptionError").innerHTML = "This field is empty.";
	} else {
		document.getElementById("descriptionError").innerHTML = "";
	}
});

// whenever user enters text into challenge directory name, check to see if the name is already taken
$("#pName").on('input', function(){
	var pName = $("#pName")[0];
	var pNameValue = pName.value;
	var len = pNameValue.length;
	
	var goodInput = true;
	var allHyphens = true;
	var empty = false;
	
	// check for non alphanumeric / non all-hyphens
	for (var i = 0; i < len; i++) {
		var code = pNameValue.charCodeAt(i);

		if (!(code === 45) && // hyphen
			!(code > 47 && code < 58) && // numeric 0-9
			!(code > 64 && code < 91) && // upper alpha A-Z
			!(code > 76 && code < 123)) { // lower alpha a-z
			goodInput = false;
			break;
		} 
	}

	// check if the input is ALL hyphens
	for (var i = 0; i < len; i++) {
		var code = pNameValue.charCodeAt(i);

		if (!(code === 45)) {
			allHyphens = false;
			break;
		} 
	}

	if (len === 0) {
		empty = true;
	}

	if (goodInput && !allHyphens) {
		document.getElementById("pNameError").innerHTML = "";

		var postParameters = {
	    	textValue:JSON.stringify(pNameValue)
    	};

	    $.post("/namecheck", postParameters, function(responseJSON){
	    	responseObject = JSON.parse(responseJSON);
	        var exists = responseObject.exists;

	        if (exists) {
	        	document.getElementById("pNameError").innerHTML = "This path name is already taken.";
	        } else {
	        	document.getElementById("pNameError").innerHTML = "";
	        }
	    });
	} else if (empty) {
		document.getElementById("pNameError").innerHTML = "This field is empty.";
	} else if (allHyphens) {
		document.getElementById("pNameError").innerHTML = "This path name has ONLY hyphens.";
	} else {
		document.getElementById("pNameError").innerHTML = 
					"No non-alphanumeric character or non-hyphens.";
	}
});

// whenever user enters text into new category, check to see if the category is already taken
$("#newCategory").on('input', function(){
	var category = $("#newCategory")[0];
	var categoryValue = category.value;

	var postParameters = {
	    textValue:JSON.stringify(categoryValue),
    };

    $.post("/categorycheck", postParameters, function(responseJSON){
    	responseObject = JSON.parse(responseJSON);
        var exists = responseObject.exists;

        if (exists) {
        	document.getElementById("newCategoryError").innerHTML = "This category already exists.";
        } else {
        	document.getElementById("newCategoryError").innerHTML = "";
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
	// check if the challenge path name is valid
	var pName = $("#pName")[0];
	var pNameValue = pName.value;
	var bad = false;
	
	// extra, get RID OF ****
	if (pNameValue === "") {
		document.getElementById("pNameError").innerHTML = "This field is empty.";
		bad = true;
	}

	if (document.getElementById("pNameError").innerHTML != "") {
		bad = true;
	}

	if ($("#name")[0].value === "") {
		document.getElementById("nameError").innerHTML = "This field is empty.";
		bad = true;
	}

	if ($("#description")[0].value === "") {
		document.getElementById("descriptionError").innerHTML = "This field is empty.";
		bad = true;
	}

	// AJAX call only if the not bad input
	if (!bad) {
		// parse the rest of the input
		if ($("#challengeSelect").val() == "Add a new category") {
			var cat = $("#newCategory")[0].value;
		} else {
			var cat = $("#challengeSelect :selected").text();
		}

		var postParameters = {
		    category: JSON.stringify(cat),
		    name: JSON.stringify($("#name")[0].value),
		    pName: JSON.stringify($("#pName")[0].value),
		    description: JSON.stringify($("#description").val()),
		    javaTestName: JSON.stringify($("#javaTestName").val()),
		    javaInput: JSON.stringify($("#javaInput").val()),
		    javaOutput: JSON.stringify($("#javaOutput").val()),
		    javaStub: JSON.stringify(javaEditor.getValue()),
		    pythonTestName: JSON.stringify($("#pythonTestName").val()),
		    pythonInput: JSON.stringify($("#pythonInput").val()),
		    pythonOutput: JSON.stringify($("#pythonOutput").val()),
		    pythonStub: JSON.stringify(pythonEditor.getValue()),
		    rubyTestName: JSON.stringify($("#rubyTestName").val()),
		    rubyInput: JSON.stringify($("#rubyInput").val()),
		    rubyOutput: JSON.stringify($("#rubyOutput").val()),
		    rubyStub: JSON.stringify(rubyEditor.getValue()),
		    jsTestName: JSON.stringify($("#jsTestName").val()),
		    jsInput: JSON.stringify($("#jsInput").val()),
		    jsOutput: JSON.stringify($("#jsOutput").val()),
		    jsStub: JSON.stringify(jsEditor.getValue())
	    };

	    $.post("/admin_add/results", postParameters, function(responseJSON){
	    	$("#description").val("");
	    	$("#pName")[0].value = "";
	    	$("#name")[0].value = "";
	    	$("#javaTestName").val("");
	    	$("#javaInput").val("");
	    	$("#javaOutput").val("");
	    	$("#pythonTestName").val("");
	    	$("#pythonInput").val("");
	    	$("#pythonOutput").val("");
	    	$("#rubyTestName").val("");
	    	$("#rubyInput").val("");
	    	$("#rubyOutput").val("");
	    	$("#jsTestName").val("");
	    	$("#jsInput").val("");
	    	$("#jsOutput").val("");
	    	location.reload();
	    });
	} else {
		document.getElementById("submitError").innerHTML = "Errors with the submission. Find and try again.";
	}
});






