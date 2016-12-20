var myApp = angular.module('myApp', ['checklist-model']);

myApp.controller('mainController', function($scope, $http, $location, $timeout) {

    $scope.navSelection = '';
	$scope.content = '';

	$scope.showAlertBox = false;
	$scope.alertMessage = '';

	// CUSTOMERS

	$scope.showCustomers = false;
	$scope.showCreateCustomerButton = false;
	$scope.showCreateCustomerForm = false;
	$scope.selectedCustomer = {};
	$scope.showCustomerDetails = false;
    $scope.newCustomerName = '';
    $scope.createCustomerFeedback = '';

	// USERS
	$scope.showUsers = true;
	$scope.selectedUser = {};
	$scope.showUserDetails = false;
	$scope.showCreateUserButton = false;
	$scope.showCreateUserForm = false;
	$scope.allUsers = [];
	$scope.newUserPassword = '';
	$scope.newUserPasswordRepeat = '';
	// Create User Form
	$scope.newUserFirstName = '';
	$scope.newUserLastName = '';
	$scope.newUserStatusSelect = '';
	$scope.newUserType = '';
	$scope.newUserUsername = '';
	$scope.newUserPassword = '';
	$scope.newUserPasswordRepeat = '';
    $scope.availableUserTypes = [
        "User",
        "PayStation"
    ];
    $scope.showCreateUserFeedback = false;
    $scope.createUserFeedback = [];


	// ROLES
	$scope.showRoles = false;
	$scope.showCreateRoleButton = false;
	$scope.showCreateRoleForm = false;
	$scope.showRoleDetails = false;
	$scope.allRoles = [];
	$scope.selectedRoleNames = [];

    // PERMISSIONS
    $scope.permissions = [
        {id: 1, text: "Send SMS", service: "MS"},
        {id: 2, text: "End Conversation", service: "MS"},
        {id: 1, text: "Process Transactions", service: "CP"}
    ];
    $scope.selectedPermissions = {
        selected: []
    };

    $scope.getUsers = function(customerId) {
        $http.get("/users/" + customerId).success(function(data, status) {
            $scope.allUsers = angular.fromJson(data);
        }).error(function(data){
            $scope.error = 'error: ' + data + ", " + status;
        });
    };

	$scope.getRoles = function(customerId) {
		$http.get("/roles/" + customerId).success(function(data, status) {
			$scope.allRoles = angular.fromJson(data);
		}).error(function(data, status, headers, config) {
			$scope.error = 'error: ' + data + ", " + status;
		});
	};

    $scope.clickCreateCustomerButton = function() {
        $scope.showCustomerDetails = false;
        $scope.showCreateCustomerForm = !$scope.showCreateCustomerForm;
    };

	$scope.clearCreateRoleForm = function() {
	    $scope.newRoleName = '';
	    $scope.selectedPermissions.selected = [];
	};

    $scope.clickSelectRole = function(role) {
        $scope.selectedRoleNames.push(role.name);
    };

	$scope.clickUsersTab = function() {
        toggleRoles(false);
        $scope.showUsers = true;
	};

	$scope.clickRolesTab = function() {
	    toggleUsers(false);
	    $scope.showRoles = true;
	};

	$scope.clickCreateUserButton = function () {
	    $scope.showUserDetails = false;
	    $scope.showCreateUserForm = !$scope.showCreateUserForm;
	    $scope.getRoles();
	};

	$scope.clickCreateRoleButton = function () {
	    $scope.showRoleDetails = false;
	    $scope.showCreateRoleForm = !$scope.showCreateRoleForm;
	};

	function toggleRoles(toggle) {
        $scope.showRoles = toggle;
        $scope.showCreateRoleButton = toggle;
        $scope.showCreateRoleForm = toggle;
        $scope.showRoleDetails = toggle;
	};

	function toggleUsers(toggle) {
		$scope.showUsers = toggle;
    	$scope.showUserDetails = toggle;
    	$scope.showCreateUserButton = toggle;
    	$scope.showCreateUserForm = toggle;
	};

	function clearCreateUserForm() {
	    $scope.newUserPassword = '';
        $scope.newUserPasswordRepeat = '';
        $scope.newUserFirstName = '';
        $scope.newUserLastName = '';
        $scope.newUserStatusSelect = '';
        $scope.newUserType = '';
        $scope.newUserUsername = '';
        $scope.newUserPassword = '';
        $scope.newUserPasswordRepeat = '';
	};

	$(document.body).on("click", ".nav li", function() {
		$(".nav li").removeClass("active");
		$(this).addClass("active");
        console.log($(this).attr('id'));
		if ($(this).attr('id') === 'usersTab') {
			console.log("got into users tab");
			$scope.clickUsersTab();
			$scope.$apply();
		}

		if ($(this).attr('id') === 'rolesTab') {
			console.log("got into roles tab");
            $scope.clickRolesTab();
			$scope.$apply();
		}

	});

    $scope.submitCreateCustomer = function() {
        if($scope.newCustomerName !== '') {
            var data = { customerName: $scope.newCustomerName };
            $http.post("customer", data).success(function(data, status){
                console.log("GOT A SUCCESS");
                $scope.createCustomerResult = data;
                console.log($scope.createCustomerFeedback);
                $scope.alertMessage = "Customer Created Successfully!"
                $scope.showAlertBox = true;
                $scope.newCustomerName = '';
                $scope.createCustomerFeedback = '';
                $timeout(function(){
                	location.reload(true);	
                }, 2000)
                
            }).error(function(data, status){
            console.log("GOT A FAIL");
            	if(status === 401){
            		// NEED TO FIGURE OUT HOW TO REDIRECT TO LOGIN PAGE!!!
            			$location.path("/login");
            			$scope.$apply();
            	} else {
            		$scope.createCustomerResult = data;
                    console.log($scope.createCustomerResult);
                    $scope.alertMessage = data;
                    $scope.showAlertBox = true;
            	}
                
            });
        } else {
            $scope.createCustomerFeedback = "Customer name is required.";
        }
    };

	$scope.submitCreateUser = function(customerId) {

        $scope.createUserFeedback = [];

        if($scope.newUserFirstName === '') { $scope.createUserFeedback.push("- First Name") }
        if($scope.newUserLastName === '') { $scope.createUserFeedback.push("- Last Name") }
        if($scope.newUserUsername === '') { $scope.createUserFeedback.push("- Username") }
        if($scope.newUserPassword === '' || $scope.newUserPassword !== $scope.newUserPasswordRepeat) { $scope.createUserFeedback.push("- Password") }
        if($scope.newUserType === '') { $scope.createUserFeedback.push("- User Type") }
        if($scope.selectedRoleNames.length < 1) { $scope.createUserFeedback.push("- Selected Roles") }

        if($scope.createUserFeedback.length > 0){
            $scope.showCreateUserFeedback = true;
        } else {

            if($scope.newUserStatusSelect === true){
                        $scope.newUserStatus = "Enabled"
                    } else { $scope.newUserStatus = "Disabled" }

            var data = {
            	        userName: $scope.newUserUsername,
            	        password: $scope.newUserPassword,
            	        firstName: $scope.newUserFirstName,
            	        lastName: $scope.newUserLastName,
            	        customerId: customerId,
            	        userStatusType: $scope.newUserStatus,
            	        userType: $scope.newUserType,
            	        roles: $scope.selectedRoleNames
            	    };

                    $http.post("/user", data).success(function(data, status) {
                    			$scope.createUserResult = data;
                    			console.log($scope.createUserResult);
                    			$scope.alertMessage = "User Created Successfully!"
                    			$scope.showAlertBox = true;
                    			$scope.showCreateUserFeedback = false;
                    			$timeout(function(){
                    				location.reload(true);
                    			}, 2000);
                    			
                    		}).error(function(data, status, headers, config) {
                    			$scope.error = status + ": " + data;
                    			console.log($scope.error);
                    			$scope.alertMessage = $scope.error;
                    			$scope.showAlertBox = true;
                    		});
        }





	};
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
      console.log(data.valid)
      if (data.valid) {
        console.log("DATA IS VALID");
        console.log("success url is : " + successUrl);
        document.location.href = successUrl;
      } else {
        $scope.feedback = "Invalid username / password. Please try again!";
        console.log($scope.feedback);
      }
    }).error(function(data, status, headers, config) {
      $scope.feedback = 'error: ' + data + ", " + status;
      console.log($scope.feedback);
    });
  };

}]);