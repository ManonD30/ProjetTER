<%@page import="java.util.Arrays"%>
<%@page import="java.util.HashMap"%>
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
    JSONObject alignmentObject = new JSONObject();
    JSONArray jArray = new JSONArray();
    String[] indexArray = request.getParameterValues("index");
    String[] entity1 = request.getParameterValues("entity1");
    String[] entity2 = request.getParameterValues("entity2");
    String[] relation = request.getParameterValues("relation");
    String[] measure = request.getParameterValues("measure");
    // Put all mappings in an Array of Hashtable
    if (indexArray != null) {
      for (String i : indexArray) {
        // Get the index in param arrays of the validate mappings
        int paramIndex = Arrays.asList(indexArray).indexOf(i);
        JSONObject jObject = new JSONObject();
        jObject.put("entity1", entity1[paramIndex]);
        jObject.put("entity2", entity2[paramIndex]);
        jObject.put("relation", relation[paramIndex]);
        jObject.put("measure", measure[paramIndex]);
        jArray.add(jObject);
      }
    }
    
    // Post JSON to form : http://stackoverflow.com/questions/23355017/how-do-i-send-a-complex-json-object-from-an-html-form-using-synchronous-page-pos
    //JSONObject ontologies = (JSONObject) request.getParameter("ontologies");
    String testOnto = request.getParameter("ontologies");
    JSONObject ontologies = (JSONObject) request.getAttribute("ontologies");
    //JSONObject sourceOnt = (JSONObject) ontologies.get("ont1");
    //JSONObject targetOnt = (JSONObject) ontologies.get("ont2");
    JSONObject sourceOnt = null;
    JSONObject targetOnt = null;
    
    //alignmentObject.put("srcOntologyURI", (String) ontologies.get("srcOntologyURI"));
    //alignmentObject.put("tarOntologyURI", (String) ontologies.get("tarOntologyURI"));
    alignmentObject.put("entities", jArray);

    String srcOverlappingProportion = "0";
    String tarOverlappingProportion = "0";
    if (request.getAttribute("srcOverlappingProportion") != null) {
      srcOverlappingProportion = request.getAttribute("srcOverlappingProportion").toString();
    }
    if (request.getAttribute("tarOverlappingProportion") != null) {
      tarOverlappingProportion = request.getAttribute("tarOverlappingProportion").toString();
    }

    if (request.getAttribute("errorMessage") == null && alignmentObject != null) {
      //get the execution time from response
      String time = (String) request.getAttribute("time");
  %>
  <script type="text/javascript">
    // Put params to javascript to use it with angularjs
    var alignmentJson = <%=alignmentObject%>;
    var sourceOnt = <%=sourceOnt%>;
    console.log("Source in script: " + sourceOnt);
    console.log("overlap: " + <%=srcOverlappingProportion%>);
    console.log("onto in string " + <%=testOnto%>);
    var targetOnt = <%=targetOnt%>;
  </script>

  <section class="main-section" ng-app="validationApp" ng-controller="ValidationCtrl">&nbsp;
    <form action='download' method='post'>
      <div class="row">
        <div class="col-sm-6">
          <table id=table class="table table-striped">
            <thead>
              <tr style="cursor: pointer;">                                                                                 
                <th href="#" ng-click="orderByField = 'entity1.id'; reverseSort = !reverseSort" title="Sort by Source entity URI">{{ontologies.srcOntUri}}</th>
              </tr>
            </thead>
            <tbody>
              <tr ng-repeat="entity1 in ontologies.ont1|orderBy:orderByField:reverseSort|filter:searchText"
                  class="{{selected}}">

                <!-- Change details div with selected entities details when mouseover or click -->
                <td ng-mouseenter="changeDetails()" ng-click="changeDetails(true)" style="cursor: pointer; cursor: hand;">
                  <input type="text" name="index" value="{{entity1.id}}" style="display: none;" readonly>{{entity1.id}}</input>
                </td>
              </tr>
            </tbody>
          </table>
        </div>


        <div class="col-sm-6">
          <div class="list-group">
            <a href="#" class="list-group-item active">
              <h4 class="list-group-item-heading">List group item heading</h4>
              <p class="list-group-item-text">Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit.</p>
            </a>
            <a href="#" class="list-group-item">
              <h4 class="list-group-item-heading">List group item heading</h4>
              <p class="list-group-item-text">Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit.</p>
            </a>
            <a href="#" class="list-group-item">
              <h4 class="list-group-item-heading">List group item heading</h4>
              <p class="list-group-item-text">Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit.</p>
            </a>
          </div>
        </div>

        <input type="hidden" name="sourceUri" value="{{ontologies.srcOntUri}}">
        <input type="hidden" name="targetUri" value="{{ontologies.tarOntUri}}">

        <div style="text-align: center;">
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

        <p>
          This UI displays the results of the ontology matching and allows the user to validate or not each mapping.
          It shows informations about mapped concepts extracted from the provided ontologies on the right.
        </p>
        <div style="width: 100%; display: inline-block;">
          <%
            if (time != null) {
              //out.println("<p> Calculated with YAM++ Large Scale in " + time + " seconds</p>");
              out.println("<div class='col-sm-4'><div class='panel panel-info'><div class='panel-heading' style='text-align: center;'><h3 class='panel-title'>Running time</h3></div><div class='panel-body' style='text-align: center;'>"
                      + time + " seconds</div></div></div>");
            }
            if (!srcOverlappingProportion.equals("0")) {
              /* out.println("<label style='margin-left: 2%; margin-bottom: 1%;'>Source ontology mapped: </label>&nbsp;&nbsp;<div class='progress' style='width: 40%; display: inline-block; margin-bottom: 0px;'>"
                      + "<div class='progress-bar' role='progressbar' aria-valuenow='" + srcOverlappingProportion + "' aria-valuemin='0' aria-valuemax='100' style='width: "
                      + srcOverlappingProportion + "%;'><b>" + srcOverlappingProportion + "%</b></div></div>");*/
              out.println("<div class='col-sm-4'><div class='panel panel-primary'><div class='panel-heading' style='text-align: center;'><h3 class='panel-title'>Source ontology mapped</h3></div><div class='panel-body' style='padding-bottom: 0px;'>"
                      + "<div class='progress'><div class='progress-bar' role='progressbar' aria-valuenow='" + srcOverlappingProportion + "' aria-valuemin='0' aria-valuemax='100' style='width: "
                      + srcOverlappingProportion + "%;'><b>" + srcOverlappingProportion + "%</b></div></div>"
                      + "</div></div></div>");
            }
            if (!tarOverlappingProportion.equals("0")) {
              /*out.println("<br/><label style='margin-left: 2%; margin-bottom: 2%;'>Target ontology mapped: </label>&nbsp;&nbsp;<div class='progress' style='width: 40%; display: inline-block; margin-bottom: 0px;'>"
                      + "<div class='progress-bar progress-bar-success' role='progressbar' aria-valuenow='" + tarOverlappingProportion + "' aria-valuemin='0' aria-valuemax='100' style='width: "
                      + tarOverlappingProportion + "%;'><b>" + tarOverlappingProportion + "%</b></div></div>");*/
              out.println("<div class='col-md-4'><div class='panel panel-success'><div class='panel-heading' style='text-align: center;'><h3 class='panel-title'>Target ontology mapped</h3></div><div class='panel-body' style='padding-bottom: 0px;'>"
                      + "<div class='progress'><div class='progress-bar progress-bar-success' role='progressbar' aria-valuenow='" + tarOverlappingProportion + "' aria-valuemin='0' aria-valuemax='100' style='width: "
                      + tarOverlappingProportion + "%;'><b>" + tarOverlappingProportion + "%</b></div></div>"
                      + "</div></div></div>");
            }
          %>
        </div>

        <!-- Input to filter mappings table -->
        <div class="alert alert-success" style="text-align: center;     padding-top: 20px; padding-bottom: 20px;">
          <label>Search: <input type="search" ng-model="searchText"></label>
          <button id="hideAlignmentsButton" type="button" class="btn btn-sm btn-info" style="margin-left: 1%;" 
                  ng-click="hideAlignments($event)">Hide validated alignments</button>

          <label for="slider-range" id="rangeLabel" style="margin-left: 3%; margin-right: 1%">Display scores from {{rangeSlider.minValue| number:2}} to {{rangeSlider.maxValue| number:2}}</label>
          <div id="slider-range" style="width: 20%;display: inline-flex"></div>

          <label style="margin-left: 3%;">Language:</label>
          <select class="form-control"  style="display:inline; margin-left: 1%;" ng-model="selectedLang" 
                  ng-options="k as v for (k, v) in langSelect" ng-init="selectedLang = langSelect['fr']"></select>
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