<%@page import="java.util.Hashtable"%>
<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.json.simple.JSONObject"%>
<%@page import="java.util.ArrayList"%>

<%@include file="header.jsp" %>

<!-- The page to display UI to validate an ontology alignment between 2 ontologies
It is called by Result.java (matcher) and Validator.java to display validation UI
for the ont1 and ont2 ontology alignment -->

<link rel="stylesheet" href="//code.jquery.com/ui/1.12.0/themes/base/jquery-ui.css">
<link rel="stylesheet" href="https://rawgit.com/rzajac/angularjs-slider/master/dist/rzslider.css">
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.0/angular.min.js"></script>
<script src="scripts/validation.js"></script>
<script src="https://code.jquery.com/ui/1.12.0/jquery-ui.js"></script>

<script src="https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.14.3/ui-bootstrap-tpls.js"></script>
<script src="https://rawgit.com/rzajac/angularjs-slider/master/dist/rzslider.js"></script>

<div class="container theme-showcase" role="main" style="background-color: #f2f7f9;
     margin-right: 5%; margin-left: 5%; width: 90%;">

  <div ng-app="validationApp" style="width: 100%;">

    <h3 class=contentText>Mappings validation</h3>

    <div ng-controller="ValidationCtrl" id=toHideWhenDownload>

      <%
        // Get alignment Array with all aligned entities
        JSONArray alignmentArray = (JSONArray) request.getAttribute("alignment");

        // Trying to get ontology loaded using owlapi
        JSONObject ont1 = (JSONObject) request.getAttribute("ont1");
        JSONObject ont2 = (JSONObject) request.getAttribute("ont2");

        if (request.getAttribute("errorMessage") == null && request.getAttribute("alignment") != null) {
          //get the execution time from response
          String time = (String) request.getAttribute("time");
          if (time != null) {
            out.println("<p class=contentTime> Calculated with YAM++ in "
                    + time + " seconds</p><br/>");
          }
      %>

      <script type="text/javascript">
            // Put params to javascript to use it with angularjs
            var alignmentJson = <%=alignmentArray%>;
            var ont1 = <%=ont1%>;
            var ont2 = <%=ont2%>;
      </script>

      <label style="margin-left: 1%;">Score range:</label>&nbsp;&nbsp;
      <rzslider rz-slider-model="minRangeSlider.minValue" rz-slider-high="minRangeSlider.maxValue" 
                rz-slider-options="minRangeSlider.options" style="margin-left: 1%; width: 50%"></rzslider>

      <br/><br/>

      <label style="margin-left: 1%;">Search: <input type="search" ng-model="searchText"></label>
      <br/>
      <label style="margin-left: 1%;">Language:</label>
      <select class="form-control" style="width: 7%;" ng-model="selectedLang" 
              ng-options="k as v for (k, v) in langSelect" ng-init="selectedLang = langSelect['fr']"></select>

      <br/>
      <div>
        <form action='download' method='post'>
          <div style="display: table; width: 100%;">
            <div style="display: table-row;">
              <div class=tabDiv style="width: 79%; display: table-cell;">
                <table id=table>
                  <thead>
                    <tr>
                      <th href="#" ng-click="orderByField = 'index'; reverseSort = !reverseSort">Line</th>
                      <th href="#" ng-click="orderByField = 'entity1'; reverseSort = !reverseSort">Source label</th>
                      <th href="#" ng-click="orderByField = 'entity2'; reverseSort = !reverseSort">Target label</th>
                      <th href="#" ng-click="orderByField = 'relation'; reverseSort = !reverseSort">Relation</th>
                      <th href="#" ng-click="orderByField = 'measure'; reverseSort = !reverseSort">Score</th>
                      <th href="#" ng-click="" style="word-wrap: break-word;">Validity</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr ng-repeat="alignment in alignments|orderBy:orderByField:reverseSort|filter:searchText" class="{{selected}}"
                        ng-if="alignment.measure >= minRangeSlider.minValue / 100 && alignment.measure <= minRangeSlider.maxValue / 100">
                      
                      <!-- Change details div with selected entities details when mouseover or click -->
                      <td ng-mouseenter="changeDetails()" ng-click="changeDetails(true)" style="cursor: pointer; cursor: hand;">
                        <input type="text" name="index" value="{{alignment.index}}" style="display: none;" readonly>{{alignment.index}}</input>
                      </td>

                      <td ng-mouseenter="changeDetails()" ng-click="changeDetails(true)" style="cursor: pointer; cursor: hand;">
                        <!-- Remember on how to make a little window that show when mouseover
                        <div title="Source Entity details" data-toggle="popover" data-html="true" data-placement="right"
                             data-trigger="hover" data-entity="{{alignment.entity1}}"-->
                        <input type="text" id="{{alignment.entity1.id}}" name="entity1" value="{{alignment.entity1.id}}" 
                               style="display: none;" readonly>{{getEntityLabel(alignment.entity1, selectedLang)}}</input>
                        <!-- Display selectedLang, if not available take the first label in the list, then the id -->
                        <!--/div-->
                      </td>

                      <td ng-mouseenter="changeDetails()" ng-click="changeDetails(true)" style="cursor: pointer; cursor: hand;">
                        <input type="text" id="{{alignment.entity2.id}}" name="entity2" value="{{alignment.entity2.id}}" 
                               style="display: none;" readonly>{{getEntityLabel(alignment.entity2, selectedLang)}}</input>
                      </td>

                      <td>
                        <select id="{{alignment.relation}}" name="relation" class="form-control">
                          <option value="http://www.w3.org/2004/02/skos/core#exactMatch">skos:exactMatch</option>
                          <option value="http://www.w3.org/2004/02/skos/core#closeMatch">skos:closeMatch</option>
                          <option value="http://www.w3.org/2004/02/skos/core#broadMatch">skos:broadMatch</option>
                          <option value="http://www.w3.org/2004/02/skos/core#narrowMatch">skos:narrowMatch</option>
                        </select>
                      </td>
                      <td><input type="text" id="{{alignment.measure}}" name="measure" value="{{alignment.measure}}" style="display: none;" readonly>{{alignment.measure}}</input></td>
                      <td class=tdSmall style="word-wrap: break-word; text-align: center;">
                        <!--input type='checkbox' name='checkbox' class="checkbox" value='{{alignment.index}}' id='{{alignment.index}}' checked/-->
                        <select id="{{alignment.index}}" name="valid" class="form-control" ng-click="changeValidOptionColor(alignment.index)" style="{{getColoredDropdownStyle(alignment)}}">
                          <option style="background: #FFFFFF;" value="waiting">Waiting...</option>
                          <option style="background: #FFFFFF;" value="valid">Valid</option>
                          <option style="background: #FFFFFF;" value="notvalid">Not valid</option>
                        </select>
                      </td>
                    </tr>
                  </tbody>
                </table>
                <br/>

                <h3 class=contentText>Namespaces</h3><br/>
                <div class="row" style="text-align: center;">
                  <ul class="list-group" style="margin: 0 auto; max-width: 65%">
                    <li class="list-group-item" ng-repeat="(prefix, namespace) in namespaces">
                      <b>{{prefix}}</b> {{namespace}}
                    </li>
                  </ul>
                </div><br/>

                <h3 class=contentText>Formats</h3><br/>
                <div class="row" style="text-align: center;">
                  <!-- Need to change .inputFormatAlignmentAPI:checked in style.css to add a new css reaction for a new button-->
                  <div class="col-sm-4" style="text-align:center;">
                    <input type="radio" name="format" id="simpleRDF" value="simpleRDF" class="inputFormatSimpleRDF" style="display: none;">
                    <label for="simpleRDF" class="btn btn-sm btn-info inputFormatSimpleRDFLabel" 
                           title="entity1-relation-entity2 triples" data-toggle="tooltip">Simple RDF format</label>
                  </div>
                  <div class="col-sm-4" style="text-align:center;">
                    <input type="radio" name="format" id="alignmentAPI" value="alignmentAPI" style="display: none;" class="inputFormatAlignmentAPI" checked>
                    <label for="alignmentAPI" class="btn btn-sm btn-info inputFormatAlignmentAPILabel" title="OAEI EDOAL format"
                           data-toggle="tooltip">AlignmentAPI format</label>
                  </div>
                  <div class="col-sm-4" style="text-align:center;">
                    <input type="radio" name="format" id="RDF" value="RDF" class="inputFormatRDF" style="display: none;">
                    <label for="RDF" class="btn btn-sm btn-info inputFormatRDFLabel" data-toggle="tooltip"
                           title="RDF format with score (BETA: generated properties not valid)">RDF format</label>
                  </div>
                </div>

                <div class=btnCenter id='download'>
                  <input class="btn btnSubmit" type="submit" value="Download mappings"/>
                </div>
              </div>
              <div style="position: relative;">
                <div style="width: 20%; max-width: 20%; display: inline-table; position: fixed;
                     top: 15%; bottom: 15%; max-height: 70%; height: 70%"> 
                  <div id="entityDetail1" class="entityDetailsDiv"> 
                  </div>
                  <hr/>
                  <div id="entityDetail2" class="entityDetailsDiv">
                  </div>
                </div>
              </div>
            </div>
          </div>
        </form>
      </div>

    </div>
    <br/>

    <%
      // If errors
    } else {
    %>
    <div class="errorMsg alert alert-danger" role="alert">
      An error happened during matching <br/>
      <%
          out.println(request.getAttribute("errorMessage"));
        }
      %>
    </div>

  </div>
</div>

<%@include file="footer.jsp"%>