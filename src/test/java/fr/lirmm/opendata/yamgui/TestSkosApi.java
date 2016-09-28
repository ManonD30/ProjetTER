package fr.lirmm.opendata.yamgui;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//import main.MainProgram;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mainyam.MainProgram;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.skos.SKOSCreationException;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skosapibinding.SKOSManager;

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
    //System.out.println("print in test");

    // First create a new SKOSManager
    SKOSManager manager = new SKOSManager();

    // use the manager to load a SKOS vocabulary from a URI (either physical or on the web)
    SKOSDataset iamlDataset = manager.loadDataset(new File("src/test/resources/iaml.ttl").toURI());
    //SKOSDataset mimoDataset = manager.loadDataset(new File("src/test/resources/MIMO.xml").toURI());
    SKOSDataset mimoDataset = manager.loadDataset(new File("src/test/resources/rameau.ttl").toURI());
    
    File outputFile1 = new File("/tmp/yam2013/teeest1.owl");
    File outputFile2 = new File("/tmp/yam2013/teeest2.owl");

    YamFileHandler.convertSkosToOwl(new File("src/test/resources/rameau.ttl"), outputFile1, "RDF/XML");
    YamFileHandler.convertSkosToOwl(new File("src/test/resources/iaml.ttl"), outputFile2, "RDF/XML");

    OWLOntologyManager owlManager;
    OWLOntology ontology;
    //OWLReasoner reasoner;

    // Load the generated OWL ontology
    owlManager = OWLManager.createOWLOntologyManager();
    try {
      ontology = owlManager.loadOntology(IRI.create(outputFile1.toURI()));
      owlManager.saveOntology(ontology, new FileOutputStream("/tmp/yam2013/naaaan.owl"));
      //reasoner = new StructuralReasoner(ontology, new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
    } catch (OWLOntologyCreationException ex) {
      Logger.getLogger(TestSkosApi.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    MainProgram.match(outputFile1.getAbsolutePath(), outputFile2.getAbsolutePath(), "/tmp/yam2013/yam_matcher_results.rdf");

    /*
    SKOStoOWLConverter skosConverter = new SKOStoOWLConverter();
    OWLOntology convertedOwlOnto = skosConverter.getAsOWLOntology(mimoDataset);
    OWLOntologyManager owlManager = convertedOwlOnto.getOWLOntologyManager();
    owlManager.saveOntology(convertedOwlOnto, new FileOutputStream("/tmp/yam2013/teeest1.owl"));*/
    assertTrue(true);
  }
}
