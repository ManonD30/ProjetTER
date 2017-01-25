<%@page import="java.util.Hashtable"%>
<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.json.simple.JSONObject"%>
<%@page import="java.util.ArrayList"%>

<%@include file="header.jsp" %>

<!-- The page to display UI to validate an ontology alignment between 2 ontologies
It is called by Result.java (matcher) and Validator.java to display validation UI
for the sourceOnt and targetOnt ontology alignment -->

<link rel="stylesheet" href="//code.jquery.com/ui/1.12.0/themes/base/jquery-ui.css">
<link rel="stylesheet" href="https://rawgit.com/rzajac/angularjs-slider/master/dist/rzslider.css">
<link rel="stylesheet" href="css/vis.min.css" />
<!-- Custom CSS for validation: -->
<link href="css/validation.css" rel="stylesheet">
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.0/angular.min.js"></script>
<script type="text/javascript" src="scripts/vis.min.js"></script>
<script src="scripts/validation.js"></script>
<!--script src="scripts/jquery.tablesorter.js"></script>
<script src="scripts/jquery.tablesorter.pager.js"></script-->


<link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<!--script src="https://code.jquery.com/jquery-1.12.4.js"></script-->
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>

<script src="https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.14.3/ui-bootstrap-tpls.js"></script>
<script src="https://rawgit.com/rzajac/angularjs-slider/master/dist/rzslider.js"></script>

