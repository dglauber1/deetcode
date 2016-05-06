<!DOCTYPE html>
  <head>
    <meta charset="utf-8">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- In real-world webapps, css is usually minified and
         concatenated. Here, separate normalize from our code, and
         avoid minification for clarity. -->
    <link rel="stylesheet" href="/css/normalize.css">
    <link rel="stylesheet" href="/css/html5bp.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
  </head>
  <body>
    <#if name??>
      <div class="container-fluid">
        <div class="row">
          <div class="cold-md-12">
            <#include "nav.ftl">
          </div>
        </div>
        <div class="row">
          <div class="col-md-12" style="margin-left: 25px; margin-right: 25px;">
            <ul class="nav nav-tabs">
              <h3 style="text-align: center">${title}'s Profile</h3>
              <li class="active"><a data-toggle="tab" href="#about">About</a></li>
              <li><a data-toggle="tab" href="#challenges">Challenges</a></li>
            </ul>

            <div class="tab-content">
              <div id="about" class="tab-pane fade in active">
                <div class="row">
                  <div class="col-xs-6 col-md-6">
                    <h4>Username</h4>
                  </div>
                  <div class="col-xs-6 col-md-6"></div>
                </div>

                <div class="row">
                  <div class="col-xs-6 col-md-6" id="username"></div>
                </div>

                <div class="row">
                  <div class="col-xs-6 col-md-6">${title}</div>
                </div>  

                <div class="row">
                  <div class="col-xs-6 col-md-6">
                    <h4>Name</h4>
                  </div>
                  <div class="col-xs-6 col-md-6"></div>
                </div>

                <div class="row">
                  <div class="col-xs-6 col-md-6">${name}</div>
                  <div class="col-xs-6 col-md-6"></div>
                </div>

                <div class="row">
                  <div class="col-xs-6 col-md-6" id="name"></div>
                  <div class="col-xs-6 col-md-6"></div>
                </div>
              </div>


              <div id="challenges" class="tab-pane fade">
                <div class="row">
                  <div class="col-xs-6 col-md-12">
                      <table class="table table-striped">
                        <thead>
                          <tr>
                            <th>Challenge</th>
                            <th>Finished</th>
                            <th>Rank</th>
                            <th>Language</th>
                            <th>Solution</th>
                          </tr>
                        </thead>
                        <tbody>
                          <#list results as result>
                            <tr>
                              <td>${result[0]}</td>
                              <#if result[1] == "true">
                                <td>Yes</td>
                              <#else>
                                <td>No</td>
                              </#if>
                              <td>${result[2]}</td>
                              <td>${result[3]?capitalize}</td>
                              <#if result[3] != "n/a">
                                <td><button type="button" class="btn btn-primary" value="${result[4]}" onclick="popup(this)">Solution</button></td>
                              <#else>
                                <td></td>
                              </#if>    
                            </tr>
                          </#list>
                        </tbody>
                      </table>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    <#else>
      <div class="row">
        <div class="col-xs-12 col-md-12">No such user exists.</div>
      </div>
    </#if>
    <script src="/js/user.js"></script>
    <script src="https://code.jquery.com/jquery-2.2.3.min.js" integrity="sha256-a23g1Nt4dtEYOj7bR+vTu7+T8VP13humZFBJNIYoEJo=" crossorigin="anonymous"></script>
     <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js" integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS" crossorigin="anonymous"></script>
  </body>
</html>

