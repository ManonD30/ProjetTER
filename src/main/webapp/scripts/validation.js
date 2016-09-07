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
 * Build the HTML to display entity details (list ul li)
 * @param {type} entity
 * @returns {undefined}
 */
function buildEntityDetailsHtml(entity) {
  var htmlString = "";

  // Build String to be put in popover
  htmlString = htmlString + "<ul>";
  // Order the JSON string to have id and label at the beginning
  var orderedEntities = {};
  orderedEntities["id"] = entity["id"];
  if (entity["label"] != null) {
    orderedEntities["label"] = entity["label"];
  }
  Object.keys(entity).sort().forEach(function (key) {
    if (key != "id" && key != "label") {
      orderedEntities[key] = entity[key];
    }
  });

  var printHr = false;
  for (var attr in orderedEntities) {
    if (printHr) {
      htmlString = htmlString + "<hr style='margin: 1% 10%;'>";
      printHr = false;
    }
    htmlString = htmlString + "<li><b>" + attr + "</b> = " + entity[attr] + "</li>"
    if (attr == "label") {
      printHr = htmlString + "<hr>";
    }
  }
  return htmlString + "</ul>";
}

// Using rzSlider for 2 sliders range input
var validationApp = angular.module('validationApp', ['rzModule', 'ui.bootstrap']);

validationApp.controller('ValidationCtrl', function ($scope, $window) {
  // Get the 2 ont in an object
  $scope.ontologies = {"ont1": $window.ont1, "ont2": $window.ont2};
  // Merge namespaces from the 2 ont:
  $scope.namespaces = $.extend($window.ont1.namespaces, $window.ont2.namespaces);

  // Get an object with the entities of the alignment as key and their properties
  // (extracted from the ontologies) as object
  $scope.alignments = getAlignmentsWithOntologiesData($window.alignmentJson, $scope.ontologies);

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

  $scope.selectEntity = function (element, attrs) {

    $scope.selected = this.alignment;
    console.log($scope.selected);

    var stringDetail1 = buildEntityDetailsHtml(this.alignment.entity1);
    var stringDetail2 = buildEntityDetailsHtml(this.alignment.entity2);

    document.getElementById("entityDetail1").innerHTML = stringDetail1;
    document.getElementById("entityDetail2").innerHTML = stringDetail2;
  };
})
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
        ;

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

  console.log("alignments");
  console.log(alignments);
  return alignments;
} 