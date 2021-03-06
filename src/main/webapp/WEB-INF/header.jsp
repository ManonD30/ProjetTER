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
    <title>YAM++</title>
  </head>
  <body role="document" style="background-color:#f2f7f9">
    <header>
      <% String username = (String) request.getSession().getAttribute("username");
        String signLabel = "Sign in/up";
        if (username != null) {
          signLabel = username;
        }
      %>
      <div class=nav>
        <ul id=nav-box class="main-nav">
          <li><a href=index>
              <img class=navLogo alt="Home" src="images/yam_top.png">
            </a></li>
          <li><a href=matcher>Matcher</a></li>
          <li><a href=validator>Validator</a></li>
          <li><a href=sameAsValidator>SameAs Validator</a></li>
          <li><a href=documentation>API</a></li>
          <li><a href=aboutus>About us</a></li>
          <li><a href=sign><%=signLabel%></a></li>
        </ul>
      </div>
    </header>

    <!--div class=yellow></div-->

    <!-- From Marie Validation UI -->
    <!--header>
    <nav>
      <ul class="main-nav">
        <li><a href="#"><img alt="Home" src="images/yam_top.png"></a></li>
        <li><a href="#">Matcher</a></li>
        <li><a href="#">Validator</a></li>
        <li><a href="#">API</a></li>
        <li><a href="#">About us</a></li>
      </ul>
    </nav>
  </header-->