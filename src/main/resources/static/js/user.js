function popup(challengeName) {
	var value = challengeName.value;
	var edited = "<pre>" + value.split("\n").join("<br>") + "</pre>";
    w2popup.open({
        title: 'Solution',
        body: '<div class="w2ui">' + edited + '</div>',
        width     : 700,
        height    : 500,
    });
}

TABS = "<ul class='nav nav-tabs'> \
	<li class='active'><a data-toggle='tab' href='#info'>Info</a></li> \
    <li><a data-toggle='tab' href='#aggregate'>Aggregate</a></li> \
    <li><a data-toggle='tab' href='#efficiency'>Efficiency</a></li> \
    <li><a data-toggle='tab' href='#brevity'>Brevity</a></li> \
    <li><a data-toggle='tab' href='#completion'>Completion Time</a></li> \
</ul> \
<div class='tab-content'> \
	<div id='info' class='tab-pane fade in active'> \
      <div class='row'> \
          <div class='col-xs-6 col-md-12' id='username'> \
          	The tabs show statistics based on the different metrics used to score a successful entry. \
          	An entry is successful when the user completes the challenges on his/her first try. \
          	Each tab will compare the user's \
          	metric score to the average score of that metric over all successful submissions \
          	and to the best score of that metric. \
          	There will only be a bar for the user in the graph if the user successfully completed \
          	the challenge on their first attempt. Otherwise, there will only be two bars, one \
          	for the average stat and one for the best stat. \
          </div> \
        </div> \
    </div> \
    <div id='aggregate' class='tab-pane fade'> \
      <svg class='chart' id='aggregateChart'></svg> \
    </div> \
    <div id='efficiency' class='tab-pane fade'> \
      <svg class='chart' id='efficiencyChart'></svg>  \
    </div> \
    <div id='brevity' class='tab-pane fade'> \
      <svg class='chart' id='brevityChart'></svg> \
    </div> \
    <div id='completion' class='tab-pane fade'> \
      <svg class='chart' id='completionChart'></svg> \
    </div> \
</div>";

function statPopup() {
    w2popup.open({
        title: 'Stats',
        body: TABS,
        width     : 700,
        height    : 500,
    });
}

$(".stats").click(function() {
	statPopup();
	var challengeName = $(this).parent().parent().children()[0].childNodes[0].innerHTML;
	console.log(challengeName);
	var user = $("title").text();
	var language = $(this).parent().parent().children()[3].innerHTML;

	var postParameters = {
		user: JSON.stringify(user),
		challengeName: JSON.stringify(challengeName),
		language: JSON.stringify(language),
		metric: JSON.stringify("aggregate")
	};

	// post for aggregate
	$.post("/user/stats", postParameters, function(responseJSON) {
		responseObject = JSON.parse(responseJSON);
        var userStat = responseObject.user;
        var averageStat = responseObject.average;
        var bestStat = responseObject.best;

        if (userStat === "n/a") {
        	var data = [
	        	{name: "Average",	value: averageStat},
	        	{name: "Best",		value: bestStat}
	        ];
        } else {
        	var data = [
	        	{name: user,		value: userStat},
	        	{name: "Average",	value: averageStat},
	        	{name: "Best",		value: bestStat}
        	];
        }

        drawStats("#aggregateChart", data);
	});

	postParameters = {
		user: JSON.stringify(user),
		challengeName: JSON.stringify(challengeName),
		language: JSON.stringify(language),
		metric: JSON.stringify("efficiency")
	};

	// post for efficiency
	$.post("/user/stats", postParameters, function(responseJSON) {
		responseObject = JSON.parse(responseJSON);
        var userStat = responseObject.user;
        var averageStat = responseObject.average;
        var bestStat = responseObject.best;

        console.log(userStat);
        console.log(averageStat);
        console.log(bestStat);

        if (userStat === "n/a") {
        	var data = [
	        	{name: "Average",	value: averageStat},
	        	{name: "Best",		value: bestStat}
	        ];
        } else {
        	var data = [
	        	{name: user,		value: userStat},
	        	{name: "Average",	value: averageStat},
	        	{name: "Best",		value: bestStat}
        	];
        }

        drawStats("#efficiencyChart", data);
	});

	postParameters = {
		user: JSON.stringify(user),
		challengeName: JSON.stringify(challengeName),
		language: JSON.stringify(language),
		metric: JSON.stringify("brevity")
	};

	// post for brevity
	$.post("/user/stats", postParameters, function(responseJSON) {
		responseObject = JSON.parse(responseJSON);
        var userStat = responseObject.user;
        var averageStat = responseObject.average;
        var bestStat = responseObject.best;

        if (userStat === "n/a") {
        	var data = [
	        	{name: "Average",	value: averageStat},
	        	{name: "Best",		value: bestStat}
	        ];
        } else {
        	var data = [
	        	{name: user,		value: userStat},
	        	{name: "Average",	value: averageStat},
	        	{name: "Best",		value: bestStat}
        	];
        }

        drawStats("#brevityChart", data);
	});

	postParameters = {
		user: JSON.stringify(user),
		challengeName: JSON.stringify(challengeName),
		language: JSON.stringify(language),
		metric: JSON.stringify("completion time")
	};

	// post for time to completion
	$.post("/user/stats", postParameters, function(responseJSON) {
		responseObject = JSON.parse(responseJSON);
        var userStat = responseObject.user;
        var averageStat = responseObject.average;
        var bestStat = responseObject.best;

        if (userStat === "n/a") {
        	var data = [
	        	{name: "Average",	value: averageStat},
	        	{name: "Best",		value: bestStat}
	        ];
        } else {
        	var data = [
	        	{name: user,		value: userStat},
	        	{name: "Average",	value: averageStat},
	        	{name: "Best",		value: bestStat}
        	];
        }

        drawStats("#completionChart", data);
	});
});

