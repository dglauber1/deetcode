<#assign content>
  <h1>Kill the bugs!</h1>
  <p style="padding-left: 20px; width:700px">
	${prompt} 
  </p>
  <br/>
  <div>
	  <div id="CountDownTimer" data-timer="120" style="width: 300px; height: 150px; float: right"></div> 
      <textarea id="userInput" cols="30" placeholder="function_name, [args, go, here]"></textarea>
  </div>
  <div style="width: 650px; height: 300px">
	<textarea id="codepad" style="float:left">
	</textarea>
  </div>
</#assign>
<#include "game-main.ftl">
