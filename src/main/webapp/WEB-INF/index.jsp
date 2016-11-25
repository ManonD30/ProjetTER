<%@include file="header.jsp" %>

<div class="container theme-showcase" role="main">
  <div class=indexLogoDiv>
    <h2>Online ontology and thesaurus matching, mapping validation 
      and alignment evaluation</h2>
  </div>
  <div class=indexContentDiv>
    <div class=indexContent>
      <br>

      <h1>
        <a href=rest/matcher>Matcher REST API</a>
      </h1>

      <p>A RESTful API to get an alignment between 2 ontologies. 
        You can pass the URL of the ontology with "sourceUrl" and "targetUrl" parameters.<br/>
        For example:
        <a href="rest/matcher?sourceUrl=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/Conference.owl&targetUrl=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/cmt.owl">
          http://yamplusplus.lirmm.fr/rest/matcher?sourceUrl=https://Conference.owl&targetUrl=https://cmt.owl
        </a>
      </p>

      <hr>

      <h1 style='margin-top: 0;'>
        <a href=matcher>Matcher</a>
      </h1>
      <p>Takes as an input ontologies in different formats (owl, skos,
        and various serializations of rdf, such as ttl) and produces
        alignments in a format of the user's choice.</p>

      <hr>

      <h1>
        <a href=validator>Validator</a>
      </h1>

      <p>Takes as an input an alignment in one out of several possible
        formats (alignment, owl, skos), visualises it together with the
        degree of confidence of each mapping and lets the user confirm,
        reject or mark as uncertain each of the produced mappings. The
        modified alignment can be stored in one of the possible alignment
        formats.</p>

      <!--h1>
              <a href=evaluator>Evaluator</a>
      </h1>
  
      <p>Takes as an input an alignment in one out of several possible
              formats, compares it to a reference alignment and outputs the
              corresponding evaluation measures (precision, recall and f-measure)
              together with graphs.</p-->
    </div>
  </div>
</div>

<%@include file="footer.jsp" %>