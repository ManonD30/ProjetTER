var validationApp = angular.module('validationApp', []);   

validationApp.controller('validationCtrl', function ($scope, $window) {
    $scope.alignmentJson = $window.alignmentJson;
    console.log($scope.alignmentJson);
});
