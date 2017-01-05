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
        <b>SKOS</b> scheme are converted to OWL (currently supported by YAM++), so mind that the semantics may be slightly altered (skos:broader and skos:narrower to owl:subClassOf). <br/>
        <b>OBO</b> format are currently  <b>not supported</b>
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
          </label> <br/>
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
          </label> <br/>
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

          <div class="col-sm-6">
            <div class="panel panel-success">
              <div class="panel-heading">
                <h3 class="panel-title">Matcher parameters</h3>
              </div>
              <div class="panel-body">
                <label style="cursor: pointer;"><input type="checkbox" name="subLabel2subLabel" id="subLabel2subLabel" checked>&nbsp;Match skos:altLabel to skos:altLabel</label>
                <p>By default Yam++ matches skos:prefLabel to skos:prefLabel and skos:prefLabel to skos:altLabel. Enabling this option allows to perform label matching between skos:altLabel and skos:altLabel.</p>
                <hr/><br/>
                <label for="labelSimWeight">Label similarity informative word weight threshold:</label>
                <input id="labelSimWeight" name="labelSimWeight" type="number" step="0.01" min="0" max="1" value="0.34">
                <p>YAM++ relies on the notion of word informativeness in order to assign weights to tokens. 
                  If two labels differ by a highly informative word in the label similarity score computation (if the weight of the word that differ is superior to this threshold), 
                  the mapping is considered as wrong, even if the similarity score is high. 
                  Higher threshold could potentially increase the number of mappings (since it makes it harder to get to the threshold to be labeled as an informative word).</p>
              </div>
            </div>
          </div><!-- /.col-sm-6 -->

          <div class="col-md-6">
            <div class="panel panel-warning">
              <div class="panel-heading">
                <h3 class="panel-title">Remove conflicts</h3>
              </div>
              <div class="panel-body">
                <p>Disabling the removal of conflicts increases the number of mappings, but also the likelihood of error (wrong mappings).</p>
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
          </div><!-- /.col-sm-6 -->

        </div>
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