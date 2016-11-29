$(document).ready(function () {
  // Function to resize navbar, footer and right entity details window depending on screen size
  function resizePanels() {
    var contentSourceSize = $(".entity-source .entity-inner-content").height();
    var contentTargetSize = $(".entity-target .entity-inner-content").height();
    var headerHeight = $("header").height();
    var footerHeight = $("footer").height();
    var contentWidth = $(".entities").width() - 30;
    var totalSize = $(window).height() - headerHeight - footerHeight;
    var halfSize = Math.floor(totalSize / 2);
    var newSourceSize = (contentSourceSize < halfSize) ? contentSourceSize : halfSize;
    var newTargetSize = (contentTargetSize < halfSize) ? contentTargetSize : halfSize;
    //console.log("contentSourceSize", contentSourceSize, "contentTargetSize", contentTargetSize, "totalSize", totalSize)
    $(".entity-source").css({"flexBasis": newSourceSize + "px"});
    $(".entity-target").css({"flexBasis": newTargetSize + "px"});

    $("aside").css({"paddingTop": headerHeight + "px", "paddingBottom": footerHeight + "px"})
    $("main").css({"paddingTop": headerHeight + "px", "paddingBottom": footerHeight + "px"})

    $(".entity-source .entity-content").css({"height": $(".entity-source").height() + "px", "width": contentWidth + "px"});
    $(".entity-target .entity-content").css({"height": $(".entity-target").height() + "px", "width": contentWidth + "px"});
  }

  // Button to switch between text and graph entities details
  $(".switch-nav").on("click", "li", function () {
    $(".entity-view").hide();
    $(this).find("button").addClass("btn-info");
    $(this).siblings("li").find("button").removeClass("btn-info");
    $(".entity-" + $(this).attr("class")).show();
  })

  $(window).on('resize', function () {
    resizePanels();
  })
  resizePanels();
});

// Using rzSlider for 2 sliders range input
var validationApp = angular.module('validationApp', ['rzModule', 'ui.bootstrap']);

/**
 * ValidationApp controller, define all angular interactions
 */
