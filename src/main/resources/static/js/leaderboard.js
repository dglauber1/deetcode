function getLeaderboard(id) {
	// get type of info and language	
	var language = $("#language :selected").text();

	// get the challenge id
	var fullURL = window.location.href;
	var urlSplit = fullURL.split("/");
	var challengeId = urlSplit[urlSplit.length - 1]; 
	
	var postParameters = {
		type: JSON.stringify(id),
		language: JSON.stringify(language)
	};

	$.post("/leaderboard/" + challengeId + "/getInfo", postParameters, function(responseJSON){
		var table = $("#board")[0];

		// delete current rows
		while(table.rows.length > 1) {
			table.deleteRow(table.rows.length - 1);
		}

		// add new rows
		var responseObject = JSON.parse(responseJSON);
		var info = responseObject.info;

		for (var i = 0; i < info.length; i++) {
			var row = table.insertRow(table.rows.length);
			var username = row.insertCell(0);
			var language = row.insertCell(1);
			var score = row.insertCell(2);
			var solution = row.insertCell(3);

			username.innerHTML = "<a href=/user/" + info[i][0] + ">" + info[i][0] + "</a>";
			language.innerHTML = capitalizeFirstLetter(info[i][1]);
			score.innerHTML = "<span class='label label-success'>" + info[i][2] + "</span>";
			solution.innerHTML = "<button type='button' class='btn btn-primary' value='" 
									+ info[i][3] + "' onclick='popup(this)'>Solution</button>";						
		}
	});
}

$("#language").on("change", function() {
	getLeaderboard($(".active.leaderboard-type").find('input')[0].id);
});

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

var buttons = document.getElementsByClassName("btn");

$(".leaderboard-type").click(function(e) {
	var labelElement = $(e.target);
	var inputElement = labelElement.find("input");

	getLeaderboard(inputElement[0].id);
});



