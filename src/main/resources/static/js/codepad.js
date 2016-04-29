$.getScript("codemirror/lib/codemirror.js");
$.getScript("codemirror/mode/javascript/javascript.js");

var myCodeMirror = CodeMirror.fromTextArea(document.getElementById("codepad"), {
    lineNumbers: true
});

//Run Code Script
$('input[type=submit]').click(function(e) {
	vex.dialog.buttons.YES.text = "OK"; // Need to reinitialize this every click (sometimes it's set to "Submit to Leaderboard!")
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
   	var postParameters = {"language" : "python", "input" : userCode, "userTest" : userTests};
	$.post("/game/usertests", postParameters, function(responseJSON) {
		var userResultString = "<b>User Test Results</b><br/>";
		var responseObject = JSON.parse(responseJSON);
		if (responseObject.error === true) {
			userResultString += "Error occurred with Server. Please contact a DEET administrator.";
			vex.dialog.alert(userResultString);
			return;
		} else {
			console.log("not error");
			var compilationStatus = responseObject.compiled;
			if (compilationStatus != "success") {
				userResultString += ("Compilation error:<br>" + compilationStatus);
				vex.dialog.alert(userResultString);
				return;
			} else {
				var results = responseObject.runResults;
				for (var input in results){
					var output = results[input];
					var msg = "Input: " + input + " returned output: " + output + "<br/>";
					userResultString += msg;
				}
			}
		}
				
	   	postParameters = {"language" : "python", "input" : userCode, "challengeID" : challengeID}
		$.post("/game/deettests", postParameters, function(responseJSON) {
			console.log("here");
			var passedAllTests = true;
			var deetResultString = "<b>Official Test Results</b><br/>";
			var responseObject = JSON.parse(responseJSON);
			if (responseObject.error === true) {
				console.log("error");
				passedAllTests = false;
				deetResultString += "Error occurred with Server. Please contact a DEET administrator.";
			} else {
				console.log("not error");
				var compilationStatus = responseObject.compiled;
				if (compilationStatus != "success") {
					deetResultString += ("Compilation Error:<br>" + compilationStatus);
					passedAllTests = false;
					vex.dialog.alert(deetResultString);
					return;
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
				"You passed all of the official tests!<br><br>" +
				"Completed tests in " + responseObject.timeToTest + " milliseconds<br>" +
				"Brevity: " + responseObject.numLines + " total lines<br>" + 
				"Time to solve: " +  (120 - $("#CountDownTimer").TimeCircles().getTime()) + " seconds"; //TODO change 120 to whatever initial time was
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
									"efficiency" : responseObject.timeToTest,
									"numLines" : responseObject.numLines,
									"timeToSolve" : 120 - $("#CountDownTimer").TimeCircles().getTime(), //HERE TOOOOOO (see TODO above)
									"aggregate" : 100};
							console.log(leaderboardParameters);
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