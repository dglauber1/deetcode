$("#CountDownTimer").TimeCircles({ count_past_zero : false, time: { Days: { show: false }, Hours: { show: false } }});
$("#CountDownTimer").TimeCircles().start();
var isTimeRemaining = true;
$(function checkTimer() {
	var timeLeft = $("#CountDownTimer").TimeCircles().getTime();
	if (timeLeft <= 0) {
    	vex.dialog.alert("<b>Time's up!</b><br/>" +
    			"You can keep working, but your solution " +
    			"won't be submitted to the leaderboard.");
	} else {
		setTimeout(checkTimer, 1000);
	}
}); 
