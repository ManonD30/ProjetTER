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
<!-- Custom CSS for validation: -->
<link href="css/validation.css" rel="stylesheet">
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.0/angular.min.js"></script>
<script src="scripts/validation.js"></script>
<!--script src="scripts/jquery.tablesorter.js"></script>
<script src="scripts/jquery.tablesorter.pager.js"></script-->
<script src="https://code.jquery.com/ui/1.12.0/jquery-ui.js"></script>

<script src="https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.14.3/ui-bootstrap-tpls.js"></script>
<script src="https://rawgit.com/rzajac/angularjs-slider/master/dist/rzslider.js"></script>

<main>
  <section class="main-section" ng-app="validationApp" ng-controller="ValidationCtrl" id=toHideWhenDownload >&nbsp;

    <%
      // Get alignment Array with all aligned entities
      JSONArray alignmentArray = (JSONArray) request.getAttribute("alignment");

      // Trying to get ontology loaded using owlapi
      JSONObject sourceOnt = (JSONObject) request.getAttribute("sourceOnt");
      JSONObject targetOnt = (JSONObject) request.getAttribute("targetOnt");

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
      var sourceOnt = <%=sourceOnt%>;
      var targetOnt = <%=targetOnt%>;
    </script>

    <!-- Input to filter mappings table -->
    <label style="margin-left: 1%;">Score range:</label>&nbsp;&nbsp;
    <rzslider rz-slider-model="minRangeSlider.minValue" rz-slider-high="minRangeSlider.maxValue" 
              rz-slider-options="minRangeSlider.options" style="margin-left: 1%; width: 50%"></rzslider>
    <br/><br/>
    <label style="margin-left: 1%;">Search: <input type="search" ng-model="searchText"></label>
    <button id="hideAlignmentsButton" type="button" class="btn btn-sm btn-info" style="margin-left: 1%;" 
            ng-click="hideAlignments($event)">Hide validated alignments</button>
    <br/>
    <label style="margin-left: 1%;">Language:</label>
    <select class="form-control" style="width: 7%;" ng-model="selectedLang" 
            ng-options="k as v for (k, v) in langSelect" ng-init="selectedLang = langSelect['fr']"></select>
    <br/>


    <form action='download' method='post' ng-submit="displayAllRows()">
      <table id=table class="table table-striped">
        <thead>
          <tr>
            <th href="#" ng-click="orderByField = 'index'; reverseSort = !reverseSort">Line</th>
            <th href="#" ng-click="orderByField = 'entity1'; reverseSort = !reverseSort">Source label</th>
            <th href="#" ng-click="orderByField = 'entity2'; reverseSort = !reverseSort">Target label</th>
            <th href="#" ng-click="orderByField = 'relation'; reverseSort = !reverseSort"
                style="width: 11em;">Relation</th>
            <th href="#" ng-click="orderByField = 'measure'; reverseSort = !reverseSort">Score</th>
            <!--th href="#" style="word-wrap: break-word;">Validity</th-->
            <th href="#" style="width: 8em;">Validity</th>
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
              <select id="{{alignment.relation}}" name="relation" class="form-control">
                <option value="http://www.w3.org/2004/02/skos/core#exactMatch">skos:exactMatch</option>
                <option value="http://www.w3.org/2004/02/skos/core#closeMatch">skos:closeMatch</option>
                <option value="http://www.w3.org/2004/02/skos/core#broadMatch">skos:broadMatch</option>
                <option value="http://www.w3.org/2004/02/skos/core#narrowMatch">skos:narrowMatch</option>
              </select>
            </td>
            <td>
              <input type="text" id="{{alignment.measure}}" name="measure" value="{{alignment.measure}}" 
                     style="display: none;" readonly>{{alignment.measure}}</input>
            </td>
            <td>
              <!-- We are changing the color of the select when a valid option is selected -->
              <select id="{{generateValidSelectId(alignment.index)}}" name="valid" 
                      style="{{generateStyleForSelect(alignment)}}" class="form-control" 
                      ng-model="selectValidModel[alignment.index]" ng-click="updateSelectValidModels($event, alignment)"
                      ng-init="selectValidModel[alignment.index] = alignment.valid">
                <option style="background: #FFFFFF;" value="waiting">Waiting...</option>
                <option style="background: #FFFFFF;" value="valid">Valid</option>
                <option style="background: #FFFFFF;" value="notvalid">Not valid</option>
              </select>
            </td>
          </tr>
        </tbody>
      </table>


      <!--div id="pager" class="pager" style="top: 687px; position: absolute;"></div-->
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

    </form>

  </section>


  <!-- Create window at the right of the screen (to display entities details) -->
  <aside>
    <nav class="switch-nav">
      <ul>
        <!-- the 2 glyphicons to choose between list and graph view -->
        <li class="text"><button type="button" class="btn btn-default btn-info" aria-label="Left Align">
            <span class="glyphicon glyphicon-list" aria-hidden="true"></span>
          </button></li>
        <li class="graph"><button type="button" class="btn btn-default btn-lg">
            <span class="glyphicon glyphicon-picture" aria-hidden="true"></span>
          </button></li>
      </ul>
    </nav>

    <div class="entities">
      <section class="entity entity-source">
        <div class="entity-content">
          <div class="entity-inner-content">
            <div class="entity-view entity-text">
              <div id="entityDetail1" class="entityDetailsDiv"> 
              </div>
              <!--h1>fonctions psychosociales globales (fr)</h1>
              <h2>http://chu-rouen.fr/cismef/CIF#b122</h2>
              <dl>
                <dt><abbr title="http://www.w3.org/1999/02/22-rdf-syntax-ns#type">rdf:type</abbr></dt>
                <dd>owl:Class</dd>
                <dt>rdfs:subClassOf</dt>
                <dd>:b110-b139</dd>
                <dt>skos:altLabel</dt>
                <dd>C2371006@fr</dd>
                <dt>skos:definition</dt>
                <dd>Fonctions mentales générales qui se développent au cours de la vie, nécessaires pour comprendre et pour intégrer de manière constructive les fonctions mentales qui président à la formation des aptitudes aux relations sociales réciproques permettant les@fr</dd>
                <dt>skos:prefLabel</dt>
                <dd>fonctions psychosociales globales@fr</dd>
              </dl-->
            </div>
            <div class="entity-view entity-graph">
              <img src="images/placeholder.png" alt="temporaire" />
            </div>
          </div>
        </div>
      </section>


      <section class="entity entity-target">
        <div class="entity-content">
          <div class="entity-inner-content">
            <div class="entity-view entity-text">
              <div id="entityDetail2" class="entityDetailsDiv"> 
              </div>
            </div>
            <div class="entity-view entity-graph">
              <img src="images/placeholder.png" alt="temporaire" />
            </div>
          </div>
        </div>
      </section>
    </div>
  </aside>
  


  <!--div style="position: relative;">
    <div style="width: 20%; max-width: 20%; display: inline-table; position: fixed;
         top: 15%; bottom: 15%; max-height: 70%; height: 70%"> 
      <div id="entityDetail1" class="entityDetailsDiv"> 
      </div>
      <hr/>
      <div id="entityDetail2" class="entityDetailsDiv">
      </div>
    </div>
  </div-->

  <!-------------------------------------------------------------------------------------------------------------------------------------------------------->


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

</main>

<!--script>
  Pour générer la pagination ! (marche pas)
  $(document).ready(function () {
    $("table")
            .tablesorter({widthFixed: true, widgets: ['zebra']})
            .tablesorterPager({container: $("#pager")});
  });
</script-->

<%@include file="footer.jsp"%>