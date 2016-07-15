var valueAllBoxes = false;
function checkAllBoxes() {
    var checkboxes = document.getElementsByClassName("checkbox");
    
    for (var i = checkboxes.length - 1; i >= 0; i--)
    {
      checkboxes[i].checked = false;
    }
    if (valueAllBoxes == true) {
      valueAllBoxes = false;
    } else {
      valueAllBoxes = true;
    }
}

var validationApp = angular.module('validationApp', []);   

validationApp.controller('validationCtrl', function ($scope, $window) {
  // Init alignmentJson for angular js by getting the alignment from Java alignmentArray
  $scope.alignmentJson = $window.alignmentJson;
  console.log($scope.alignmentJson);
});
