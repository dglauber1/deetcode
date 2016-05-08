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

// true if it's the user's first time attempting the problem. false otherwise.
var isFirstTime = false;

// true if the user has submitted to the database already. false otherwise.
var isSubmitted = false;

//How much time the user has to solve the challenge
var totalTime = $("#CountDownTimer").TimeCircles().getTime();

/*
 * Warning message that executes when user tries to leave page.
 */
$(window).on('beforeunload', function () {
	console.log(isFirstTime);
	if (isFirstTime && !isSubmitted) {
		return "This is your first attempt at this problem. " +
 		"If you navigate away, you won't be able to submit your solution to the leaderboard. " +
 		"You are, however, allowed to continue working on the problem.";
	}
	
	if (!isSubmitted) {
		return "Wait up! You could lose your work if you navigate away.";
	}
});

/*
 * Loads the page and applies the user's chosen language setting.
 */
(function(){
	console.log("loading page ...");
	vex.dialog.alert({
		// Open loading screen
		message: "Loading language options...",
		buttons: [],
		overlayClosesOnClick: false,
		afterOpen: function() {
			// Retrieve available language options
			$.post("/game/loadlang", {"challengeID" : challengeID}, function(responseJSON) {
				var responseObject = JSON.parse(responseJSON);
				if (responseObject.status != "SUCCESS") {
					// There was an error
					vex.dialog.alert("Error: " + responseObject.message);
				} else {
					var langs = responseObject.langs;
					var langsString = "";
					for (i = 0; i < langs.length; i++) {
						langsString += ("<option value=\"" + langs[i] + "\">" 
							+ langs[i].toUpperCase() + "</option>");
					}
					// Close loading screen
					vex.close();
					// Offer language options to user
					vex.dialog.prompt({
						overlayClosesOnClick: false,
						message: "Select a language from the menu.",
						input: "<select id=\"lang-select\">" +
								langsString + 
								"</select>",
						buttons: [
							$.extend({}, vex.dialog.buttons.YES, {
							    text: "Let's get started!"
							})
						],
						callback: function() {
							// Retrieve stub code or user code from database
							// Open loading screen
							vex.dialog.alert({
								message: "Loading the editor...",
								buttons: [],
								overlayClosesOnClick: false,
								afterOpen: function() {
									lang = $("#lang-select").val();
									console.log("Language selected: " + lang);
									var loadParameters = {"challengeID" : challengeID, "language" : lang};
									$.post("/game/load", loadParameters, function (responseJSON) {
										responseObject = JSON.parse(responseJSON);
										// set isFirstTime flag
										isFirstTime = responseObject.isFirstTime;
										if (responseObject.status != "SUCCESS") {
											vex.dialog.alert("Error: " + responseObject.message);
										} else {
											// change indicator on screen
											if (!isFirstTime) {
												console.log("changing indicator");
												$("#indicator")[0].innerHTML = 
													"<a id=\"indicator\">This isn't your first attempt</a>";
											}
											// get stub code or user code
											var stubOrUserSolution = responseObject.code;
											// filler values
											var leaderboardParameters = {"input" : stubOrUserSolution,
													"language" : lang,
													"challengeID" : challengeID,
													"passed" : false,
													"efficiency" : -1,
													"numLines" : -1,
													"timeToSolve" : -1, 
													"aggregate" : -1};
											saveSolution(leaderboardParameters, false);
											myCodeMirror.setOption("mode", lang);
											myCodeMirror.getDoc().setValue(stubOrUserSolution);
											// close modal window
											vex.close();
											// display prompt
											$("#promptContent").show();
											// start timer
											$("#CountDownTimer").TimeCircles().start();
										}
									});
								}
							})
						}
					});
				}
			});		
		}
	})
})();

/*
 * displayDialog
 *  - a boolean that indicating whether or not to display modal dialogs after save
 * leaderboardParameters must contain the following keys:
 * 	(1) input - the user's code
 * 	(2) challengeID - the challenge ID
 * 	(3) passed - boolean indicating whether or not the user passed the test
 * 	(4) efficiency - the time it took to compile the user's code
 * 	(5) numLines - the number of lines in the user's solution
 * 	(6) timeToSolve - the time it took the user to solve the challenge
 * 	(7) aggregate - some type of composite score TODO clarify this
 */
function saveSolution(leaderboardParameters, displayDialog) {
	var msg = "";
	if (isFirstTime) {
		msg = "Successfully added your solution to the leaderboard.";
	} else {
		msg = "Successfully saved your solution. " +
				"Try another question, or continue optimizing this solution!";
	}
	
	$.post("/game/save", leaderboardParameters, function(responseJSON) {
		responseObject = JSON.parse(responseJSON);
		if (responseObject.status === "SUCCESS") {
			if (displayDialog) {
				vex.dialog.alert({
					overlayClosesOnClick: false,
					message: msg,
					buttons: [
						$.extend({}, vex.dialog.buttons.YES, {
							text: "Let's do another question!"
						})
					],
					callback: function() {
						isSubmitted = true;
						window.location.href = "/categories";
					}
				});
			}
		} else {
			console.log("Something went wrong while saving.");
			console.log(responseObject.message);
			if (displayDialog) {
				vex.dialog.alert({
					message: responseObject.message,
					buttons: [
						$.extend({}, vex.dialog.buttons.YES, {
							text: "Close"
						})
					]
				});
			}
		}
	});
}

