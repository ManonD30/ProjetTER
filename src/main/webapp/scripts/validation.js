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

// Using rzSlider for 2 sliders range input
var validationApp = angular.module('validationApp', ['rzModule', 'ui.bootstrap']);

validationApp.controller('ValidationCtrl', function ($scope, $window) {
  // Init alignmentJson for angular js by getting the alignment from Java alignmentArray
  $scope.alignmentJson = $window.alignmentJson;
  $scope.ont1 = $window.ont1;
  $scope.ont2 = $window.ont2;
  $scope.ontologies = {"ont1": $window.ont1, "ont2": $window.ont2};
  //console.log($scope.alignmentJson);
  console.log($scope.alignmentJson);
  
  // Get an object with the entities of the alignment as key and their properties
  // (extracted from the ontologies) as object
  $scope.alignments = getAlignmentsWithOntologiesData($window.alignmentJson, $scope.ontologies);

  // TODO: ici on récupère le match ontology hash + alignment
  // Dans un hash alignments = {"align1": {"attr":"value}, "align2":{}}
  // Et on utilise que celui là dans le jsp

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
                var popoverString = "";
                /*for (var prefix in scope[attrs.ontology]['namespaces']) {
                  popoverString = popoverString + "<b>" + prefix + "</b> " + scope[attrs.ontology]['namespaces'][prefix] + " \n";
                }*/

                // Build String to be put in popover
                popoverString = popoverString + "<ul>";
                console.log("GET ENTITY");
                var entity = JSON.parse(attrs.entity);
                console.log(entity);
                /*console.log("lalal");
                 console.log(scope[attrs.ontology]['entities']);
                 console.log(attrs.entity.toString());
                 //if (scope[attrs.ontology]['entities'][attrs.entity.toString()] != null) {
                 console.log("lalal in null");*/
                for (var attr in entity) {
                  //console.log("lalal in for " + attr);
                  popoverString = popoverString + "<li><b>" + attr + "</b> = " + entity[attr] + "</li>"
                }
                //}
                popoverString = popoverString + "</ul>";

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
 * 
 * DES LE DEBUT faire un objet super facile à manipuler avec toutes les infos dont on a besoin dedans
 * Pas avoir besoin de checker l'ontologie ensuite.
 * Style {"entity1": {"attrs": {"attr1": "value1"}}
 * En gros on a une ontologie sous forme de hash (dont certaines entites useless) et une list d'entities mappées
 * Il faut faire un hash avec seulement les entities mappées
 * 
 * Et faire un fct pour recup quel alignment = quel onto
 * Pour ça on itere les concepts et on check dans quel hash ils sont, dès qu'il y a assez 
 * de différences on considère que c'est bon
 * @returns {undefined}
 */
function getAlignmentsWithOntologiesData(alignment, ontologies) {
  var alignments = [];
  var matchScore = {"entity1": {"ont1": 0, "ont2": 0}, "entity2": {"ont1": 0, "ont2": 0}};
  var ontEntity1 = null;
  var ontEntity2 = null;

  if (ontologies["ont1"]["entities"] == null) {
    console.log("Loading of ont1 in OwlApi failed")
  } else if (ontologies["ont2"]["entities"] == null) {
    console.log("Loading of ont2 in OwlApi failed")
  } else {
    // Iterate alignment and check if entity in the ont 1 or 2 to define an ontology for
    // each entity of the alignment
    for (var key in alignment) {
      // Check if entity1 in ont1 or 2 and increment the compter
      if (alignment[key]['entity1'] in ontologies["ont1"]["entities"]) {
        matchScore["entity1"]["ont1"] = matchScore["entity1"]["ont1"] + 1;
      }
      if (alignment[key]['entity1'] in ontologies["ont2"]["entities"]) {
        matchScore["entity1"]["ont2"] = matchScore["entity1"]["ont2"] + 1;
      }
      // Check if entity2 in ont1 or 2 and increment the compter
      if (alignment[key]['entity2'] in ontologies["ont1"]["entities"]) {
        matchScore["entity2"]["ont1"] = matchScore["entity2"]["ont1"] + 1;
      }
      if (alignment[key]['entity2'] in ontologies["ont2"]["entities"]) {
        matchScore["entity2"]["ont2"] = matchScore["entity2"]["ont2"] + 1;
      }

      // If more than 3 
      if (Math.abs(matchScore["entity1"]["ont1"] - matchScore["entity1"]["ont2"]) >= 3) {
        if (matchScore["entity1"]["ont1"] > matchScore["entity1"]["ont2"]) {
          ontEntity1 = "ont1";
        } else {
          ontEntity1 = "ont2";
        }
        if (ontEntity2 != null) {
          break;
        }
      }

      if (Math.abs(matchScore["entity2"]["ont1"] - matchScore["entity2"]["ont2"]) >= 3) {
        if (matchScore["entity2"]["ont1"] > matchScore["entity2"]["ont2"]) {
          ontEntity2 = "ont1";
        } else {
          ontEntity2 = "ont2";
        }
        if (ontEntity1 != null) {
          break;
        }
      }
    }
  }

  if (ontEntity1 != ontEntity2) {
    for (var key in alignment) {
      var alignToAdd = {"entity1": {}, "entity2": {}}
      // Check if entity1 in ont1 or 2 and increment the compter
      if (alignment[key]['entity1'] in ontologies[ontEntity1]["entities"]) {
        alignToAdd["entity1"] = ontologies[ontEntity1]['entities'][alignment[key]['entity1']];
      } else {
        alignToAdd["entity1"] = {"id": alignment[key]['entity1'].toString()};
      }

      if (alignment[key]['entity2'] in ontologies[ontEntity2]["entities"]) {
        alignToAdd["entity2"] = ontologies[ontEntity2]['entities'][alignment[key]['entity2']];
      } else {
        alignToAdd["entity2"] = {"id": alignment[key]['entity2'].toString()}
      }
      alignToAdd["measure"] = alignment[key]['measure'];
      alignToAdd["relation"] = alignment[key]['relation'];
      alignToAdd["index"] = key;
      alignments.push(alignToAdd);
    }
  } else {
    console.log("Error when figuring out which ontology match which alignment");
  }

  console.log("alignmentss");
  console.log(alignments);
  return alignments;
}

function getOntologyForAlignment(alignment, ont) {

}


  