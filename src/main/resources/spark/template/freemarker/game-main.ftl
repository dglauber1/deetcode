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
  </head>
  
  <body>
  	<div id="wrapper" class="toggled">
        <!-- Sidebar -->
        <div id="sidebar-wrapper">
            <ul class="sidebar-nav">
                <li class="sidebar-brand">
                    <a href="#">
                        Codegolf
                    </a>
                </li>
                <li>
                    <a href="#">Homepage</a>
                </li>
                <li>
                    <a href="#">Categories</a>
                </li>
                <li>
                    <a href="#">Your Profile</a>
                </li>
                <li>
                    <a href="#">About</a>
                </li>
            </ul>
        </div>
        <!-- /#sidebar-wrapper -->
	
        <!-- Page Content -->
        <div id="page-content-wrapper">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-lg-12">
                        <a href="#menu-toggle" class="btn btn-default" id="menu-toggle">Menu</a> 
                        <input type="submit" id="run-button" class="btn btn-default" value="Run code">	 	                           
                    </div>
                    <div class="col-lg-12">
                         ${content}
                    </div>
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
  	 
     <!-- Menu Toggle Script -->
     <script>
	     $("#menu-toggle").click(function(e) {
	         e.preventDefault();
	         $("#wrapper").toggleClass("toggled");
	     });
     </script>
     
     <!-- Load stub into the code editor -->
     <script>
     	<!-- Combine with the one below -->
     </script>
     
     <!-- Load User's progress into code editor -->
     <script>
     </script>
	
     
   	 <script src="/js/main.js"></script>
     <script src="/js/timer.js"></script>
     <script src="/js/codepad.js"></script>
  </body>
</html>
