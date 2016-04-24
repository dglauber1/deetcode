<!DOCTYPE html>
<html>
  <head lang="en">
    <meta charset="UTF-8">
    <title>${title}</title>
    <link rel="stylesheet" type="text/css" href="/css/categories.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.10.0/css/bootstrap-select.min.css">
  </head>
  <body>

    <nav class="navbar navbar-default navbar-static-top" id="main-nav">
      <div class="container">
        <a class="navbar-brand" href="#">Code Golf</a>
        <div id="navbar" class="navbar-collapse">
          <ul class="nav navbar-nav header-right-navbar" id="links">
            <li class="active"><a href="#">Home</a></li>
            <li><a href="#">Account</a></li>
            <li><a href="#">Logout</a></li>
          </ul>
        </div>
      </div>
    </nav>

    <div class="container-fluid">

      <div class="row">
        <div class="col-md-10 col-md-offset-1">
          <div class="page-header" id="title">
            <h2>
              Browse By Category
              <div class="dropdown pull-right">
                <select class="selectpicker" data-width="150px" id="filters" title="Filter Options">
                  <option>Solved</option>
                  <option>Unsolved</option>
                  <option>No Filter</option>
                </select>
              </div>
            </h2>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col-md-10 col-md-offset-1" data-toggle="collapse">
          <div class="list-group categories">
            <a href="#item-1" class="list-group-item" data-toggle="collapse">
              <i class="glyphicon glyphicon-chevron-right icon-addon"></i>Item 1
            </a>
            <div class="list-group collapse questions" id="item-1">
              <a href="#" class="list-group-item" data-toggle="collapse">
                <span class="pull-right">
                  <button class="leaderboard-button">Leaderboard</button>
                </span>
                Sample Solved Problem
              </a>
              <a href="#" class="list-group-item" data-toggle="collapse">Problem 3</a>
              <a href="#" class="list-group-item" data-toggle="collapse">
                <span class="pull-right">
                  <button class="leaderboard-button">Leaderboard</button>
                </span>
                Another Solved Problem
              </a>
              <a href="#" class="list-group-item" data-toggle="collapse">Problem 4</a>
              <a href="#" class="list-group-item" data-toggle="collapse">Problem 5</a>
            </div>
            <a href="#item-2" class="list-group-item" data-toggle="collapse">
              <i class="glyphicon glyphicon-chevron-right icon-addon"></i>Item 1
            </a>
            <div class="list-group collapse questions" id="item-2">
              <a href="#" class="list-group-item" data-toggle="collapse">Blah</a>
              <a href="#" class="list-group-item" data-toggle="collapse">Blah</a>
              <a href="#" class="list-group-item" data-toggle="collapse">Blah</a>
              <a href="#" class="list-group-item" data-toggle="collapse">Blah</a>
              <a href="#" class="list-group-item" data-toggle="collapse">Blah</a>
              <a href="#" class="list-group-item" data-toggle="collapse">Blah</a>
              <a href="#" class="list-group-item" data-toggle="collapse">Blah</a>
            </div>
            <a href="#item-3" class="list-group-item" data-toggle="collapse">
              <i class="glyphicon glyphicon-chevron-right icon-addon"></i>Item 1
            </a>
            <div class="list-group collapse questions" id="item-3">
              <a href="#" class="list-group-item" data-toggle="collapse">Blah</a>
              <a href="#" class="list-group-item" data-toggle="collapse">Blah</a>
              <a href="#" class="list-group-item" data-toggle="collapse">Blah</a>
              <a href="#" class="list-group-item" data-toggle="collapse">Blah</a>
              <a href="#" class="list-group-item" data-toggle="collapse">Blah</a>
              <a href="#" class="list-group-item" data-toggle="collapse">Blah</a>
              <a href="#" class="list-group-item" data-toggle="collapse">Blah</a>
            </div>
          </div>
        </div>
      </div>
    </div>
   
    <script src="https://code.jquery.com/jquery-2.2.3.min.js" integrity="sha256-a23g1Nt4dtEYOj7bR+vTu7+T8VP13humZFBJNIYoEJo=" crossorigin="anonymous"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js" integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.10.0/js/bootstrap-select.min.js"></script>
    <script src="/js/categories.js"></script>
  </body>
</html>


