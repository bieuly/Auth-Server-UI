var myApp = angular.module('myApp', ['checklist-model']);

myApp.controller('mainController', function($scope, $http) {

    $scope.navSelection = '';
	$scope.content = '';

	// CUSTOMERS

	$scope.showCustomrs = false;
	$scope.showCreateCustomerButton = false;
	$scope.selectedCustomer = {};
	$scope.showCustomerDetails = false;
	// USERS
	$scope.showUsers = true;
	$scope.showCreateUserButton = false;
	$scope.showCreateUserForm = false;

	$scope.newUserPassword = '';
	$scope.newUserPasswordRepeat = '';

	// ROLES
	$scope.showRoles = false;
	$scope.showCreateRoleButton = false;
	$scope.showCreateRoleForm = false;
//
//	$scope.allRoles = [
//	    {}
//	];

    // PERMISSIONS
    $scope.permissions = [
        {id: 1, text: "Send SMS", service: "MS"},
        {id: 2, text: "End Conversation", service: "MS"},
        {id: 1, text: "Process Transactions", service: "CP"}
    ];
    $scope.selectedPermissions = {
        selected: []
    };



	$scope.getRoles = function(customerId) {
		$http.get(getDataUrl).success(function(data, status, headers, config) {
			$scope.content = data;
		}).error(function(data, status, headers, config) {
			$scope.error = 'error: ' + data + ", " + status;
		});
	};

	$scope.clearCreateRoleForm = function() {
	    $scope.newRoleName = '';
	    $scope.selectedPermissions.selected = [];
	};

//	$scope.clearCreateUserForm = function() {
//	    $scope.
//	};

	$(".nav li").on("click", function() {
		$(".nav li").removeClass("active");
		$(this).addClass("active");

		if ($(this).attr('id') === 'usersTab') {
			console.log("got into users tab");
			$scope.showUsers = true;
			$scope.showRoles = false;
			$scope.showCreateRoleForm = false;
			$scope.$apply();
		}

		if ($(this).attr('id') === 'rolesTab') {
			console.log("got into roles tab");
			$scope.showUsers = false;
			$scope.showCreateUserForm = false;
			$scope.showRoles = true;
			$scope.$apply();
		}

	});
});


myApp.controller('loginController', ['$scope', '$http', function($scope, $http){

  $scope.feedback = '';

  $scope.postLoginForm = function(loginUrl, successUrl) {
    var data = {
      username : $scope.username,
      password : $scope.password
    };
    $scope.successUrl = successUrl;
    $http.post(loginUrl, data).success(function(data, status, headers, config) {
      if (data.valid) {
        document.location.href = successUrl;
      } else {
        $scope.feedback = "Invalid username / password. Please try again!";
      }
    }).error(function(data, status, headers, config) {
      $scope.feedback = 'error: ' + data + ", " + status;
    });
  };

}]);