<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.json.simple.JSONObject"%>
<!DOCTYPE html>
<html ng-app="validationApp">
  <!-- Here we don't use include header.jsp because we need to make it a angular js app -->
<%@page import="java.util.ArrayList"%>
<%@ page pageEncoding="UTF-8"%>
  
  <head>
    <meta charset='utf-8' />
    <link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
    <!-- Bootstrap core CSS and theme -->
    <link href="css/bootstrap.css" rel="stylesheet">
    <link href="css/bootstrap-theme.css" rel="stylesheet">
    <link rel="stylesheet" href="style.css" />
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.0/angular.min.js"></script>
    <script src="scripts/validation.js"></script>
    <script src="scripts/bootstrap.js"></script>
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
				<li><a href=documentation>REST API</a></li>
				<li><a href=aboutus>About us</a></li>
			</ul>
		</div>
	</div>
	
	<div class=yellow></div>

<!-- The page to display UI to validate an ontology alignment between 2 ontologies
It is called by Result.java (matcher) and Validator.java to display validation UI
for the ont1 and ont2 ontology alignment -->

<div id=sideLeft class="sideLeft"></div>

<div class=sideMiddle>

	<h3 class=contentText>Mappings</h3>
        
	<div id=toHideWhenDownload>
                
                    <%
                      //get lists from response
                      //ArrayList<fr.lirmm.opendata.yamgui.Map> liste = (ArrayList) request.getAttribute("data");
                      
                      ArrayList<fr.lirmm.opendata.yamgui.Map> alignmentArray = (ArrayList) request
                                      .getAttribute("data");

                      java.util.Map<String, String> onto1 = (java.util.Map) request
                                      .getAttribute("onto1");

                      java.util.Map<String, String> onto2 = (java.util.Map) request
                                      .getAttribute("onto2");
                      
                      if (request.getAttribute("errorMessage") == null && request.getAttribute("data") != null) {
                        //get the execution time from response
                        String time = (String) request.getAttribute("time");
                        if (time != null) {
                          out.println("<p class=contentTime> Calculated with YAM++ in "
                                      + time + " seconds</p>");
                        }
                    %>
        
                <script type="text/javascript">
                  var alignmentJson = <%=alignmentArray%>; 
                  //console.log(alignmentJson);
                </script>
                    
                <div ng-controller="validationCtrl" ></div>
                    
                <label for=seuilDynamic>Select your threshold:</label>
                <span id="threshold_display">0</span>
                <br> <input
			id=seuilDynamic name=seuilDynamic type="range" min=0 max=1 step=0.05
			size=3 value=0 oninput="refreshTab();" style="width: 25%;" onchange="refreshTab();">
                
                
		<table>
			<thead>
				<tr>
					<th class=thSmall>Line</th>
					<th>Source label</th>
					<th>Target label</th>
					<th>Relation</th>
					<th>Score</th>
					<th>Validity</th>
				</tr>
			</thead>

		</table>

		<form action='download'
			method='post'>
			<div class=tabDiv>
				<table id=table>
                                    <%--
                                        //for each cell in the list
                                        for (int line = 0; line < liste.size(); line++) {
                                                int numLine = line + 1;
                                                out.println("<tr>"); //new table line
                                                out.println("<td class=tdSmall>" + numLine + "</td>"); //display line number
                                                out.println("<td>" + onto1.get(liste.get(line).getE1())
                                                                + "</td>"); //display first entity
                                                out.println("<td>" + onto2.get(liste.get(line).getE2())
                                                                + "</td>"); //display second entity
                                                out.println("<td>" + liste.get(line).getRelation() + "</td>"); //display relation
                                                out.println("<td>" + liste.get(line).getScore() + "</td>"); //display score
                                                out.println("<td class=tdSmall" + line
                                                                + "><INPUT type='checkbox' name='checkbox' value='"
                                                                + line + "' id='" + line + "' checked></td>"); //display checkbox
                                                out.println("</tr>"); //close table line
                                        }
                                    --%>
				</table>
			</div>

            </div>

            <div class=btnCenter id='download'>
                    <input class=btn type="submit" value="Download mappings" />
            </div>
	</form>

	<br/>
        
        <%
          // If errors
          } else {    
        %>

        An error happened during matching <br/>
        
        <%
          out.println(request.getAttribute("errorMessage"));
          }
        %>
	<div id="overlay">
		<div class="popup_block">
			<p class=popup_text>
				If you continue, all your results will be lost.<br> <br> <a
					href=#noWhere style="text-decoration: none"><input class=btn
					type="button" value="Stay on this page"></a> <a href=matcher
					style="text-decoration: none"><input class=btn type="button"
					value="Return to matcher"></a>
			</p>
		</div>
	</div>

</div>
<div class="sideRight"></div>

<%@include file="footer.jsp"%>