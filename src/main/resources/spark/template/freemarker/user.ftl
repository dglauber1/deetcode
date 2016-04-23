<#assign content>

<div class="container">
  <ul class="nav nav-tabs">
    <h3>eddyword</h3>
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
        <div class="col-xs-6 col-md-6"></div>
      </div>

      <div class="row">
        <div class="col-xs-6 col-md-6">
          <h4>Name</h4>
        </div>
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
                  <th>Solution</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>add_one</td>
                  <td>Yes</td>
                  <td>1</td>
                  <td><button type="button" class="btn btn-primary" onclick="popup('add_one')">Solution</button></td>
                </tr>
                <tr>
                  <td>reverse</td>
                  <td>Yes</td>
                  <td>2</td>
                  <td><button type="button" class="btn btn-primary" onclick="popup('reverse')">Solution</button></td>
                </tr>
              </tbody>
            </table>
        </div>
      </div>
    </div>
  </div>
</div>

</#assign>
<#include "main.ftl">