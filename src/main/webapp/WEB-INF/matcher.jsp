<%@include file="header.jsp" %>

<div class="container theme-showcase" role="main">
  <% if (request.getSession().getAttribute("apikey") == null) { %>
  <p style="margin: 4%; text-align: center;"><b><a href="sign">Login or signup</a></b> to use Yam++ Online ontology matcher</p>
        <% } else { %>

  <h3 class=contentText>Select the ontologies to match</h3>
  <div class=form>
    <form action="matcherinterface" method="post" enctype="multipart/form-data" name=form
          onsubmit="return  validateForm()">

      <select name="selectExample" id="selectExample" class="form-control"  style="width: auto; display:inline; margin-left: 1%;">
        <option value="empty" selected>Try an example</option>
        <option value="doremusMop">Doremus means of performance (iaml - diabolo)</option>
        <option value="doremusGenre">Doremus Genre (redomi - rameau)</option>
        <option value="anatomy">Anatomy (human - mouse)</option>
        <option value="oaeiFmaNci">OAEI Large Bio (FMA - NCI)</option>
        <option value="oaeiFmaSnomed">OAEI Large Bio (FMA - SNOMED)</option>
        <option value="oaeiNciSnomed">OAEI Large Bio (NCI - SNOMED)</option>
      </select>
      <br/><br/>

      <div class="alert alert-warning" role="alert">
        <b>SKOS</b> scheme are converted to OWL (currently supported by YAM++), so mind that the semantics may be slightly altered (skos:broader and skos:narrower to owl:subClassOf). <br/>
        <b>OBO</b> format is currently  <b>not supported</b>
      </div>

      <!-- The user can provide ontologies from URL or by uploading a file -->
      <div class="row">
        <%
          String acceptFormatTitle = "title='Accepting ontology files of following extensions: .owl, .rdf, .nt, .ttl, .jsonld, .json, .xml, .xsd'";
          String acceptFormatInput = "accept='.owl, .rdf, .nt, .ttl, .jsonld, .json, .xml, .xsd'";
        %>
        <div class="col-md-6" style="border-right: 1px solid #ccc;">

          <h3>Source</h3>

          <!--label for="sourceType">Scheme type</label>
          <select name="sourceType" id="sourceType" class="form-control"  style="display:block; margin-bottom: 5%;">
            <option value="ONTOLOGY" selected>Ontology</option>
            <option value="SCHEME">BETA: Database scheme (xsd)</option>
          </select-->

          <label for=sourceUrl>Source file</label> <br/>
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
          <div class="saveDiv" style="display:block;">
            <hr>
            <label for="sourceName" style="margin: 2% 1%;">Source ontology name:</label>
            <input type="text" id="sourceName" name="sourceName" placeholder="Enter a name for the Source ontology"
                   maxlength="32" pattern="[A-Za-z0-9_-]+" title="Only alphanumeric and - or _" style="width:32ch;" required/><br>
          </div>
        </div>

        <div class="col-md-6">
          <h3>Target</h3>

          <!--label for="targetType">Scheme type</label>
          <select name="targetType" id="targetType" class="form-control"  style="display:block; margin-bottom: 5%;">
            <option value="ONTOLOGY" selected>Ontology</option>
            <option value="SCHEME">BETA: Database scheme (xsd)</option>
          </select-->

          <label for=targetUrl>Target file</label> <br/>
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

          <div class="saveDiv" style="display:block;">
            <hr>
            <label for="targetName" style="margin: 2% 1%;">Target ontology name:</label>
            <input type="text" id="targetName" name="targetName" placeholder="Enter a name for the Target ontology"
                   maxlength="32" pattern="[A-Za-z0-9_-]+" title="Only alphanumeric and - or _" style="width:32ch;" required/>
          </div>

        </div>
      </div>
      <br/>
      <button type="button" id="paramsBtn" class="btn btn-default" onclick="toggleParams()" 
              style="margin-bottom: 3%;">Show matcher parameters</button>

      <div id="paramsDiv" style="display:none;">
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
                  <label><input type="checkbox" name="crisscrossConflict" id="crisscrossConflict">&nbsp;Remove Crisscross conflicts</label>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
      <br/>
      <label style="font-weight: normal;">
        <input type="checkbox" id=saveFile name="saveFile" onchange="toggleSave()" checked>
        I agree to let YAM++ save my ontologies
      </label>

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
   * To fill ontologies URL with examples
   */
  $(function () {
    $('#selectExample').change(function () {
      var i = $('#selectExample').val();
      if (i == "doremusMop") {
        document.getElementById('sourceUrl').value = "https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/mop-iaml.ttl";
        document.getElementById('targetUrl').value = "https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/mop-diabolo.ttl";
      } else if (i == "doremusGenre") {
        document.getElementById('sourceUrl').value = "https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/genre-redomi.ttl";
        document.getElementById('targetUrl').value = "https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/genre-rameau.ttl";
      } else if (i == "anatomy") {
        document.getElementById('sourceUrl').value = "https://gite.lirmm.fr/opendata/yampp-ls/raw/master/src/test/resources/Anatomy/human.owl";
        document.getElementById('targetUrl').value = "https://gite.lirmm.fr/opendata/yampp-ls/raw/master/src/test/resources/Anatomy/mouse.owl";
      } else if (i == "oaeiFmaNci") {
        document.getElementById('sourceUrl').value = "https://gite.lirmm.fr/opendata/yampp-ls/raw/master/src/test/resources/oaei2016/oaei_FMA_whole_ontology.owl";
        document.getElementById('targetUrl').value = "https://gite.lirmm.fr/opendata/yampp-ls/raw/master/src/test/resources/oaei2016/oaei_NCI_whole_ontology.owl";
      } else if (i == "oaeiFmaSnomed") {
        document.getElementById('sourceUrl').value = "https://gite.lirmm.fr/opendata/yampp-ls/raw/master/src/test/resources/oaei2016/oaei_FMA_whole_ontology.owl";
        document.getElementById('targetUrl').value = "https://gite.lirmm.fr/opendata/yampp-ls/raw/master/src/test/resources/oaei2016/oaei_SNOMED_small_overlapping_fma.owl";
      } else if (i == "oaeiNciSnomed") {
        document.getElementById('sourceUrl').value = "https://gite.lirmm.fr/opendata/yampp-ls/raw/master/src/test/resources/oaei2016/oaei_NCI_whole_ontology.owl";
        document.getElementById('targetUrl').value = "https://gite.lirmm.fr/opendata/yampp-ls/raw/master/src/test/resources/oaei2016/oaei_SNOMED_small_overlapping_nci.owl";
      } else {
        document.getElementById('sourceUrl').value = "";
        document.getElementById('targetUrl').value = "";
      }
    });
  });

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
    var elems = document.getElementsByClassName('saveDiv');
    for (var i = 0; i < elems.length; i++)
    {
      if (elems.item(i).style.display == 'block') {
        elems.item(i).style.display = 'none';
        document.getElementById('sourceName').required = false;
        document.getElementById('targetName').required = false;
      } else {
        elems.item(i).style.display = 'block';
        document.getElementById('sourceName').required = true;
        document.getElementById('targetName').required = true;
      }
    }

    /*var e = document.getElementById("saveDiv");
     if (e.style.display == 'block') {
     e.style.display = 'none';
     //document.getElementById("paramsBtn").innerText = "Show matcher parameters";
     } else {
     e.style.display = 'block';
     //document.getElementById("paramsBtn").innerText = "Hide matcher parameters";
     }*/
  }
</script>

<%@include file="footer.jsp" %>