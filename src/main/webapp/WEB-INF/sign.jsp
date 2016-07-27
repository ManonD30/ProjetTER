<%@include file="header.jsp" %>

<%
  String canMatch = (String) request.getSession().getAttribute(
          "canMatch");
  if (name != null) {
    out.println("<br><h2>Connected as " + name + ".</h2>");
    out.println("<p class=contentCenter>You can do " + canMatch
            + " matching each day.</p>");
    out.println("<form action='changePassword'"
            + "method='get'name=modify enctype='multipart/form-data'>"
            + "<input type='submit' class=btnBig value='Change my password'>"
            + "</form>"
            + "<form action='disconnect'"
            + "method='post'name=disconnect enctype='multipart/form-data'>"
            + "<input type='submit' class=btnBig value='Disconnect'>"
            + "</form>" + "<div class=sign style='display:none'>");
  } else {
    out.print("<div class=sign>");
  }
%>
<h3 style="margin-bottom: 0">To benefit from YAM++ online, you
  have to create a (free) account.</h3>
<div class=signup>
  <h1>Sign up:</h1>
  <form action="rest/account/signup" method="post" name=signup
        enctype="multipart/form-data">
    <p>Mail:</p>
    <input type="email" name="mailUp" placeholder=joesmith@example.com
           maxlength=32 required>

    <p>Name:</p>
    <input type="text" name="nameUp" placeholder='ex: Joe Smith'
           maxlength=32 required>

    <p>Affiliation:</p>
    <input type="text" name="affiliationUp" placeholder='ex: LIRMM'
           maxlength=32 required>

    <p>Password:</p>
    <input type="password" id=password name="passwordUp"
           placeholder=******* required>

    <p>Password confirmation:</p>
    <input type="password" id=confirmation name="confirmationUp"
           placeholder=******* required
           onkeyup="checkPassword(); return false;">

    <div id=message></div>
    <br>

    <div id=submitSignup style="display: none">
      <input type="submit" class=btn value="Sign up" required>
    </div>
  </form>
</div>


<div class=signin>
  <h1>Sign in:</h1>
  <form action="sign"
        method="post" name=signin>
    <p>Mail:</p>
    <input type="email" name="mailIn" placeholder=joesmith@example.com
           maxlength=32 required>

    <p>Password:</p>
    <input type="password" name="passwordIn" placeholder=*******
           required> <br> <br> <input type="submit"
           class=btn value="Sign in">
  </form>

  <%
    //get the error message
    String error = (String) request.getAttribute("error");
    out.println("<p>" + error + "</p>");
  %>
</div>
</div>

<%@include file="footer.jsp" %>