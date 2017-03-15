<%@page import="org.json.simple.JSONObject"%>
<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.apache.http.client.methods.HttpGet"%>
<%@page import="org.apache.http.HttpResponse"%>
<%@page import="org.apache.http.impl.client.HttpClientBuilder"%>
<%@page import="org.apache.http.impl.client.CloseableHttpClient"%>
<%@page import="java.nio.charset.Charset"%>
<%@include file="header.jsp" %>

<div class="container theme-showcase" role="main">

  <% if (request.getSession().getAttribute("apikey") == null) { %>
  <p style="margin: 4%; text-align: center;"><b><a href="sign">Login or signup</a></b> to use Yam++ Online alignment validator</p>
        <% } else { %>

  <h3 class=contentText>Select the alignment to validate and the corresponding ontologies</h3>
  <p style="text-align: center;">
    The Yam++ Online Validator is an interface for human validation of already generated mappings. 
    It takes as an input an alignment in AlignmentAPI RDF format and the corresponding ontologies. 
    Then it displays the concepts details for each mapping to help the user choose the relation between the concepts or unvalidate it. 
    The validated alignment can be stored using one of the available formats.
  </p>

  <div class=form>
    <form action="validator" method="post"
          enctype="multipart/form-data" name=form>

      <label>
        AlignmentAPI RDF file
        <a class="infolink" href="http://alignapi.gforge.inria.fr/format.html" target="_blank"></a>
      </label> <br/>

      <label class="btn btn-info btn-file" title="Accepting alignment files of following extensions: .rdf, .xml">
        Choose file
        <input id=rdfAlignmentFile type="file" name="rdfAlignmentFile" accept=".rdf, .xml" 
               onchange="refreshFileUpload('rdfAlignmentFile', 'fileRdfAlignmentFile');" style="display: none;" />
      </label> <br/>

      <label id="fileRdfAlignmentFile" style="font-weight: normal;"></label>

      <div class="row">
        <%          String acceptFormatTitle = "title='Accepting ontology files of following extensions: .owl, .rdf, .nt, .ttl, .jsonld, .json, .xml'";
          String acceptFormatInput = "accept='.owl, .rdf, .nt, .ttl, .jsonld, .json, .xml'";
        %>
        <div class="col-md-6" style="border-right: 1px solid #ccc;">
          <label for=sourceUrl>Source Ontology</label> <br/>
          <input type="url" class='ontologyUrl' id="sourceUrl" name="sourceUrl" placeholder="Enter ontology URL"/>
          <br/>
          <span style="text-align: center">or</span> <br/>
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
          <label for=targetUrl>Target Ontology</label> <br/>
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
      <input class="btn btnSubmit " type="submit" value="Validate!"/>
    </form>
  </div>

  <% } %>


  <%@page import="java.io.*" %>
  <%@page import="java.net.*" %>
  <%@page import="org.apache.http.client.utils.URIBuilder" %>

  <%
    // TODO:  getting Stageportal / bioportal ontologies to let the user choose from them
    /* define ApiKey in conf.properties
    String myApiKey = "ffdfa1d6-8db4-4257-a25f-43dd9671063e";

    CloseableHttpClient client = HttpClientBuilder.create().build();
    HttpResponse httpResponse = null;
    try {
      URI uri = new URI("http://data.stageportal.lirmm.fr/ontologies?apikey=" + myApiKey);
      // Execute HTTP request
      httpResponse = client.execute(new HttpGet(uri));
    } catch (URISyntaxException e) {
    } catch (IOException e) {
    }

    String responseLine;
    String responseString = null;
    BufferedReader reader = null;
    try {
      // Read HTTP GET response
      reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), Charset.forName("UTF-8")));
    } catch (IOException e) {
    }

    while ((responseLine = reader.readLine()) != null) {
      responseString += responseLine;
    }
    reader.close();
    //JSONObject jsonObject = new JSONObject(ontologiesString);
    //JSONArray jsonArray = new JSONArray(ontologiesString);

    //out.println(ontologiesString);
    /*
     URL requestUrl = new URL("http://data.bioontology.org/?apikey=" + myApiKey);
     //URL requestUrl = new URL("http://data.stageportal.lirmm.fr");
     //URL requestUrl = new URL("http://advanse.lirmm.fr:8082/advanse_api");
   
     //URI buildy = new URIBuilder("http://data.stageportal.lirmm.fr").addParameter("apikey", "1cfae05f-9e67-486f-820b-b393dec5764b").build();
     //URI buildy = new URIBuilder("http://advanse.lirmm.fr:8082/advanse_api").build();
   
     HttpURLConnection httpConn = (HttpURLConnection) (requestUrl).openConnection();
     httpConn.setRequestMethod("GET");
     httpConn.setRequestProperty("Content-Type", "application/json");
     httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36 OPR/38.0.2220.31");
     httpConn.setRequestProperty ("Authorization", "apikey token=" + myApiKey);

      InputStream is;
      if (httpConn.getResponseCode() >= 400) {
          is = httpConn.getErrorStream();
      } else {
          out.println("pas error");
          is = httpConn.getInputStream();
      }
  
    BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
   
   
     /*URL jsonpage = new URL("http://data.stageportal.lirmm.fr/ontologies/?apikey=1cfae05f-9e67-486f-820b-b393dec5764b");
     URLConnection urlcon = jsonpage.openConnection();
     BufferedReader buffread = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));*/
  %>


  <div id="overlay">
    <div class="popup_block">
      <img width=300 alt="" src="images/loading-blue.gif">
      <p class=popup_text>Please wait...</p>
    </div>
  </div>

</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>

<%@include file="footer.jsp" %>