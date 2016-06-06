<!DOCTYPE html>
<html>
<head>
<meta charset='utf-8' />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
<link rel="stylesheet" href="style.css" />
<script type="text/javascript" src="javascript.js"></script>
<script type="text/javascript" src="account.js"></script>
<script type="text/javascript" src="cookies.js"></script>
<title>YAM++</title>
</head>
<body>
	<div class=header>
		<div class=nav>
			<ul id=nav-box>
				<li><a href=index><img class=navLogo alt="Home"
						src="images/yam_top.png"></a></li>
				<li><a href=matcher>Matcher</a></li>
				<li><a href=validator>Validator</a></li>
				<li><a href=evaluator>Evaluator</a></li>
				<li><a href=aboutus>About us</a></li>
				<%
					String name = (String) request.getSession().getAttribute("name");
					if(name==null){
						out.println("<li><a href=http://193.49.107.124/matcher.rest.sw/sign>Sign in/up</a></li>");
					} else {
						out.println("<li><a href=http://193.49.107.124/matcher.rest.sw/sign>"+name+"</a></li>");
					}
				%>
			</ul>
		</div>
	</div>
	
	<div class=yellow></div>