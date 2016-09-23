/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lirmm.opendata.yamgui;

import com.hp.hpl.jena.rdf.model.LiteralRequiredException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.vocabulary.RDF;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import static fr.lirmm.opendata.yamgui.Result.round;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Cell;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

/**
 *
 * @author emonet
 */
public class YamFileHandler {

  String workDir;

  /**
   * YamFileHandler constructor. It gets the workdir from the conf.properties
   * file
   *
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public YamFileHandler() throws IOException, ClassNotFoundException {
    // Load properties file for work directory
    Properties prop = new Properties();
    prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

    this.workDir = prop.getProperty("workdir");
  }

  /**
   * Read the ontology file or source URL from the request and returns a String.
   * We are using ontNumber to get ontology either from uploaded file "ont1" or
   * URL "sourceUrl1". NOT USED anymore (now we are using the jenaLoadOnto
   * method)
   *
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
   *
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
   * Upload a file taking its ont number in the params (i.e.: 1 for ont1 or 2
   * for ont2) and the HTTP request It download the file if it is an URL or get
   * it from the POST request
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
   * Store the contentString in a file in the working directory in a
   * subdirectory working dir + /data/tmp/ + sub dir auto generated + / +
   * filename (ont1.txt or ont2.txt) Usually the sub directory is randomly
   * generated before calling uploadFile And returns the path to the created
   * file
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
    try {
      URI uri = new URI(sourceUrl);
      httpResponse = client.execute(new HttpGet(uri));
    } catch (IOException e) {
      Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, e);
    } catch (URISyntaxException ex) {
      Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, null, ex);
    }

    // process response
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), Charset.forName("UTF-8")));
    } catch (IOException e) {
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

  /**
   * Take a OAEI AlignmentAPI string and return a JSONArray containing the data
   * of the alignment Format of the array: [{"index": 1, "entity1":
   * "http://entity1.fr", "entity2": "http://entity2.fr", "relation":
   * "skos:exactMatch", "measure": 0.34, }]
   *
   * @param oaeiResult
   * @return
   * @throws AlignmentException
   */
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

  /**
   * Load the ontology number from the request with OWLAPI. Returns a JSONArray
   * with class URI in "id" and all other properties i.e.: { namespaces:
   * {"rdfs": "http://rdfs.org/"}, entities: {"http://entity1.org/": {"id":
   * "http://entity1.org/", "rdfs:label": "test"}}}
   *
   * @param request
   * @param ontNumber
   * @return JSONArray
   * @throws IOException
   * @throws OWLOntologyCreationException
   * @throws ServletException
   */
  public static JSONObject loadOwlapiOntoFromRequest(HttpServletRequest request, String ontNumber) throws IOException, OWLOntologyCreationException, ServletException {
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    OWLOntology ont = null;

    // Load ontology in OWLAPI from the URL or the file
    if (request.getParameter("sourceUrl" + ontNumber) != null && !request.getParameter("sourceUrl" + ontNumber).isEmpty()) {
      ont = manager.loadOntologyFromOntologyDocument(IRI.create(request.getParameter("sourceUrl" + ontNumber)));
    } else if (request.getPart("ont" + ontNumber) != null) {
      Part filePart = request.getPart("ont" + ontNumber); // Retrieves <input type="file" name="file">
      //String fileName = filePart.getSubmittedFileName();
      InputStream fileContent = filePart.getInputStream();
      ont = manager.loadOntologyFromOntologyDocument(fileContent);
    } else {
      return null;
    }

    JSONObject jObject = new JSONObject();
    JSONObject clsJObject = null;
    JSONObject jPrefix = new JSONObject();

    try {
      // Get prefix from ontology using OwlApi
      OWLOntologyFormat format = manager.getOntologyFormat(ont);
      PrefixOWLOntologyFormat prefixFormat = format.asPrefixOWLOntologyFormat();
      java.util.Map<String, String> prefixMap = prefixFormat.getPrefixName2PrefixMap();

      // The prefix used in this ontology
      Set<String> keys = prefixMap.keySet();

      // Iterate over classes
      String ontologyString = "";
      // Iterate over all classes of the ontology
      for (OWLClass cls : ont.getClassesInSignature()) {
        clsJObject = new JSONObject();
        clsJObject.put("id", cls.getIRI().toString());
        String clsLabel = null;

        // Iterate over annotations of the class
        for (Iterator<OWLAnnotationAssertionAxiom> it = cls.getAnnotationAssertionAxioms(ont).iterator(); it.hasNext();) {
          OWLAnnotationAssertionAxiom annotation = it.next();
          String propertyString = annotation.getProperty().getIRI().toString();
          String valueString = annotation.getValue().toString();

          // Get label for skos:prefLabel or rdfs:label
          if (clsLabel == null && propertyString.equals("http://www.w3.org/2004/02/skos/core#prefLabel")) {
            clsLabel = valueString;
          } else if (clsLabel == null && propertyString.equals("http://www.w3.org/2000/01/rdf-schema#label")) {
            clsLabel = valueString;
          }

          // Get the used prefix in the ontology
          for (String key : keys) {
            if (propertyString.contains(prefixMap.get(key))) {
              propertyString = propertyString.replaceAll(prefixMap.get(key), key);
              if (!jPrefix.containsKey(key)) {
                // Add prefix to json objet
                jPrefix.put(key, prefixMap.get(key));
              }
            }
            if (valueString.contains(prefixMap.get(key))) {
              valueString = valueString.replaceAll(prefixMap.get(key), key);
              if (!jPrefix.containsKey(key)) {
                jPrefix.put(key, prefixMap.get(key));
              }
            }
          }
          // Careful : it bugs. With bioportal examples
          clsJObject.put(propertyString, valueString);
        }
        // If no skos:prefLabel or rdfs:label, we get the label from the URI
        if (clsLabel == null) {
          clsLabel = getLabelFromUri(cls.getIRI().toString());
        }
        clsJObject.put("label", clsLabel);
        jObject.put(cls.getIRI().toString(), clsJObject);
      }
    } catch (Exception e) {
      Logger.getLogger(YamFileHandler.class.getName()).log(Level.SEVERE, null, e);
    }

    JSONObject fullJObject = new JSONObject();
    fullJObject.put("namespaces", jPrefix);
    fullJObject.put("entities", jObject);

    return fullJObject;
  }

