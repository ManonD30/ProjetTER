package fr.lirmm.opendata.yamgui;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//import main.MainProgram;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.skos.SKOSCreationException;

/**
 *
 * @author emonet
 */
public class TestOaeiAlignment {

  public TestOaeiAlignment() {
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
  public void testOaeiAlignment() throws IOException, ClassNotFoundException, SKOSCreationException, OWLOntologyStorageException {
    
    String iamlRameauAlignment = FileUtils.readFileToString(new File("src/test/resources/iaml-rameau.rdf"), "UTF-8");
    
    YamFileHandler fileHandler = new YamFileHandler();
    JSONArray parseOaeiJson = null;
    try {
      parseOaeiJson = fileHandler.parseOaeiAlignmentFormat(iamlRameauAlignment);
    } catch (AlignmentException ex) {
      Logger.getLogger(TestOaeiAlignment.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    System.out.println(parseOaeiJson.toString());
    assertTrue(true);
  }
}
