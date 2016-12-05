function NavController($scope, $http) {
  
  $scope.navSelection = '';

  $scope.content = '';

  $scope.selectedCustomer = {};

  $scope.showCreateUserForm = false;

  $scope.showCustomerDetails = false;

  $scope.showUsers = true;

  $scope.showCreateUserForm = false;

  $scope.showRoles = false;

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

      if($(this).attr('id') === 'usersTab'){
        console.log("got into users tab");
        $scope.showUsers = true;
        $scope.showRoles = false;
        $scope.showCreateRoleForm = false;
        $scope.$apply();
      }

      if($(this).attr('id') === 'rolesTab'){
      console.log("got into roles tab");
        $scope.showUsers = false;
        $scope.showCreateUserForm = false;
        $scope.showRoles = true;
        $scope.$apply();
      }


    });

}