// draws stats for a particular metric. does not draw stats if viewer hasn't done a challenge.
function drawStats(id, data) {
	if (data[0].value === "n/a") {
		if (id === "#aggregateChart") {
			document.getElementById("aggregate").innerHTML = "<div class='row'> \
          <div class='col-xs-6 col-md-12'>You cannot see statistics for a challenge you haven't completed. \
          </div></div>";
		} else if (id === "#efficiencyChart") {
			document.getElementById("efficiency").innerHTML = "<div class='row'> \
          <div class='col-xs-6 col-md-12'>You cannot see statistics for a challenge you haven't completed. \
          </div></div>";
		} else if (id === "#brevityChart") {
			document.getElementById("brevity").innerHTML = "<div class='row'> \
          <div class='col-xs-6 col-md-12'>You cannot see statistics for a challenge you haven't completed. \
          </div></div>";
		} else if (id === "#completionChart") {
			document.getElementById("completion").innerHTML = "<div class='row'> \
          <div class='col-xs-6 col-md-12'>You cannot see statistics for a challenge you haven't completed. \
          </div></div>";
		}
		
	} else {
		var margin = {top: 20, right: 30, bottom: 30, left: 40},
		width = 700 - margin.left - margin.right,
		height = 400 - margin.top - margin.bottom;

		var x = d3.scale.ordinal()
		    .rangeRoundBands([0, width], .1);

		var y = d3.scale.linear()
		    .range([height, 0]);

		var xAxis = d3.svg.axis()
		    .scale(x)
		    .orient("bottom");

		var yAxis = d3.svg.axis()
		    .scale(y)
		    .orient("left");

		var chart = d3.select(id)
		    .attr("width", width + margin.left + margin.right)
		    .attr("height", height + margin.top + margin.bottom)
		  .append("g")
		    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

		x.domain(data.map(function(d) { return d.name; }));
		y.domain([0, d3.max(data, function(d) { return d.value; })]);

		  chart.append("g")
		      .attr("class", "x axis")
		      .attr("transform", "translate(0," + height + ")")
		      .call(xAxis);

		  chart.append("g")
		      .attr("class", "y axis")
		      .call(yAxis);

		  chart.selectAll(".bar")
		      .data(data)
		    .enter().append("rect")
		      .attr("class", "bar")
		      .attr("x", function(d) { return x(d.name); })
		      .attr("y", function(d) { return y(d.value); })
		      .attr("height", function(d) { return height - y(d.value); })
		      .attr("width", x.rangeBand());

		function type(d) {
		  d.value = +d.value; // coerce to number
		  return d;
		}

		var text = chart.append("g")
						    .attr("class", "y axis")
						    .call(yAxis)
					    .append("text")
						    .attr("transform", "rotate(-90)")
						    .attr("y", 6)
						    .attr("dy", ".71em")
						    .style("text-anchor", "end");

		if (id === "#aggregateChart") {
			text.text("Aggregate (Points)");
		} else if (id === "#efficiencyChart") {
			text.text("Efficiency (Seconds)");
		} else if (id === "#brevityChart") {
			text.text("Brevity (Lines)");
		} else if (id === "#completionChart") {
			text.text("Completion Time (Seconds)");
		}

		var yAxis = d3.svg.axis()
		    .scale(y)
		    .orient("left")
		    .ticks(10, "%");  
	}
}