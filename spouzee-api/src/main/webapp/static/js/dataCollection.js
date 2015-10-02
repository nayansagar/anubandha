var sampleApp = angular.module('spzDetails', []);

//Define Routing for app
//Uri /AddNewOrder -> template add_order.html and Controller AddOrderController
//Uri /ShowOrders -> template show_orders.html and Controller AddOrderController
sampleApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/details', {
        templateUrl: 'spouzee-api/details.html',
        controller: 'DetailsController'
    })
      otherwise({
        redirectTo: '/details'
      });
}]);


sampleApp.controller('DetailsController', function($scope, $http) {

    $scope.message = 'This is Add new order screen';

});


sampleApp.controller('ShowOrdersController', function($scope) {

    $scope.message = 'This is Show orders screen';

});