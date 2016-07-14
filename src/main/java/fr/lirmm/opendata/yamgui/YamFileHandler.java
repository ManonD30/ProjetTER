/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lirmm.opendata.yamgui;

import fr.inrialpes.exmo.align.parser.AlignmentParser;
import static fr.lirmm.opendata.yamgui.Result.round;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Cell;

/**
 *
 * @author emonet
 */
public class YamFileHandler {
  
    String workDir;

    public YamFileHandler() throws IOException, ClassNotFoundException {
        // Load properties file for work directory
        Properties prop = new Properties();
        prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));
        
        this.workDir = prop.getProperty("workdir");
    }
    
    /**
     * Read the ontology file or source URL from the request and returns a String.
     * We are using ontNumber to get ontology either from uploaded file "ont1" or URL "sourceUrl1"
     * @param ontNumber
     * @param request
     * @return
     * @throws IOException 
     */
    public String getOntFileFromRequest(String ontNumber, HttpServletRequest request) throws IOException {
      String ontologyString = null;
      String sourceUrl = request.getParameter("sourceUrl" + ontNumber);
      
      // Take sourceUrl in priority. If not, then get the uploaded file
      if (sourceUrl != null && !sourceUrl.isEmpty()) {
        ontologyString = getUrlContent(sourceUrl);
      } else {
        // ontology name are "ont" + ontology number (ont1 or ont2)
        ontologyString = readFileFromRequest("ont" + ontNumber, request);
      }
      return ontologyString;
    }
    
    /**
     * Read the file passed in params in the HTTP request
     * @param fileParam
     * @param request
     * @return
     * @throws IOException 
     */
    public String readFileFromRequest(String fileParam, HttpServletRequest request) throws IOException {
      String fileString = null;
      Part filePart = null;
      String filename = null;
      
      try {
        filePart = request.getPart(fileParam); // Retrieves <input type="file" name="file">
      } catch (Exception e) {
        fileString = "Could not load provided ontology file";
      }
      if (filePart != null) {
        filename = filePart.getSubmittedFileName();
        InputStream fileContent = filePart.getInputStream();
        fileString = IOUtils.toString(fileContent, "UTF-8");
      }
      return fileString;
    }
    
    /**
     * Upload a file taking its ont number in the params (i.e.: 1 for ont1 or 2 for ont2) and the HTTP request
     * It download the file if it is an URL or get it from the POST request
     * 
     * @param ontNumber
     * @param subDir
     * @param request
     * @return
     * @throws IOException 
     */
    public String uploadFile(String ontNumber, String subDir, HttpServletRequest request) throws IOException {
      
      // Read the file or source URL in the request and returns a String
      String ontologyString = getOntFileFromRequest(ontNumber, request);
      boolean saveFile = false;
      if (request.getParameter("saveFile") != null) {
        saveFile = true;
      }
      
      // Store the ontology String in the generated subDir and return file path
      String storagePath = storeFile("ont" + ontNumber + ".owl", subDir, ontologyString, saveFile);
      
      return storagePath;
    }
    
    
    /**
     * Store the contentString in a file in the working directory in a subdirectory
     * working dir + /data/tmp/ + sub dir auto generated + / + filename (ont1.txt or ont2.txt)
     * Usually the sub directory is randomly generated before calling uploadFile
     * And returns the path to the created file
     * 
     * @param filename
     * @param subDir
     * @param contentString
     * @param saveFile
     * @return 
     */
    public String storeFile(String filename, String subDir, String contentString, boolean saveFile) 
            throws FileNotFoundException, UnsupportedEncodingException, IOException {
      // Generate file storage name: /$WORKING_DIR/ontologies/MYRANDOMID/ont1.txt for example
      String storagePath = this.workDir + "/data/tmp/" + subDir + "/" + filename;
      FileUtils.writeStringToFile(new File(storagePath), contentString);
      if (saveFile == true) {
        FileUtils.writeStringToFile(new File(this.workDir + "/data/save/" + subDir + "/" + filename), contentString);
      }
      return storagePath;
    }
    
    
    /**
     * Get the content of a URL page (to get ontologies from the URL)
     * 
     * @param sourceUrl
     * @return
     * @throws IOException 
     */
    public String getUrlContent(String sourceUrl) throws IOException {
      CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpResponse httpResponse = null;
        try{
          URI uri = new URI(sourceUrl);
          httpResponse = client.execute(new HttpGet(uri));
        }catch(IOException e){
          Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, e);
        } catch (URISyntaxException ex) {
          Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, ex);
        }

        // process response
        BufferedReader reader = null;
        try{
          reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), Charset.forName("UTF-8")));
        } catch (IOException e){
          Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, e);
        }

        String contentString = "";
        String line;
        while ((line = reader.readLine()) != null) {
          contentString += line + "\n";
        }
        reader.close();
        return contentString;
    }
    
    public JSONArray parseOaeiAlignmentFormat(String oaeiResult) throws AlignmentException {
      AlignmentParser aparser = new AlignmentParser(0);
      // rdf file
      Alignment file = aparser.parseString(oaeiResult);
      
      JSONObject jObject = null;
      JSONArray jArray = new JSONArray();
      
      ArrayList<Map> liste = new ArrayList<>();

      // cell iterator
      Iterator<Cell> align = file.iterator();
      // clear the list
      liste.clear();
      
      int index = 1;
      // add all iteration to the list
      while (align.hasNext()) {
              // new Map which will contain a cell
              Map mapping = new Map();
              Cell cell = align.next();
              
              jObject = new JSONObject();
              jObject.put("index", index);
              jObject.put("entity1", cell.getObject1().toString());
              jObject.put("entity2", cell.getObject2().toString());
              jObject.put("relation", cell.getRelation().getRelation().toString());
              jObject.put("measure", round(cell.getStrength()));
              index += 1;
              jArray.add(jObject);
      }
      return jArray;
    }
    
    public String getWorkDir() {
      return workDir;
    }
}
