$.getScript("/codemirror/lib/codemirror.js");
$.getScript("/codemirror/mode/javascript/javascript.js");

// parsing URL to get challenge ID
var url = document.URL.replace("http://", "");
url = url.substr(url.indexOf("/") + 1);
var challengeID = url.substr(url.indexOf("/") + 1);

// initial settings for CodeMirror input
var myCodeMirror = CodeMirror.fromTextArea(document.getElementById("codepad"), {
    lineNumbers: true,
});

// default language is Python
var lang = "python";

// load the page
loadPage();

/*
 * Loads the page and applies the user's chosen language setting.
 */
function loadPage() {
	console.log("loading page ...")
	vex.dialog.prompt({
		message: "Select a language from the menu.",
		input: "<select id=\"lang-select\">" +
				"	<option selected value=\"python\">Python</option>" +
				"	<option value=\"javascript\">Javascript</option>" +
				"</select>",
		buttons: [
			$.extend({}, vex.dialog.buttons.YES, {
			    text: "Let's get started!"
			})
		],
		overlayClosesOnClick: false,
		callback: function() {
			vex.dialog.alert({
				message: "Loading the editor...",
				buttons: [],
				overlayClosesOnClick: false,
				afterOpen: function() {
					lang = $("#lang-select").val();
					console.log("Language selected: " + lang);
					var loadParameters = {"challengeID" : challengeID, "language" : lang};
					$.post("/load", loadParameters, function (responseJSON) {
						var responseObject = JSON.parse(responseJSON);
						if (responseObject.status != "SUCCESS") {
							vex.dialog.alert("Error: " + responseObject.message);
						} else {
							var stubOrUserSolution = responseObject.code;
							console.log("Setting CodeMirror mode to: " + lang);
							myCodeMirror.setOption("mode", lang);
							myCodeMirror.getDoc().setValue(stubOrUserSolution);
							vex.close();
							$("#CountDownTimer").TimeCircles().start();
						}
					});
				}
			})
		}
	})
};

/*
 * Warning message that executes when user tries to leave page.
 */
$(window).on('beforeunload', function () {
	return "You've attempted to leave or refresh this page. " +
 		"If you do choose to leave, you won't be able to " +
 		"submit your solution to this question's leaderboard. ";
});


//Run Code Script
$("#run-button").click(function(e) {
	vex.dialog.buttons.YES.text = "OK"; // Need to reinitialize this every click (sometimes it's set to "Submit to Leaderboard!")
   	var timeLeft = $("#CountDownTimer").TimeCircles().getTime();
   	var isTimeOver = (timeLeft <= 0);
        
   	// note: separate on new line?
   	var userTests = $("#userInput")[0].value;
   	var userCode = myCodeMirror.getValue();
   	
   	// testing against user input
	console.log("submitting to usertests with language: " + lang)
   	var postParameters = {"language" : lang, "input" : userCode, "userTest" : userTests};
	vex.dialog.alert({
		message: "Compiling, please wait...",
		overlayClosesOnClick: false,
		buttons: []
	})
	$.post("/game/usertests", postParameters, function(responseJSON) {
		vex.close();
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
		
		console.log("submitting to deettests with language: " + lang)
	   	postParameters = {"language" : lang, "input" : userCode, "challengeID" : challengeID}
		$.post("/game/deettests", postParameters, function(responseJSON) {
			console.log("here");
			var passedAllTests = true;
			var deetResultString = "<b>Official Test Results</b><br/>";
			console.log(responseJSON);
			var responseObject = JSON.parse(responseJSON);
			if (responseObject.error === true) {
				console.log("error");
				passedAllTests = false;
				deetResultString += "Error occurred with Server. Please contact a DEET administrator.";
			} else {
				console.log("not error yay");
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
						var testStatus = "Passed";
						if (expected != actual) {
							testStatus = "Failed";
							passedAllTests = false;
						}
						var line = testStatus + ": " + "<i>" + testName + "</i><br/>";
						deetResultString += line;
					}
					
				}
			}
			
			if (passedAllTests) {
				$("#CountDownTimer").TimeCircles().stop();
				var currentTime = $("#CountDownTimer").TimeCircles().getTime();
				var isTimeUp = currentTime <= 0;
				deetResultString = "<b>Official Test Results</b><br/>" +
				"Congratulations! You passed all of the official tests!<br><br>" +
				"<i>Completed tests in " + responseObject.timeToTest + " milliseconds<br>" +
				"Brevity: " + responseObject.numLines + " total lines<br>" + 
				"Time to solve: " +  (120 - currentTime) + " seconds</i>"; 
				//TODO change 120 to whatever initial time was

				if (isTimeUp) {
					// don't allow user to submit to the leaderboard
					vex.dialog.open({
						message: userResultString + "<br/><br/>" + deetResultString,
						buttons: [
							$.extend({}, vex.dialog.buttons.YES, {
							    text: "Let's do another question!"
							})
						],
						callback: function() {
							window.location.href = "/categories";
						}
					});
				} else {
					// allow user to submit to the leaderboard
					vex.dialog.open({
						message: userResultString + "<br/><br/>" + deetResultString,
						buttons: [
							$.extend({}, vex.dialog.buttons.YES, {
							    text: "Submit to leaderboard!"
							}),
							$.extend({}, vex.dialog.buttons.NO, {
							    text: "Don't submit."
							    /* TODO insert some sort of prompt that tells the user that he can
							    	either keep working on the problem for a better score or 
							    	do a different problem */
							})
						],
						afterClose: function() {
							$("#CountDownTimer").TimeCircles().start();
						},
						callback: function(value) { 
							if (value) {
								var leaderboardParameters = {"input" : userCode,
										"language" : lang,
										"challengeID" : challengeID,
										"passed" : true,
										"efficiency" : responseObject.timeToTest,
										"numLines" : responseObject.numLines,
										"timeToSolve" : 120 - currentTime, 
										//Change 120 to whatever initial time was
										"aggregate" : 100};
								console.log(leaderboardParameters);
								$.post("/save", leaderboardParameters, function(responseJSON) {
									responseObject = JSON.parse(responseJSON);
									if (responseObject.status === "SUCCESS") {
										vex.dialog.alert({
											message: "Succesfully added your solution to the leaderboard.",
											buttons: [
												$.extend({}, vex.dialog.buttons.YES, {
													text: "Let's do another question!"
												})
											],
											callback: function() {
												window.location.href = "/categories";
											}
										});
									} else {
										vex.dialog.alert({
											message: responseObject.message,
											buttons: [
												$.extend({}, vex.dialog.buttons.YES, {
													text: "Close"
												})
											]
										});
										
									}
								});
							}
						}
					});
				}
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