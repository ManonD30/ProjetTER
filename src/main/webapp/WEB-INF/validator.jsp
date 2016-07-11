<%@page import="org.json.simple.JSONObject"%>
<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.apache.http.client.methods.HttpGet"%>
<%@page import="org.apache.http.HttpResponse"%>
<%@page import="org.apache.http.impl.client.HttpClientBuilder"%>
<%@page import="org.apache.http.impl.client.CloseableHttpClient"%>
<%@page import="java.nio.charset.Charset"%>
<%@include file="header.jsp" %>

	<script>
		createCookie(0);
	</script>
	<div class="sideLeft"></div>

	<div class=sideMiddle>

		<h3 class=contentText>Select your ontologies and your alignment.</h3>
		<div class=form>

			<form action="rest/matcher/uploadValidationFiles" method="post"
				enctype="multipart/form-data" name=form
				onsubmit="display_div('btnValidate');">
                          <div style="padding-bottom: 20px">
				<label for=firstFile>Source ontology</label> <br/>
                                <input type="file" id=firstFile name="firstFile" accept=".owl" required /> <br/>
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
    httpResponse = client.execute(new HttpGet(uri));
  } catch (URISyntaxException e) {
  }catch(IOException e){
  }

  // process response
  String recv;
  String ontologiesString = null;
  BufferedReader reader = null;
  try{
    reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), Charset.forName("UTF-8")));
  }catch (IOException e){
    
  }
  
  while ((recv = reader.readLine()) != null) {
    ontologiesString += recv;
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
                          </div>
                          <div style="padding-bottom: 20px">
				<label for=secondFile>Target ontology&nbsp;</label> <br/>
                                <input id=secondFile type="file" name="secondFile" accept=".owl" required />
                          </div>
                          <div style="padding-bottom: 20px">
                                <label for=rdfFile>Alignment &nbsp;</label> <br/>
                                <input id=rdfFile type="file" name="rdfFile" accept=".rdf" required />
                          </div>
				<br> <br> <input class=btn type="submit" value="Upload" />
			</form>

		</div>

		<div class=btnMatch id=btnValidate style="display: none;">
			<form action="validation"
				method="post" name=runMatch
				onsubmit="document.location.href = '#overlay';">

				I'm agreeing to let YAM++ save my files. <input type="radio"
					name="saveOption" value="yes" checked>Yes <input
					type="radio" name="saveOption" value="no">No <br> <br>
				<input class=btn type="submit" value="Validate!" />
			</form>
		</div>

		<div id="overlay">
			<div class="popup_block">
				<img width=300 alt="" src="images/loading-blue.gif">
				<p class=popup_text>Please wait...</p>
			</div>
		</div>

		<hr>

	</div>
	<div class="sideRight"></div>

<%@include file="footer.jsp" %>