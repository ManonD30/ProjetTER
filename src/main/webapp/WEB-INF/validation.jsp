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
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.0/angular.min.js"></script>
<script type="text/javascript" src="scripts/vis.min.js"></script>
<script src="scripts/validation.js"></script>
<!--script src="scripts/jquery.tablesorter.js"></script>
<script src="scripts/jquery.tablesorter.pager.js"></script-->

<!-- To remove if jQuery floatingScroll don't work -->
<link rel="stylesheet" href="css/jquery.floatingscroll.css" />
<script src="scripts/jquery.floatingscroll.min.js"></script>

<link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<!-- Custom CSS for validation: -->
<link href="css/validation.css" rel="stylesheet">

<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.14.3/ui-bootstrap-tpls.js"></script>
<script src="https://rawgit.com/rzajac/angularjs-slider/master/dist/rzslider.js"></script>

<main>

  <%    // Get alignment Array with all aligned entities
    if (request.getAttribute("errorMessage") == null && request.getAttribute("alignment") != null) {
      //java.util.logging.Logger.getLogger("fr.lirmm.yamplusplus.yampponline.Validation").log(java.util.logging.Level.INFO, "START validation.jsp");
      JSONObject alignmentObject = (JSONObject) request.getAttribute("alignment");
      request.setAttribute("srcOntologyURI", (String) alignmentObject.get("srcOntologyURI"));
      request.setAttribute("tarOntologyURI", (String) alignmentObject.get("tarOntologyURI"));

      // Trying to get ontology loaded using owlapi
      JSONObject sourceOnt = (JSONObject) request.getAttribute("sourceOnt");
      JSONObject targetOnt = (JSONObject) request.getAttribute("targetOnt");
      int srcMappingCount = (int) request.getAttribute("srcMappingCount");
      int tarMappingCount = (int) request.getAttribute("tarMappingCount");
      int srcConceptCount = 1;
      int tarConceptCount = 1;
      String srcOverlappingProportion;
      String tarOverlappingProportion;
      
      try {
        srcConceptCount = ((Long) sourceOnt.get("entityCount")).intValue();
        tarConceptCount = ((Long) targetOnt.get("entityCount")).intValue();
        srcOverlappingProportion = String.valueOf(srcMappingCount * 100 / srcConceptCount);
        tarOverlappingProportion = String.valueOf(tarMappingCount * 100 / tarConceptCount);
      } catch (ClassCastException e) {
        srcOverlappingProportion = "0";
        tarOverlappingProportion = "0";
      }
      
      ((JSONObject) sourceOnt.get("entities")).size();

      String sourceName = (String) request.getAttribute("sourceName");
      String targetName = (String) request.getAttribute("targetName");


      java.util.logging.Logger.getLogger("fr.lirmm.yamplusplus.yampponline.Validation").log(java.util.logging.Level.INFO, "get time (jsp)");
      //get the execution time from response
      String time = (String) request.getAttribute("time");%>
  <script>
    // Put params to javascript to use it with angularjs    
    var alignmentJson = <%=alignmentObject%>;
    var sourceOnt = <%=sourceOnt%>;
    var targetOnt = <%=targetOnt%>;
    var sourceName = "<%=sourceName%>";
    var targetName = "<%=targetName%>";
  </script>

  <section class="main-section" ng-app="validationApp" ng-controller="ValidationCtrl">&nbsp;

    <div id="fixDiv">
      <div id="fixAddMapping" style="display: none; margin-bottom: 20px;">

        <select id="addMappingSelect" name="addMappingSelect" class="form-control" style="margin-right: 20px;"
                ng-model="addRelation" ng-init="addRelation = 'http://www.w3.org/2004/02/skos/core#exactMatch'">
          <option style="background: #fff;" value="http://www.w3.org/2004/02/skos/core#exactMatch"selected >skos:exactMatch</option>
          <option style="background: #fff;" value="http://www.w3.org/2004/02/skos/core#closeMatch">skos:closeMatch</option>
          <option style="background: #fff;" value="http://www.w3.org/2004/02/skos/core#broadMatch">skos:broadMatch</option>
          <option style="background: #fff;" value="http://www.w3.org/2004/02/skos/core#narrowMatch">skos:narrowMatch</option>
          <option style="background: #fff;" value="http://www.w3.org/2004/02/skos/core#relatedMatch">skos:relatedMatch</option>
        </select>

        <button type="button" class="btn btn-sm btn-info"
                ng-disabled="srcDetailsLocked == false || tarDetailsLocked == false"
                ng-click="addMapping($event)">Add mapping</button>
      </div>

      <div>
        <button type="button" id="extendedBtn" class="btn btn-default" onclick="toggleExtended()" 
                style="margin-bottom: 3%;">Add new mappings to alignment (beta)</button>
      </div>
    </div>

    <!-- Contains table to validate mappings. Hided when adding new mappings -->
    <div id="validationForm" class="validationForm" style="overflow-x: scroll;">

      <p>
        This UI displays the results of the ontology matching and allows the user to validate or not each mapping.
        It shows informations about mapped concepts extracted from the provided ontologies on the right.<br>
        The user can also add new mappings that have not been found by the Yam++ Matcher (note that 
        for performance reason only the aligned concepts are retrieved for ontologies with more than 30 000 statements)
      </p>
      <div style="width: 100%; display: inline-block;">
        <%
          if (time != null) {%>
        <div class='col-sm-4'>
          <div class='panel panel-info'>
            <div class='panel-heading' style='text-align: center;'>
              <h3 class='panel-title'>Running time</h3>
            </div>
            <div class='panel-body' style='text-align: center;'>
              <%=time%> seconds
            </div>
          </div>
        </div>
        <%}
          if (!srcOverlappingProportion.equals("0")) {
        %>
        <div class='col-sm-4'>
          <div class='panel panel-primary'>
            <div class='panel-heading' style='text-align: center;'>
              <h3 class='panel-title'>Source ontology mapped</h3>
            </div>
            <div class='panel-body' style='padding-bottom: 0px; text-align: center;'>
              <span> Matched <%=srcMappingCount%> entities on <%=srcConceptCount%></span>
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
              <span> Matched <%=tarMappingCount%> entities on <%=tarConceptCount%></span>
              <div class='progress'>
                <div class='progress-bar progress-bar-success' role='progressbar' aria-valuenow='<%=tarOverlappingProportion%>' 
                     aria-valuemin='0' aria-valuemax='100' style='width: <%=tarOverlappingProportion%>%;'><b><%=tarOverlappingProportion%>%</b>
                </div>
              </div>
            </div>
          </div>
        </div>
        <%}
        %>
      </div>

      <!-- Input to filter mapping table -->
      <div class="alert alert-success" style="text-align: center;     padding-top: 20px; padding-bottom: 20px;">
        <label>Search: <input type="search" ng-model="searchText"></label>
        <button id="hideAlignmentsButton" type="button" class="btn btn-sm btn-info" style="margin-left: 1%;" 
                ng-click="hideAlignments($event)">Hide unvalid mappings</button>

        <label for="slider-range" id="rangeLabel" style="margin-left: 3%; margin-right: 1%" title="Show mappings with a score in the selected range">
          Show scores from {{rangeSlider.minValue| number:2}} to {{rangeSlider.maxValue| number:2}}</label>
        <div id="slider-range" style="width: 20%;display: inline-flex" title="Show mappings with a score in the selected range"></div>

        <label style="margin-left: 3%;">Language:</label>
        <select class="form-control"  style="display:inline; margin-left: 1%;" ng-model="selectedLang" 
                ng-options="k as v for (k, v) in langSelect" ng-init="selectedLang = langSelect['fr']"></select>
      </div>

      <form action='download' method='post'>
        <table id=table class="table table-striped">
          <thead>
            <tr style="cursor: pointer;">
              <th href="#" ng-click="orderByField = 'entity1.id'; reverseSort = !reverseSort" title="Sort by Source entity URI">{{sourceName}}</th>
              <th href="#" ng-click="orderByField = 'entity2.id'; reverseSort = !reverseSort" title="Sort by Target entity URI">{{targetName}}</th>
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
                <input type="text" id="{{alignment.measure}}" name="measure" value="{{alignment.measure}}" 
                       style="display: none;" readonly>{{alignment.measure}}</input>
              </td>
            </tr>
          </tbody>
        </table>
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

        <!-- Pass ontologies URI to the form to be able to name the files when ddl -->
        <input type="hidden" name="sourceUri" value="{{ontologies.srcOntUri}}">
        <input type="hidden" name="targetUri" value="{{ontologies.tarOntUri}}">
        <input type="hidden" name="sourceName" value="{{sourceName}}">
        <input type="hidden" name="targetName" value="{{targetName}}">

        <div style="text-align: center;">

          <div class=btnCenter id='download'>
            <label class="inputFormatSimpleRDFLabel" 
                   title="OAEI EDOAL format" data-toggle="tooltip">Save to: </label>
            <input type="submit" name="validationSubmit" value="AlignmentAPI format" class="btn btnSubmit"
                   title="OAEI EDOAL format" style="margin-bottom: 0;">
          </div>
          <br>
          <div class=btnCenter id='export' style="margin-bottom: 8em;">
            <label class="inputFormatSimpleRDFLabel">Export to: </label>

            <input type="submit" name="validationSubmit" value="Simple RDF format" class="btn"
                   title="entity1-relation-entity2 triples">

            <input type="submit" name="validationSubmit" value="RDF format" class="btn btn-info"
                   title="RDF format with score">
          </div>
        </div>
      </form>
    </div>

    <!-- The UI to add new mappings (alternate with validate existing mappings when click on last button) -->
    <div id="extendedValidationDiv" class="row" style="display: none;">

      <!-- Params to display concepts (lang here) -->
      <!--div class="alert alert-success" style="text-align: center;     padding-top: 20px; padding-bottom: 20px;">
        <label style="margin-left: 3%;">Language:</label>
        <select class="form-control"  style="display:inline-block; margin-left: 1%;" ng-model="selectedLang" 
                ng-options="k as v for (k, v) in langSelect" ng-init="selectedLang = langSelect['fr']"></select>
      </div-->

      <!-- Source ontology concepts tables -->
      <div class="col-sm-6">

        <label style="position: fixed;">Search: <input type="search" ng-model="searchSrc"></label>

        <table id=table class="table table-striped" style="margin-top: 40px;">
          <thead>
            <tr style="cursor: pointer;">                                                                                 
              <th href="#" ng-click="orderByField = 'entity.id'; reverseSort = !reverseSort" title="Sort by Source entity URI">{{ontologies.srcOntUri}}</th>
              <!--th href="#" title="Sort by Source entity URI">{{ontologies.srcOntUri}}</th-->
            </tr>
          </thead>
          <tbody>
            <!--tr ng-repeat="entity1 in ontologies.ont1.entities|orderBy:orderByField:reverseSort|filter:searchSrc"
                class="{{selected}}"-->
            <tr ng-repeat="entity in srcOntArray| orderBy:orderByField:reverseSort | filter:searchSrc" class="{{selected}}">

              <!-- Change details div with selected entities details when mouseover or click -->
              <td ng-mouseenter="changeDetailsExtended('Source', entity)" ng-click="changeDetailsExtended('Source', entity, true)" style="cursor: pointer; cursor: hand;">
                <input type="text" name="index" value="{{entity.id}}" style="display: none;" readonly>{{getEntityLabel(entity, "en")}}</input>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Target ontology concepts tables -->
      <div class="col-sm-6">
        <label style="position: fixed;">Search: <input type="search" ng-model="searchTar"></label>

        <table id=table class="table table-striped" style="margin-top: 40px;">
          <thead>
            <tr style="cursor: pointer;">                                                                                 
              <th href="#" ng-click="orderByField = 'entity.id'; reverseSort = !reverseSort" title="Sort by Target entity URI">{{ontologies.tarOntUri}}</th>
            </tr>
          </thead>
          <tbody>
            <tr ng-repeat="entity in tarOntArray| orderBy:orderByField:reverseSort | filter:searchTar" class="{{selected}}">

              <!-- Change details div with selected entities details when mouseover or click -->
              <td ng-mouseenter="changeDetailsExtended('Target', entity)" ng-click="changeDetailsExtended('Target', entity, true)" style="cursor: pointer; cursor: hand;">
                <input type="text" name="index" value="{{entity.id}}" style="display: none;" readonly>{{getEntityLabel(entity, "en")}}</input>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>


  <!-- Createwindow at the right of the screen (to display entities details) -->
  <aside>
    <nav class="switch-nav">
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