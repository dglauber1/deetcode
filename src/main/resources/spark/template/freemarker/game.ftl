<#assign content>
<div>
  <h2 style="display: inline-block">Implement </h2>
  <h2 style="display: inline-block">[${questionName}]</h2> 
  <h2 style="display: inline-block">to kill the bugs!</h2>
  <p id="promptContent" style="padding-left: 20px; width:700px; display: none">
    ${prompt} <br/>
    <a id="indicator">This is your first attempt</a>
  </p>
  <br/>
  <div id="timers" style="display: none">
  	<div id="CountDownTimer" data-timer="${time}" style="width: 300px; height: 150px; float: right">
        <h4 id="timeInfo">Time Left</h4>
    </div> 
    <div id="CountUpTimer" style="width: 300px; height: 150px; float: right; display: none">
        <h4 id="timeInfo">Time Elapsed</h4>
    </div> 
  </div>
  Write your test cases here: <br/>
  <textarea id="userInput" cols="30" placeholder="function_name, [args, go, here]"></textarea>
  <div style="width: 650px; height: 300px">
    Code editor: <br/>
  	<textarea id="codepad" style="float:left"></textarea>
  </div>
</div>
</#assign>
<#include "game-main.ftl">
