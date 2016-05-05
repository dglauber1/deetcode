$(function() {

  var bugs = new SpiderController();

  $("h1").click(function() {
    console.log(bugs.killAll);
    bugs.killAll();
  })
});