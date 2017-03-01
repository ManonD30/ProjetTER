
<%@include file="header.jsp" %>
<script src="https://cdn.rawgit.com/google/code-prettify/master/loader/run_prettify.js"></script>

<% String apikey = "YOUR_APIKEY";
  if (request.getSession().getAttribute("apikey") != null) {
    apikey = request.getSession().getAttribute("apikey").toString();
  }
  String sourceUrl = "https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/mop-iaml.ttl";
  String targetUrl = "https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/mop-diabolo.ttl";
%>

<div class="container theme-showcase" role="main">
  <div class=textMainBody>
    <h1>Using the HTTP API</h1>

    <h3>HTTP GET Request</h3>

    <p>
      You can pass the URL of the ontology  and the API Keywith "apikey", "sourceUrl" and "targetUrl" parameters. 
      If you are logged in, you don't need to use the apikey.
    </p>

    <h4>Using your browser</h4>
    <p>
      <a href="rest/matcher?sourceUrl=<%=sourceUrl%>&targetUrl=<%=targetUrl%>&apikey=<%=apikey%>">
        http://yamplusplus.lirmm.fr/rest/matcher?sourceUrl=<%=sourceUrl%>&targetUrl=<%=targetUrl%>&apikey=<%=apikey%>
      </a>
    </p>
    <br/>

    <h4>Using cURL command</h4>

    <pre class="prettyprint">curl -X GET http://yamplusplus.lirmm.fr/rest/matcher?sourceUrl=<%=sourceUrl%>&targetUrl=<%=targetUrl%>&apikey=<%=apikey%>
curl -X GET http://yamplusplus.lirmm.fr/rest/matcher?sourceUrl=<%=sourceUrl%>&targetUrl=<%=targetUrl%>&apikey=<%=apikey%>
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
  -F sourceFile=@/path/to/source_ontology_file.owl 
  http://yamplusplus.lirmm.fr/rest/matcher?targetUrl=<%=targetUrl%>&apikey=<%=apikey%>'
curl -X POST -H "Content-Type: multipart/form-data" 
  -F sourceFile=@/path/to/source_ont.owl 
  -F targetFile=@/path/to/target_ont.owl 
  -d 'apikey=<%=apikey%>'
  http://yamplusplus.lirmm.fr/rest/matcher
curl -X POST http://localhost:8083/rest/matcher 
  -d 'sourceUrl=<%=sourceUrl%>'
  -d 'targetUrl=<%=targetUrl%>'
  -d 'apikey=<%=apikey%>'
    </pre>

  </div>
</div>

<%@include file="footer.jsp" %>