validationApp.controller('ValidationCtrl', function ($scope, $window) {
  // Get the 2 ont in an object
  if ($window.alignmentJson.srcOntologyURI === undefined) {
    $window.alignmentJson.srcOntologyURI = "Source entities";
  }
  if ($window.alignmentJson.tarOntologyURI === undefined) {
    $window.alignmentJson.tarOntologyURI = "Target entities";
  }
  $scope.ontologies = {"ont1": $window.sourceOnt, "ont2": $window.targetOnt, "srcOntUri": $window.alignmentJson.srcOntologyURI, "tarOntUri": $window.alignmentJson.tarOntologyURI};
  // Merge namespaces from the 2 ont:
  $scope.namespaces = $.extend($window.sourceOnt.namespaces, $window.targetOnt.namespaces);
  $scope.detailsLocked = false;

  // init the ng-model used by the valid select dropdown
  $scope.selectValidModel = {};
  $scope.hideValidatedAlignments = false;

  // Get an object with the entities of the alignment as key and their properties
  // (extracted from the ontologies) as object
  $scope.alignments = getAlignmentsWithOntologiesData($window.alignmentJson.entities, $scope.ontologies);
  alignments = $scope.alignments;

  $scope.langSelect = {"en": "en", "fr": "fr"};

  // Little function to get the first element of an object (used to get first label if selectedLang not available
  $scope.getEntityLabel = function (entity, selectedLang) {
    if (entity.label !== undefined) {
      if (entity.label[selectedLang]) {
        return entity.label[selectedLang];
      } else if (entity.label[Object.keys(entity.label)[0]]) {
        return entity.label[Object.keys(entity.label)[0]];
      }
    }
    return entity.id;
  };

  //Range slider config
  $scope.minRangeSlider = {
    minValue: 0,
    maxValue: 100,
    options: {
      floor: 0,
      ceil: 100,
      step: 1
    }
  };

  // Hide all validated alignments when click on hideValidatedAlignments button
  $scope.hideAlignments = function ($event) {
    $scope.hideValidatedAlignments = !$scope.hideValidatedAlignments;
    // Change button color
    if ($scope.hideValidatedAlignments === true) {
      angular.element($event.currentTarget).css('background', '#d531b9');
    } else {
      angular.element($event.currentTarget).css('background', 'linear-gradient(to bottom, #5bc0de 0%, #2aabd2 100%)');
    }
  };

  // Display all rows before Download to get all alignments. But they all get back as waiting 
  // Je pense que dans le html c'est init sur waiting. Mais dans le $scope.alignment c'est updated
  $scope.displayAllRows = function () {
    $scope.hideValidatedAlignments = false;
    angular.element("#hideAlignmentsButton").css('background', 'linear-gradient(to bottom, #5bc0de 0%, #2aabd2 100%)');
  };

  /**
   * Generate the ng if condition to manage which rows will be display
   * @param {type} alignment
   * @returns {Boolean}
   */
  $scope.generateTableNgIf = function (alignment) {
    if (alignment.measure >= $scope.minRangeSlider.minValue / 100
            && alignment.measure <= $scope.minRangeSlider.maxValue / 100) {
      if ($scope.hideValidatedAlignments === true && alignment.valid !== "waiting") {
        return false;
      }
      ;
      return true;
    }
    return false;
  };

  /**
   * Generate the style string for the valid select dropdown to change background color
   * @param {type} alignment
   * @returns {String}
   */
  $scope.generateStyleForSelect = function (alignment) {
    var styleString = null;
    if (alignment.valid === "waiting") {
      // Orange
      styleString = "color: #fff; background-color: #f0ad4e;";
    } else if (alignment.valid === "valid") {
      // Green
      styleString = "color: #fff; background-color: #5cb85c;";
    } else if (alignment.valid === "notvalid") {
      // Red
      styleString = "color: #fff; background-color: #d9534f;";
    }
    return styleString;
  };

  /**
   * Generate the id of an HTML element by concatenating a "validSelect" with the id
   * @param {int} id
   * @returns {undefined}
   */
  $scope.generateValidSelectId = function (id) {
    return "validSelect" + id;
  };

  /**
   * Put the new value in the valid select dropdown ng-model and change the value in the alignment object
   * @param {int} elementId
   * @returns {undefined}
   */
  $scope.updateSelectValidModels = function ($event, alignment) {
    $scope.selectValidModel[alignment.index] = angular.element($event.currentTarget).val();
    alignment.valid = angular.element($event.currentTarget).val();
  };

  /**
   * Change details div to show selected entity details
   * @param {boolean} clickedOn
   * @returns {undefined}
   */
  $scope.changeDetails = function (clickedOn) {
    //console.log(this.alignment);
    if ($scope.detailsLocked === false || clickedOn === true) {
      var stringDetail1 = buildEntityDetailsHtml(this.alignment.entity1, "Source", $scope.selectedLang);
      var stringDetail2 = buildEntityDetailsHtml(this.alignment.entity2, "Target", $scope.selectedLang);

      document.getElementById("entityDetail1").innerHTML = stringDetail1;
      document.getElementById("entityDetail2").innerHTML = stringDetail2;
      if (clickedOn === true) {
        $scope.detailsLocked = true;
        if ($scope.lastSelected) {
          var selected = this.selected;
          $scope.lastSelected.selected = "";
        }

        // Remove selected if click again on selected row
        if (selected === "selected") {
          this.selected = "";
          $scope.detailsLocked = false;
        } else {
          this.selected = "selected";
        }
        $scope.lastSelected = this;
      }
    }
  };
});

/**
 * a function to get the ontology that is linked to an alignment
 * Example of the alignments object:
 * {"entity1": {"attr1": "http://attr1.fr"}, "entity2": {"attr1": "http://attr1.fr"}, 
 * "measure": 0.84, "relation": "skos:exactMatch", "index": 1}
 * 
 * @returns alignments
 */
function getAlignmentsWithOntologiesData(alignment, ontologies) {
  var alignments = [];

  for (var key in alignment) {
    var alignToAdd = {"entity1": {}, "entity2": {}};
    if (alignment[key]['entity1'] in ontologies["ont1"]["entities"]) {
      alignToAdd["entity1"] = ontologies["ont1"]['entities'][alignment[key]['entity1']];
    } else {
      alignToAdd["entity1"] = {"id": alignment[key]['entity1'].toString()};
    }

    if (alignment[key]['entity2'] in ontologies["ont2"]["entities"]) {
      alignToAdd["entity2"] = ontologies["ont2"]['entities'][alignment[key]['entity2']];
    } else {
      alignToAdd["entity2"] = {"id": alignment[key]['entity2'].toString()};
    }
    alignToAdd["measure"] = alignment[key]['measure'];
    alignToAdd["relation"] = alignment[key]['relation'];
    alignToAdd["index"] = key;
    // All entities are "waiting" for the moment, but we need to extract it from the uploaded alignment
    alignToAdd["valid"] = alignment[key]['valid'];
    ;
    alignments.push(alignToAdd);
  }
  return alignments;
}

