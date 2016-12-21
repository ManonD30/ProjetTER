<%@include file="header.jsp" %>

<div class="container theme-showcase" role="main">
  <h3 class=contentText>Select your ontologies.</h3>
  <div class=form>
    <form action="result" method="post"
          enctype="multipart/form-data" name=form
          onsubmit="return  validateForm()">

      <button type="button" class="btn btn-sm btn-info" onclick="getExample()">Fill with example</button>
      <br/><br/>

      <div class="alert alert-warning" role="alert">
        <b>SKOS</b> scheme are converted to OWL to be <b>supported</b>, so the semantic may be slightly altered (skos:broader and skos:narrower to owl:subClassOf). <br/>
        <b>OBO</b> format are <b>not supported</b>
      </div>

      <!-- The user can provide ontologies from URL or by uploading a file -->
      <div class="row">
        <div class="col-md-6">
          <label for=firstFile>Source Ontology</label> <br/>
          <input type="url" class='ontologyUrl' id="sourceUrl" name="sourceUrl" placeholder="Enter ontology URL"/>

          <br/><span style="text-align: center">or</span><br/>

          <label class="btn btn-info btn-file">
            Choose file
            <input id=sourceFile type="file" name="sourceFile" accept=".owl, .rdf, .nt, .ttl, .jsonld, .json, .xml" 
                   onchange="refreshFileUpload('sourceFile', 'sourceFilename');" style="display: none;"/>
          </label> <br/>
          <label id="sourceFilename" style="font-weight: normal;"></label>
        </div>
        <div class="col-md-6">
          <label for=secondFile>Target Ontology</label> <br/>
          <input type="url" class='ontologyUrl' id="targetUrl" name="targetUrl" placeholder="Enter ontology URL"/>
          <br/>
          <span style="text-align: center">or</span> <br/>
          <label class="btn btn-info btn-file">
            Choose file
            <input id=targetFile type="file" name="targetFile" accept=".owl, .rdf, .nt, .ttl, .jsonld, .json, .xml" 
                   onchange="refreshFileUpload('targetFile', 'targetFilename');" style="display: none;" />
          </label> <br/>
          <label id="targetFilename" style="font-weight: normal;"></label>
        </div>
      </div>
      <br/>
      <div class="alert alert-info" role="alert">
        Accepting ontology files of following extensions: .owl, .rdf, .nt, .ttl, .jsonld, .json, .xml
      </div>
      <br/>
      <button type="button" class="btn btn-default" onclick="toggleParams()" style="margin-bottom: 2%;">Show matcher parameters</button>
      <div id="paramsDiv" style="display:none;">
        <br/>
        <label style="margin-left: 3%;">Matcher type</label>
        <p>Changing the matcher type can change the matching results. Use the Very Large Scale matcher for ontologies bigger than 4 000 concepts</p>
        <select name="matcherType" id="matcherType" class="form-control"  style="width: auto; display:inline; margin-left: 1%;">
          <option value="VERYLARGE" selected>Very Large Scale (for big ontologies)</option>
          <option value="LARGE">Large Scale</option>
          <option value="SCALABILITY">Scalability versionning (for ontologies containing less than 4000 concepts)</option>
          <option value="SMALL">Small Scale (for ontologies containing less than 500 concepts)</option>
        </select>
        <br/><br/>
        <label>Remove conflicts</label>
        <p>Disabling the removal of conflicts increases number of mappings, but there is more chances to get wrong mappings.</p>
        <div class="checkbox">
          <label><input type="checkbox" name="explicitConflictCheckbox" id="explicitConflictCheckbox" checked>&nbsp;Remove Explicit Disjoint conflicts</label>
        </div>
        <div class="checkbox">
          <label><input type="checkbox" name="relativeConflictCheckbox" id="relativeConflictCheckbox" checked>&nbsp;Remove Relative Disjoint conflicts</label>
        </div>
        <div class="checkbox">
          <label><input type="checkbox" name="crisscrossConflictCheckbox" id="crisscrossConflictCheckbox" checked>&nbsp;Remove Crisscross conflicts</label>
        </div>
        <br/><br/>
        <label><input type="checkbox" name="subLab2subLabCheck" id="subLab2subLabCheck">&nbsp;Match altLabel with altLabel</label>
        <p>By default Yam++ match prefLabel with prefLabel and prefLabel with altLabel</p>
      </div>
      <br/>
      <label style="font-weight: normal;"><input type="checkbox" id=saveFile name="saveFile">I agree to let YAM++ save my ontologies</label>
      <br/><br/>
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
</div>

<script type="text/javascript">
  /**
   * Fill sourceUrl fields with default ontologies from the GitLab opendata repo
   */
  function getExample()
  {
    document.getElementById('sourceUrl').value = "https://gite.lirmm.fr/opendata/yampp-online/raw/master/src/test/resources/Conference.owl";
    document.getElementById('targetUrl').value = "https://gite.lirmm.fr/opendata/yampp-online/raw/master/src/test/resources/cmt.owl";
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
    if (e.style.display == 'block')
      e.style.display = 'none';
    else
      e.style.display = 'block';
  }

  $(document).ready(function () {
    $("paramsBtn").click(function () {
      $("paramsDiv").toggle();
    });
  });
</script>

<%@include file="footer.jsp" %>