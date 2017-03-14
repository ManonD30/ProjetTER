package fr.lirmm.yamplusplus.yampponline;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//import main.MainProgram;
import fr.lirmm.yamplusplus.yamppls.YamppOntologyMatcher;
import fr.lirmm.yamplusplus.yampponline.YamFileHandler;
import java.io.File;
import java.io.IOException;
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
public class TestYam {

  public TestYam() {
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

    //assertEquals("lalala", fileHandler.getUrlContent("http://advanse.lirmm.fr:8082/advanse_api/preprocessing/argot?text=lalala"));
  }

  /**
   * mvn -Dtest=TestYam#testRunYam test -Dmaven.test.skip=false
   *
   * @throws IOException
   * @throws ClassNotFoundException
   */
  @Test
  public void testRunYam() throws IOException, ClassNotFoundException, InterruptedException {
    // Fail a cause de net.didion.jwnl.jwnl exception : WordNet. Passer direct à 2013
    /*MainProgram.match("/home/emonet/java_workspace/yam-gui/WebContent/data/cmt.owl",
                            "/home/emonet/java_workspace/yam-gui/WebContent/data/Conference.owl",
                            "/srv/yam-gui/result.rdf");*/
    //YamFileHandler fileHandler = new YamFileHandler();
    //System.out.println("print in test");

    ProcessBuilder pb = new ProcessBuilder("java", "-jar", "/home/emonet/java_workspace/yampp-ls/target/yampp-ls.jar", "-s", "/home/emonet/test_yam/mop-iaml.ttl", "-t", "/home/emonet/test_yam/mop-diabolo.ttl", "-sc", "TEST", 
            "--removeExplicitConflict", "false", "--removeCrisscrossConflict", "false", "--removeRelativeConflict", "false", "--altLabel2altLabel", "true");
    pb.redirectErrorStream(true); // equivalent of 2>&1
    Process p = pb.start();
    p.waitFor();
    
    //java -jar /home/emonet/java_workspace/yampp-ls/target/yampp-ls.jar -s https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/mop-iaml.ttl -t https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/mop-iaml.ttl -sc TEST
    //java -jar /home/emonet/java_workspace/yampp-ls/target/yampp-ls.jar -s /home/emonet/test_yam/mop-diabolo.ttl -t /home/emonet/test_yam/mop-iaml.ttl -sc TEST


    //assertEquals("/srv/yam-gui", fileHandler.getWorkDir());
    //assertTrue(fileHandler.getWorkDir() instanceof String);

    //assertEquals("lalala", fileHandler.getUrlContent("http://advanse.lirmm.fr:8082/advanse_api/preprocessing/argot?text=lalala"));
  }
  
  /**
   * mvn -Dtest=TestYam#testRunMatcher test -Dmaven.test.skip=false
   * 
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws InterruptedException 
   */
  @Test
  public void testRunMatcher() throws IOException, ClassNotFoundException, InterruptedException {
    YamppOntologyMatcher matcher = new YamppOntologyMatcher();
    matcher.setVlsExplicitDisjoint(false);
    matcher.setVlsRelativeDisjoint(false);
    matcher.setVlsCrisscross(false);
    
    matcher.alignOntologies(new File("/home/emonet/test_yam/mop-iaml.ttl").toURI(), new File("/home/emonet/test_yam/mop-diabolo.ttl").toURI());
  }
  
}
