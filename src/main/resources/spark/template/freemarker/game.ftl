<#assign content>
  <h1>Defuse the bomb!</h1>
  <p style="padding-left: 20px; width:700px">
	${prompt} 
  </p>
  <br/>
    <div id="CountDownTimer" data-timer="120" style="width: 300px; height: 150px; float: right"></div> 
  <div style="width: 650px; height: 300px">
	<textarea id="codepad" style="float:left" ></textarea>
  </div>
</#assign>
<#include "game-main.ftl">
