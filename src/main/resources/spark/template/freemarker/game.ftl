<#assign content>
<div>
  <div class="row">
    <div class="col-lg-8 col-lg-offset-2">
      <h2 style="text-align: center">Implement [${questionName}] to kill the bugs!</h2>   
    </div>
    <div class="col-lg-2">
      <input type="checkbox" class="pull-right" id="spider-toggle" checked data-toggle="toggle" data-style="spider" data-on="Spiders On" data-off="Spiders Off">
    </div>
  </div>

  <div class="row">
    <div class="col-lg-12">
      <p id="promptContent" style="display: none">
        Description: ${prompt} <br/>
      </p>
    </div>
  </div>

  <div class="row">
    <div class="col-lg-8 col-md-8">
      Code editor: <br/>
      <textarea id="codepad"></textarea>
    </div>
    <div class="col-lg-4 col-md-4 right-side">
      <div class="row">
        <div class="col-lg-12">
          <div id="timers" style="display: none">
            <div id="CountDownTimer" data-timer="${time}" style="height: 150px; float: right">
                <h4 id="timeInfo">Time Left</h4>
            </div> 
            <div id="CountUpTimer" style="height: 150px; display: none">
                <h4 id="timeInfo">Time Elapsed</h4>
            </div> 
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-lg-12" style="margin-top: 66px">
          Write your test cases here: <br/>
          <textarea id="userInput" style="width: 100%; height: 75px" placeholder="function_name, [args, go, here]"></textarea>
        </div>
      </div>
    </div>
  </div>

  <div class="row">
    <div class="col-lg-12">
      <input type="submit" id="run-button" class="btn btn-success" value="Run Your Code!"> 
      <span id="indicator" class="btn text-warning" style="padding-left: 5px">This is your first attempt</span>
    </div>  
  </div>

</div>
</#assign>
<#include "game-main.ftl">