<main>

  <%    // Get alignment Array with all aligned entities
    JSONObject alignmentObject = (JSONObject) request.getAttribute("alignment");
    request.setAttribute("srcOntologyURI", (String) alignmentObject.get("srcOntologyURI"));
    request.setAttribute("tarOntologyURI", (String) alignmentObject.get("tarOntologyURI"));

    // Trying to get ontology loaded using owlapi
    JSONObject sourceOnt = (JSONObject) request.getAttribute("sourceOnt");
    JSONObject targetOnt = (JSONObject) request.getAttribute("targetOnt");

    String srcOverlappingProportion = "0";
    String tarOverlappingProportion = "0";
    if (request.getAttribute("srcOverlappingProportion") != null) {
      srcOverlappingProportion = request.getAttribute("srcOverlappingProportion").toString();
    }
    if (request.getAttribute("tarOverlappingProportion") != null) {
      tarOverlappingProportion = request.getAttribute("tarOverlappingProportion").toString();
    }

    if (request.getAttribute("errorMessage") == null && request.getAttribute("alignment") != null) {
      //get the execution time from response
      String time = (String) request.getAttribute("time");
  %>
  <script type="text/javascript">
    // Put params to javascript to use it with angularjs
    var alignmentJson = <%=alignmentObject%>;
    var sourceOnt = <%=sourceOnt%>;
    var targetOnt = <%=targetOnt%>;
  </script>

  <section class="main-section" ng-app="validationApp" ng-controller="ValidationCtrl">&nbsp;

    <p>
      This UI displays the results of the ontology matching and allows the user to validate or not each mapping.
      It shows informations about mapped concepts extracted from the provided ontologies on the right.
    </p>
    <div style="width: 100%; display: inline-block;">
      <%
        if (time != null) {
          out.println("<div class='col-sm-4'><div class='panel panel-info'><div class='panel-heading' style='text-align: center;'><h3 class='panel-title'>Running time</h3></div><div class='panel-body' style='text-align: center;'>"
                  + time + " seconds</div></div></div>");
        }
        if (!srcOverlappingProportion.equals("0")) {
      %>
      <div class='col-sm-4'>
        <div class='panel panel-primary'>
          <div class='panel-heading' style='text-align: center;'>
            <h3 class='panel-title'>Source ontology mapped</h3>
          </div>
          <div class='panel-body' style='padding-bottom: 0px; text-align: center;'>
            <span> Matched <%=((JSONArray) alignmentObject.get("entities")).size()%> on <%=((JSONObject) sourceOnt.get("entities")).size()%> entities</span>
            <div class='progress'>
              <div class='progress-bar' role='progressbar' aria-valuenow='<%=srcOverlappingProportion%>' aria-valuemin='0' aria-valuemax='100' 
                   style='width: <%=srcOverlappingProportion%>%;'><b><%=srcOverlappingProportion%>%</b>
              </div>
            </div>
          </div>
        </div>
      </div>
      <%
        }
        if (!tarOverlappingProportion.equals("0")) {%>
      <div class='col-md-4'>
        <div class='panel panel-success'>
          <div class='panel-heading' style='text-align: center;'>
            <h3 class='panel-title'>Target ontology mapped</h3>
          </div>
          <div class='panel-body' style='padding-bottom: 0px; text-align: center;'>
            <span> Matched <%=((JSONArray) alignmentObject.get("entities")).size()%> on <%=((JSONObject) targetOnt.get("entities")).size()%> entities</span>
            <div class='progress'>
              <div class='progress-bar progress-bar-success' role='progressbar' aria-valuenow='<%=tarOverlappingProportion%>' 
                   aria-valuemin='0' aria-valuemax='100' style='width: <%=tarOverlappingProportion%>%;'><b><%=tarOverlappingProportion%>%</b></div></div>"
          </div>
        </div>
      </div>
      <%}
      %>
    </div>

    <!-- Input to filter mappings table -->
    <div class="alert alert-success" style="text-align: center;     padding-top: 20px; padding-bottom: 20px;">
      <label>Search: <input type="search" ng-model="searchText"></label>
      <button id="hideAlignmentsButton" type="button" class="btn btn-sm btn-info" style="margin-left: 1%;" 
              ng-click="hideAlignments($event)">Hide unvalid mappings</button>

      <label for="slider-range" id="rangeLabel" style="margin-left: 3%; margin-right: 1%">Display scores from {{rangeSlider.minValue| number:2}} to {{rangeSlider.maxValue| number:2}}</label>
      <div id="slider-range" style="width: 20%;display: inline-flex"></div>

      <label style="margin-left: 3%;">Language:</label>
      <select class="form-control"  style="display:inline; margin-left: 1%;" ng-model="selectedLang" 
              ng-options="k as v for (k, v) in langSelect" ng-init="selectedLang = langSelect['fr']"></select>
    </div>


    <form action='download' method='post'>
      <table id=table class="table table-striped">
        <thead>
          <tr style="cursor: pointer;">
            <th href="#" ng-click="orderByField = 'index'; reverseSort = !reverseSort" title="Sort by index">Line</th>
            <th href="#" ng-click="orderByField = 'entity1.id'; reverseSort = !reverseSort" title="Sort by Source entity URI">{{ontologies.srcOntUri}}</th>
            <th href="#" ng-click="orderByField = 'entity2.id'; reverseSort = !reverseSort" title="Sort by Target entity URI">{{ontologies.tarOntUri}}</th>
            <th href="#" ng-click="orderByField = 'relation'; reverseSort = !reverseSort" style="width: 11em;"
                title="Sort by relatiion">Relation</th>
            <th href="#" ng-click="orderByField = 'measure'; reverseSort = !reverseSort" title="Sort by score">Score</th>
            <!--th href="#" style="word-wrap: break-word;">Validity</th-->
            <!--th href="#" style="width: 8em;">Validity</th-->
          </tr>
        </thead>
        <tbody>
          <tr ng-repeat="alignment in alignments|orderBy:orderByField:reverseSort|filter:searchText"
              class="{{selected}}" ng-if="generateTableNgIf(alignment)">

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
              <select id="{{generateRelationSelectId(alignment.index)}}" name="relation" class="form-control"
                      style="{{generateStyleForSelect(alignment)}}" ng-model="selectRelationModel[alignment.index]" 
                      ng-click="updateSelectRelationModels($event, alignment)" ng-init="selectRelationModel[alignment.index] = alignment.relation || 'http://www.w3.org/2004/02/skos/core#exactMatch'">
                <option style="background: #fff;" value="http://www.w3.org/2004/02/skos/core#exactMatch">skos:exactMatch</option>
                <option style="background: #fff;" value="http://www.w3.org/2004/02/skos/core#closeMatch">skos:closeMatch</option>
                <option style="background: #fff;" value="http://www.w3.org/2004/02/skos/core#broadMatch">skos:broadMatch</option>
                <option style="background: #fff;" value="http://www.w3.org/2004/02/skos/core#narrowMatch">skos:narrowMatch</option>
                <option style="background: #fff;" value="http://www.w3.org/2004/02/skos/core#relatedMatch">skos:relatedMatch</option>
                <option style="background: #d9534f;" value="notvalid">Not valid</option>
              </select>
            </td>
            <td>
              <input ty              pe="text" id="{{alignment.measure}}" name="measure" value="{{alignment.measure}}" 
                     style="display: none;" readonly>{{alignment.measure}}</input>
            </td>
            <!--td>
            <!-- We are changing the color of the select when a valid option is se            lected ->
            <select id="{{generateValidSelectId(alignment.index)}}" name="valid" 
style="{{generateStyleForSelect(alignment)}}" class="form-co                      ntrol" 
                        ng-model="selectValidModel[alignment.index]" ng-click="upd                        ateSelectValidMo                    dels($event, alignment)"
                    ng-init="selectValidModel[alignment.index] = alignment.valid">                    
                             <option style="background: #f0ad4e;" value="waiting">                             Waiting...</option>
                   <option style="background: #5cb85c;" value="valid">Valid</optio                   n>
                          <option style="background: #d9534f;" value="notvalid">No                          t valid</option>
                   </select>
</          td-->
          </tr>
        </tbody>
      </table>

      <!--div id="pager" cl          ass="pager" style="top: 687px; position: absolute;">      </div-->
      <br/>

      <!-- List the different prefixes/namespaces used by the 2 ontologies (not used anymore)
 h3 class=contentText>Namespaces</h3><br/>
      <div class="row" style="text-align: center;">
        <ul class="list-group" style="margin: 0 auto; max-width: 65%">
          <li class="list-group-item" ng-repeat="(prefix, namespace) in namespaces">
            <b>{{prefix}}</b> {{namespace}}
          </li>
        </ul>
      </div><br/-->

      <input type="hidden" name="sourceUri" value="{{ontologies.srcOntUri}}">
      <input type="hidden" name="targetUri" value="{{ontologies.tarOntUri}}">

      <div style="text-align: center;">

        <div class=btnCenter id='extendedValidation'>
          <label class="inputFormatSimpleRDFLabel" 
                 title="Extended validation" data-toggle="tooltip">Add new mappings between ontologies concepts: </label>
          <input type="submit" name="validationSubmit" value="Extended validation" class="btn btn-default"
                 title="Extended validation" style="margin-bottom: 0;">
        </div>
        <br>

        <div class=btnCenter id='download'>
          <label class="inputFormatSimpleRDFLabel" 
                 title="OAEI EDOAL format" data-toggle="tooltip">Save to: </label>
          <input type="submit" name="validationSubmit" value="AlignmentAPI format" class="btn btnSubmit"
                 title="OAEI EDOAL format" style="margin-bottom: 0;">
        </div>
        <br>
        <div class=btnCenter id='download' style="margin-bottom: 8em;">
          <label class="inputFormatSimpleRDFLabel">Export to: </label>

          <input type="submit" name="validationSubmit" value="Simple RDF format" class="btn"
                 title="entity1-relation-entity2 triples">

          <input type="submit" name="validationSubmit" value="RDF format" class="btn btn-info"
                 title="RDF format with score">
        </div>
      </div>
    </form>
  </section>


  <!-- Createwindow at the right of the screen (to display entities details) -->
  <aside>
    <nav class="    switch-nav">
      <ul>
        <!-- the 2 glyphicons to choose between list a    nd graph view -->
        <li class="text"><button type="button" class="btn btn-default btn-info" aria-label="Left Align">
            <span class="glyphicon glyphicon-list" aria-hidden="true"></span>
          </button></li>
        <li class="graph"><button type="button" class="btn btn-default btn-lg">
            <span class="glyphicon glyphicon-picture" aria-hidden="true"></span>
          </button></li>
      </ul>
    </nav>

    <div class="entities">
      <section id="sourceSection" class="entity entity-source">
        <div class="entity-content">
          <div class="entity-inner-content">
            <div id="entityDetail1"  class="entity-view entity-text">
            </div>
            <div class="entity-view entity-graph">
              <div id="sourceNetwork"></div>                
            </div>
          </div>
        </div>
      </section>

      <section id="targetSection" class="entity entity-target">
        <div class="entity-content">
          <div class="entity-inner-content">
            <div id="entityDetail2" class="entity-view entity-text">
            </div>
            <div class="entity-view entity-graph">
              <div id="targetNetwork"></div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </aside>


  <%
    // If errors
  } else {
  %>
  <section class="main-section" style="margin: 0 auto;"
           ng-app="validationApp" ng-controller="ValidationCtrl">&nbsp;
    <div class="errorMsg alert alert-danger" role="alert" style="width: 75%;">
      An error happened during matching <br/>
      <%
          out.println(request.getAttribute("errorMessage"));
        }
      %>
    </div>

</main>

<!--script>
  Pour generer la pagination ! (marche pas)
  $(document).ready(function () {
    $("table")
            .tablesorter({widthFixed: true, widgets: ['zebra']})
            .tablesorterPager({container: $("#pager")});
  });
</script-->

<%@include file="footer.jsp"%>