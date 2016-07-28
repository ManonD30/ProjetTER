<%@include file="header.jsp" %>

<div class="container theme-showcase" role="main">
  <br>
  <%
    String asMatched = (String) request.getAttribute("asMatched");
    String canMatch = (String) request.getAttribute("canMatch");
    out.println("<br><h2>Sorry " + name + ", you are limited to "
            + canMatch + " matchings per day and you tried to do "
            + asMatched + " today.</h2>");
  %>
  <br>
  <p class=contentParagraph>Ontology matching is a heavy task, so to
    allow a maximum number of persons to access the Matcher, we have
    limited the user access per day (the user account is reset everyday
    at 00:00 CET). Feel free to use the Validator and Evaluator as many
    times as you please, their use is unlimited.</p>

  <p class=contentParagraph>
    To any request of increase the maximum number of matching per day,
    please <a href=aboutus> contact us. </a>
  </p>
  <br>
  <h2>Or come back tomorrow...</h2>

</div>

<%@include file="footer.jsp" %>