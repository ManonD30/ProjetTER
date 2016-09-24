var valueAllBoxes = false;
/**
 * To check or uncheck all validity checkboxes
 */
function checkAllBoxes() {
  var checkboxes = document.getElementsByClassName("checkbox");
  for (var i = checkboxes.length - 1; i >= 0; i--)
  {
    checkboxes[i].checked = valueAllBoxes;
  }
  if (valueAllBoxes == true) {
    valueAllBoxes = false;
  } else {
    valueAllBoxes = true;
  }
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
  if (entityName === undefined) {
    var htmlString = "";
  } else {
    var htmlString = "<h3 class='contentText'>" + entityName + " entity details</h3>";
  }

  // Build String to be put in popover
  htmlString = htmlString + "<ul>";
  // Order the JSON string to have id and label at the beginning
  var orderedEntities = {};
  orderedEntities["id"] = entity["id"].link(entity["id"]);

  // Get the label
  if (entity["label"] != null) {
    // Select label according to user selection
    if (entity["label"].hasOwnProperty(selectedLang)) {
      orderedEntities["label"] = entity["label"][selectedLang] + " (" + selectedLang + ")";
    } else {
      // Take first label in object if selected lang not available
      orderedEntities["label"] = entity["label"][Object.keys(entity["label"])[0]] + " (" + Object.keys(entity["label"])[0] + ")";
    }
  }

  // add each property object linked to each subject
  // Iterate over the different properties (predicates) of an entity
  Object.keys(entity).sort().forEach(function (key) {
    if (key != "id" && key != "label") {
      orderedEntities[key] = null;
      // Iterate over the different values of the object of a predicate (the same property can point to different objects)
      for (var valuesObject in entity[key]) {
        // to get the value of the object depending if it's an URI or a literal
        if (entity[key][valuesObject]["value"].startsWith("http://")) {
          // Concatenate URI too ? With <a href>
          if (orderedEntities[key] === null) {
            orderedEntities[key] = entity[key][valuesObject]["value"].link(entity[key][valuesObject]["value"]);
          } else {
            orderedEntities[key] = orderedEntities[key] + "<br/> " + entity[key][valuesObject]["value"].link(entity[key][valuesObject]["value"]);
          }
          break;
          //} else if (entity[key][valuesObject]["type"] == "literal") {
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
  });

  var printHr = false;
  for (var attr in orderedEntities) {
    if (printHr) {
      htmlString = htmlString + "<hr style='margin: 1% 10%;'>";
      printHr = false;
    }
    htmlString = htmlString + "<li><b>" + attr + "</b> = " + orderedEntities[attr] + "</li>"
    if (attr == "label") {
      printHr = htmlString + "<hr>";
    }
  }
  //console.log("ordered entities");
  //console.log(orderedEntities);
  return htmlString + "</ul>";
}

// Using rzSlider for 2 sliders range input
var validationApp = angular.module('validationApp', ['rzModule', 'ui.bootstrap']);

validationApp.controller('ValidationCtrl', function ($scope, $window) {
  // Get the 2 ont in an object
  $scope.ontologies = {"ont1": $window.ont1, "ont2": $window.ont2};
  // Merge namespaces from the 2 ont:
  $scope.namespaces = $.extend($window.ont1.namespaces, $window.ont2.namespaces);
  $scope.detailsLocked = false;

  // Get an object with the entities of the alignment as key and their properties
  // (extracted from the ontologies) as object
  $scope.alignments = getAlignmentsWithOntologiesData($window.alignmentJson, $scope.ontologies);

  $scope.langSelect = {"en": "en", "fr": "fr"};

  // Little function to get the first element of an object (used to get first label if selectedLang not available
  $scope.getFirstElement = function (obj) {
    return obj[Object.keys(obj)[0]];
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

  $scope.changeDetails = function (clickedOn) {
    if ($scope.detailsLocked == false || clickedOn == true) {
      //$scope.selected = this.alignment;
      //$scope.selected = '';

      var stringDetail1 = buildEntityDetailsHtml(this.alignment.entity1, "Source", $scope.selectedLang);
      var stringDetail2 = buildEntityDetailsHtml(this.alignment.entity2, "Target", $scope.selectedLang);

      document.getElementById("entityDetail1").innerHTML = stringDetail1;
      document.getElementById("entityDetail2").innerHTML = stringDetail2;
      if (clickedOn == true) {
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
})
/* Remember on how to make a little window that show when mouseover
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
 $(_this).popover("hide")
 }
 }, 100);
 });
 }
 }
 };
 })
 ;*/

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
    var alignToAdd = {"entity1": {}, "entity2": {}}
    if (alignment[key]['entity1'] in ontologies["ont1"]["entities"]) {
      alignToAdd["entity1"] = ontologies["ont1"]['entities'][alignment[key]['entity1']];
    } else {
      alignToAdd["entity1"] = {"id": alignment[key]['entity1'].toString()};
    }

    if (alignment[key]['entity2'] in ontologies["ont2"]["entities"]) {
      alignToAdd["entity2"] = ontologies["ont2"]['entities'][alignment[key]['entity2']];
    } else {
      alignToAdd["entity2"] = {"id": alignment[key]['entity2'].toString()}
    }
    alignToAdd["measure"] = alignment[key]['measure'];
    alignToAdd["relation"] = alignment[key]['relation'];
    alignToAdd["index"] = key;
    alignments.push(alignToAdd);
  }

  //console.log("alignments");
  //console.log(alignments);
  return alignments;
} 