  /**
   * Load ontology in Jena to get class label and other triples. Returns the
   * following JSON: Returns a JSONArray with class URI in "id" and all other
   * properties i.e.: { namespaces: {"rdfs": "http://rdfs.org/"}, entities:
   * {"http://entity1.org/": {"id": "http://entity1.org/", "rdfs:label":
   * "test"}}} New: { namespaces: {"rdfs": "http://rdfs.org/"}, entities:
   * {"http://entity1.org/": {"id": [{"type": "uri", "value":
   * "http://entity1.org/"}], "http://rdfs.org/label": [{"type": "literal",
   * "fr": "bonjour", "en": "hello"}]}}
   *
   * @param request
   * @param ontNumber
   * @return
   * @throws IOException
   * @throws javax.servlet.ServletException
   */
  public static JSONObject jenaLoadOnto(HttpServletRequest request, String ontNumber) throws IOException, ServletException {
    Model model = ModelFactory.createDefaultModel();

    // Load ontology in JENA from the URL or the file
    if (request.getParameter("sourceUrl" + ontNumber) != null && !request.getParameter("sourceUrl" + ontNumber).isEmpty()) {
      URL url = new URL(request.getParameter("sourceUrl" + ontNumber));
      model.read(url.toString());
    } else if (request.getPart("ont" + ontNumber) != null) {
      // Load ontology from file
      Part filePart = request.getPart("ont" + ontNumber); // Retrieves <input type="file" name="file">
      //String fileName = filePart.getSubmittedFileName();
      try {
        model.read(filePart.getInputStream(), null);
      } catch (Exception e) {
        // Read in TTL if first parsing failed (it waits for RDF/XML)
        model.read(filePart.getInputStream(), null, "TTL");
      }
    } else {
      return null;
    }

    // Get prefix namespaces used in the ontology
    JSONObject jPrefix = new JSONObject();
    Iterator prefixes = model.getNsPrefixMap().entrySet().iterator();
    while (prefixes.hasNext()) {
      Entry thisEntry = (Entry) prefixes.next();
      jPrefix.put(thisEntry.getKey(), thisEntry.getValue());
    }

    JSONObject entitiesJObject = new JSONObject();
    ArrayList<Resource> classTypes = new ArrayList<>();
    // Only check for owl:Class and skos:Concept. Is it interesting to add instances ?
    // Or better to use OWLAPI and SKOS API ?
    classTypes.add(model.getResource("http://www.w3.org/2002/07/owl#Class"));
    classTypes.add(model.getResource("http://www.w3.org/2004/02/skos/core#Concept"));
    // Iterate over resources to get all owl:Class and skos:Concept
    for (Resource classType : classTypes) {
      ResIterator owlClasses = model.listSubjectsWithProperty(RDF.type, classType);
      // get all owl:Class and skos:Concept and add it to the class JSON object
      while (owlClasses.hasNext()) {
        JSONObject clsJObject = new JSONObject();
        Resource cls = owlClasses.next();
        String clsLabel = null;
        if (cls != null) {
          StmtIterator stmts = cls.listProperties();
          clsJObject.put("id", cls.getURI());
          while (stmts.hasNext()) {
            // the iterator returns statements: [subject, predicate, object]
            StatementImpl tripleArray = (StatementImpl) stmts.next();

            // Get label for skos:prefLabel or rdfs:label
            if (clsLabel == null && tripleArray.getPredicate().toString().equals("http://www.w3.org/2004/02/skos/core#prefLabel")) {
              clsLabel = tripleArray.getLiteral().toString();
              //clsLabel = tripleArray.getLiteral().getLexicalForm(); To get without the lang
            } else if (clsLabel == null && tripleArray.getPredicate().toString().equals("http://www.w3.org/2000/01/rdf-schema#label")) {
              clsLabel = tripleArray.getLiteral().toString();
            }

            // Generate a set with prefixes used in this ontology
            java.util.Map<String, String> prefixMap = model.getNsPrefixMap();
            Set<String> prefixKeys = prefixMap.keySet();

            String predicateString = tripleArray.getPredicate().toString();
            //String objectString = tripleArray.getObject().toString();
            String objectString = "nooop";
            String objectType = "No object";
            JSONObject resourceJObject = new JSONObject();
            try {
              objectString = tripleArray.getLiteral().toString();
              objectType = "literal";
              resourceJObject.put("type", objectType);
              resourceJObject.put("value", objectString);
            } catch (LiteralRequiredException e) {
              objectString = tripleArray.getObject().toString();
              objectType = "uri";
              resourceJObject.put("type", objectType);
              resourceJObject.put("value", objectString);
            }

            //Object: predicate: [{"type": "literal", "fr": "bonjour", "en": "hello"}]
            // Add type ? {"type": "literal", "fr": "bonjour", "en": "hello"}
            // Add type ? {"type": "uri", "value": "http://myuri.org/"}
            // Add type ? {"type": "integer", "value": "13"}
            // Add type ? {"type": "float", "value": "13.2"}
            // Add type ? {"type": "date", "value": "13-11-1991"}
            // Replace URI with namespace prefix
            /*
            { namespaces: {"rdfs": "http://rdfs.org/"},
              entities: {"http://entity1.org/": 
                            {"id": [{"type": "uri", "value": "http://entity1.org/"}],
                             "http://rdfs.org/label": [{"type": "literal", "fr": "bonjour", "en": "hello"}]
                             }
                        }
            }
            
             */
            JSONArray objectsJArray = new JSONArray();
            if (clsJObject.containsKey(predicateString)) {
              objectsJArray = (JSONArray) clsJObject.get(predicateString);
            }
            for (String key : prefixKeys) {
              // To replace namespaces by prefix in URI
              if (predicateString.contains(prefixMap.get(key))) {
                predicateString = predicateString.replaceAll(prefixMap.get(key), key + ":");
              }
              if (tripleArray.getObject().isURIResource()) {
                if (objectString.contains(prefixMap.get(key))) {
                  objectString = objectString.replaceAll(prefixMap.get(key), key + ":");
                }
              } else if (tripleArray.getObject().isLiteral()) {
                //objectString = tripleArray.getLiteral().getLexicalForm();
                objectString = tripleArray.getLiteral().toString();
              }
            }
            // Add predicate and object to class JSON object
            //clsJObject.put(predicateString, objectString);
            //clsJObject.put(predicateString, resourceJObject);
            objectsJArray.add(resourceJObject);
            clsJObject.put(predicateString, objectsJArray);
          }
          if (clsLabel == null) {
            getLabelFromUri(cls.getURI());
          }
          clsJObject.put("label", clsLabel);
          entitiesJObject.put(cls.getURI(), clsJObject);

        }
      }
    }

    JSONObject fullJObject = new JSONObject();
    fullJObject.put("namespaces", jPrefix);
    fullJObject.put("entities", entitiesJObject);

    return fullJObject;
  }

  /**
   * Get the label of a class from its URI (taking everything after the last #
   * Or after the last / if # not found
   *
   * @param uri
   * @return String
   */
  public static String getLabelFromUri(String uri) {
    String label = null;
    if (uri != null) {
      if (uri.lastIndexOf("#") != -1) {
        label = uri.substring(uri.lastIndexOf("#") + 1);
      } else {
        label = uri.substring(uri.lastIndexOf("/") + 1);
      }
    }
    return label;
  }

  /**
   * Return the file size in MB
   *
   * @param filepath
   * @return long of file size in MB
   */
  public int getFileSize(String filepath) {
    File file = new File(filepath);
    //long size = file.length();
    Integer size = (int) (long) file.length();
    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
    size = size / 1024;
    // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
    size = size / 1024;
    return size;
  }

  public String getWorkDir() {
    return workDir;
  }
}
