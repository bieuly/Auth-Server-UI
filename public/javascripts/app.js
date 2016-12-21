var myApp = angular.module('myApp', ['checklist-model', 'ngAnimate']);

myApp.controller('mainController', function($scope, $http, $location, $timeout, $window) {

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
    $scope.parsedRolesId = [];


	// ROLES
	$scope.showRoles = false;
	$scope.showCreateRoleButton = false;
	$scope.showCreateRoleForm = false;
	$scope.showRoleDetails = false;
	$scope.allRoles = [];
	$scope.selectedRoles = [];
	$scope.selectedRoleNames = [];
	$scope.createRoleFeedback = '';

    // PERMISSIONS
    $scope.permissions = [];
    $scope.selectedPermissions = {
        selected: []
    };
    $scope.newRoleName = '';
    $scope.newRoleDisplayName = '';
    $scope.selectedService = '';
    $scope.allAvailableServices = [];
    $scope.specifiedServicePermissions = [];
    

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
	
	$scope.getPermissions = function() {
		$http.get("/permissions").success(function(data, status) {
			$scope.permissions = angular.fromJson(data);
			var array = getAllAvailableServices(angular.fromJson(data));
			var uniqueNames = [];
            $.each(array, function(i, el){
                if($.inArray(el, uniqueNames) === -1) uniqueNames.push(el);
            });
            $scope.allAvailableServices = uniqueNames;
		}).error(function(data, status, headers, config) {
			$scope.error = 'error: ' + data + ", " + status;
		});
	};

	function getAllAvailableServices(permissions) {
        var result = [];

        permissions.forEach(function(permission){
            var idx = result.indexOf(permission.serviceOwner);
            if(idx = -1){
                result.push(permission.serviceOwner);
            }
    	});
    	console.log("HEY: " + result);
    	return result;

	}

    $scope.clickCreateCustomerButton = function() {
        $scope.showCustomerDetails = false;
        $scope.showCreateCustomerForm = !$scope.showCreateCustomerForm;
        console.log($scope.showCreateCustomerForm);
    };

	$scope.clearCreateRoleForm = function() {
	    $scope.newRoleName = '';
	    $scope.selectedPermissions.selected = [];
	};

    $scope.selectRole = function(role) {
        var idx=$scope.allRoles.indexOf(role);
        if(idx != -1){
            $scope.allRoles.splice(idx, 1);
        }
        $scope.selectedRoles.push(role);
        $scope.selectedRoleNames.push(role.name);
    };

     $scope.deselectRole = function(role) {
        var idx=$scope.selectedRoles.indexOf(role);
                if(idx != -1){
                    $scope.selectedRoles.splice(idx, 1);
                    $scope.selectedRoleNames.splice(idx, 1);
                }
                $scope.allRoles.push(role);
     };

	$scope.clickUsersTab = function() {
        $(".nav li").removeClass("active");
        $("#usersTab").addClass("active");
        toggleRoles(false);
        $scope.showUsers = true;
	};

	$scope.clickRolesTab = function() {
	    $(".nav li").removeClass("active");
        $("#rolesTab").addClass("active");
	    toggleUsers(false);
	    $scope.showRoles = true;
	};

	$scope.clickCreateUserButton = function () {
	    $scope.showUserDetails = false;
	    $scope.getRoles($scope.selectedCustomer._id);
	    $scope.showCreateUserForm = !$scope.showCreateUserForm;
	};

	$scope.clickCreateRoleButton = function () {
	    $scope.showRoleDetails = false;
	    $scope.getPermissions();
	    $scope.showCreateRoleForm = !$scope.showCreateRoleForm;
	};

	$scope.selectService = function (service) {
	        $("#services").prop('checked', false);
            $scope.specifiedServicePermissions = [];
            $scope.selectedPermissions = [];
	        $scope.permissions.forEach(function(permission) {
                if(permission.serviceOwner === service){
                    $scope.specifiedServicePermissions.push(permission);
                }
            })


	};

	function getPermissionServiceOwnerArray(permissions){
		var serviceOwners = {};
		
		permissions.forEach(function(permission){
			if(!serviceOwners[permission.serviceOwner]){
				serviceOwners[permission.serviceOwner] = [permission.permissionId];
			} else {
				serviceOwners[permission.serviceOwner].push(permission.permissionId);
			}
		});
		
		console.log(serviceOwners);
		return permissions.length <= 0 ? null : serviceOwners;
	};

	$scope.parseRolesIdArray = function() {
	    for (var roleString in $scope.selectedUser.rolesId){
	        if($scope.selectedUser.rolesId.hasOwnProperty(roleString)){
	            var both = roleString.split("_");
	            var serviceOwner = both[0];
	            var roleName = both[1];
	            var data = {"serviceOwner": serviceOwner, "roleName": roleName};
	            $scope.parsedRolesId.push(data);
	        }
	    }
	}
	
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
            $http.post("/customer", data).success(function(data, status){
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
            	if(status === 401){
            		// NEED TO FIGURE OUT HOW TO REDIRECT TO LOGIN PAGE!!!
            		    $scope.alertMessage = data;
            		    $scope.showAlertBox = true;
            		    $timeout(function(){
            		        $window.location.href = '/';
                            $window.location.href;
            		    }, 1000);
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
                    			if(status === 401){
                                    // NEED TO FIGURE OUT HOW TO REDIRECT TO LOGIN PAGE!!!
                                        $scope.alertMessage = data;
                                        $scope.showAlertBox = true;
                                        $timeout(function(){
                                            $window.location.href = '/';
                                            $window.location.href;
                                        }, 1000);
                                } else {
                                    $scope.createCustomerResult = data;
                                    console.log($scope.createCustomerResult);
                                    $scope.alertMessage = data;
                                    $scope.showAlertBox = true;
                                }
                    		});
        }





	};
	
	$scope.clickSubmitCreateRole = function() {
		$scope.createRoleFeedback = [];
		
		if($scope.newRoleName === '') { $scope.createRoleFeedback.push("- Name") }
		if($scope.newRoleDisplayName === '') { $scope.createRoleFeedback.push("- Display Name") }
		
		var permissions2 = getPermissionServiceOwnerArray($scope.selectedPermissions.selected);
		if(permissions2 === null) { $scope.createRoleFeedback.push("- Permissions") }
		
		console.log($scope.selectedPermissions.selected);
		
		if($scope.createRoleFeedback.length > 0) {
			$scope.showCreateRoleFeedback = true;
		} else {

			var data = {
					"name": $scope.newRoleName,
					"displayName": $scope.newRoleDisplayName,
					"customerId": $scope.selectedCustomer._id,
					"permissions": permissions2
			};

            $http.post("role", data).success(function(data, status){
                console.log("GOT A SUCCESS");
                $scope.createRoleResult = data;
                $scope.alertMessage = "Role Created Successfully!"
                $scope.showAlertBox = true;
                $scope.newRoleName = '';
                $scope.newRoleDisplayName = '';
                $scope.createRoleFeedback = '';
                $timeout(function(){
                	location.reload(true);	
                }, 2000);

            }).error(function(data, status){
                    if(status === 401){
                        // NEED TO FIGURE OUT HOW TO REDIRECT TO LOGIN PAGE!!!
                            $scope.alertMessage = data;
                            $scope.showAlertBox = true;
                            $timeout(function(){
                                $window.location.href = '/';
                                $window.location.href;
                            }, 1000);
                    } else {
                        $scope.createCustomerResult = data;
                        console.log($scope.createCustomerResult);
                        $scope.alertMessage = data;
                        $scope.showAlertBox = true;
                    }
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