$.getScript("codemirror/lib/codemirror.js");
$.getScript("codemirror/mode/javascript/javascript.js");

var myCodeMirror = CodeMirror.fromTextArea(document.getElementById("codepad"), {
    lineNumbers: true
});

//Run Code Script
$('input[type=submit]').click(function(e) {
   	var timeLeft = $("#CountDownTimer").TimeCircles().getTime();
   	var isTimeOver = (timeLeft <= 0);
   	
   	// parsing URL to get challenge ID
   	// note: hardcoding this value later on for now until tyler and i resolve formatting
    var url = document.URL.replace("http://", "");
    url = url.substr(url.indexOf("/") + 1);
    
   	// note: separate on new line?
   	var userTests = $("#userInput")[0].value;
   	var userCode = myCodeMirror.getValue();
   	
   	console.log(userTests);
   	console.log(userCode);
   	// testing against user input
   	var postParameters = {"language" : "python", "input" : userCode, "userTest" : userTests}
	var userResultString = "<b>User Test Results</b><br/>";
	$.post("/game/usertests", postParameters, function(responseJSON) {
		var responseObject = JSON.parse(responseJSON);
		if (responseObject.error === true) {
			userResultString = userResultString.concat("Error: Failed to compile.");
		} else {
			var results = responseObject.runResults;
			for (var input in results){
				var output = results[input];
				var msg = "Input: " + input + " returned output: " + output + "<br/>";
				userResultString += msg;
			}
		}
		
		var passedAllTests = true;
	   	postParameters = {"language" : "python", "input" : userCode}
		var deetResultString = "<b>Official Test Results</b><br/>";
		$.post("/game/deettests", postParameters, function(responseJSON) {
			var responseObject = JSON.parse(responseJSON);
			if (responseObject.error === true) {
				deetResultString += "Error: Invalid language, could not compile.";
			} else {
				var compilationStatus = responseObject.compiled;
				if (compilationStatus != "success") {
					vex.dialog.alert("Error: " + compilationStatus);
				} else {
					var results = responseObject.testResults;
					for (i = 0; i < results.length; i++) {
						var input = results[i][0];
						var expected = results[i][1];
						var actual = results[i][2];
						var testName = results[i][3];
						var testStatus = "SUCCESS";
						if (expected != actual) {
							testStatus = "FAILURE";
							passedAllTests = false;
						}
						var line = testStatus + " on " + testName + ": on (" + input 
							+ "), expected " + expected + ", got " + actual + "<br/>";
						deetResultString += line;
					}
				}
			}
		});
		
		if (passedAllTests) {
			$("#CountDownTimer").TimeCircles().stop();
			deetResultString = "<b>Official Test Results</b><br/>" +
			"You passed all of the official tests!";
			vex.dialog.buttons.YES.text = "Submit to leaderboard!";
			vex.dialog.buttons.NO.text = "Don't submit.";
			vex.dialog.open({
				message: userResultString + "<br/><br/>" + deetResultString
			});
		} else {
			vex.dialog.alert(userResultString + "<br/><br/>" + deetResultString);
		}
	});

   	// determine whether or not this is the user has solved this problem
   	// or has attempted to run it
    // note: dan's code could do this
//   	var isRepeatedAttempt = false;
//   	
//   	if (isTimeOver || isRepeatedAttempt) {
//   		
//   		// communicate with back end to run code 
//   		// determine whether or not code passed or failed 
//   		// don't let the user submit to the leaderboard 	
//   		vex.dialog.alert("<b>Ran your code! Unfortunately, you can't submit to the leaderboard.</b>");
//   	} else {
//   		// communicate with back end to run code 
//   		// determine whether or not code passed or failed 
//   		vex.dialog.alert("<b>Ran your code! Submitting to the leaderboard.</b>");
//   	}
});