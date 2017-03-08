/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lirmm.yamplusplus.yampponline;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import static fr.lirmm.yamplusplus.yampponline.MatcherInterface.round;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import yamVLS.tools.SystemUtils;

/**
 *
 * @author emonet
 */
//@MultipartConfig // removing it make matching doid - ma.owl working
public class YamFileHandler {

  String workDir;
  String tmpDir = "/tmp/yam-gui/";

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
    // Create the working directory
    FileUtils.forceMkdir(new File(this.workDir));
    FileUtils.forceMkdir(new File(this.tmpDir));
  }

  /**
   * Upload the 2 ontology files from HTTP request using the Streaming API. It
   * downloads the file if it is an URL or get it from the POST request. It
   * stores the contentString in a file in the tmp directory. In a subdirectory
   * /tmp/yam-gui/ + subDir generated + / + filename (source.owl or target.owl).
   * Usually the sub directory is randomly generated before calling uploadFile
   * And return the path to the created file
   *
   * @param subDir
   * @param request
   * @return
   * @throws IOException
   */
  /*public String uploadFiles(String subDir, HttpServletRequest request) throws IOException {

    Logger.getLogger(YamFileHandler.class.getName()).log(Level.INFO, "In uploadFiles");
    if (ServletFileUpload.isMultipartContent(request) == true) {
      try {
        //someone on stack: http://stackoverflow.com/questions/23612381/apache-file-upload-fileitemiterator-is-empty
        //List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        // Parse the request
        FileItemIterator iter = upload.getItemIterator(request);
        Logger.getLogger(YamFileHandler.class.getName()).log(Level.INFO, "Before while");
        while (iter.hasNext()) {
          FileItemStream item = iter.next();
          String name = item.getFieldName();
          Logger.getLogger(YamFileHandler.class.getName()).log(Level.INFO, "In while: " + name);
          InputStream stream = item.openStream();
          // Use commons-io Streams to copy from the inputstrea to a brand-new file: http://stackoverflow.com/questions/6204412/commons-file-upload-not-working-in-servlet
          //Streams.copy(stream, new FileOutputStream(file), true);
          if (item.isFormField()) {
            if (name.equals("sourceFile")) {
              String storagePath = this.tmpDir + subDir + "/source.owl";
              FileUtils.writeStringToFile(new File(storagePath), Streams.asString(stream), "UTF-8");
            } else if (name.equals("targetFile")) {
              String storagePath = this.tmpDir + subDir + "/source.owl";
              FileUtils.writeStringToFile(new File(storagePath), Streams.asString(stream), "UTF-8");
            } else {
              Logger.getLogger(YamFileHandler.class.getName()).log(Level.INFO, "File field {0} with file name {1} detected.", new Object[]{name, item.getName()});
            }
            Logger.getLogger(YamFileHandler.class.getName()).log(Level.INFO, "Uploaded: {0}", name);
          } else { // REMOVE AFTER DEBUG
            Logger.getLogger(YamFileHandler.class.getName()).log(Level.INFO, "Not form field: {0}", name);
          }
        }
      } catch (FileUploadException ex) {
        Logger.getLogger(YamFileHandler.class.getName()).log(Level.SEVERE, "error: uploading file: {0}", ex);
      }
    } else {
      Logger.getLogger(YamFileHandler.class.getName()).log(Level.INFO, "isMultipartContent not true...");
    }

    Logger.getLogger(YamFileHandler.class.getName()).log(Level.INFO, "nothing");

    // Read the file or source URL in the request and returns a String
    /*String ontologyString = getOntFileFromRequest(ontName, request);
    boolean saveFile = false;
    if (request.getParameter("saveFile") != null) {
      saveFile = true;
    }
    String filename = ontName + ".owl";
    // Store the file in the tmp dir: /tmp/yam-gui/subDir/source.owl for example

    if (ontologyString.startsWith("error:")) {
      // Return the error if error when loading file
      return ontologyString;
    }

    String storagePath = this.tmpDir + subDir + "/" + filename;
    FileUtils.writeStringToFile(new File(storagePath), ontologyString, "UTF-8");
    if (request.getParameter("saveFile") != null) {
      // Save file in workdir/save/username/subDir
      FileUtils.writeStringToFile(new File(this.workDir + "/save/" + request.getSession().getAttribute("username") + "/" + subDir + "/" + filename), ontologyString, "UTF-8");
    }*
    return this.tmpDir + subDir;
  }*/
  /**
   * Upload a file from HTTP request. It downloads the file if it is an URL or
   * get it from the POST request. It stores the contentString in a file in the
   * tmp directory. In a subdirectory /tmp/yam-gui/ + subDir generated + / +
   * filename (source.owl or target.owl). Usually the sub directory is randomly
   * generated before calling uploadFile And return the path to the created
   * file. OntName can be "source" or "target"
   *
   * @param ontName
   * @param subDir
   * @param request
   * @return file storage path
   * @throws IOException
   * @throws java.net.URISyntaxException
   * @throws javax.servlet.ServletException
   */
  public String uploadFile(String ontName, String subDir, HttpServletRequest request) throws IOException, URISyntaxException, ServletException {
    // Store given ontology in /tmp/yam-gui/SUBDIR/source.owl
    String storagePath = this.tmpDir + subDir + "/" + ontName + ".owl";

    // Check if an URL have been provided
    String ontologyUrl = request.getParameter(ontName + "Url");

    // Use URL in priority. If not, then get the uploaded file from the form
    if (ontologyUrl != null && !ontologyUrl.isEmpty()) {
      //ontologyString = getUrlContent(ontologyUrl); TO REMOVE
      // Copy file from remote URL
      SystemUtils.copyFileFromURL(new URI(ontologyUrl), storagePath);

    } else {
      // Get file from uploaded file in form
      Part filePart = null;
      Logger.getLogger(Matcher.class.getName()).log(Level.INFO, "Justeeee AVANT request.getPart(fileParam) dans readFileFromRequest");

      // Retrieve file from input where name is sourceFile or targetFile
      filePart = request.getPart(ontName + "File");
      if (filePart != null) {
        //String uploadedFilename = filePart.getSubmittedFileName();
        //storagePath = this.tmpDir + subDir + "/" + uploadedFilename;
        InputStream fileStream = filePart.getInputStream();

        // Write InputStream to file
        File storageFile = new File(storagePath);
        storageFile.getParentFile().mkdirs();

        java.nio.file.Files.copy(
                fileStream,
                storageFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        IOUtils.closeQuietly(fileStream);
      }
    }

    Logger.getLogger(Matcher.class.getName()).log(Level.SEVERE, "End uploadFileee");
    // Store ontology in workDir if asked (/srv/yam-gui/save/field/username)

    return storagePath;
  }

  /**
   * Take a OAEI AlignmentAPI string and use classic XML parser. To return a
   * JSONObject with onto URIs and containing a JSONArray with the data of the
   * alignment Format of the array: {srcOntologyURI: "http://onto1.fr",
   * tarOntologyUri; "http://onto2.fr", entities: [{"index": 1, "entity1":
   * "http://entity1.fr", "entity2": "http://entity2.fr", "relation":
   * "skos:exactMatch", "measure": 0.34, }]} We can't use AlignmentAPI parser
   * because of the "valid" property (trigger error at load
   *
   * @param oaeiResult
   * @return JSONObject
   */
  public JSONObject parseOaeiAlignmentFormat(String oaeiResult) throws SAXException, IOException {
    JSONObject jObjectAlign = new JSONObject();
    JSONObject jObject = null;
    JSONArray jArray = new JSONArray();

    /*<onto1>
    <Ontology rdf:about="http://chu-rouen.fr/cismef/CIF">
      <location>http://chu-rouen.fr/cismef/CIF</location>
    </Ontology>
  </onto1>
  <onto2>
    <Ontology rdf:about="http://chu-rouen.fr/cismef/MedlinePlus">
      <location>http://chu-rouen.fr/cismef/MedlinePlus</location>
    </Ontology>
  </onto2>*/
    // We need to iterate the XML file to add the valid field
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = null;
    try {
      docBuilder = docBuilderFactory.newDocumentBuilder();
    } catch (ParserConfigurationException ex) {
      Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
    }

    docBuilderFactory.setIgnoringComments(true);
    DocumentBuilder builder = null;
    try {
      builder = docBuilderFactory.newDocumentBuilder();
    } catch (ParserConfigurationException ex) {
      Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
    }
    Document doc = null;
    // Read OAEI alignment
    InputSource is = new InputSource(new StringReader(oaeiResult));

    doc = builder.parse(is);

    // Get source and target ontology URI
    Element srcOntoElem = (Element) doc.getElementsByTagName("onto1").item(0);
    jObjectAlign.put("srcOntologyURI", srcOntoElem.getElementsByTagName("Ontology").item(0).getAttributes().getNamedItem("rdf:about").getNodeValue());

    Element tarOntoElem = (Element) doc.getElementsByTagName("onto2").item(0);
    jObjectAlign.put("tarOntologyURI", tarOntoElem.getElementsByTagName("Ontology").item(0).getAttributes().getNamedItem("rdf:about").getNodeValue());

    // Iterate over Cell XML elements to get if valid or not
    int index = 0;
    NodeList nodes = doc.getElementsByTagName("Cell");
    for (int i = 0; i < nodes.getLength(); i++) {
      Element cellElem = (Element) nodes.item(i);

      // Get first node for each field (entities, relation, valid) in the Cell node
      // And add it to the JSON Array
      jObject = new JSONObject();
      jObject.put("index", index);
      jObject.put("entity1", cellElem.getElementsByTagName("entity1").item(0).getAttributes().getNamedItem("rdf:resource").getNodeValue());
      jObject.put("entity2", cellElem.getElementsByTagName("entity2").item(0).getAttributes().getNamedItem("rdf:resource").getNodeValue());
      jObject.put("relation", cellElem.getElementsByTagName("relation").item(0).getTextContent());
      jObject.put("measure", round(Double.parseDouble(cellElem.getElementsByTagName("measure").item(0).getTextContent())));

      index += 1;
      jArray.add(jObject);
    }
    // Put the array of entities in the alignment JSON object
    jObjectAlign.put("entities", jArray);
    return jObjectAlign;
  }

  /**
   * Return a HashMap with 2 keys : to store source and target ArrayList of
   * concept URL. It contains all aligned concepts URL. To retrieve from the
   * ontologies only concepts that are aligned, for big ontologies
   *
   * @param alignmentJson
   * @return HashMap containing 2 ArrayLists
   */
  public static HashMap<String, List<String>> getAlignedConceptsArray(JSONObject alignmentJson) {

    HashMap<String, List<String>> map = new HashMap<String, List<String>>();

    List<String> sourceArray = new ArrayList<String>();
    List<String> targetArray = new ArrayList<String>();

    JSONArray entitiesArray = (JSONArray) alignmentJson.get("entities");
    for (int i = 0; i < entitiesArray.size(); i++) {
      JSONObject entityJson = (JSONObject) entitiesArray.get(i);
      sourceArray.add(entityJson.get("entity1").toString());
      targetArray.add(entityJson.get("entity2").toString());
    }

    map.put("source", sourceArray);
    map.put("target", targetArray);
    return map;
  }

  /**
   * Get the Ontology JSON model for javascript by loading ontology in Jena to
   * get class label and other triples. Get only the aligned concepts if
   * ontologies with more than 30 000 statements, because javascript can't such
   * big ontologies. Returns the following JSON: Returns a JSONArray with class
   * URI in "id" and all other properties i.e.: { namespaces: {"rdfs":
   * "http://rdfs.org/"}, entities: {"http://entity1.org/": {"id":
   * "http://entity1.org/", "label": {"fr": "bonjour", "en": "hello"},
   * "http://rdfs.org/label": [{"type": "literal", "value": "bonjour", "lang":
   * "fr"}, {"type": "literal", "value": "hello", "lang": "en"}]}}, entityCount:
   * 3042}
   *
   * @param model
   * @param alignmentArray
   * @return JSONObject
   * @throws IOException
   * @throws javax.servlet.ServletException
   */
  public static JSONObject getOntoJsonFromJena(Model model, List<String> alignmentArray) throws IOException, ServletException {
    // Get prefix namespaces used in the ontology
    JSONObject jPrefix = new JSONObject();
    Iterator prefixes = model.getNsPrefixMap().entrySet().iterator();
    while (prefixes.hasNext()) {
      Map.Entry thisEntry = (Map.Entry) prefixes.next();
      jPrefix.put(thisEntry.getKey(), thisEntry.getValue());
    }

    Logger.getLogger(Matcher.class.getName()).log(Level.INFO, "Onto Size !!! {0}", model.size());
    int entityCount = 0;

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
        JSONObject clsLabel = new JSONObject();

        entityCount++;

        if (model.size() > 30000 && !alignmentArray.contains(cls.getURI())) {
          // Only get classes that have been aligned for ontologies with more than 30 000 statements
          continue;
        }

        if (cls != null) {
          StmtIterator stmts = cls.listProperties();
          clsJObject.put("id", cls.getURI());
          while (stmts.hasNext()) {
            // the iterator returns statements: [subject, predicate, object]
            StatementImpl tripleArray = (StatementImpl) stmts.next();

            // Generate a set with prefixes used in this ontology
            java.util.Map<String, String> prefixMap = model.getNsPrefixMap();
            Set<String> prefixKeys = prefixMap.keySet();

            String predicateString = tripleArray.getPredicate().toString();
            String prefixedPredicate = getUriWithPrefix(predicateString, prefixMap);

            // Get label for skos:prefLabel or rdfs:label
            if (tripleArray.getPredicate().toString().equals("http://www.w3.org/2004/02/skos/core#prefLabel")) {
              clsLabel.put(tripleArray.getLiteral().getLanguage(), tripleArray.getLiteral().getLexicalForm());
              //clsLabel = tripleArray.getLiteral().getLexicalForm(); To get without the lang
            } else if (tripleArray.getPredicate().toString().equals("http://www.w3.org/2000/01/rdf-schema#label") && !clsLabel.containsKey(tripleArray.getLiteral().getLanguage())) {
              clsLabel.put(tripleArray.getLiteral().getLanguage(), tripleArray.getLiteral().getLexicalForm());
            }

            //String objectString = tripleArray.getObject().toString();
            String objectString = "No object";
            String objectType = "No object";
            JSONObject resourceJObject = new JSONObject();

            if (tripleArray.getObject().isLiteral()) {
              objectString = tripleArray.getLiteral().toString();
              objectType = "literal";
              resourceJObject.put("type", objectType);
              resourceJObject.put("value", objectString);
              resourceJObject.put("lang", tripleArray.getLiteral().getLanguage());
              // We add the prefixed predicates as triple attributes, to avoid having to calculate on the fly in javascript afterwards
              resourceJObject.put("prefixedPredicate", prefixedPredicate);
            } else {
              objectString = tripleArray.getObject().toString();
              objectType = "uri";
              resourceJObject.put("type", objectType);
              resourceJObject.put("value", objectString);
              resourceJObject.put("prefixedPredicate", prefixedPredicate);
            }

            JSONArray objectsJArray = new JSONArray();
            if (clsJObject.containsKey(predicateString)) {
              objectsJArray = (JSONArray) clsJObject.get(predicateString);
            }
            // Add predicate and object to class JSON object
            objectsJArray.add(resourceJObject);
            clsJObject.put(predicateString, objectsJArray);
          }
          if (clsLabel.isEmpty()) {
            clsLabel.put("n/a", getLabelFromUri(cls.getURI()));
          }
          clsJObject.put("label", clsLabel);
          entitiesJObject.put(cls.getURI(), clsJObject);

        }
      }
    }

    JSONObject fullJObject = new JSONObject();
    fullJObject.put("namespaces", jPrefix);
    fullJObject.put("entities", entitiesJObject);
    fullJObject.put("entityCount", entityCount);

    return fullJObject;
  }

  /**
   * Replace the full URI by a prefixed URI. Using namespaces defined in
   * prefixMap
   *
   * @param uri
   * @param prefixMap
   * @return String
   */
  public static String getUriWithPrefix(String uri, java.util.Map<String, String> prefixMap) {
    for (String key : prefixMap.keySet()) {
      // To replace namespaces by prefix in URI
      if (uri.contains(prefixMap.get(key))) {
        uri = uri.replaceAll(prefixMap.get(key), key + ":");
      }
    }
    return uri;
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

  /**
   * Returns the working directory as a String
   *
   * @return workDir String
   */
  public String getWorkDir() {
    return workDir;
  }

  /**
   * Returns the tmp directory as a String
   *
   * @return workDir String
   */
  public String getTmpDir() {
    return tmpDir;
  }

  /**
   * TODO: REMOVE? Done by yampp-ls. Convert Skos Ontology to OWL. We are adding
   * the rdf:type owl:Class to every skos:Concept. And
   * skos:broader/skos:narrower are replaced by rdfs:subClassOf. Also adding the
   * triple <http://my_ontology_URI/> rdf:type owl:Ontology And adding
   * rdfs:label for all skos:prefLabel (not really useful because we handle
   * skos:prefLabel)
   *
   * @param skosFile
   * @param outputFile
   * @param outputFormat
   * @return String
   */
  public static String convertSkosToOwl(File skosFile, File outputFile, String outputFormat) {
    Model model = ModelFactory.createDefaultModel();

    try {
      model.read(skosFile.toURI().toString());
    } catch (Exception e) {
      // Read in TTL if first parsing failed (it waits for RDF/XML)
      model.read(skosFile.toURI().toString(), null, "TTL");
    }

    Property inSchemeProperty = model.getProperty("http://www.w3.org/2004/02/skos/core#inScheme");
    // Add rdf:type owl:Ontology to the namespace URI
    if (model.getNsPrefixURI("") != null) {
      model.createResource(model.getNsPrefixURI("")).addProperty(RDF.type, OWL.Ontology);
    } else if (model.listSubjectsWithProperty(RDF.type, model.getResource("http://www.w3.org/2004/02/skos/core#ConceptScheme")).hasNext()) {
      ResIterator skosSchemeIterator = model.listSubjectsWithProperty(RDF.type, model.getResource("http://www.w3.org/2004/02/skos/core#ConceptScheme"));
      while (skosSchemeIterator.hasNext()) {
        Resource cls = skosSchemeIterator.next();
        if (cls != null) {
          cls.addProperty(RDF.type, OWL.Ontology);
        }
      }
    } else if (model.listSubjectsWithProperty(inSchemeProperty).hasNext()) {
      // If no base namespace, then we try to take it from skos:inScheme
      ResIterator skosInSchemeIterator = model.listSubjectsWithProperty(inSchemeProperty);
      // Iterate over skos:Concept to add the rdf:type owl:Class to all concepts
      while (skosInSchemeIterator.hasNext()) {
        Resource cls = skosInSchemeIterator.next();
        if (cls != null) {
          Statement stmt = cls.getProperty(inSchemeProperty);
          // Add rdf:type owl:Class triple to the 1st inScheme object found
          model.createResource(stmt.getObject().toString()).addProperty(RDF.type, OWL.Ontology);
          break;
        }
      }
    } else {
      // Define a default ontology URI if nothing found
      model.createResource("http://yamplusplus.lirmm.fr/matching_ontology").addProperty(RDF.type, OWL.Ontology);
    }

    //Property hasName = ResourceFactory.createProperty(yourNamespace, "hasName"); // hasName property
    /*Resource owlOntologyResource = model.createResource(RDF.);
    Resource instance2 = model.createResource(instance2Uri);
    <http://ontology.irstea.fr/cropusage/2016/05> rdf:type owl:Ontology ;

    // Create statements
    owlOntologyResource.addProperty(RDF.type, class1); // Classification of instance1*/
    ResIterator skosConceptsIterator = model.listSubjectsWithProperty(RDF.type, model.getResource("http://www.w3.org/2004/02/skos/core#Concept"));
    // Iterate over skos:Concept to add the rdf:type owl:Class to all concepts
    while (skosConceptsIterator.hasNext()) {
      Resource cls = skosConceptsIterator.next();
      if (cls != null) {
        cls.addProperty(RDF.type, OWL.Class);
      }
    }

    ResIterator skosBroaderIterator = model.listSubjectsWithProperty(model.getProperty("http://www.w3.org/2004/02/skos/core#broader"));
    // Iterate over skos:broader properties to add the equivalent with the rdfs:subClassOf property
    while (skosBroaderIterator.hasNext()) {
      List<Resource> broaderResources = new ArrayList();
      List<Resource> narrowerResources = new ArrayList();
      Resource cls = skosBroaderIterator.next();
      if (cls != null) {
        StmtIterator stmts = cls.listProperties();
        while (stmts.hasNext()) {
          // the iterator returns statements: [subject, predicate, object]
          StatementImpl tripleArray = (StatementImpl) stmts.next();
          if (tripleArray.getPredicate().toString().equals("http://www.w3.org/2004/02/skos/core#broader")) {
            broaderResources.add(tripleArray.getResource());
          } else if (tripleArray.getPredicate().toString().equals("http://www.w3.org/2004/02/skos/core#narrower")) {
            narrowerResources.add(tripleArray.getResource());
          }
        }
        for (Resource broaderResource : broaderResources) {
          cls.addProperty(RDFS.subClassOf, broaderResource);
        }
        for (Resource narrowerResource : narrowerResources) {
          narrowerResource.addProperty(RDFS.subClassOf, cls);
        }
      }
    }

    ResIterator skosLabelIterator = model.listSubjectsWithProperty(model.getProperty("http://www.w3.org/2004/02/skos/core#prefLabel"));
    // Iterate over skos:broader properties to add the equivalent with the rdfs:subClassOf property
    /*while (skosLabelIterator.hasNext()) {
      List<Literal> labelResources = new ArrayList();
      Resource cls = skosLabelIterator.next();
      if (cls != null) {
        StmtIterator stmts = cls.listProperties();
        while (stmts.hasNext()) {
          // the iterator returns statements: [subject, predicate, object]
          StatementImpl tripleArray = (StatementImpl) stmts.next();
          if (tripleArray.getPredicate().toString().equals("http://www.w3.org/2004/02/skos/core#prefLabel")) {
            labelResources.add(tripleArray.getLiteral());
          }
        }
        // Add all label resource to rdfs:label
        for (Literal labelResource : labelResources) {
          cls.addProperty(RDFS.label, labelResource);
        }
      }
    }*/
    while (skosLabelIterator.hasNext()) {
      List<String> labelResources = new ArrayList();
      Resource cls = skosLabelIterator.next();
      if (cls != null) {
        StmtIterator stmts = cls.listProperties();
        while (stmts.hasNext()) {
          // the iterator returns statements: [subject, predicate, object]
          StatementImpl tripleArray = (StatementImpl) stmts.next();
          if (tripleArray.getPredicate().toString().equals("http://www.w3.org/2004/02/skos/core#prefLabel")) {
            labelResources.add(tripleArray.getString());
          }
        }
        // Add all label resource to rdfs:label
        for (String labelResource : labelResources) {
          cls.addProperty(RDFS.label, labelResource);
        }
      }
    }

    String owlOntologyString = null;
    try {
      StringWriter out = new StringWriter();
      model.write(out, outputFormat);
      owlOntologyString = out.toString();
      if (outputFile != null) {
        model.write(new FileOutputStream(outputFile), outputFormat);
      }
    } catch (FileNotFoundException ex) {
      Logger.getLogger(YamFileHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
    return owlOntologyString;
  }

  /**
   * NOT USED anymore. Load the ontology number from the request with OWLAPI.
   * Returns a JSONArray with class URI in "id" and all other properties i.e.: {
   * namespaces: {"rdfs": "http://rdfs.org/"}, entities: {"http://entity1.org/":
   * {"id": "http://entity1.org/", "rdfs:label": "test"}}}
   *
   * @param request
   * @param ontName
   * @return JSONArray
   * @throws IOException
   * @throws OWLOntologyCreationException
   * @throws ServletException
   */
  /*public static JSONObject loadOwlapiOntoFromRequest(HttpServletRequest request, String ontName) throws IOException, OWLOntologyCreationException, ServletException {
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    OWLOntology ont = null;

    // Load ontology in OWLAPI from the URL or the file
    if (request.getParameter(ontName + "Url") != null && !request.getParameter(ontName + "Url").isEmpty()) {
      ont = manager.loadOntologyFromOntologyDocument(IRI.create(request.getParameter(ontName + "Url")));
    } else if (request.getPart(ontName + "File") != null) {
      Part filePart = request.getPart(ontName + "File"); // Retrieves <input type="file" name="file">
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
  }*/
}
