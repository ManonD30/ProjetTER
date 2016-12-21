package fr.lirmm.yamplusplus.yampponline;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Evaluator;

import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.parser.AlignmentParser;

public class Evaluation extends HttpServlet {

  private static final long serialVersionUID = 1L;

  // servlet's doPost which run YAM++ and redirect to the .JSP
  public void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {

    // get the key in the cookie
    String key = null;
    Cookie[] cookies = request.getCookies();
    for (int i = 0; i < cookies.length; i++) {
      Cookie ck = cookies[i];
      if (ck.getName().equals("key")) {
        key = cookies[i].getValue();
      }
    }

    try {

      // add FMTab to response
      request.setAttribute("FMTab", (getFMTab(key)));

      // add PrTab to response
      request.setAttribute("PrTab", getPrTab(key));

      // add RecTab to response
      request.setAttribute("RecTab", getRecTab(key));

      // add FM to response
      request.setAttribute("FM", round(getFm(key)));

      // add Pr to response
      request.setAttribute("Pr", round(getPr(key)));

      // add Rec to response
      request.setAttribute("Rec", round(getRec(key)));

    } catch (AlignmentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // send response
    this.getServletContext()
            .getRequestDispatcher("/WEB-INF/evaluation.jsp")
            .forward(request, response);

  }

  // round a double to 2 decimal places
  public static double round(double value) {
    long factor = (long) Math.pow(10, 2);
    value = value * factor;
    long tmp = Math.round(value);
    return (double) tmp / factor;
  }

  //get F measure
  private double getFm(String key) throws AlignmentException, IOException {
    // Load properties file for work directory
    Properties prop = new Properties();
    prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

    AlignmentParser aparser = new AlignmentParser(0);
    Alignment reference = aparser.parse(new File(
            prop.getProperty("workdir") + "/ontologies/reference" + key + ".rdf").toURI());
    Alignment a1 = aparser.parse(new File(prop.getProperty("workdir") + "/ontologies/alignment"
            + key + ".rdf").toURI());
    Evaluator evaluator2 = new PRecEvaluator(reference, a1);
    Properties p1 = new Properties();
    evaluator2.eval(p1);
    double Fm = ((PRecEvaluator) evaluator2).getFmeasure();
    return (Fm);
  }

  //get precision
  private double getPr(String key) throws AlignmentException, IOException {
    // Load properties file for work directory
    Properties prop = new Properties();
    prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

    AlignmentParser aparser = new AlignmentParser(0);
    Alignment reference = aparser.parse(new File(
            prop.getProperty("workdir") + "/ontologies/reference" + key + ".rdf").toURI());
    Alignment a1 = aparser.parse(new File(prop.getProperty("workdir") + "/ontologies/alignment"
            + key + ".rdf").toURI());
    Evaluator evaluator2 = new PRecEvaluator(reference, a1);
    Properties p1 = new Properties();
    evaluator2.eval(p1);
    double Pr = ((PRecEvaluator) evaluator2).getPrecision();
    return (Pr);
  }

  //get recall
  private double getRec(String key) throws AlignmentException, IOException {
    // Load properties file for work directory
    Properties prop = new Properties();
    prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

    AlignmentParser aparser = new AlignmentParser(0);
    Alignment reference = aparser.parse(new File(
            prop.getProperty("workdir") + "/ontologies/reference" + key + ".rdf").toURI());
    Alignment a1 = aparser.parse(new File(prop.getProperty("workdir") + "/ontologies/alignment"
            + key + ".rdf").toURI());
    Evaluator evaluator2 = new PRecEvaluator(reference, a1);
    Properties p1 = new Properties();
    evaluator2.eval(p1);
    double Rec = ((PRecEvaluator) evaluator2).getRecall();
    return (Rec);
  }

  //get f measure table
  private double[] getFMTab(String key) throws AlignmentException, IOException {
    // Load properties file for work directory
    Properties prop = new Properties();
    prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

    AlignmentParser aparser = new AlignmentParser(0);
    Alignment reference = aparser.parse(new File(
            prop.getProperty("workdir") + "/ontologies/reference" + key + ".rdf").toURI());
    Alignment a1 = aparser.parse(new File(prop.getProperty("workdir") + "/ontologies/alignment"
            + key + ".rdf").toURI());
    Evaluator evaluator2 = new PRecEvaluator(reference, a1);
    Properties p1 = new Properties();
    evaluator2.eval(p1);
    double[] FMTab = new double[21];
    Properties p = new Properties();
    for (int i = 0; i <= 20; i += 1) {
      a1.cut(((double) i) / 20);
      Evaluator evaluator = new PRecEvaluator(reference, a1);
      evaluator.eval(p);
      FMTab[i] = round(((PRecEvaluator) evaluator).getFmeasure());
    }
    return (FMTab);
  }

  //get precision table
  private double[] getPrTab(String key) throws AlignmentException, IOException {
    // Load properties file for work directory
    Properties prop = new Properties();
    prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

    AlignmentParser aparser = new AlignmentParser(0);
    Alignment reference = aparser.parse(new File(
            prop.getProperty("workdir") + "/ontologies/reference" + key + ".rdf").toURI());
    Alignment a1 = aparser.parse(new File(prop.getProperty("workdir") + "/ontologies/alignment"
            + key + ".rdf").toURI());
    Evaluator evaluator2 = new PRecEvaluator(reference, a1);
    Properties p1 = new Properties();
    evaluator2.eval(p1);
    double[] PrTab = new double[21];
    Properties p = new Properties();
    for (int i = 0; i <= 20; i += 1) {
      a1.cut(((double) i) / 20);
      Evaluator evaluator = new PRecEvaluator(reference, a1);
      evaluator.eval(p);
      PrTab[i] = round(((PRecEvaluator) evaluator).getPrecision());
    }
    return (PrTab);
  }

  //get recall tab
  private double[] getRecTab(String key) throws AlignmentException, IOException {
    // Load properties file for work directory
    Properties prop = new Properties();
    prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

    AlignmentParser aparser = new AlignmentParser(0);
    Alignment reference = aparser.parse(new File(
            prop.getProperty("workdir") + "/ontologies/reference" + key + ".rdf").toURI());
    Alignment a1 = aparser.parse(new File(prop.getProperty("workdir") + "/ontologies/alignment"
            + key + ".rdf").toURI());
    Evaluator evaluator2 = new PRecEvaluator(reference, a1);
    Properties p1 = new Properties();
    evaluator2.eval(p1);
    double[] RecTab = new double[21];
    Properties p = new Properties();
    for (int i = 0; i <= 20; i += 1) {
      a1.cut(((double) i) / 20);
      Evaluator evaluator = new PRecEvaluator(reference, a1);
      evaluator.eval(p);
      RecTab[i] = round(((PRecEvaluator) evaluator).getRecall());
    }
    return (RecTab);
  }
}
