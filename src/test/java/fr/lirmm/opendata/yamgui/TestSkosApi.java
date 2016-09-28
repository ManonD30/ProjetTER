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
public class TestSkosApi {

  public TestSkosApi() {
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
  public void testSkosApi() throws IOException, ClassNotFoundException, SKOSCreationException, OWLOntologyStorageException {
    
    // First create a new SKOSManager
    //SKOSManager manager = new SKOSManager();
    // use the manager to load a SKOS vocabulary from a URI (either physical or on the web)
    //SKOSDataset iamlDataset = manager.loadDataset(new File("src/test/resources/iaml.ttl").toURI());
    //SKOSDataset mimoDataset = manager.loadDataset(new File("src/test/resources/MIMO.xml").toURI());
    //SKOSDataset mimoDataset = manager.loadDataset(new File("src/test/resources/rameau.ttl").toURI());

    FileUtils.forceMkdir(new File("/tmp/yam2013"));
    File outputFile1 = new File("/tmp/yam2013/yam_test_rameau.owl");
    File outputFile2 = new File("/tmp/yam2013/yam_test_iaml.owl");

    String skosRameau = YamFileHandler.convertSkosToOwl(new File("src/test/resources/rameau.ttl"), new File("/tmp/yam2013/canon.xml"), "RDF/XML");
    String skosIaml = YamFileHandler.convertSkosToOwl(new File("src/test/resources/iaml.ttl"), null, "RDF/XML");
    FileUtils.writeStringToFile(new File("/tmp/yam2013/pascon.xml"), skosRameau);
    OWLOntologyManager owlManager;
    OWLOntology ontology;
    //OWLReasoner reasoner;

    // Load the generated OWL ontology
    owlManager = OWLManager.createOWLOntologyManager();
    try {
      ontology = owlManager.loadOntologyFromOntologyDocument(new ByteArrayInputStream(skosRameau.getBytes(StandardCharsets.UTF_8)));
      //ontology = owlManager.loadOntology(IRI.create(outputFile1.toURI()));
      // Write to file: 
      //owlManager.saveOntology(ontology, new FileOutputStream("/tmp/yam2013/naaaan.owl"));
      for (OWLClass cls : ontology.getClassesInSignature()) {
        System.out.println("Class:");
        System.out.println(cls.getIRI());
        for (OWLClassExpression subClsExpr : cls.getSubClasses(ontology)) {
          OWLClass subCls = subClsExpr.asOWLClass();
          System.out.println("SubClass:");
          System.out.println(subCls.getIRI());
        }
        for (OWLAnnotation annotation : cls.getAnnotations(ontology)) {
          System.out.println("cls property and value:");
          System.out.println(annotation.getProperty());
          System.out.println(annotation.getValue());
        }
      }
      //reasoner = new StructuralReasoner(ontology, new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
    } catch (OWLOntologyCreationException ex) {
      Logger.getLogger(TestSkosApi.class.getName()).log(Level.SEVERE, null, ex);
    }

    //MainProgram.match(outputFile1.getAbsolutePath(), outputFile2.getAbsolutePath(), "/tmp/yam2013/yam_matcher_results.rdf");

    /*
    SKOStoOWLConverter skosConverter = new SKOStoOWLConverter();
    OWLOntology convertedOwlOnto = skosConverter.getAsOWLOntology(mimoDataset);
    OWLOntologyManager owlManager = convertedOwlOnto.getOWLOntologyManager();
    owlManager.saveOntology(convertedOwlOnto, new FileOutputStream("/tmp/yam2013/teeest1.owl"));*/
    assertTrue(true);
  }
}