//Run Code Script
$("#run-button").click(function(e) {
	vex.dialog.buttons.YES.text = "OK"; // Need to reinitialize this every click (sometimes it's set to "Submit to Leaderboard!")
   	var timeLeft = $("#CountDownTimer").TimeCircles().getTime();
   	var isTimeOver = (timeLeft <= 0);
        
   	var userTests = $("#userInput")[0].value;
   	var userCode = myCodeMirror.getValue();
   	
   	// testing against user input
	console.log("submitting to usertests with language: " + lang);
   	var postParameters = {"language" : lang, "input" : userCode, "userTest" : userTests};
	vex.dialog.alert({
		message: "Compiling, please wait...",
		overlayClosesOnClick: false,
		buttons: []
	});
	
	$("#CountDownTimer").TimeCircles().stop();
	$.post("/game/usertests", postParameters, function(responseJSON) {
		$("#CountDownTimer").TimeCircles().start();
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
		
		console.log("submitting to deettests with language: " + lang);
	   	postParameters = {"language" : lang, "input" : userCode, "challengeID" : challengeID};
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
				var currentTime = $("#CountDownTimer").TimeCircles().getTime();
				var isTimeUp = currentTime <= 0;
				var efficiency = responseObject.timeToTest;
				var numLines = responseObject.numLines;
				var timeToSolve = Math.round(totalTime - currentTime);
				var aggregate = (Math.round(currentTime) + (100 - numLines) * 9 + (900 - efficiency)) * 1000;
				deetResultString = "<b>Official Test Results</b><br/>" +
				"Congratulations! You passed all of the official tests!<br>" +
				"<i>Completed tests in " + efficiency + " milliseconds<br>" +
				"Brevity: " + numLines + " total lines<br>" + 
				"Time to solve: " +  timeToSolve + " seconds</i><br>" + 
				"<b>Aggregate score: " + aggregate + "</b><br/><br/>"; 
				var saveParameters = {"input" : userCode,
						"language" : lang,
						"challengeID" : challengeID,
						"passed" : true,
						"efficiency" : responseObject.timeToTest,
						"numLines" : responseObject.numLines,
						"timeToSolve" : timeToSolve, 
						"aggregate" : aggregate};

				if (isTimeUp) {
					// time's up, don't allow user to submit to the leaderboard
					vex.dialog.open({
						message: userResultString + "<br/><br/>" + deetResultString +
							"<b>You can either move on to another " +
							"question or continue optimizing your solution.</b>",
						buttons: [
							$.extend({}, vex.dialog.buttons.YES, {
							    text: "Do another question!"
							}),
							$.extend({}, vex.dialog.buttons.NO, {
							    text: "Stay here"
							})
						],
						callback: function(value) {
							saveSolution(saveParameters, false);
							isSubmitted = true;
							if (value) {
								window.location.href = "/categories";
							}
						}
					});
				} else {
					if (!isFirstTime) {
						vex.dialog.alert(userResultString + "<br/><br/>" + deetResultString);
						saveSolution(saveParameters, false);
						$("#CountDownTimer").TimeCircles().start();
					} else {
						var leaderboardParameters = {"language" : lang,
							"challengeID" : challengeID,
							"efficiency" : efficiency,
							"numLines" : numLines,
							"timeToSolve" : timeToSolve,
							"aggregate" : aggregate
							// TODO add timestamp and fix on backend!
						};

						// compare user scores to leaderboard scores
						$.post("/game/compare", leaderboardParameters, function(responseJSON) {
							var responseObject = JSON.parse(responseJSON);
							if (responseObject.status != "SUCCESS") {
								// something went wrong on the back end
								vex.dialog.alert(responseObject.message);
							} else {
								if (responseObject.isBetter) {
									// if the user's score is ranked highly enough
									vex.dialog.open({
										message: userResultString + "<br/>" + deetResultString +
											"<b>Great job! You managed to earn a place on the leaderboard. " + 
											"You can either submit this result or continue " +
											"optimizing your solution.<b>",
										buttons: [
											$.extend({}, vex.dialog.buttons.YES, {
											    text: "Submit to leaderboard!"
											}),
											$.extend({}, vex.dialog.buttons.NO, {
											    text: "Don't submit."
											})
										],
										afterClose: function() {
											$("#CountDownTimer").TimeCircles().start();
										},
										callback: function(value) { 
											if (value) {
												$.post("/game/remove", leaderboardParameters, function(responseJSON) {
													var responseObject = JSON.parse(responseJSON);
													if (responseObject.status != "SUCCESS") {
														// something went wrong on the back end
														vex.dialog.alert(responseObject.message);
													} else {
														saveSolution(saveParameters, true);
													}
												});
											}
										}
									});
								} else {
									// user's score wasn't good enough to make the leaderboard
									vex.dialog.open({
										message: userResultString + "<br/>" + deetResultString +
											"<b>Your score, however, wasn't good enough to qualify " + 
											"for the leaderboard. You can either move on to another " +
											"question or continue optimizing your solution<b>",
										buttons: [
											$.extend({}, vex.dialog.buttons.YES, {
											    text: "Move on!"
											}),
											$.extend({}, vex.dialog.buttons.NO, {
											    text: "Keep working."
											})
										],
										afterClose: function() {
											$("#CountDownTimer").TimeCircles().start();
										},
										callback: function(value) { 
											if (value) {
												saveSolution(saveParameters, true);
											}
										}
									});
								}
							}
						});
					}
				} 	
			} else {
				vex.dialog.alert(userResultString + "<br/><br/>" + deetResultString);
			}
		});
	});
});