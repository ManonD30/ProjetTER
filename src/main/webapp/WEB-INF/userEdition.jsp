<%@page import="fr.lirmm.yamplusplus.yampponline.YamUser"%>
<%@page import="fr.lirmm.yamplusplus.yampponline.YamDatabaseConnector"%>
<%@include file="header.jsp" %>

<div class="container theme-showcase" role="main">
  <%    YamUser user = null;
    if (request.getSession().getAttribute("apikey") != null) {
      user = new YamUser(request.getSession());
    }

    if (user != null) {%>
  <h2>Update user informations</h2>
  <div style="text-align: center;">
    <form action='userEdition' method='post' enctype='multipart/form-data'>
      <p>Affiliation/Institute:</p>
      <input type="text" name="affiliation" placeholder='ex: LIRMM' maxlength=32>

      <p>Working field: </p>
      <input type="text" name="field" placeholder='ex: Biomedical, music' maxlength=32>
      <br><br>

      <div id=submitEdition>
        <input type="submit" class="btn btn-info" value="Edit user informations" required>
      </div>
    </form>
  </div>
  <% }%>
</div>

<%@include file="footer.jsp" %>
