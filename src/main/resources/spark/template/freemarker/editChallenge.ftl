<#assign content>
<#if info??>
  <div class="container">
    <ul class="nav nav-tabs">
      <h3>Edit Challenge</h3>
    <li class="descTab active" disabled><a href="#basic">Description</a></li>
    <#-- <li class="langTab disabled"><a href="#java" >Java</a></li> -->
    <li class="langTab disabled"><a href="#python">Python</a></li>
    <#-- <li class="langTab disabled"><a href="#ruby">Ruby</a></li> -->
    <li class="langTab disabled"><a href="#javascript">Javascript</a></li>
    <li class="submitTab disabled"><a href="#submitChallenge">Submit</a></li>
    </ul>

    <div class="tab-content">
      <div id="basic" class="tab-pane fade in active">
        <div class="row">
          <div class="col-xs-12 col-md-12">
            <h4>Challenge Category</h4>
          </div>
        </div>

        <div class="row">
          <div class="col-xs-12 col-md-6">
            <div class="dropdown">
              <select class="form-control" id="challengeSelect">
                <option value="Pick a Category">Pick a Category</option>
                <#list categories as category>
                  <#if category == info[0][0]>
                    <option value="${category}" selected>${category}</option>
                  <#else>
                    <option value="${category}">${category}</option>   
                  </#if>  
                </#list>
                <option value="Add a new category">Add a new category</option>
              </select>
            </div>
          </div>
          <div class="col-xs-6 col-md-6" id="categoryError"></div>
        </div>

        <div class="row" id="newCategoryDivTitle">
          <div class="col-xs-6 col-md-12">
            <h4>New Category Name</h4>
          </div>
        </div>

        <div class="row" id="newCategoryDiv">
          <div class="col-xs-12 col-md-6">
            <input type="text" id="newCategory" class="form-control" placeholder="New Challenge Category" aria-describedby="basic-addon1">
          </div>
          <div class="col-xs-6 col-md-6" id="newCategoryError"></div>
        </div>

        <div class="row">
          <div class="col-xs-12 col-md-12">
            <h4>Challenge Difficulty</h4>
          </div>
        </div>

        <div class="row">
          <div class="col-xs-6 col-md-6">
            <div class="dropdown">
              <select class="form-control" id="difficultyLevel">
                <option value="unpicked">Pick a Difficulty Level</option>
                <#if info[0][3] == "easy">
                  <option value="easy" selected>Easy</option>
                <#else>
                  <option value="easy">Easy</option>
                </#if>
                
                <#if info[0][3] == "medium">
                  <option value="medium" selected>Medium</option>
                <#else>
                  <option value="medium">Medium</option>
                </#if>

                <#if info[0][3] == "hard">
                  <option value="hard" selected>Hard</option>
                <#else>
                  <option value="hard">Hard</option>
                </#if>
              </select>
            </div>
          </div>
          <div class="col-xs-6 col-md-6" id="difficultyError"></div>
        </div>

        <div class="row">
          <div class="col-xs-6 col-md-12">
            <h4>Challenge Directory Name</h4>
          </div>
        </div>

        <div class="row">
          <div class="col-xs-12 col-md-6">
            <input type="text" id="pName" class="form-control" placeholder="Challenge Directory Name" aria-describedby="basic-addon1" value="${info[0][1]}"">
          </div>
          <div class="col-xs-6 col-md-6" id="pNameError"></div>
        </div>

        <div class="row">
          <div class="col-xs-6 col-md-12">
            <h4>Challenge Name</h4>
          </div>
        </div>

        <div class="row">
          <div class="col-xs-12 col-md-6">
            <input type="text" id="name" class="form-control" placeholder="Challenge Name" aria-describedby="basic-addon1" value="${info[0][2]}">
          </div>
          <div class="col-xs-6 col-md-6" id="nameError"></div>
        </div>

        <div class="row">
          <div class="col-xs-6 col-md-12">
            <h4>Challenge Description</h4>
          </div>
        </div>

        <div class="row">
          <div class="col-xs-12 col-md-6">
            <textarea class="form-control" id="description" rows="4">${info[0][4]}</textarea>
          </div>
          <div class="col-xs-6 col-md-6" id="descriptionError"></div>
        </div>

        <a class="btn btn-default btnNext">Next</a>
      </div>

      <div id="python" class="tab-pane fade">
        <div class="row">
          <div class="col-xs-4 col-md-4">
              <h4>Test Name (one per line):</h4>
              <textarea class="form-control" id="pythonTestName" rows="4">${info[2][0]}</textarea>
          </div>
          <div class="col-xs-4 col-md-4">
              <h4>Test Input (one per line):</h4>
              <textarea class="form-control" id="pythonInput" rows="4">${info[2][1]}</textarea>
          </div>
          <div class="col-xs-4 col-md-4">
              <h4>Test Output (one per line):</h4>
              <textarea class="form-control" id="pythonOutput" rows="4">${info[2][2]}</textarea>
          </div>

          <div class="row">
            <div class="col-xs-6 col-md-12" id="pythonError"></div>
          </div>
        </div>

        <div class="row">
          <div class="col-md-12">
            <h4>Stub Code (in Python):</h4>
            <textarea class="form-control" id="pythonStub">${info[2][3]}</textarea>
          </div>
        </div> 

        <a class="btn btn-default btnPrevious">Back</a>
        <a class="btn btn-default btnNext">Next</a>  
      </div>

      <div id="ruby" class="tab-pane fade">
        <div class="row">
          <div class="col-xs-4 col-md-4">
              <h4>Test Name (one per line):</h4>
              <textarea class="form-control" id="rubyTestName" rows="4">${info[3][0]}</textarea>
          </div>
          <div class="col-xs-4 col-md-4">
              <h4>Test Input (one per line):</h4>
              <textarea class="form-control" id="rubyInput" rows="4">${info[3][1]}</textarea>
          </div>
          <div class="col-xs-4 col-md-4">
              <h4>Test Output (one per line):</h4>
              <textarea class="form-control" id="rubyOutput" rows="4">${info[3][2]}</textarea>
          </div>
        </div>

        <div class="row">
            <div class="col-xs-6 col-md-12" id="rubyError"></div>
        </div>

        <div class="row">
          <div class="col-md-12">
            <h4>Stub Code (in Ruby):</h4>
            <textarea class="form-control" id="rubyStub">${info[3][3]}</textarea>
          </div>
        </div> 

        <a class="btn btn-default btnPrevious">Back</a>
        <a class="btn btn-default btnNext">Next</a>  
      </div>

      <div id="javascript" class="tab-pane fade">
        <div class="row">
          <div class="col-xs-4 col-md-4">
              <h4>Test Name (one per line):</h4>
              <textarea class="form-control" id="jsTestName" rows="4">${info[4][0]}</textarea>
          </div>
          <div class="col-xs-4 col-md-4">
              <h4>Test Input (one per line):</h4>
              <textarea class="form-control" id="jsInput" rows="4">${info[4][1]}</textarea>
          </div>
          <div class="col-xs-4 col-md-4">
              <h4>Test Output (one per line):</h4>
              <textarea class="form-control" id="jsOutput" rows="4">${info[4][2]}</textarea>
          </div>
        </div>

        <div class="row">
          <div class="col-xs-6 col-md-12" id="jsError"></div>
        </div>

        <div class="row">
          <div class="col-md-12">
            <h4>Stub Code (in JavaScript):</h4>
            <textarea class="form-control" id="jsStub">${info[4][3]}</textarea>
          </div>
        </div>  

        <a class="btn btn-default btnPrevious">Back</a>
        <a class="btn btn-default btnNext">Next</a> 
      </div>

      <div id="java" class="tab-pane fade">
        <div class="row">
          <div class="col-xs-4 col-md-4">
              <h4>Test Name (one per line):</h4>
              <textarea class="form-control" id="javaTestName" rows="4">${info[1][0]}</textarea>
          </div>
          <div class="col-xs-4 col-md-4">
              <h4>Test Input (one per line):</h4>
              <textarea class="form-control" id="javaInput" rows="4">${info[1][1]}</textarea>
          </div>
          <div class="col-xs-4 col-md-4">
              <h4>Test Output (one per line):</h4>
              <textarea class="form-control" id="javaOutput" rows="4">${info[1][2]}</textarea>
          </div>
        </div>

        <div class="row">
          <div class="col-xs-6 col-md-12" id="javaError"></div>
        </div>

        <div class="row">
          <div class="col-md-12">
            <h4>Stub Code (in Java):</h4>
            <textarea class="form-control" id="javaStub">${info[1][3]}</textarea>
          </div>
        </div>  

        <a class="btn btn-default btnPrevious">Back</a>
        <a class="btn btn-default btnNext">Next</a> 
      </div>

      <div id="submitChallenge" class="tab-pane fade">
        <div class="row">
          <div class="col-xs-8 col-md-12">
            <h4>Are you sure you want to edit this challenge?</h4>
            <a class="btn btn-default btnPrevious">Back</a>
            <a class="btn btn-default" id="editSubmit">Edit Challenge</a>
          </div>
          <div class="col-xs-2 col-md-2"></div>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col-xs-2 col-md-12" id="submitError"></div>
    </div>

  </div>
<#else>
  <div class="row">
    <div class="col-xs-12 col-md-12">No such challenge exists.</div>
  </div>
</#if>

</#assign>
<#include "main.ftl">