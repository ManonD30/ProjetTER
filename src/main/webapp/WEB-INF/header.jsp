<!DOCTYPE html>
<html>
  <head>
    <meta charset='utf-8' />
    <link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
    <!-- Bootstrap core CSS and theme -->
    <link href="css/bootstrap.css" rel="stylesheet">
    <link href="css/bootstrap-theme.css" rel="stylesheet">
    <link rel="stylesheet" href="css/style.css" />
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="scripts/bootstrap.js"></script>
    <script type="text/javascript" src="scripts/javascript.js"></script>
    <script type="text/javascript" src="scripts/account.js"></script>
    <script type="text/javascript" src="scripts/cookies.js"></script>
    <title>YAM++</title>
  </head>
  <body role="document" style="background-color:#f2f7f9">
    <header>
      <div class=nav>
        <ul id=nav-box>
          <li><a href=index>
              <img class=navLogo alt="Home" src="images/yam_top.png">
            </a></li>
          <li><a href=matcher>Matcher</a></li>
          <li><a href=validator>Validator</a></li>
          <li><a href=documentation>REST API</a></li>
          <li><a href=aboutus>About us</a></li>
            <%--  To display Sign up (commented because we don't use user for the moment)
                    String name = (String) request.getSession().getAttribute("name");
                    if(name==null){
                            out.println("<li><a href=sign>Sign in/up</a></li>");
                    } else {
                            out.println("<li><a href=sign>"+name+"</a></li>");
                    }
            --%>
        </ul>
      </div>
    </header>

    <div class=yellow></div>
    
    <!-- From Marie Validation UI -->
    <!--header>
    <nav>
      <ul class="main-nav">
        <li><a href="#"><img alt="Home" src="images/yam_top.png"></a></li>
        <li><a href="#">Matcher</a></li>
        <li><a href="#">Validator</a></li>
        <li><a href="#">REST API</a></li>
        <li><a href="#">About us</a></li>
      </ul>
    </nav>
  </header-->