/**
 * Build the HTML to display entity details (list ul li). 
 * It takes the following objetc as entity:
 * {"http://entity1.org/": {"id": "http://entity1.org/", "label": {"fr":
 * "bonjour", "en": "hello"}, "http://rdfs.org/label": [{"type": "literal",
 * "value": "bonjour", "lang": "fr"}, {"type": "literal", "value": "hello",
 * "lang": "en"}]}}
 * @param {type} entity
 * @returns {undefined}
 */
function buildEntityDetailsHtml(entity, entityName, selectedLang) {
  // Order the JSON string to have id and label at the beginning
  var orderedEntities = {};
  var id = entity["id"].link(entity["id"]);
  //orderedEntities["id"] = entity["id"].link(entity["id"]);

  // Get the label (using "!=" instead of "!==" allows to avoid null AND undefined)
  if (entity["label"] != null) {
    // Select label according to user selection
    if (entity["label"].hasOwnProperty(selectedLang)) {
      var label = entity["label"][selectedLang] + " (" + selectedLang + ")";
    } else {
      // Take first label in object if selected lang not available
      if (Object.keys(entity["label"])[0] == null || Object.keys(entity["label"])[0] == "") {
        var label = entity["label"][Object.keys(entity["label"])[0]];
      } else {
        // Add language between parenthesis if not undefined
        var label = entity["label"][Object.keys(entity["label"])[0]] + " (" + Object.keys(entity["label"])[0] + ")";
      }
    }
  } else {
    var label = id;
  }

  // add each property object linked to each subject
  // Iterate over the different properties (predicates) of an entity
  Object.keys(entity).sort().forEach(function (key) {
    if (key !== "id" && key !== "label") {
      orderedEntities[key] = null;
      // Iterate over the different values of the object of a predicate (the same property can point to different objects)
      for (var valuesObject in entity[key]) {
        if (typeof entity[key][valuesObject]["value"] !== "undefined") {
          // to get the value of the object depending if it's an URI or a literal
          if (entity[key][valuesObject]["value"].startsWith("http://")) {
            // Concatenate URI too ? With <a href>
            if (orderedEntities[key] === null) {
              orderedEntities[key] = entity[key][valuesObject]["value"].link(entity[key][valuesObject]["value"]);
            } else {
              orderedEntities[key] = orderedEntities[key] + "<br/> " + entity[key][valuesObject]["value"].link(entity[key][valuesObject]["value"]);
            }
            break;
          } else {
            // If it is a literal then we concatenate them
            if (orderedEntities[key] === null) {
              orderedEntities[key] = entity[key][valuesObject]["value"];
            } else {
              orderedEntities[key] = orderedEntities[key] + "<br/> " + entity[key][valuesObject]["value"];
            }
          }
        }
      }
    }
  });

  // Build String to be put in the details div
  var htmlString = "<h1 style='text-align: center;'>" + entityName + " entity details</h1><h1>" + label + "</h1><h2>" + id + "</h2><dl>";
  for (var attr in orderedEntities) {
    var prefixedPredicate = attr;
    if (entity[attr][0]["prefixedPredicate"] !== null) {
      // Display full URI when mouseover
      //<dt><abbr title="http://www.w3.org/1999/02/22-rdf-syntax-ns#type">rdf:type</abbr></dt>
      prefixedPredicate = '<abbr title="' + attr + '">' + entity[attr][0]["prefixedPredicate"] + "</abbr>";
    }
    htmlString = htmlString + "<dt>" + prefixedPredicate + "</dt><dd>" + orderedEntities[attr] + "</dd>";
  }
  return htmlString + "</dl>";
}

/* Remember on how to make a little window that show when mouseover with angularjs
 * Uncomment popover in style.css if you want to use it
 .directive('toggle', function () {
 return {
 restrict: 'A',
 link: function (scope, element, attrs) {
 if (attrs.toggle == "tooltip") {
 $(element).tooltip();
 }
 if (attrs.toggle == "popover") {
 // Allows the popover to stay when mouseovered
 // And allows us to setup the popover
 
 var entity = JSON.parse(attrs.entity);
 // Not working properly: it doesn't change with selectedLang
 var popoverString = buildEntityDetailsHtml(entity);
 
 $(element).popover({
 html: true,
 trigger: 'manual',
 container: $(this).attr('id'),
 content: popoverString
 }).on("mouseenter", function () {
 var _this = this;
 $(this).popover("show");
 $(this).siblings(".popover").on("mouseleave", function () {
 $(_this).popover('hide');
 });
 }).on("mouseleave", function () {
 var _this = this;
 setTimeout(function () {
 if (!$(".popover:hover").length) {
 $(_this).popover("hide")}}, 100); }); }}}; });
 */