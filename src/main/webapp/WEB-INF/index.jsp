<%@include file="header.jsp" %>

<%
  String apikey = "YOUR_APIKEY";
    if (request.getSession().getAttribute("apikey") != null) {
      apikey = request.getSession().getAttribute("apikey").toString();
    }
%>

<div class="container theme-showcase" role="main">
  <div class=indexLogoDiv>
    <h2>Online ontology and thesaurus matching, mapping validation 
      and alignment evaluation</h2>
  </div>
  <div class=indexContentDiv>
    <div class=indexContent>
      <br>

      <b>Yam++ Online</b> is a web tool for ontology and thesaurus matching, with an interface for human validation of the generated mappings. 
      <br/>It uses the 
      <!--a href="http://search.maven.org/#artifactdetails%7Cfr.lirmm.yamplusplus%7Cyampp-ls%7C0.1.1%7Cjar"--><b>Yam++ Large Scale library</b><!--/a--> 
      to perform the matching.

      <h1 style='margin-top: 0;'>
        <a href=matcher>Matcher</a>
      </h1>
      <p>
        Takes as an input ontologies in different formats (owl, skos, and various serializations of rdf, 
        such as ttl) and produces alignments in a format of the user's choice.
      </p>
      <hr>

      <h1>
        <a href=validator>Validator</a>
      </h1>
      <p>
        Takes as an input an alignment in AlignmentAPI RDF format, visualises it together with the 
        degree of confidence of each mapping and lets the user confirm, reject or mark as uncertain each 
        of the produced mappings. The modified alignment can be stored in one of the possible alignment formats.
      </p>
      <hr>
      
      <h1>
        <a href=api/matcher>Matcher API</a>
      </h1>
      <p>A <a href="api/matcher">HTTP API</a> to get an alignment between 2 ontologies. 
        You can pass the URL of the ontology with "sourceUrl" and "targetUrl" parameters.<br/>
        <b>Example:</b><br/>
        <a href="api/matcher?sourceUrl=https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/mop-iaml.ttl&targetUrl=https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/mop-diabolo.ttl&crisscrossConflict=false&apikey=<%=apikey%>">
          http://yamplusplus.lirmm.fr/api/matcher?sourceUrl=https://mop-iaml.ttl&targetUrl=https://mop-diabolo.ttl&crisscrossConflict=false&apikey=<%=apikey%>
        </a>
      </p>

    </div>
  </div>
</div>

<%@include file="footer.jsp" %>