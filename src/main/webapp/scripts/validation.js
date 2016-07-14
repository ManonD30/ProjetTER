var validationApp = angular.module('validationApp', []);   

validationApp.controller('validationCtrl', function ($scope, $window) {
  // Init alignmentJson for angular js by getting the alignment from Java alignmentArray
  $scope.alignmentJson = $window.alignmentJson;
  console.log($scope.alignmentJson);
});
