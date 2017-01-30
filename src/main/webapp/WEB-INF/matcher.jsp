<%@include file="header.jsp" %>

<div class="container theme-showcase" role="main">
  <% if (request.getSession().getAttribute("apikey") == null) { %>
  <p style="margin: 4%; text-align: center;"><b><a href="sign">Login or signup</a></b> to use Yam++ Online ontology matcher</p>
        <% } else { %>

  <h3 class=contentText>Select the ontologies to match</h3>
  <div class=form>
    <form action="matcherinterface" method="post" enctype="multipart/form-data" name=form
          onsubmit="return  validateForm()">

      <button type="button" class="btn btn-sm btn-info" onclick="getExample()">Fill with example</button>
      <br/><br/>

      <div class="alert alert-warning" role="alert">
        <b>SKOS</b> scheme are converted to OWL (currently supported by YAM++), so mind that the semantics may be slightly altered (skos:broader and skos:narrower to owl:subClassOf). <br/>
        <b>OBO</b> format is currently  <b>not supported</b>
      </div>

      <!-- The user can provide ontologies from URL or by uploading a file -->
      <div class="row">
        <%
          String acceptFormatTitle = "title='Accepting ontology files of following extensions: .owl, .rdf, .nt, .ttl, .jsonld, .json, .xml'";
          String acceptFormatInput = "accept='.owl, .rdf, .nt, .ttl, .jsonld, .json, .xml'";
        %>
        <div class="col-md-6">
          <label for=firstFile>Source Ontology</label> <br/>
          <input type="url" class='ontologyUrl' id="sourceUrl" name="sourceUrl" placeholder="Enter ontology URL"/>

          <br/><span style="text-align: center">or</span><br/>

          <label class="btn btn-info btn-file" <%=acceptFormatTitle%>>
            Choose file
            <input id=sourceFile type="file" name="sourceFile" <%=acceptFormatInput%> 
                   onchange="refreshFileUpload('sourceFile', 'sourceFilename');" style="display: none;"/>
          </label> 
          <a class="infolink" <%=acceptFormatTitle%> target="_blank"></a>
          <br/>
          <label id="sourceFilename" style="font-weight: normal;"></label>
        </div>
        <div class="col-md-6">
          <label for=secondFile>Target Ontology</label> <br/>
          <input type="url" class='ontologyUrl' id="targetUrl" name="targetUrl" placeholder="Enter ontology URL"/>
          <br/>
          <span style="text-align: center">or</span> <br/>
          <label class="btn btn-info btn-file" <%=acceptFormatTitle%>>
            Choose file
            <input id=targetFile type="file" name="targetFile" <%=acceptFormatInput%> 
                   onchange="refreshFileUpload('targetFile', 'targetFilename');" style="display: none;" />
          </label>
          <a class="infolink" <%=acceptFormatTitle%> target="_blank"></a>
          <br/>
          <label id="targetFilename" style="font-weight: normal;"></label>
        </div>
      </div>
      <br/>
      <button type="button" id="paramsBtn" class="btn btn-default" onclick="toggleParams()" 
              style="margin-bottom: 3%;">Show matcher parameters</button>

      <div id="paramsDiv" style="display:none;">
        <br/>

        <div class="panel panel-primary">
          <div class="panel-heading">
            <h3 class="panel-title" style="font-weight: bold;">Matcher type</h3>
          </div>
          <div class="panel-body">
            <p>Changing the matcher type can change the matching results. Use the Very Large Scale matcher for ontologies bigger than 4 000 concepts</p>
            <select name="matcherType" id="matcherType" class="form-control"  style="width: auto; display:inline; margin-left: 1%;">
              <option value="VERYLARGE" selected>Very Large Scale (for big ontologies)</option>
              <option value="LARGE">Large Scale</option>
              <option value="SCALABILITY">Scalability versionning (for ontologies containing less than 4000 concepts)</option>
              <option value="SMALL">Small Scale (for ontologies containing less than 500 concepts)</option>
            </select>
          </div>
        </div>
        <br/>
        <div id="veryLargeParams" class="row" style="width: 150%; margin-left: -25%;">

          <p>More details on the matcher used and its parameters in 
            <a href="http://www.websemanticsjournal.org/index.php/ps/article/view/483/499">
              Overview of YAM++ - (not) Yet Another Matcher for ontology alignment task</a>.
          </p>

          <div class="col-sm-6">
            <div class="panel panel-success">
              <div class="panel-heading">
                <h3 class="panel-title">Matcher parameters</h3>
              </div>
              <div class="panel-body">
                <label style="cursor: pointer;"><input type="checkbox" name="subLabel2subLabel" id="subLabel2subLabel" checked>&nbsp;Match synonyms to synonyms</label>
                <p>By default Yam++ matches the preferred label (e.g.: skos:prefLabel) to the preferred label, and the synonyms (e.g.: skos:altLabel) to the preferred label of the 2 ontologies. 
                  Enabling this option allows to perform label matching between synonyms and synonyms.</p>
              </div>
            </div>
          </div>

          <div class="col-md-6">
            <div class="panel panel-warning">
              <div class="panel-heading">
                <h3 class="panel-title">Remove conflicts</h3>
              </div>
              <div class="panel-body">
                <p>Enabling the removal of conflicts decreases the number of mappings, but also the likelihood of error (wrong mappings).</p>
                <div class="checkbox">
                  <label><input type="checkbox" name="explicitConflict" id="explicitConflict">&nbsp;Remove Explicit Disjoint conflicts</label>
                </div>
                <div class="checkbox">
                  <label><input type="checkbox" name="relativeConflict" id="relativeConflict">&nbsp;Remove Relative Disjoint conflicts</label>
                </div>
                <div class="checkbox">
                  <label><input type="checkbox" name="crisscrossConflictCheckbox" id="crisscrossConflict">&nbsp;Remove Crisscross conflicts</label>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
      <br/>
      <label style="font-weight: normal;">
        <input type="checkbox" id=saveFile name="saveFile" onchange="toggleSave()">
        I agree to let YAM++ save my ontologies
      </label>
      <br/>
      <div id="saveDiv" style="display:none;">
        <label for="sourceName" style="margin: 2% 1%;">Source ontology name:</label>
        <input type="text" id="sourceName" name="sourceName" placeholder="Enter a name for the Source ontology"
               maxlength="32" pattern="[A-Za-z0-9_-]+" title="Only alphanumeric and - or _" style="width:32ch;"/><br>
        <label for="targetName" style="margin: 2% 1%;">Target ontology name:</label>
        <input type="text" id="targetName" name="targetName" placeholder="Enter a name for the Target ontology"
               maxlength="32" pattern="[A-Za-z0-9_-]+" title="Only alphanumeric and - or _" style="width:32ch;"/>
      </div>
      
      <br/>
      <input class="btn btnSubmit" type="submit" value="Match!"/>
    </form>
  </div>

  <div id="overlay">
    <div class="popup_block">
      <img width=300 alt="" src="images/loading-blue.gif">
      <p class=popup_text>
        Please wait while matching.<br>This can take a while...
      </p>
    </div>
  </div>
  <% }%>
