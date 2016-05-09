// GLOBAL VARIABLES for edit challenge
ORIG_CHALLENGE_DIR_NAME = "";

// code to instantiate Code Mirror interface for stub code
var pythonArea = document.getElementById("pythonStub")
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
var jsEditor = CodeMirror.fromTextArea(jsArea, 
	{lineNumbers: true, 
	 lineWrapping: false,
	 autoRefresh: true,
	 mode: "javascript"});
jsEditor.setSize('100%', 400);
jsEditor.refresh();

var javaArea = $("#javaStub")[0]; 
var javaEditor = CodeMirror.fromTextArea(javaArea, 
	{lineNumbers: true, 
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
		document.getElementById("categoryError").innerHTML = "";
	} else if ($(this).val() == "Pick a Category") {
		document.getElementById("categoryError").innerHTML = "Please pick a category.";
	} else {
		$("#newCategoryDiv").hide();
		$("#newCategoryDivTitle").hide();
		document.getElementById("categoryError").innerHTML = "";
	}
});

// detect if difficulty is picked or not
$("#difficultyLevel").change(function() {
	if ($(this).val() == "unpicked") {
		document.getElementById("difficultyError").innerHTML = "Please pick a difficulty level.";
	} else {
		document.getElementById("difficultyError").innerHTML = "";
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

	        if (exists && pNameValue != ORIG_CHALLENGE_DIR_NAME) {
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
	var len = categoryValue.length;
	
	var goodInput = true;
	var allHyphens = true;
	var empty = false;
	
	// check for non alphanumeric / non all-hyphens
	for (var i = 0; i < len; i++) {
		var code = categoryValue.charCodeAt(i);

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
		var code = categoryValue.charCodeAt(i);

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
	} else if (empty) {
		document.getElementById("newCategoryError").innerHTML = "This field is empty.";
	} else if (allHyphens) {
		document.getElementById("newCategoryError").innerHTML = "Category has ONLY hyphens.";
	} else {
		document.getElementById("newCategoryError").innerHTML = 
					"No non-alphanumeric character or non-hyphens.";
	}    
});

// loads all the current categories into the dropdown
// $(window).load(function() {
	
// 	var postParameters = {};

// 	$.post("/getallcategories", postParameters, function(responseJSON){
//     	responseObject = JSON.parse(responseJSON);
//         var categories = responseObject.categories;

//         var dropdown = $("#challengeSelect");
//         dropdown.find('option').remove().end();

//         // default value
//         dropdown.append($("<option></option>")
//         					.attr("value", "Pick a Category")
//         					.text("Pick a Category"));

//         for (var i = 0; i < categories.length; i++) {
//         	var category = categories[i];
//         	dropdown.append($("<option></option>")
//         					.attr("value", category)
//         					.text(category));
//         }

//         dropdown.append($("<option></option>")
//         					.attr("value", "Add a new category")
//         					.text("Add a new category"));
//     });

//     ORIG_CHALLENGE_DIR_NAME = $("#pName")[0].value;
// });

// submit for adding a challenge
$("#submit").click(function() {
	// parse the rest of the input
	if ($("#challengeSelect").val() == "Add a new category") {
		var cat = $("#newCategory")[0].value;
	} else {
		var cat = $("#challengeSelect :selected").text();
	}

	var postParameters = {
	    category: JSON.stringify(cat),
	    difficulty: JSON.stringify($("#difficultyLevel :selected").val()),
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

    $.post("/admin/add/results", postParameters, function(responseJSON){
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
    	alert("Successfully added the challenge!");
    	location.reload();
    });
});

// submit for editing a challenge
$("#editSubmit").click(function() {
	// parse the rest of the input
	if ($("#challengeSelect").val() == "Add a new category") {
		var cat = $("#newCategory")[0].value;
	} else {
		var cat = $("#challengeSelect :selected").text();
	}

	var postParameters = {
	    category: JSON.stringify(cat),
	    difficulty: JSON.stringify($("#difficultyLevel :selected").val()),
	    name: JSON.stringify($("#name")[0].value),
	    origPName: JSON.stringify(ORIG_CHALLENGE_DIR_NAME),
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

    console.log(postParameters);


    $.post("/admin/edit/results", postParameters, function(responseJSON){
    	// unlike admin/add/results, does not clear every field
    	if ($("#pName")[0].value === ORIG_CHALLENGE_DIR_NAME) {
    		alert("Successfully edited the challenge!");
    		location.reload();
    	} else {
    		alert("Successfully edited the challenge!");
    		window.location.href = "http://localhost:4567/admin/edit/" + $("#pName")[0].value;
    	}
    });
});

function isBad(whichTab) {
	var bad = false;

	if (whichTab === "#basic") {
		// check if the challenge path name is valid
		var pName = $("#pName")[0];
		var pNameValue = pName.value;
		
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

		if (document.getElementById("newCategoryError").innerHTML != "") {
			bad = true;
		}

		if ($("#challengeSelect").val() === "Add a new category" && 
			$("#newCategory")[0].value === "") {
			document.getElementById("newCategoryError").innerHTML = "This field is empty.";
			bad = true;
		}

		if ($("#challengeSelect").val() === "Pick a Category") {
			document.getElementById("categoryError").innerHTML = "Please pick a category.";
			bad = true;
		}

		if ($("#difficultyLevel").val() === "unpicked") {
			document.getElementById("difficultyError").innerHTML = "Please pick a difficulty level.";
			bad = true;
		}
	} else if (whichTab === "#java") {
		// check if test case pages are correct
		var javaTestNameLines = $("#javaTestName").val().split(/\n/).length;
		var javaInputLines = $("#javaInput").val().split(/\n/).length;
		var javaOutputLines = $("#javaOutput").val().split(/\n/).length;

		if (javaTestNameLines != javaInputLines || 
			javaTestNameLines != javaOutputLines || 
			javaInputLines != javaOutputLines ||
			(($("#javaTestName").val() === "" || $("#javaInput").val() === "" || $("#javaOutput").val() === "") &&
			 ($("#javaTestName").val() != "" || $("#javaInput").val() != "" || $("#javaOutput").val() != ""))) {
			document.getElementById("javaError").innerHTML = "Number of test names, inputs, and outputs must match.";
			bad = true;
		} else {
			document.getElementById("javaError").innerHTML = "";
		}
	} else if (whichTab === "#python") {
		var pythonTestNameLines = $("#pythonTestName").val().split(/\n/).length;
		var pythonInputLines = $("#pythonInput").val().split(/\n/).length;
		var pythonOutputLines = $("#pythonOutput").val().split(/\n/).length;

		if (pythonTestNameLines != pythonInputLines || 
			pythonTestNameLines != pythonOutputLines || 
			pythonInputLines != pythonOutputLines ||
			(($("#pythonTestName").val() === "" || $("#pythonInput").val() === "" || $("#pythonOutput").val() === "") &&
			 ($("#pythonTestName").val() != "" || $("#pythonInput").val() != "" || $("#pythonOutput").val() != ""))) {
			document.getElementById("pythonError").innerHTML = "Number of test names, inputs, and outputs must match.";
			bad = true;
		} else {
			document.getElementById("pythonError").innerHTML = "";
		}
	} else if (whichTab === "#ruby") {
		var rubyTestNameLines = $("#rubyTestName").val().split(/\n/).length;
		var rubyInputLines = $("#rubyInput").val().split(/\n/).length;
		var rubyOutputLines = $("#rubyOutput").val().split(/\n/).length;

		if (rubyTestNameLines != rubyInputLines || 
			rubyTestNameLines != rubyOutputLines || 
			rubyInputLines != rubyOutputLines ||
			(($("#rubyTestName").val() === "" || $("#rubyInput").val() === "" || $("#rubyOutput").val() === "") &&
			 ($("#rubyTestName").val() != "" || $("#rubyInput").val() != "" || $("#rubyOutput").val() != ""))) {
			document.getElementById("rubyError").innerHTML = "Number of test names, inputs, and outputs must match.";
			bad = true;
		} else {
			document.getElementById("rubyError").innerHTML = "";
		}
	} else if (whichTab === "#javascript") {
		var jsTestNameLines = $("#jsTestName").val().split(/\n/).length;
		var jsInputLines = $("#jsInput").val().split(/\n/).length;
		var jsOutputLines = $("#jsOutput").val().split(/\n/).length;

		if (jsTestNameLines != jsInputLines || 
			jsTestNameLines != jsOutputLines || 
			jsInputLines != jsOutputLines ||
			(($("#jsTestName").val() === "" || $("#jsInput").val() === "" || $("#jsOutput").val() === "") &&
			 ($("#jsTestName").val() != "" || $("#jsInput").val() != "" || $("#jsOutput").val() != ""))) {
			document.getElementById("jsError").innerHTML = "Number of test names, inputs, and outputs must match.";
			bad = true;
		} else {
			document.getElementById("jsError").innerHTML = "";
		}
	}

	return bad;
}

// continue and previous buttons
$('.btnNext').click(function(){
	var whichTab = $('.nav-tabs > .active').find('a').attr("href");
	var bad = isBad(whichTab);

	if (bad) {
		document.getElementById("submitError").innerHTML = "Please fix errors and try again.";
	} else {
		document.getElementById("submitError").innerHTML = "";
		$('.nav-tabs > .active').next('li').find('a').tab('show');
	}
});

$('.btnPrevious').click(function(){
  $('.nav-tabs > .active').prev('li').find('a').tab('show');
});

$(window).on('beforeunload', function () {
	return "Are you sure you want to leave? Your changes will not be saved.";
});
