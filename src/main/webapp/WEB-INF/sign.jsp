<%@page import="fr.lirmm.yamplusplus.yampponline.YamUser"%>
<%@page import="fr.lirmm.yamplusplus.yampponline.YamDatabaseConnector"%>
<%@include file="header.jsp" %>

<div class="container theme-showcase" role="main">
  <% String matchCount = "0";
    String isAffiliateTo = "null";
    String apikey = "null";
    String field = "null";
    if (request.getSession().getAttribute("apikey") != null) {
      apikey = request.getSession().getAttribute("apikey").toString();
      matchCount = request.getSession().getAttribute("matchCount").toString();
      isAffiliateTo = request.getSession().getAttribute("isAffiliateTo").toString();
      field = request.getSession().getAttribute("field").toString();
    }

    if (username != null) {%>
  <br><h2>Connected as <%=username%></h2>
  <p class=contentCenter>Apikey to authenticate yourself to use the RESTful Matcher: <b><%=apikey%></b></p>
  <p class=contentCenter>You have done <%=matchCount%> ontology matching.</p>
  <p class=contentCenter>Institut/Affiliate to  <%=isAffiliateTo%>.</p>
  <p class=contentCenter>Working field: <%=field%>.</p>
  <form action='changePassword' method='get' name=modify enctype='multipart/form-data'>
    <input type='submit' class=btnBig value='Change my password'>
  </form>
  <form action='disconnect' method='post'name=disconnect enctype='multipart/form-data'>
    <input type='submit' class=btnBig value='Disconnect'>
  </form>

  <%
    // Display admin interface if user role is admin
    if (request.getSession().getAttribute("role") != null && request.getSession().getAttribute("role").equals("admin")) {
  %>
  <hr/>
  <h2>Administration</h2>
  <p>Resetting a password reset it to "changeme"</p>
  <table class="table table-bordered">
    <thead>
      <tr>
        <th>Apikey</th>
        <th>Mail</th>
        <th>Username</th>
        <th>Role</th>
        <th>Affiliation</th>
        <th>Field</th>
        <th>Match count</th>
        <th>Reset password</th>
      </tr>
    </thead>
    <tbody>
      <%
        YamDatabaseConnector dbConnector = new YamDatabaseConnector();
        for (YamUser user : dbConnector.getUserList()) {
      %>

      <tr>
        <td><%=user.getApikey()%></td>
        <td><%=user.getMail()%></td>
        <td><%=user.getUsername()%></td>
        <td><%=user.getRole()%></td>
        <td><%=user.getIsAffiliateTo()%></td>
        <td><%=user.getField()%></td>
        <td><%=user.getMatchCount()%></td>
        <td>
          <form action="resetPassword" method='post'>
            <input type="hidden" name="resetApikey" value="<%=user.getApikey()%>" />
            <input type="submit" value="Reset" class="btn">
          </form>
        </td>
      </tr>

      <% } %>
    </tbody>
  </table>
  <% } %>

  <div class=sign style='display:none'>
    <%
      } else {
        out.print("<div class=sign>");
      }
    %>
    <h2 style="margin-top: 2%;">Create an account to use Yam++ matcher</h2>
    <div class="row">
      <div class=signup>
        <h1>Sign up:</h1>
        <!--form action="signup" method="post" name=signup enctype="multipart/form-data"-->
        <form action="signup" method="post" name=signup>
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
        <form action="sign" method="post" name=signin>
          <p>Mail:</p>
          <input type="email" name="mailIn" placeholder="joesmith@example.com" maxlength=32 required>
          <p>Password:</p>
          <input type="password" name="passwordIn" placeholder=******* required> <br/><br/> 
          <input type="submit" class=btn value="Sign in">
        </form>
      </div>
    </div>
  </div>
  <%
    //get the error message
    String error = (String) request.getAttribute("error");
      if (error != null && !error.equals("")) {%>
  <div class="alert alert-danger" role="alert" style="text-align: center; margin: 3% 20%;">
    <%=error%>
  </div>
  <% }%>
</div>

<%@include file="footer.jsp" %>
