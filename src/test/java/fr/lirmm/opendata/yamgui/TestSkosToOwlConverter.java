package fr.lirmm.opendata.yamgui;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//import main.MainProgram;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.skos.SKOSCreationException;

/**
 *
 * @author emonet
 */
public class TestSkosToOwlConverter {

  public TestSkosToOwlConverter() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  // The methods must be annotated with annotation @Test. For example:
  //
  // @Test
  // public void hello() {}
  @Test
  public void testSkosToOwlConverter() throws IOException, ClassNotFoundException, SKOSCreationException, OWLOntologyStorageException {
    FileUtils.forceMkdir(new File("/tmp/yam-gui"));
    String skosRameau = YamFileHandler.convertSkosToOwl(new File("src/test/resources/rameau.ttl"), new File("/tmp/yam-gui/rameau_converted.xml"), "RDF/XML");
    String skosIaml = YamFileHandler.convertSkosToOwl(new File("src/test/resources/iaml.ttl"), new File("/tmp/yam-gui/iaml_converted.xml"), "RDF/XML");
    String skosMimo = YamFileHandler.convertSkosToOwl(new File("src/test/resources/MIMO.xml"), new File("/tmp/yam-gui/mimo_converted.xml"), "RDF/XML");
    OWLOntologyManager owlManager;
    OWLOntology ontology;

    // Test values
    boolean testSubClass = false;
    boolean testRdfsLabel = false;

    // Load the generated OWL ontology
    owlManager = OWLManager.createOWLOntologyManager();
    try {
      ontology = owlManager.loadOntologyFromOntologyDocument(new ByteArrayInputStream(skosRameau.getBytes(StandardCharsets.UTF_8)));
      for (OWLClass cls : ontology.getClassesInSignature()) {
        for (OWLClassExpression subClsExpr : cls.getSubClasses(ontology)) {
          OWLClass subCls = subClsExpr.asOWLClass();
          // Check for a subClass of a Class to see if well created
          if (cls.getIRI().toString().equals("http://data.bnf.fr/ark:/12148/cb16688937f") 
                  && subCls.getIRI().toString().equals("http://data.bnf.fr/ark:/12148/cb16742478j")) {
            testSubClass = true;
          }
        }
        
        for (OWLAnnotation annotation : cls.getAnnotations(ontology)) {
          // Check for a rdfs:label to see if well created
          if (cls.getIRI().toString().equals("http://data.bnf.fr/ark:/12148/cb151095953") 
                  && annotation.getProperty().getIRI().toString().equals("http://www.w3.org/2000/01/rdf-schema#label")
                  && annotation.getValue().toString().equals("\"Violon électrique\"")) {
            testRdfsLabel = true;
          }
        }
      }
    } catch (OWLOntologyCreationException ex) {
      Logger.getLogger(TestSkosToOwlConverter.class.getName()).log(Level.SEVERE, null, ex);
    }

    //MainProgram.match(outputFile1.getAbsolutePath(), outputFile2.getAbsolutePath(), "/tmp/yam-gui/yam_matcher_results.rdf");

    assertTrue(true);
    assertTrue(testSubClass);
    assertTrue(testRdfsLabel);
  }
}
