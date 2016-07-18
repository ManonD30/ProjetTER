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

var validationApp = angular.module('validationApp', []);   

validationApp.controller('ValidationCtrl', function ($scope, $window) {
  // Init alignmentJson for angular js by getting the alignment from Java alignmentArray
  $scope.alignmentJson = $window.alignmentJson;
  //console.log($scope.alignmentJson);
  
  // Pour recup les objets depuis le javascript
  //$scope.lowThreshold = $window.lowThreshold;
  //$scope.minValue = $window.minValue;
  //$scope.minValue = 50;
  
  $scope.change = function() {
    console.log("yyyy");
    $scope.minValue = $scope.minValue = 50;
    console.log($scope.minValue);
  };
  
    /**
   * Function to get 2 sliders range content
   */
  $( function() {
      $( "#slider-range" ).slider({
        range: true,
        min: 0,
        max: 100,
        values: [ 25, 100 ],
        slide: function( event, ui ) {
          $( "#thresholdRange" ).val(ui.values[ 0 ]/100 + " - " + ui.values[ 1 ]/100);
          console.log("Update minValues");
          //$('[ng-controller="ValidationCtrl"]').scope().minValue = ui.values[ 0 ];
          $scope.minValue = ui.values[ 0 ];
          console.log($scope.minValue);
        }
      });
      $( "#thresholdRange" ).val( $( "#slider-range" ).slider( "values", 0 )/100 +
        " - " + $( "#slider-range" ).slider( "values", 1 )/100 );
    } );
});

  
  
  