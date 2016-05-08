<!DOCTYPE html>
  <head>
    <meta charset="utf-8">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- In real-world webapps, css is usually minified and
         concatenated. Here, separate normalize from our code, and
         avoid minification for clarity. -->
    <link rel="stylesheet" href="/js/codemirror-5.14.2/lib/codemirror.css">
    <link rel="stylesheet" href="/css/normalize.css">
    <link rel="stylesheet" href="/css/html5bp.css">
    <link rel="stylesheet" href="/css/bootstrap.min.css">
<!-- 	<link rel="stylesheet" href="/css/simple-sidebar.css">       
 -->    <link rel="stylesheet" href="/css/TimeCircles.css" />
    <link rel="stylesheet" href="/css/main.css">
    <link rel="stylesheet" href="/css/newChallenge.css">
    <link rel="stylesheet" href="/css/leaderboard.css">
    <link rel="stylesheet" href="/css/user.css">
    <link rel="stylesheet" type="text/css" href="http://w2ui.com/src/w2ui-1.4.2.min.css"/>
  </head>
  <body>
     <div id="wrapper">

        <!-- Sidebar -->
        <nav class="navbar navbar-default navbar-static-top" id="main-nav">
          <div class="container">
            <a class="navbar-brand" href="#">DEETCode</a>
            <div id="navbar" class="navbar-collapse">
              <ul class="nav navbar-nav header-right-navbar" id="links">
                <li><a href="/categories">Home</a></li>
                <li><a href="/user/${username}">${username}</a></li>
                <li><a href="/logout">Logout</a></li>
              </ul>
            </div>
          </div>
        </nav>
        <!-- /#sidebar-wrapper -->
	
        <!-- Page Content -->
        <div id="page-content-wrapper">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-lg-12 main-content">
                    	${content}
                    </div>
                </div>
            </div>
        </div>
        <!-- /#page-content-wrapper -->

    </div>
    <!-- /#wrapper -->
     
     <!-- Again, we're serving up the unminified source for clarity. -->
     <script src="https://code.jquery.com/jquery-2.2.3.min.js" integrity="sha256-a23g1Nt4dtEYOj7bR+vTu7+T8VP13humZFBJNIYoEJo=" crossorigin="anonymous"></script>
     <script src="https://d3js.org/d3.v3.min.js" charset="utf-8"></script>
     <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js" integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS" crossorigin="anonymous"></script>
     <!-- <script src="/js/jquery-2.1.1.js"></script> -->
     <script type="text/javascript" src="/js/TimeCircles.js"></script>
     <script type="text/javascript" src="http://w2ui.com/src/w2ui-1.4.2.min.js"></script>

     <!-- Code Mirror Scripts -->
     <script src="/js/codemirror-5.14.2/lib/codemirror.js"></script>
     <script src="/js/codemirror-5.14.2/mode/javascript/javascript.js"></script>
     <script src="/js/codemirror-5.14.2/mode/python/python.js"></script>
     <script src="/js/codemirror-5.14.2/mode/ruby/ruby.js"></script>
     <script src="/js/codemirror-5.14.2/mode/clike/clike.js"></script>
     
     <script src="/js/main.js"></script>
     <script src="/js/timer.js"></script>
     <script src="/js/newChallenge.js"></script>
     <script src="/js/leaderboard.js"></script>
     <script src="/js/user.js"></script>
  </body>
</html>
