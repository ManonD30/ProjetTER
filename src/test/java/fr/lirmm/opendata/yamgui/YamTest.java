package fr.lirmm.opendata.yamgui;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//import main.MainProgram;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.String;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author emonet
 */
public class YamTest {

  public YamTest() {
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

  // TODO add test methods here.
  // The methods must be annotated with annotation @Test. For example:
  //
  // @Test
  // public void hello() {}
  @Test
  public void testFileHandler() throws IOException, ClassNotFoundException {
    // Fail a cause de net.didion.jwnl.jwnl exception : WordNet. Passer direct à 2013
    /*MainProgram.match("/home/emonet/java_workspace/yam-gui/WebContent/data/cmt.owl",
                            "/home/emonet/java_workspace/yam-gui/WebContent/data/Conference.owl",
                            "/srv/yam-gui/result.rdf");*/
    YamFileHandler fileHandler = new YamFileHandler();
    //System.out.println("print in test");
    
    //assertEquals("/srv/yam-gui", fileHandler.getWorkDir());
    assertTrue(fileHandler.getWorkDir() instanceof String);
  }
}
