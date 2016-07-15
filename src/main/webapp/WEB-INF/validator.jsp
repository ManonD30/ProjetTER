<%@page import="org.json.simple.JSONObject"%>
<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.apache.http.client.methods.HttpGet"%>
<%@page import="org.apache.http.HttpResponse"%>
<%@page import="org.apache.http.impl.client.HttpClientBuilder"%>
<%@page import="org.apache.http.impl.client.CloseableHttpClient"%>
<%@page import="java.nio.charset.Charset"%>
<%@include file="header.jsp" %>

        <div class="sideLeft"></div>

	<div class=sideMiddle>

            <h3 class=contentText>Select your ontologies and your alignment.</h3>
		<div class=form>
			<form action="validator" method="post"
				enctype="multipart/form-data" name=form>
                          
                          <label for=rdfAlignmentFile><a href="http://alignapi.gforge.inria.fr/format.html" target="_blank">AlignmentAPI</a> RDF file</label> <br/>
                                <label class="btn btn-info btn-file">
                                  Choose file
                                  <input id=rdfAlignmentFile type="file" name="rdfAlignmentFile" accept=".rdf" 
                                         onchange="refreshFileUpload('rdfAlignmentFile', 'fileRdfAlignmentFile');" style="display: none;" />
                                </label> <br/>
                                <label id="fileRdfAlignmentFile" style="font-weight: normal;"></label>
                          
                                <div class="row">
                                  <div class="col-md-6">
                                    <label for=sourceUrl1>Ontology 1</label> <br/>
                                    <input type="url" id="sourceUrl1" name="sourceUrl1" placeholder="Enter ontology URL"/>
                                    <br/>
                                    <span style="text-align: center">or</span> <br/>
                                    <label class="btn btn-info btn-file">
                                      Choose file
                                      <input id=ont1 type="file" name="ont1" accept=".owl, .rdf, .nt, .ttl, .jsonld, .json" onchange="refreshFileUpload('ont1','fileOnt1');" style="display: none;"/>
                                    </label> <br/>
                                    <label id="fileOnt1" style="font-weight: normal;"></label>
                                  </div>
                                  <div class="col-md-6">
                                    <label for=sourceUrl2>Ontology 2</label> <br/>
                                    <input type="url" id="sourceUrl2" name="sourceUrl2" placeholder="Enter ontology URL"/>
                                    <br/>
                                    <span style="text-align: center">or</span> <br/>
                                    <label id="labelOnt2" class="btn btn-info btn-file">
                                      Choose file
                                      <input id=ont2 type="file" name="ont2" accept=".owl, .rdf, .nt, .ttl, .jsonld, .json" onchange="refreshFileUpload('ont2','fileOnt2');" style="display: none;" />
                                    </label> <br/>
                                    <label id="fileOnt2" style="font-weight: normal;"></label>
                                  </div>
                                </div>
                                <br/>
                                <div class="alert alert-info" role="alert">
                                  Accepting ontology files of following extensions: .owl, .rdf, .nt, .ttl, .jsonld, .json
                                </div>
                                <br/>
                                <input class=btn type="submit" value="Validate!" />
			</form>
		</div>

		
<%@page import="java.io.*" %>
<%@page import="java.net.*" %>
<%@page import="org.apache.http.client.utils.URIBuilder" %>

<%   
  // TODO: define ApiKey in conf.properties
  // Goal here: getting Stageportal ontologies to let the user choose from them
  String myApiKey = "ffdfa1d6-8db4-4257-a25f-43dd9671063e";
   
  CloseableHttpClient client = HttpClientBuilder.create().build();
  HttpResponse httpResponse = null;
  try{
    URI uri = new URI("http://data.stageportal.lirmm.fr/ontologies?apikey=" + myApiKey);
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

		<hr>

	</div>
	<div class="sideRight"></div>

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        
<%@include file="footer.jsp" %>