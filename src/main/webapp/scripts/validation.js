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
  //console.log($scope.alignmentJson);

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

                // Build String to be put in popover
                var popoverString = "<ul>";
                if (scope[attrs.ontology][attrs.entity] != null) {
                  for (var attr in scope[attrs.ontology][attrs.entity]) {
                    popoverString = popoverString + "<li>" + attr + " : " + scope[attrs.ontology][attrs.entity][attr] + "</li>"
                  }
                }
                popoverString + "</ul>";

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



  