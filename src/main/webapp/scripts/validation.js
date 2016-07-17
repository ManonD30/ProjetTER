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
});

$( function() {
    $( "#slider-range" ).slider({
      range: true,
      min: 0,
      max: 500,
      values: [ 75, 300 ],
      slide: function( event, ui ) {
        $( "#amount" ).val( "$" + ui.values[ 0 ] + " - $" + ui.values[ 1 ] );
      }
    });
    $( "#amount" ).val( "$" + $( "#slider-range" ).slider( "values", 0 ) +
      " - $" + $( "#slider-range" ).slider( "values", 1 ) );
  } );