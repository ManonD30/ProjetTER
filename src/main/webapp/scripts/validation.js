var validationApp = angular.module('validationApp', []);   

validationApp.controller('validationCtrl', ['$scope', '$window', function($scope, $window) {
    angular.element(document).ready(function () {
        $scope.alignmentJson = $window.alignmentJson;
        console.log($scope.alignmentJson);
    });
}]);
  
/*$scope.init = function (liste, ont1, ont2) {
    //console.log(liste);
    //console.log(ont1);
    //console.log(ont2);
    console.log("in ctrler");
    //console.log(liste);
};*/
