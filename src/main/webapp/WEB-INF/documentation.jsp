
<%@include file="header.jsp" %>
<script src="https://cdn.rawgit.com/google/code-prettify/master/loader/run_prettify.js"></script>

<div class="container theme-showcase" role="main">
  <div class=textMainBody>
    <h1>Using the REST API</h1>

    <h3>HTTP GET Request</h3>

    <p>
      You can pass the URL of the ontology with "sourceUrl" and "targetUrl" parameters
    </p>

    <h4>Using your browser</h4>
    <p>
      <a href="rest/matcher?sourceUrl=https://gite.lirmm.fr/opendata/yampp-online/raw/master/src/test/resources/Conference.owl&targetUrl=https://gite.lirmm.fr/opendata/yampp-online/raw/master/src/test/resources/Conference.owl">
        http://yamplusplus.lirmm.fr/rest/matcher?sourceUrl=https://Conference.owl&targetUrl=https://cmt.owl
      </a>
    </p>
    <br/>

    <h4>Using cURL command</h4>

    <pre class="prettyprint">curl -X GET http://yamplusplus.lirmm.fr/rest/matcher?sourceUrl=http://purl.obolibrary.org/obo/po.owl&targetUrl=https://web.archive.org/web/20111213110713/http://www.movieontology.org/2010/01/movieontology.owl
curl -X GET http://yamplusplus.lirmm.fr/rest/matcher?sourceUrl=https://gite.lirmm.fr/opendata/yampp-online/raw/master/src/test/resources/Conference.owl&targetUrl=https://gite.lirmm.fr/opendata/yampp-online/raw/master/src/test/resources/cmt.owl
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
      You can pass ontology files either by uploading file with "sourceFile" and "targetFile".<br/>
      Or you can pass the URL of the ontology with "sourceUrl" and "targetUrl"
    </p>

    <pre class="prettyprint">curl -X POST -H \"Content-Type: multipart/form-data\ 
  -F sourceFile=@/path/to/ontology_file.owl 
  http://yamplusplus.lirmm.fr/rest/matcher?targetUrl=https://gite.lirmm.fr/opendata/yampp-online/raw/master/src/test/resources/Conference.owl'
curl -X POST -H "Content-Type: multipart/form-data" 
  -F targetFile=@/path/to/ont1.owl 
  -F sourceFile=@/path/to/ont2.owl 
  http://yamplusplus.lirmm.fr/rest/matcher
curl -X POST http://localhost:8083/rest/matcher 
  -d 'sourceUrl=https://gite.lirmm.fr/opendata/yampp-online/raw/master/src/test/resources/Conference.owl'
  -d 'targetUrl=https://gite.lirmm.fr/opendata/yampp-online/raw/master/src/test/resources/cmt.owl'
    </pre>

  </div>

</div>

<%@include file="footer.jsp" %>