</div>

<script type="text/javascript">
  /**
   * Fill sourceUrl fields with default ontologies from the GitLab opendata repo
   */
  function getExample()
  {
    document.getElementById('sourceUrl').value = "https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/mop-iaml.ttl";
    document.getElementById('targetUrl').value = "https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/mop-diabolo.ttl";
  }

  /**
   * Check if source and target ontologies have been provided if the form
   * @returns {Boolean}
   */
  function validateForm() {
    if (document.forms["form"]["sourceUrl"].value == "" && document.getElementById("sourceFile").files.length == 0) {
      console.log("You must provide a Source ontology");
      alert("You must provide a Source ontology");
      return false;
    }
    if (document.forms["form"]["targetUrl"].value == "" && document.getElementById("targetFile").files.length == 0) {
      console.log("You must provide a Target ontology");
      alert("You must provide a Target ontology");
      return false;
    }
    document.location.href = '#overlay';
  }

  /**
   * To show/hide the matcher params div
   */
  function toggleParams()
  {
    var e = document.getElementById("paramsDiv");
    if (e.style.display == 'block') {
      e.style.display = 'none';
      document.getElementById("paramsBtn").innerText = "Show matcher parameters";
    } else {
      e.style.display = 'block';
      document.getElementById("paramsBtn").innerText = "Hide matcher parameters";
    }
  }
  
  /**
   * To show/hide the save div (for ontologies names)
   */
  function toggleSave()
  {
    var e = document.getElementById("saveDiv");
    if (e.style.display == 'block') {
      e.style.display = 'none';
      //document.getElementById("paramsBtn").innerText = "Show matcher parameters";
    } else {
      e.style.display = 'block';
      //document.getElementById("paramsBtn").innerText = "Hide matcher parameters";
    }
  }

  /**
   * To show/hide the VeryLargeScale matcher params (when selected in dropdown)
   */
  $(function () {
    $('#matcherType').change(function () {
      var i = $('#matcherType').val();
      if (i == "VERYLARGE") {
        $('#veryLargeParams').show();
      } else {
        $('#veryLargeParams').hide();
      }
    });
  });
</script>

<%@include file="footer.jsp" %>