/* @ngInject */
function AdministrationController($scope, $rootScope, UserManagerService, MetadataService, UtilityService, RoleManagementService) {
    
    $scope.userDatas = [];
    
    $scope.loadRoleList = function() {
        RoleManagementService.listManagementRoles().$promise.then(function(roles) {
            $scope.roleList = roles;
        }, function(errResponse) {

        });
    };
    $scope.loadRoleList();
    $scope.searchQuery = '';
    $scope.roleFilter = 'All Roles';
    
    $scope.stats = [
        { label: 'Total Users', value: 0, icon: 'users', colorClass: 'cyan' },
        { label: 'Admins', value: 0, icon: 'shield', colorClass: 'green' },
        { label: 'Employee', value: 0, icon: 'user', colorClass: 'amber' },
        { label: 'Pilot', value: 0, icon: 'alert', colorClass: 'purple' }
    ];

    $scope.showUserModal = false;
    $scope.checkRole = false;

    $scope.disabledRoles = [];

    $scope.userForm = {
        username: '',
        password: '',
        fullname: '',
        firstName: '',
        name: '',
        email: '',
        phone: '',
        note: '',
        roles: {},
        roleName: ''
    };

    $scope.resetUserForm = function() {
        $scope.userForm = {
            username: '',
            password: '',
            fullname: '',
            firstName: '',
            name: '',
            email: '',
            phone: '',
            note: '',
            roles: {},
            roleName: ''
        };
        $scope.checkRole = false;
    };

    $scope.getInitials = function(user) {
        if (user.fullname) {
            var parts = user.fullname.split(' ');
            if (parts.length >= 2) {
                return (parts[0][0] + parts[1][0]).toUpperCase();
            }
            return user.fullname.substring(0, 2).toUpperCase();
        }
        if (user.firstName && user.name) {
            return (user.firstName[0] + user.name[0]).toUpperCase();
        }
        if (user.username) {
            return user.username.substring(0, 2).toUpperCase();
        }
        return 'U';
    };

    $scope.getRoleName = function(user) {
        if (user.roleName) return user.roleName;
        if (user.roles) {
            var roleNames = Object.keys(user.roles).filter(function(key) {
                return user.roles[key] === true;
            });
            return roleNames.length > 0 ? roleNames[0] : 'User';
        }
        return 'User';
    };

    $scope.getRoleClass = function(user) {
        var roleName = $scope.getRoleName(user).toLowerCase();
        if (roleName.indexOf('admin') >= 0 || roleName.indexOf('super') >= 0) return 'danger';
        if (roleName.indexOf('angestellter') >= 0 || roleName.indexOf('leiter') >= 0) return 'warning';
        if (roleName.indexOf('customer') >= 0 || roleName.indexOf('bearbeiter') >= 0) return 'info';
        return 'success';
    };

    $scope.getDisplayName = function(user) {
        if (user.fullname) return user.fullname;
        if (user.firstName && user.name) return user.firstName + ' ' + user.name;
        return user.username || 'Unknown';
    };

    $scope.updateStats = function() {
        var total = $scope.userDatas.length;
        var admins = 0;
        var employees = 0;
        var pilot = 0;

        $scope.userDatas.forEach(function(user) {
            var roleName = $scope.getRoleName(user).toLowerCase();
            if (roleName.indexOf('admin') >= 0) admins++;
            if (roleName.indexOf('pilot') >= 0) employees++;
            if (roleName.indexOf('pilot') >= 0) pilot++;
        });

        $scope.stats[0].value = total;
        $scope.stats[1].value = admins;
        $scope.stats[2].value = employees;
        $scope.stats[3].value = pilot;
    };
    
    $scope.addUser = function() {
        $scope.resetUserForm();
        $scope.showUserModal = true;
    };

    $scope.closeUserModal = function() {
        $scope.showUserModal = false;
        $scope.resetUserForm();
    };

    $scope.saveUser = function() {
        if (!$scope.userForm.username) {
            UtilityService.toast.error('Username is required');
            return;
        }
        if (!$scope.userForm.password) {
            UtilityService.toast.error('Password is required');
            return;
        }

        if ($scope.userForm.roleName) {
            $scope.userForm.roles[$scope.userForm.roleName] = true;
        }

        // Check if at least one role is selected
        var roles = $scope.userForm.roles;
        var check = false;
        for (var role in roles) {
            check = $scope.userForm.roles[role];
            if (check) {
                break;
            }
        }
        if (!check) {
            $scope.checkRole = true;
            UtilityService.toast.error('Please select a role');
            return;
        } else {
            $scope.checkRole = false;
        }

        // Build user data object
        var userData = {
            username: $scope.userForm.username,
            password: $scope.userForm.password,
            fullname: $scope.userForm.fullname,
            firstName: $scope.userForm.firstName,
            name: $scope.userForm.name,
            email: $scope.userForm.email,
            phone: $scope.userForm.phone,
            note: $scope.userForm.note,
            roles: $scope.userForm.roles,
            roleName: $scope.userForm.roleName
        };

        UtilityService.showLoading();
        UserManagerService.create(userData).$promise.then(function(data) {
            UtilityService.hideLoading();
            if (data != undefined && data != null) {
                if (data.resultCode != undefined && data.resultCode != null && data.resultCode != 200) {
                    UtilityService.messageErrorDilog(data.resultMessage);
                } else {
                    UtilityService.notificationDilog("User added successfully!");
                    $scope.closeUserModal();
                    $scope.getAllUser();
                }
            }
        }, function(error) {
            UtilityService.hideLoading();
            if (error.errorCode != undefined && error.errorCode != null) {
                $rootScope.error = error.errorMessage;
            }
            UtilityService.messageErrorDilog('Failed to create user. Please try again.');
        });
    };

    // Delete user
    $scope.deleteUser = function(user) {
        var displayName = $scope.getDisplayName(user);
        
        swal({
            title: 'Delete User?',
            text: 'Are you sure you want to delete "' + displayName + '"?\n\nThis action cannot be undone.',
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#dc2626',
            confirmButtonText: 'Yes, delete',
            cancelButtonText: 'Cancel',
            closeOnConfirm: false
        }, function(isConfirm) {
            if (isConfirm) {
                    UtilityService.showLoading();
                    UserManagerService.deleteUser($.param({
                        userId: user.id
                    })).$promise.then(function(data) {
                        UtilityService.hideLoading();
                        if (data != undefined && data != null)
                            if (data.resultCode != undefined && data.resultCode != null
                                    && data.resultCode != 200)
                                $rootScope.error = data.errorMessage;
                            else {
                                UtilityService.notificationDilog('Delete user successfully!');
                                $rootScope.resetForm();
                            }

                    }, function(error) {
                        UtilityService.hideLoading();
                        UtilityService.messageErrorDilog('Failed to delete user. Please try again.');
                    });
                console.log('Delete user:', user.id, user.username);
                swal({
                    title: 'Deleted!',
                    text: 'User "' + displayName + '" has been deleted.',
                    type: 'success'
                });
                $scope.getAllUser();
                $scope.$apply();
            }
        });
    };

    // Reset user password
    $scope.resetPassword = function(user) {
        var displayName = $scope.getDisplayName(user);
        
        swal({
            title: 'Reset Password?',
            text: 'Reset password for "' + displayName + '"?\n\nThe new password will be: 1234',
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#f59e0b',
            confirmButtonText: 'Yes, reset password',
            cancelButtonText: 'Cancel',
            closeOnConfirm: false
        }, function(isConfirm) {
            if (isConfirm) {
                UtilityService.showLoading();
                UserManagerService.resetPassword($.param({
                        userId: user.id
                    })).$promise.then(function(data) {
                    UtilityService.hideLoading();
                    if (data != undefined && data != null){
                        if (data.resultCode != undefined && data.resultCode != null && data.resultCode != 200){
                                UtilityService.messageErrorDilog(data.resultMessage);
                                
                            }else {
                                swal({
                                    title: 'Password Reset!',
                                    text: 'Password for "' + displayName + '" has been reset to: 1234',
                                    type: 'success'
                                });
                            }
                        }
                    }, function(error) {
                        UtilityService.hideLoading();
                        UtilityService.messageErrorDilog('Failed to resetpassword. Please try again.');
                    });
                // console.log('Reset password for user:', user.id, user.username);
            }
        });
    };

    // Load all employees
    $scope.getAllUser = function() {
        MetadataService.getAllUser().$promise.then(function(data) {
            if (data != undefined && data != null) {
                if (data.resultCode != undefined && data.resultCode != null && data.resultCode != 200) {
                    $rootScope.error = data.resultMessage;
                } else {
                    $scope.userDatas = data.data || [];
                    $scope.updateStats();
                }
            }
        }, function(error) {
            $rootScope.error = "Could not load users.";
            if (error.errorCode != undefined && error.errorCode != null) {
                $rootScope.error = error.errorMessage;
            }
        });
    };
    $scope.getAllUser();
    
}