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
    var challengeID = url.indexOf("game" + 1);
    
    // temporary hard coding of challengeID!
    challengeID = "add-one";
    
    console.log(challengeID);
    
   	// note: separate on new line?
   	var userTests = $("#userInput")[0].value;
   	var userCode = myCodeMirror.getValue();
   	
   	console.log(userTests);
   	console.log(userCode);
   	// testing against user input
   	var postParameters = {"language" : "python", "input" : userCode, "userTest" : userTests}
	$.post("/game/usertests", postParameters, function(responseJSON) {
		var userResultString = "<b>User Test Results</b><br/>";
		var responseObject = JSON.parse(responseJSON);
		if (responseObject.error === true) {
			userResultString += "Error: Failed to compile.";
		} else {
			var results = responseObject.runResults;
			for (var input in results){
				var output = results[input];
				var msg = "Input: " + input + " returned output: " + output + "<br/>";
				userResultString += msg;
			}
		}
				
	   	postParameters = {"language" : "python", "input" : userCode}
		$.post("/game/deettests", postParameters, function(responseJSON) {
			console.log("here");
			var passedAllTests = true;
			var deetResultString = "<b>Official Test Results</b><br/>";
			var responseObject = JSON.parse(responseJSON);
			if (responseObject.error === true) {
				console.log("error");
				passedAllTests = false;
				deetResultString += "Error: Invalid language, could not compile.";
			} else {
				console.log("not error");
				var compilationStatus = responseObject.compiled;
				if (compilationStatus != "success") {
					passedAllTests = false;
					deetResultString += ("Error: " + compilationStatus);
					vex.dialog.alert(deetResultString);
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
			
			if (passedAllTests) {
				$("#CountDownTimer").TimeCircles().stop();
				deetResultString = "<b>Official Test Results</b><br/>" +
				"You passed all of the official tests!";
				vex.dialog.buttons.YES.text = "Submit to leaderboard!";
				vex.dialog.buttons.NO.text = "Don't submit.";
				vex.dialog.open({
					message: userResultString + "<br/><br/>" + deetResultString,
					callback: function(value) { 
						if (value) {
							// this is currently hard coded in
							var leaderboardParameters = {"input" : userCode,
									"language" : "python",
									"challengeID" : challengeID,
									"passed" : true,
									"efficiency" : 0.0,
									"numLines" : 10,
									"timeToSolve" : $("#CountDownTimer").TimeCircles().getTime(),
									"aggregate" : 100};
							$.post("/save", postParameters, function(responseJSON) {
								responseObject = JSON.parse(responseJSON);
								if (reponseObject.status === "SUCCESS") {
									vex.dialog.alert("Succesfully added your solution to the leaderboard.");
								} else {
									vex.dialog.alert(responseObject.message);
								}
							});
						}
					}
				});
			} else {
				vex.dialog.alert(userResultString + "<br/><br/>" + deetResultString);
			}
			
		});
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