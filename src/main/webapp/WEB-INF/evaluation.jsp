<%@page import="java.util.ArrayList"%>
<%@ page pageEncoding="UTF-8"%>

<%@include file="header.jsp" %>

<script src="https://www.amcharts.com/lib/3/amcharts.js"></script>
<script src="https://www.amcharts.com/lib/3/serial.js"></script>
<script src="https://www.amcharts.com/lib/3/themes/light.js"></script>
<script src="https://www.amcharts.com/lib/3/plugins/export/export.js"></script>
<link href="https://www.amcharts.com/lib/3/plugins/export/export.css"
      media="all" rel="stylesheet" type="text/css" />

<%
  //get values and tabs from response

  //f measure, precision, recall
  String Fm = String.valueOf(request.getAttribute("FM"));
  String Pr = String.valueOf(request.getAttribute("Pr"));
  String Rec = String.valueOf(request.getAttribute("Rec"));
  //f measure tab
  double[] FMTab = (double[]) request.getAttribute("FMTab");
  //precision tab
  double[] PrTab = (double[]) request.getAttribute("PrTab");
  //recall tab
  double[] RecTab = (double[]) request.getAttribute("RecTab");
%>

<div class=valueDiv>
  <%
    out.print("<p class=value>F Measure = " + Fm + "</p>");
    out.print("<p class=value> Precision = " + Pr + "</p>");
    out.print("<p class=value> Recall = " + Rec + "</p>");
  %>
</div>

<div class=chartdiv>
  <h2>F Measure</h2>
  <div id="chartFM" class=chart></div>
  <hr>
  <h2>Precision</h2>
  <div id="chartPr" class=chart></div>
  <hr>
  <h2>Recall</h2>
  <div id="chartRec" class=chart></div>
</div>

