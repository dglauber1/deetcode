<#assign content>
  <#if name??>
    <div class="container" id="header">
      <div class="row">
        <div class="col-xs-12 col-md-12"><h3>${name?capitalize} Leaderboard</h3></div>
      </div>
      
      <div class="row">
        <div class="col-xs-12 col-md-10">Sort by:</div>
        <div class="col-xs-12 col-md-2">Language:</div>
      </div>
      <div class="row">
        <div class="col-xs-12 col-md-10">
          <div class="btn-group" data-toggle="buttons" id="type-buttons">
            <label class="btn btn-info active leaderboard-type">
              <input type="radio" name="options" value="aggregate" class="choice" id="aggregate" checked>Aggregate Score
            </label>  
            <label class="btn btn-info leaderboard-type">
              <input type="radio" name="options" value="efficiency" class="choice" id="efficiency" checked>Code Efficiency
            </label>  
            <label class="btn btn-info leaderboard-type">
              <input type="radio" name="options" value="brevity" class="choice" id="brevity" checked>Code Brevity
            </label>  
            <label class="btn btn-info leaderboard-type">
              <input type="radio" name="options" value="speed" class="choice" id="speed" checked>Completion Speed
            </label>  
          </div>
        </div>

        <div class="col-xs-12 col-md-2">
          <div class="dropdown">
            <select class="form-control" id="language">
              <#list 0..languages?size-1 as i>
                <#if i == 0>
                  <option value="${languages[i]?cap_first}" selected>${languages[i]?cap_first}</option>
                <#else>
                  <option value="${languages[i]?cap_first}">${languages[i]?cap_first}</option>
                </#if>
              </#list>
            </select>
          </div>
        </div>
      </div>
    </div>

    <div class="container">
      <table class="table table-striped" id="board">
        <thead>
          <tr>
            <th class="col-md-5">Username</th>
            <th class="col-md-4">Language</th>
            <th class="col-md-2">Score</th>
            <th class="col-md-1">Solution</th>
          </tr>
        </thead>
        <tbody>

        </tbody>
      </table>
    </div>
  <#else>
    <div class="row">
      <div class="col-xs-12 col-md-12">No such challenge exists.</div>
    </div>
  </#if>
  <script>
    <#include "/js/leaderboard.js">
  </script>
</#assign>
<#include "main.ftl">