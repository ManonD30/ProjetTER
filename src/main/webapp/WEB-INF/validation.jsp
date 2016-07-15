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
                                      + time + " seconds</p><br/>");
                        }
                    %>
        
                <script type="text/javascript">
                  var alignmentJson = <%=alignmentArray%>; 
                  //console.log(alignmentJson);
                </script>
                    
                <div style="margin-left: 1%;">
                  <label for=thresholdRange>Select your threshold:</label>
                  <span id="thresholdDisplay">0</span><br/> 
                  <input id="thresholdRange" name="thresholdRange" ng-model="threshold" type="range" min=0 max=1 step=0.05
                         size=3 value=0 style="width: 25%;" onInput="refreshThreshold();">
                </div><br/>
                  
                <label style="margin-left: 1%;">Search: <input type="search" ng-model="searchText"></label>
                <button type="button" class="btn btn-sm btn-info" style="margin-left: 1%;" onclick="checkAllBoxes()">Check/uncheck all mappings</button>
                
                <br/><br/>
                
		<form action='download'
			method='post'>
			<div class=tabDiv ng-controller="ValidationCtrl">
                          
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
                                    <tr ng-repeat="alignment in alignmentJson|orderBy:orderByField:reverseSort|filter:searchText" ng-if="alignment.measure >= threshold || threshold == null">  
                                      <td><input type="text" id="{{alignment.index}}" name="index" value="{{alignment.index}}" style="display: none;" readonly>{{alignment.index}}</input></td>
                                      <td><input type="text" id="{{alignment.entity1}}" name="entity1" value="{{alignment.entity1}}" style="display: none;" readonly>{{alignment.entity1}}</input></td>
                                      <td><input type="text" id="{{alignment.entity2}}" name="entity2" value="{{alignment.entity2}}" style="display: none;" readonly>{{alignment.entity2}}</input></td>
                                      <td>
                                        <select id="{{alignment.relation}}" name="relation" class="form-control">
                                          <option value="http://www.w3.org/2004/02/skos/core#exactMatch">skos:exactMatch</option>
                                          <option value="http://www.w3.org/2004/02/skos/core#closeMatch">skos:closeMatch</option>
                                          <option value="http://www.w3.org/2004/02/skos/core#broadMatch">skos:broadMatch</option>
                                          <option value="http://www.w3.org/2004/02/skos/core#narrowMatch">skos:narrowMatch</option>
                                        </select>
                                      </td>
                                      <td><input type="text" id="{{alignment.measure}}" name="measure" value="{{alignment.measure}}" style="display: none;" readonly>{{alignment.measure}}</input></td>
                                      <td class=tdSmall>
                                        <input type='checkbox' name='checkbox' class="checkbox" value='{{alignment.index}}' id='{{alignment.index}}' checked/>
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

<script>
  /**
   * Update threshold display text
   */
  function refreshThreshold() {
    document.getElementById("thresholdDisplay").innerHTML = document.getElementById("thresholdRange").value;
  }
</script>