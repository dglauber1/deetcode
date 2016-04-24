$(function() {
        
  $('.list-group-item').on('click', function() {
    $('.glyphicon', this)
      .toggleClass('glyphicon-chevron-right')
      .toggleClass('glyphicon-chevron-down');
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

});