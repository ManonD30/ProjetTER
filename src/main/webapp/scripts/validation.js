var validationApp = angular.module('validationApp', []);   
validationApp.controller('validationCtrl', function ($scope) { 
  
  $scope.init = function (liste, ont1, ont2) {
      //console.log(liste);
      //console.log(ont1);
      //console.log(ont2);
      console.log("in ctrler");
      console.log(alignmentJson);
  };
});