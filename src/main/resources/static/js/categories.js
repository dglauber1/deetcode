$(function() {

  // var bugs = new SpiderController();
        
  $('.category').on('click', function() {
    $('.glyphicon', this)
      .toggleClass('glyphicon-chevron-right')
      .toggleClass('glyphicon-chevron-down');
  });

  $('#usernameForm').submit(function(e) {
    e.preventDefault();

    var username = $('input[name=username]').val();

    if (username === "") {
      alert("You must enter a username!");
      return;
    }

    var data = {
      username: $('input[name=username]').val()
    };
    $('#usernameSubmitButton').prop('disabled', true);
    $('#usernameSubmitButton').html('Adding...');
    $.ajax({
      type: 'POST',
      url: '/add-user',
      data: data,
      success: function(response) {
        var data = $.parseJSON(response);
        setTimeout(function() {
          $('#signupModal').modal('hide');
          $('#usernameSubmitButton').prop('disabled', false);
          $('#usernameSubmitButton').html('Submit');
          window.location.href = "/categories";
        }, 500);
      },
      error: function(response) {
        var error = $.parseJSON(response.responseText);
        alert(error.error);
        $('#usernameSubmitButton').prop('disabled', false);
        $('#usernameSubmitButton').html('Submit');
      }
    });
  });

  $('.edit-button').click(function(e) {
    var link = $(e.target).parent().parent()[0].href;
    var challenge_id = link.substr(link.lastIndexOf("/") + 1);
    window.location.href = "http://localhost:4567/admin/edit/" + challenge_id;
    return false;
  });

  $('.leaderboard-button').click(function(e) {
    var link = $(e.target).parent().parent()[0].href;
    var challenge_id = link.substr(link.lastIndexOf("/") + 1);
    window.location.href = "http://localhost:4567/leaderboard/" + challenge_id;
    return false;
  });

  $('.delete-button').click(function(e) {
    var link = $(e.target).parent().parent()[0].href;
    var challenge_id = link.substr(link.lastIndexOf("/") + 1);
    if (confirm("Are you sure you want to delete " + challenge_id + "?")) {
      $.ajax({
        type: 'POST',
        url: "http://localhost:4567/admin/delete/" + challenge_id,
        data: null,
        success: function(response) {
          alert("Delete successful!");
          location.reload();
        },
        error: function(response) {
          var error = $.parseJSON(response.responseText);
          console.log(error);
          alert("Unable to delete the challenge!");
        }
      });
    }
    return false;
  });

  // not sure why these links don't just click on their own...
  $('.challenge').click(function(e) {
    e.preventDefault();
    window.location.href = $(e.target).prop('href');
  });

  $("#newChallengeButton").click(function(e) {
    e.preventDefault();
    window.location.href = "http://localhost:4567/admin/add";
  });

  $('.selectpicker').change(function() {
    var status = $("#status-filter").val();
    var difficulty = $("#difficulty-filter").val();
    var difficultyClass = "." + difficulty.toLowerCase();

    var allQuestions = $(".questions a.list-group-item");
    var filteredQuestions = allQuestions;
    if (status === "Solved") {
      filteredQuestions = filteredQuestions.has(".leaderboard-button");
    } else if (status === "Unsolved") {
      filteredQuestions = filteredQuestions.not(filteredQuestions.has(".leaderboard-button"));
    }

    if (difficulty && difficulty !== "No Filter") {
      filteredQuestions = filteredQuestions.has(difficultyClass);
    }

    allQuestions.not(filteredQuestions).hide();
    filteredQuestions.show();
  });

  var modalType = window.location.hash;
  if (modalType === "#signup") {
    $('#signupModal').modal('show');
  }
});
