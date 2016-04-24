<#assign content>
<div class="container">
  <ul class="nav nav-tabs">
    <h3>New Challenge</h3>
    <li class="active"><a data-toggle="tab" href="#basic">Description</a></li>
    <li><a data-toggle="tab" href="#java">Java</a></li>
    <li><a data-toggle="tab" href="#python">Python</a></li>
    <li><a data-toggle="tab" href="#ruby">Ruby</a></li>
    <li><a data-toggle="tab" href="#javascript">Javascript</a></li>
    <li><a data-toggle="tab" href="#submit">Submit</a></li>
  </ul>

  <div class="tab-content">
    <div id="basic" class="tab-pane fade in active">
      <div class="row">
        <div class="col-xs-12 col-md-6">
          <h4>Challenge Category</h4>
          <div class="dropdown">
            <select class="form-control" id="challengeSelect">
              <option>Trees</option>
              <option>Lists</option>
              <option>Sorting</option>
              <option>Add a new category</option>
            </select>
          </div>
          <div class="col-xs-6 col-md-6"></div>
        </div>
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
        <div class="col-xs-6 col-md-12">
          <h4>Challenge Directory Name</h4>
        </div>
      </div>

      <div class="row">
        <div class="col-xs-12 col-md-6">
          <input type="text" id="pName" class="form-control" placeholder="Challenge Directory Name" aria-describedby="basic-addon1">
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
          <input type="text" id="name" class="form-control" placeholder="Challenge Name" aria-describedby="basic-addon1">
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
          <textarea class="form-control" id="description" rows="4"></textarea>
        </div>
        <div class="col-xs-6 col-md-6" id="descriptionError"></div>
      </div>
    </div>

    <div id="python" class="tab-pane fade">
      <div class="row">
        <div class="col-xs-4 col-md-4">
            <h4>Test Name (one per line):</h4>
            <textarea class="form-control" id="pythonTestName" rows="4"></textarea>
        </div>
        <div class="col-xs-4 col-md-4">
            <h4>Test Input (one per line):</h4>
            <textarea class="form-control" id="pythonInput" rows="4"></textarea>
        </div>
        <div class="col-xs-4 col-md-4">
            <h4>Test Output (one per line):</h4>
            <textarea class="form-control" id="pythonOutput" rows="4"></textarea>
        </div>
      </div>

      <div class="row">
        <div class="col-md-12">
          <h4>Stub Code (in Python):</h4>
          <textarea class="form-control" id="pythonStub"></textarea>
        </div>
      </div>  
    </div>

    <div id="ruby" class="tab-pane fade">
      <div class="row">
        <div class="col-xs-4 col-md-4">
            <h4>Test Name (one per line):</h4>
            <textarea class="form-control" id="rubyTestName" rows="4"></textarea>
        </div>
        <div class="col-xs-4 col-md-4">
            <h4>Test Input (one per line):</h4>
            <textarea class="form-control" id="rubyInput" rows="4"></textarea>
        </div>
        <div class="col-xs-4 col-md-4">
            <h4>Test Output (one per line):</h4>
            <textarea class="form-control" id="rubyOutput" rows="4"></textarea>
        </div>
      </div>

      <div class="row">
        <div class="col-md-12">
          <h4>Stub Code (in Ruby):</h4>
          <textarea class="form-control" id="rubyStub"></textarea>
        </div>
      </div>  
    </div>

    <div id="javascript" class="tab-pane fade">
      <div class="row">
        <div class="col-xs-4 col-md-4">
            <h4>Test Name (one per line):</h4>
            <textarea class="form-control" id="jsTestName" rows="4"></textarea>
        </div>
        <div class="col-xs-4 col-md-4">
            <h4>Test Input (one per line):</h4>
            <textarea class="form-control" id="jsInput" rows="4"></textarea>
        </div>
        <div class="col-xs-4 col-md-4">
            <h4>Test Output (one per line):</h4>
            <textarea class="form-control" id="jsOutput" rows="4"></textarea>
        </div>
      </div>

      <div class="row">
        <div class="col-md-12">
          <h4>Stub Code (in JavaScript):</h4>
          <textarea class="form-control" id="jsStub"></textarea>
        </div>
      </div>  
    </div>

    <div id="java" class="tab-pane fade">
      <div class="row">
        <div class="col-xs-4 col-md-4">
            <h4>Test Name (one per line):</h4>
            <textarea class="form-control" id="javaTestName" rows="4"></textarea>
        </div>
        <div class="col-xs-4 col-md-4">
            <h4>Test Input (one per line):</h4>
            <textarea class="form-control" id="javaInput" rows="4"></textarea>
        </div>
        <div class="col-xs-4 col-md-4">
            <h4>Test Output (one per line):</h4>
            <textarea class="form-control" id="javaOutput" rows="4"></textarea>
        </div>
      </div>

      <div class="row">
        <div class="col-md-12">
          <h4>Stub Code (in Java):</h4>
          <textarea class="form-control" id="javaStub"></textarea>
        </div>
      </div>  
    </div>

    <div id="submit" class="tab-pane fade">
      <div class="row">
        <div class="col-xs-2 col-md-2"></div>
        <div class="col-xs-8 col-md-8">
          <h4>Are you sure you want to submit this challenge?</h4>
          <button type="button" class="btn btn-primary" id="submit">Submit Challenge</button>
        </div>
        <div class="col-xs-2 col-md-2"></div>
      </div>

      <div class="row">
        <div class="col-xs-2 col-md-2"></div>
        <div class="col-xs-2 col-md-8" id="submitError"></div>
        <div class="col-xs-2 col-md-2"></div>
      </div>
    </div>
  </div>
</div>

</#assign>
<#include "main.ftl">