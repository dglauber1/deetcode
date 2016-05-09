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
        <a class="navbar-brand" href="/categories">DeetCode</a>
        <div id="navbar" class="navbar-collapse">
          <ul class="nav navbar-nav header-right-navbar" id="links">
            <li class="active"><a href="#">Home</a></li>
            <li><a href="/user/${username}">${username}</a></li>
            <li><a href="/logout">Logout</a></li>
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
              <#if !isAdmin>
                <div class="dropdown pull-right">
                  <select class="selectpicker" data-width="150px" id="status-filter" title="Filter By Status">
                    <option>Solved</option>
                    <option>Unsolved</option>
                    <option>No Filter</option>
                  </select>
                </div>
                <div id="difficulty-div" class="dropdown pull-right">
                  <select class="selectpicker" data-width="150px" id="difficulty-filter" title="Filter By Difficulty">
                    <option>Easy</option>
                    <option>Medium</option>
                    <option>Hard</option>
                    <option>No Filter</option>
                  </select>
                </div>
              <#else>
                <small style="font-size:16px">Admin Mode</small>
                <button class="pull-right btn btn-default" id="newChallengeButton">New Challenge</button>
              </#if>
            </h2>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col-md-10 col-md-offset-1" data-toggle="collapse">
          <div class="list-group categories">
            <#list data?keys as key>
              <a href="#${key}" class="list-group-item category" data-toggle="collapse">
                <i class="glyphicon glyphicon-chevron-right icon-addon"></i>${key?capitalize}
              </a>
              <div class="list-group collapse questions" id="${key}">
                <#list data[key] as challenge>
                  <a href="/game/${challenge.id}" class="list-group-item challenge">
                    <#if isAdmin>
                      <span class="pull-right">
                        <button class="edit-button">Edit</button>
                      </span>
                      <span class="pull-right">
                        <button class="delete-button">Delete</button>
                      </span>
                      <span class="pull-right">
                        <button class="leaderboard-button">Leaderboard</button>
                      </span>
                    <#elseif !isAdmin && challenge.solved == "true">
                      <span class="pull-right">
                        <button class="leaderboard-button">Leaderboard</button>
                      </span>
                      <span class="glyphicon glyphicon-ok"></span>
                    </#if>
                    ${challenge.name?capitalize}
                    <#if challenge.difficulty == "easy">
                      <div class="easy">Easy</div>
                    <#elseif challenge.difficulty == "medium">
                      <div class="medium">Medium</div>
                    <#else>
                      <div class="hard">Hard</div>
                    </#if>
                  </a>
                </#list>
              </div>
            </#list>            
          </div>
        </div>
      </div>
    </div>

    <div id="signupModal" class="modal fade" role="dialog" tabindex="-1" data-backdrop="static" data-keyboard="false">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
              <!-- <button type="button" class="close" data-dismiss="modal">&times;</button> -->
              <h4 class="modal-title text-center">Welcome to DEETCode!</h4>
          </div>
          <div class="modal-body">
            <p>Welcome to DeetCode!</p>
            <p>Before you begin, we just need to know what to call you:</p>
            <form id="usernameForm" action="#" method="POST" autocomplete="off">       
              <div class="form-group">
                <input type="text" class="form-control" name="username" placeholder="Type your username here!">
              </div>
              <div class="form-group separator" style="text-align:center">
                <button id="usernameSubmitButton" name="submit" class="btn btn-primary">Submit</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
   
    <script src="https://code.jquery.com/jquery-2.2.3.min.js" integrity="sha256-a23g1Nt4dtEYOj7bR+vTu7+T8VP13humZFBJNIYoEJo=" crossorigin="anonymous"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js" integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.10.0/js/bootstrap-select.min.js"></script>
    <script src="/js/Bug/bug.js"></script>
    <script src="/js/categories.js"></script>
  </body>
</html>


