<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.json.simple.JSONObject"%>
<%@page import="java.util.ArrayList"%>
  
<%@include file="header.jsp" %>

<!-- The page to display UI to validate an ontology alignment between 2 ontologies
It is called by Result.java (matcher) and Validator.java to display validation UI
for the ont1 and ont2 ontology alignment -->

<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.0/angular.min.js"></script>
<script src="scripts/validation.js"></script>

<div id=sideLeft class="sideLeft"></div>

<div class=sideMiddle ng-app="validationApp">

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
                    
                <label for=seuilDynamic>Select your threshold:</label>
                <span id="threshold_display">0</span><br/> 
                <input id=seuilDynamic name=seuilDynamic type="range" min=0 max=1 step=0.05
                       size=3 value=0 oninput="refreshTab();" style="width: 25%;" onchange="refreshTab();">
                <br/>
		<form action='download'
			method='post'>
			<div class=tabDiv ng-controller="validationCtrl">
				<table id=table>
                                  <thead>
                                    <tr>
                                      <th href="#" ng-click="orderByField='index'; reverseSort = !reverseSort">Line</th>
                                      <th href="#" ng-click="orderByField='entity1'; reverseSort = !reverseSort">Source label</th>
                                      <th href="#" ng-click="orderByField='entity2'; reverseSort = !reverseSort">Target label</th>
                                      <th href="#" ng-click="orderByField='relation'; reverseSort = !reverseSort">Relation</th>
                                      <th href="#" ng-click="orderByField='measure'; reverseSort = !reverseSort">Score</th>
                                      <th href="#" ng-click="">Validity</th>
                                    </tr>
                                  </thead>
                                  <tbody>
                                    <tr ng-repeat="alignment in alignmentJson|orderBy:orderByField:reverseSort">
                                      <td name="index">{{alignment.index}}</td>
                                      <td><input type="text" id="{{alignment.entity1}}" name="entity1" value="{{alignment.entity1}}" style="display: none;" readonly>{{alignment.entity1}}</input></td>
                                      <td><input type="text" id="{{alignment.entity2}}" name="entity2" value="{{alignment.entity2}}" style="display: none;" readonly>{{alignment.entity2}}</input></td>
                                      <td><input type="text" id="{{alignment.relation}}" name="relation" value="{{alignment.relation}}" style="display: none;" readonly>{{alignment.relation}}</input></td>
                                      <td><input type="text" id="{{alignment.measure}}" name="measure" value="{{alignment.measure}}" style="display: none;" readonly>{{alignment.measure}}</input></td>
                                      <td class=tdSmall>
                                        <input type='checkbox' name='checkbox' value='{{alignment.index}}' id='{{alignment.index}}' checked/>
                                      </td>
                                    </tr>
                                  </tbody>
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