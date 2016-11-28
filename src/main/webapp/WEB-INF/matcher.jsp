<%@include file="header.jsp" %>

<div class="container theme-showcase" role="main">
  <h3 class=contentText>Select your ontologies.</h3>
  <div class=form>
    <form action="result" method="post"
          enctype="multipart/form-data" name=form
          onsubmit="document.location.href = '#overlay';">

      <button type="button" class="btn btn-sm btn-info" onclick="getExample()">Fill with example</button>
      <br/><br/>

      <div class="alert alert-warning" role="alert">
        <b>SKOS</b> scheme are <b>supported</b> but <b>OBO</b> format are <b>not</b>
        <br/>
        The version of YAM++ used in this app is <b>not optimized for big ontologies</b>.<br/>
        So it may crash or take a really long time for more than 3000 classes ontologies :D
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

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script type="text/javascript">
            /**
             * Fill sourceUrl fields with default ontologies from the GitLab opendata repo
             */
            function getExample()
            {
              document.getElementById('sourceUrl').value = "https://gite.lirmm.fr/opendata/yampp-online/raw/master/src/test/resources/Conference.owl";
              document.getElementById('targetUrl').value = "https://gite.lirmm.fr/opendata/yampp-online/raw/master/src/test/resources/cmt.owl";
            }
</script>

<%@include file="footer.jsp" %>