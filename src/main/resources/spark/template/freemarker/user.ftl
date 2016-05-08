<#assign content>

<#if name??>
  <div class="container">
    <ul class="nav nav-tabs">
      <h3>${title}'s Profile</h3>
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
                    <th>Stats</th>
                    <th>Solution</th>
                  </tr>
                </thead>
                <tbody>
                  <#list results as result>
                    <tr>
                      <td><a href=${"/leaderboard/"+ result[1]}>${result[0]}</a></td>
                      <#if result[2] == "true">
                        <td>Yes</td>
                      <#else>
                        <td>No</td>
                      </#if>
                      <td>${result[3]}</td>
                      <td>${result[4]?capitalize}</td>
                      <td><button type="button" class="btn btn-primary stats">Stats</button></td>
                      <#if result[5] == "n/a">
                        <td></td>
                      <#else>
                        <td><button type="button" class="btn btn-primary" value="${result[5]}" onclick="popup(this)">Solution</button></td>
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
<#else>
  <div class="row">
    <div class="col-xs-12 col-md-12">No such user exists.</div>
  </div>
</#if>

</#assign>
<#include "main.ftl">