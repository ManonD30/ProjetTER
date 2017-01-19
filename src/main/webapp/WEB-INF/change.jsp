<%@include file="header.jsp" %>

<div class="container theme-showcase" role="main">

  <h1>Change your password:</h1>
  <div class=changePassword>
    <form action="changePassword"
          method="post" name=changePassword>
      <p>Old password:</p>
      <input type="password" id=oldPassword name="oldPassword"
             placeholder=******* required>

      <p>New password:</p>
      <input type="password" id=password name="newPassword"
             placeholder=******* required>


      <p>Password confirmation:</p>
      <input type="password" id=confirmation name="confirmation"
             placeholder=******* required>

      <div id=message></div>
      <br>

      <div id=submitSignup>
        <input type="submit" class=btn value="Change Password" required>
      </div>
    </form>
  </div>

  <%
    //get the error message
    String error = (String) request.getAttribute("error");
    out.println("<p>" + error + "</p>");
  %>

</div>

<%@include file="footer.jsp" %>