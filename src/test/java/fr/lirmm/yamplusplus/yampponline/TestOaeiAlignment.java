package fr.lirmm.yamplusplus.yampponline;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//import main.MainProgram;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.xml.sax.SAXException;

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
  public void testOaeiAlignment() throws IOException, ClassNotFoundException, OWLOntologyStorageException, SAXException {

    String iamlRameauAlignment = FileUtils.readFileToString(new File("src/test/resources/iaml-rameau_valid_test.rdf"), "UTF-8");

    boolean testExtractValid = false;
    YamFileHandler fileHandler = new YamFileHandler();
    JSONObject parseOaeiJson = new JSONObject();
    parseOaeiJson = fileHandler.parseOaeiAlignmentFormat(iamlRameauAlignment);
    JSONArray entities = (JSONArray) parseOaeiJson.get("entities");

    // Look for a specific valid mapping to test if parsing worked
    for (int i = 0; i < entities.size(); i++) {
      JSONObject mappingJObject = (JSONObject) entities.get(i);
      if (mappingJObject.get("valid").equals("valid") 
              && mappingJObject.get("entity1").equals("http://iflastandards.info/ns/unimarc/terms/mop/wdb")
              && mappingJObject.get("entity2").equals("http://data.bnf.fr/ark:/12148/cb12270245r")) {
        testExtractValid = true;
        break;
      }
    }
    //System.out.println(parseOaeiJson.toString());
    assertTrue(testExtractValid);
  }
}
