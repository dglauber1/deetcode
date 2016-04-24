$(function() {
        
  $('.list-group-item').on('click', function() {
    $('.glyphicon', this)
      .toggleClass('glyphicon-chevron-right')
      .toggleClass('glyphicon-chevron-down');
  });

  $('#usernameForm').submit(function(e) {
    e.preventDefault();
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

  $('#filters').change(function() {
    var filter = $(this).val();
    var $solved = $(".questions a.list-group-item").has(".leaderboard-button");
    var $notSolved = $(".questions a.list-group-item").not($solved);

    if (filter === "No Filter") {
      $solved.show();
      $notSolved.show();
    } else if (filter === "Solved") {
      $solved.show();
      $notSolved.hide();
    } else if (filter === "Unsolved") {
      $solved.hide();
      $notSolved.show();
    }
  });

  var modalType = window.location.hash;
  if (modalType === "#signup") {
    $('#signupModal').modal('show');
  }
});
