
<%@include file="header.jsp" %>
<script src="https://cdn.rawgit.com/google/code-prettify/master/loader/run_prettify.js"></script>

<div class="sideLeft"></div>

<div class=sideMiddle>

  <div class=publicationPage>
    <h1>Using the REST API</h1>

    <h3>HTTP GET Request</h3>

    <p>
      You can pass the URL of the ontology with "sourceUrl1" and "sourceUrl2" parameters
    </p>

    <h4>Using your browser</h4>
    <p>
    <ul>
      <li>
        <a href="rest/matcher?sourceUrl2=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/Conference.owl&sourceUrl1=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/cmt.owl">
          http://yamplusplus.lirmm.fr/rest/matcher?sourceUrl2=https://Conference.owl&sourceUrl1=https://cmt.owl
        </a>
      </li>
      <li>
        <a href="rest/matcher?sourceUrl2=http://data.bioportal.lirmm.fr/ontologies/MEDLINEPLUS/download?apikey=7b82f0a5-a784-494c-9d2e-cae6698099db&sourceUrl1=http://data.bioportal.lirmm.fr/ontologies/CIF/download?apikey=7b82f0a5-a784-494c-9d2e-cae6698099db">
          http://yamplusplus.lirmm.fr/rest/matcher?sourceUrl2=http://data.bioportal.lirmm.fr/ontologies/MEDLINEPLUS/download&sourceUrl1=http://data.bioportal.lirmm.fr/ontologies/CIF/download
        </a>
      </li>
    </ul>
    </p>
    <br/>

    <h4>Using cURL command</h4>

    <pre class="prettyprint">curl -X GET http://yamplusplus.lirmm.fr/rest/matcher?ont2=http://purl.obolibrary.org/obo/po.owl&ont1=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl
curl -X GET http://yamplusplus.lirmm.fr/rest/matcher?sourceUrl2=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/Conference.owl&sourceUrl1=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/cmt.owl
    </pre>

    <h4>Using Java</h4>

    <pre class="prettyprint">CloseableHttpClient client = HttpClientBuilder.create().build();
  HttpResponse httpResponse = null;
  try{
    URI uri = new URI("http://yamplusplus.lirmm.fr/aboutus");
    // Execute HTTP request
    httpResponse = client.execute(new HttpGet(uri));
  } catch (URISyntaxException e) {
  } catch(IOException e){}

  String responseLine;
  String responseString = null;
  BufferedReader reader = null;
  try{
    // Read HTTP GET response
    reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), Charset.forName("UTF-8")));
  } catch (IOException e){}
  
  while ((responseLine = reader.readLine()) != null) {
    responseString += responseLine;
  }
  reader.close();</pre>

    <hr>
    <h3>HTTP POST Request</h3>

    <h4>Using cURL command</h4>

    <p>
      You can pass ontology files either by uploading file with "ont1" and "ont2".<br/>
      Or you can pass the URL of the ontology with "sourceUrl1" and "sourceUrl2"
    </p>

    <pre class="prettyprint">curl -X POST -H \"Content-Type: multipart/form-data\ 
  -F ont1=@/path/to/ontology_file.owl 
  http://yamplusplus.lirmm.fr/rest/matcher?sourceUrl2=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/Conference.owl'
curl -X POST -H "Content-Type: multipart/form-data" 
  -F ont2=@/path/to/ont1.owl 
  -F ont1=@/path/to/ont2.owl 
  http://yamplusplus.lirmm.fr/rest/matcher
curl -X POST http://localhost:8083/rest/matcher 
  -d 'sourceUrl1=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/Conference.owl'
  -d 'sourceUrl2=https://raw.githubusercontent.com/vemonet/sifr_project_ruby_scripts/master/src/cmt.owl'
    </pre>

  </div>
</div>

<div class="sideRight"></div>

<%@include file="footer.jsp" %>