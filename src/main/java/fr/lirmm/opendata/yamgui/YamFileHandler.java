/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lirmm.opendata.yamgui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

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
    
    public String uploadFile(String ontId, HttpServletRequest request) throws IOException {
      
      String ontologyString = null;
      String sourceUrl = request.getParameter(ontId); // Retrieves <input type="text" name="description">
      Part filePart = null;
      
      if (sourceUrl != null) {
        ontologyString = getUrlContent(sourceUrl);
      } else {
        
        try {
          filePart = request.getPart(ontId); // Retrieves <input type="file" name="file">
        } catch (Exception e) {
          ontologyString = "Could not load provided ontology file";
        }
        if (filePart != null) {
          String fileName = filePart.getSubmittedFileName();
          InputStream fileContent = filePart.getInputStream();
          ontologyString = IOUtils.toString(fileContent, "UTF-8");
        }
      }
      return ontologyString;
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
          contentString += line;
        }
        reader.close();
        return contentString;
    }
}