<script>
  var chartDataFM = [];
  var chartDataPr = [];
  var chartDataRec = [];

  //maximum values to display in graph	
  var maxFM = 0;
  var maxPr = 0;
  var maxRec = 0;

  <%
    double[] threshold = {0, 0.5, 0.1, 0.15, 0.20, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95, 1};

    //maximum values to display in graph	
    double maxFM = 0;
    double maxPr = 0;
    double maxRec = 0;

    for (int i = 0; i < FMTab.length; i++) {
      out.println("var point" + i + " = {'Threshold':" + threshold[i]
              + ",'FMeasure':" + FMTab[i] + "};");
      out.println("chartDataFM.push(point" + i + ");");
      if (FMTab[i] > maxFM) {
        maxFM = FMTab[i];
        out.println("maxFM=" + maxFM); //JS max = java max
      }
    }

    for (int i = 0; i < PrTab.length; i++) {
      out.println("var point" + i + " = {'Threshold':" + threshold[i]
              + ",'Precision':" + PrTab[i] + "};");
      out.println("chartDataPr.push(point" + i + ");");
      if (PrTab[i] > maxPr) {
        maxPr = PrTab[i];
        out.println("maxPr=" + maxPr); //JS max = java max
      }
    }

    for (int i = 0; i < RecTab.length; i++) {
      out.println("var point" + i + " = {'Threshold':" + threshold[i]
              + ",'Recall':" + RecTab[i] + "};");
      out.println("chartDataRec.push(point" + i + ");");
      if (RecTab[i] > maxRec) {
        maxRec = RecTab[i];
          out.println("maxRec=" + maxRec); //JS max = java max
      }
    }%>

  //display F Measure chart
  var chart = AmCharts.makeChart("chartFM", {
    "type": "serial",
    "theme": "light",
    "dataProvider": chartDataFM,
    "valueAxes": [{
        "maximum": maxFM,
        "gridColor": "#719eb7",
        "gridAlpha": 0.3,
        "dashLength": 0,
        "title": "F Measure",
        "titleBold": false,
        "titleColor": "#719eb7"
      }],
    "gridAboveGraphs": true,
    "startDuration": 1,
    "graphs": [{
        "title": "Your tool",
        "balloonText": "[[category]]: <b>[[value]]</b>",
        "fillAlphas": 0,
        "lineAlpha": 1,
        "type": "smoothedLine",
        "lineThickness": 2,
        "lineColor": "#f08b80",
        "valueField": "FMeasure"
      }],
    "chartCursor": {
      "categoryBalloonEnabled": false,
      "cursorAlpha": 0,
      "zoomable": true,
      "valueLineEnabled": true,
      "valueLineBalloonEnabled": false,
      "valueLineAlpha": 0.5,
      "fullWidth": true
    },
    "categoryField": "Threshold",
    "categoryAxis": {
      "autoGridCount": false,
      "gridCount": 20,
      "gridColor": "#719eb7",
      "gridAlpha": 0.3,
      "tickLength": 20,
      "labelRotation": 90,
      "title": "Threshold",
      "titleBold": false,
      "titleColor": "#719eb7"
    },
    "legend": {
      "useGraphSettings": true,
      "valueAlign": "left"
    },
    "export": {
      "enabled": true
    }
  });

  //display Pr chart
  var chart = AmCharts.makeChart("chartPr", {
    "type": "serial",
    "theme": "light",
    "dataProvider": chartDataPr,
    "valueAxes": [{
        "maximum": maxPr,
        "gridColor": "#719eb7",
        "gridAlpha": 0.3,
        "dashLength": 0,
        "title": "Precision",
        "titleBold": false,
        "titleColor": "#719eb7"
      }],
    "gridAboveGraphs": true,
    "startDuration": 1,
    "graphs": [{
        "title": "Your tool",
        "balloonText": "[[category]]: <b>[[value]]</b>",
        "fillAlphas": 0,
        "lineAlpha": 1,
        "type": "smoothedLine",
        "lineThickness": 2,
        "lineColor": "#f08b80",
        "valueField": "Precision"
      }],
    "chartCursor": {
      "categoryBalloonEnabled": false,
      "cursorAlpha": 0,
      "zoomable": true,
      "valueLineEnabled": true,
      "valueLineBalloonEnabled": false,
      "valueLineAlpha": 0.5,
      "fullWidth": true
    },
    "categoryField": "Threshold",
    "categoryAxis": {
      "autoGridCount": false,
      "gridCount": 20,
      "gridColor": "#719eb7",
      "gridAlpha": 0.3,
      "tickLength": 20,
      "labelRotation": 90,
      "title": " Threshold",
      "titleBold": false,
      "titleColor": "#719eb7"
    },
    "legend": {
      "useGraphSettings": true,
      "valueAlign": "left"
    },
    "export": {
      "enabled": true
    }
  });

  //display Recall chart
  var chart = AmCharts.makeChart("chartRec", {
    "type": "serial",
    "theme": "light",
    "dataProvider": chartDataRec,
    "valueAxes": [{
        "maximum": maxRec,
        "gridColor": "#719eb7",
        "gridAlpha": 0.3,
        "dashLength": 0,
        "title": "Recall",
        "titleBold": false,
        "titleColor": "#719eb7"
      }],
    "gridAboveGraphs": true,
    "startDuration": 1,
    "graphs": [{
        "title": "Your tool",
        "balloonText": "[[category]]: <b>[[value]]</b>",
        "fillAlphas": 0,
        "lineAlpha": 1,
        "type": "smoothedLine",
        "lineThickness": 2,
        "lineColor": "#f08b80",
        "valueField": "Recall"
      }],
    "chartCursor": {
      "categoryBalloonEnabled": false,
      "cursorAlpha": 0,
      "zoomable": true,
      "valueLineEnabled": true,
      "valueLineBalloonEnabled": false,
      "valueLineAlpha": 0.5,
      "fullWidth": true
    },
    "categoryField": "Threshold",
    "categoryAxis": {
      "autoGridCount": false,
      "gridCount": 20,
      "gridColor": "#719eb7",
      "gridAlpha": 0.3,
      "tickLength": 20,
      "labelRotation": 90,
      "title": " Threshold",
      "titleBold": false,
      "titleColor": "#719eb7"
    },
    "legend": {
      "useGraphSettings": true,
      "valueAlign": "left"
    },
    "export": {
      "enabled": true
    }
  });
</script>

<%@include file="footer.jsp" %>