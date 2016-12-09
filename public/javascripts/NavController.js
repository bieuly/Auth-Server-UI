function NavController($scope, $http) {

	$scope.navSelection = '';

	$scope.content = '';

	// CUSTOMERS

	$scope.showCustomrs = false;
	$scope.showCreateCustomerButton = false;
	$scope.selectedCustomer = {};
	$scope.showCustomerDetails = false;

	// USERS
	$scope.showUsers = true;
	$sceope.showCreateUserButton = false;
	$scope.showCreateUserForm = false;

	// ROLES
	$scope.showRoles = false;
	$scope.showCreateRoleButton = false;
	$scope.showCreateRoleForm = false;

	$scope.getData = function(getDataUrl) {
		$http.get(getDataUrl).success(function(data, status, headers, config) {
			$scope.content = data;
		}).error(function(data, status, headers, config) {
			$scope.error = 'error: ' + data + ", " + status;
		});
	};

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

}