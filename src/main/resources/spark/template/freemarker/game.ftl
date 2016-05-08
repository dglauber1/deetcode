<#assign content>
<div>
  <h1>Kill the bugs!</h1>
  <p id="promptContent" style="padding-left: 20px; width:700px; display: none">
    ${prompt} <br/>
    <a id="indicator">This is your first attempt</a>
  </p>
  <br/>
  <div>
	  <div id="CountDownTimer" data-timer="10" style="width: 300px; height: 150px; float: right"></div> 
      <textarea id="userInput" cols="30" placeholder="function_name, [args, go, here]"></textarea>
  </div>
  <div style="width: 650px; height: 300px">
	<textarea id="codepad" style="float:left">
	</textarea>
  </div>
</div>
</#assign>
<#include "game-main.ftl">
