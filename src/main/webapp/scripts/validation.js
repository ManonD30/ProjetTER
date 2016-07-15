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
  //jQuery('#thresholdRange').val("0");
  //console.log(jQuery('#thresholdRange').val())
  //console.log($scope.threshold  );
  if ($scope.threshold == null) {
    //$scope.threshold = 0.5;
    $('#thresholdRange').val(0);
    console.log("juste avant threshold range");
    console.log($('#thresholdRange').val());
    console.log("now init");
  }
  console.log($scope.threshold);
});
