<!DOCTYPE html>
  <head>
    <meta charset="utf-8">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- In real-world webapps, css is usually minified and
         concatenated. Here, separate normalize from our code, and
         avoid minification for clarity. -->
    <link rel="stylesheet" href="/css/normalize.css">
    <link rel="stylesheet" href="/css/html5bp.css">
    <link href="/css/bootstrap.min.css" rel="stylesheet">
	 <link href="/css/simple-sidebar.css" rel="stylesheet">       
    <link rel="stylesheet" href="/css/TimeCircles.css" />
	 <link rel="stylesheet" href="/codemirror/lib/codemirror.css">
    <link rel="stylesheet" href="/vex-2.2.1/css/vex.css"/>
  	<link rel="stylesheet" href="/vex-2.2.1/css/vex-theme-os.css"/>
  	<link rel="stylesheet" href="/css/main.css">
    <link rel="stylesheet" href="/css/game.css">
    <link href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css" rel="stylesheet">
  </head>
  
  <body>
  	<div id="wrapper" class="toggled">
        <!-- Sidebar -->
        <nav class="navbar navbar-default navbar-static-top" id="main-nav">
          <div class="container">
            <a class="navbar-brand" href="/categories">DeetCode</a>
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
                  <div class="col-lg-1"></div>
                  <div class="col-lg-10">
                     ${content}
                  </div>
                  <div class="col-lg-1"></div>
                </div>
            </div>
        </div>
        <!-- /#page-content-wrapper -->

    </div>
    <!-- /#wrapper -->
     
     <!-- Again, we're serving up the unminified source for clarity. -->
     <script src="/js/jquery-2.1.1.js"></script>
     <script type="text/javascript" src="/js/TimeCircles.js"></script>
     <script src="/codemirror/lib/codemirror.js"></script>
   	 <script src="/codemirror/mode/javascript/javascript.js"></script>
   	 <script src="/vex-2.2.1/js/vex.combined.min.js"></script>
  	 <script>vex.defaultOptions.className = 'vex-theme-os';</script>
     <script src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>

     
     <!-- Load stub into the code editor -->
     <script>
     	<!-- Combine with the one below -->
     </script>
     
     <!-- Load User's progress into code editor -->
     <script>
     </script>
	
     
   	 <script src="/js/main.js"></script>
<!--      <script src="/js/timer.js"></script>
 -->     <script src="/js/Bug/bug.js"></script>
     <script src="/js/codepad.js"></script>
  </body>
</html>
