$("#CountDownTimer").TimeCircles({ count_past_zero : false, time: { Days: { show: false }, Hours: { show: false } }});
$("#CountDownTimer").TimeCircles().start();
var isTimeRemaining = true;
$(function checkTimer() {
	var timeLeft = $("#CountDownTimer").TimeCircles().getTime();
	if (timeLeft <= 0) {
		alert("Time's up!");
	} else {
		setTimeout(checkTimer, 1000);
	}
}); 
