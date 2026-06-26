/**
 * gpstracker.Hub - Profile Controller
 */
/* @ngInject */
function ProfileController($scope, $rootScope, $cookies, UtilityService, ProfileService, MetadataService, gpstrackerActionLogService) {
    
    // Get user data from $rootScope or cookies
    var savedUser = $cookies.getObject('gpstracker_hub_user');
    $scope.user = $rootScope.user || savedUser || {};
    
    $scope.isEditing = false;
    $scope.editForm = {};
    
    // Password change form
    $scope.passwordForm = {};
    $scope.showCurrentPassword = false;
    $scope.showNewPassword = false;
    $scope.showConfirmPassword = false;
    
    $scope.pagination = {
        currentPage: 0, 
        pageSize: 10,
        totalItems: 0,
        totalPages: 0,
        pages: [] 
    };

    $scope.stats = [
        { label: 'Missions Completed', value: 156, icon: 'rocket', colorClass: 'cyan' },
        { label: 'Success Rate', value: '98.5%', icon: 'check', colorClass: 'green' },
        { label: 'Certification', value: 'Level 5', icon: 'award', colorClass: 'purple' }
    ];
    
    $scope.getPaginationRange = function(currentPage, totalPages, displayRange = 5) {
        var range = [];
        var start;

        // Use 1-based page for calculation
        var current = currentPage + 1;

        if (totalPages <= displayRange) {
            // show all pages
            for (let i = 1; i <= totalPages; i++) {
                range.push(i);
            }
        } else {
            // more pages than can be displayed
            let halfRange = Math.floor(displayRange / 2);

            if (current <= halfRange + 1) {
                start = 1;
            } else if (current >= totalPages - halfRange) {
                start = totalPages - displayRange + 1;
            } else {
                start = current - halfRange;
            }
            
            var end = Math.min(start + displayRange - 1, totalPages);

            if (start > 1) {
                range.push(1);
                if (start > 2) {
                    range.push('...');
                }
            }

            for (let i = start; i <= end; i++) {
                range.push(i);
            }

            if (end < totalPages) {
                if (end < totalPages - 1) {
                    range.push('...');
                }
                range.push(totalPages);
            }
        }
        return range;
    }

    $scope.loadRecentActivity = function(page) {
        UtilityService.showLoading();
        gpstrackerActionLogService.list({
            page: page,
            size: $scope.pagination.pageSize,
            field : 'creationTime',
            direction : 'desc',
            filter:{username:$scope.user.username}
        }).$promise.then(function(pageData) {
            $scope.activity = pageData.data;
            $scope.pagination.totalItems = pageData.totalElements;
            $scope.pagination.totalPages = pageData.totalPages;
            $scope.pagination.currentPage = pageData.number; // API returns current page (0-based)
            $scope.pagination.pages = $scope.getPaginationRange($scope.pagination.currentPage, $scope.pagination.totalPages);
            
            UtilityService.hideLoading();
        }, function(errResponse) {
            UtilityService.hideLoading();
            UtilityService.toast.error('Could not load recent activity.');
        });
    };

    $scope.goToPage = function(page) { // page is 1-based from UI
        if (typeof page === 'number') {
            $scope.loadRecentActivity(page - 1); // Convert to 0-based for API
        }
    };
    
    $scope.nextPage = function() {
        if ($scope.pagination.currentPage < $scope.pagination.totalPages - 1) {
            $scope.loadRecentActivity($scope.pagination.currentPage + 1);
        }
    };

    $scope.previousPage = function() {
        if ($scope.pagination.currentPage > 0) {
            $scope.loadRecentActivity($scope.pagination.currentPage - 1);
        }
    };

    // Initial load
    $scope.loadRecentActivity($scope.pagination.currentPage);
    
    $scope.getAvatarInitials = function() {
        if ($scope.user.avatar) return $scope.user.avatar;
        if ($scope.user.fullname) return $scope.user.fullname.substring(0, 2).toUpperCase();
        if ($scope.user.username) return $scope.user.username.substring(0, 2).toUpperCase();
        return 'U';
    };
    
    $scope.getRoleName = function() {
        if ($scope.user.roleName) return $scope.user.roleName;
        if ($scope.user.roles) {
            var roleKeys = Object.keys($scope.user.roles);
            if (roleKeys.length > 0) {
                return roleKeys[0].replace('ROLE_', '').replace(/_/g, ' ');
            }
        }
        return 'User';
    };
    
    $scope.editProfile = function() {
        $scope.editForm = {
            username: $scope.user.username || '',
            fullname: $scope.user.fullname || '',
            email: $scope.user.email || '',
            firstName: $scope.user.firstName || '',
            name: $scope.user.name || '',
            phone: $scope.user.phone || '',
            note: $scope.user.note || ''
        };
        $scope.isEditing = true;
    };
    
    $scope.cancelEdit = function() {
        $scope.isEditing = false;
        $scope.editForm = {};
    };
    
    $scope.saveProfile = function() {
        UtilityService.showLoading();
        
        $scope.user.username = $scope.editForm.username;
        $scope.user.fullname = $scope.editForm.fullname;
        $scope.user.email = $scope.editForm.email;
        $scope.user.firstName = $scope.editForm.firstName;
        $scope.user.name = $scope.editForm.name;
        $scope.user.phone = $scope.editForm.phone;
        $scope.user.note = $scope.editForm.note;
        
        MetadataService.updateProfile($scope.user).$promise.then(
            function(response) {
                UtilityService.hideLoading();
                if (response != undefined && response != null){
                    if (response.resultCode != undefined && response.resultCode != null && response.resultCode != 200)
                        $rootScope.error = response.resultMessage;
                    else {
                        $rootScope.user = $scope.user;
                        $cookies.putObject('gpstracker_hub_user', $scope.user);
                        $scope.isEditing = false;
                        UtilityService.notificationDilog('Profile updated successfully!');
                    }
    		    }
            },
            function(error) {
                UtilityService.hideLoading();
                UtilityService.messageErrorDilog('Failed to update profile. Please try again.');
                // $rootScope.error = "Das vortragende konnte nicht gelöscht werden.";
                // if (error.errorCode != undefined && error.errorCode != null)
                //     $rootScope.error = error.errorMessage;
            }
        );
    };
    
    // Change Password
    $scope.changePassword = function() {
        // Validate passwords match
        if ($scope.passwordForm.newPassword !== $scope.passwordForm.confirmPassword) {
            UtilityService.messageErrorDilog('New passwords do not match!');
            return;
        }
        
        // Validate password strength (min 8 chars, uppercase, lowercase, number)
        var password = $scope.passwordForm.newPassword;
        // var hasUpperCase = /[A-Z]/.test(password);
        var hasLowerCase = /[a-z]/.test(password);
        var hasNumber = /[0-9]/.test(password);
        var hasMinLength = password.length >= 8;
        
        if (!hasMinLength || !hasLowerCase || !hasNumber) {
            // UtilityService.messageErrorDilog('Password must be at least 8 characters and include uppercase, lowercase, and number.');
            UtilityService.messageErrorDilog('Password must be at least 8 characters, lowercase, and number.');
            return;
        }
        
        UtilityService.showLoading();
        
        var passwordData = {
            password: $scope.passwordForm.currentPassword,
            newPassword: $scope.passwordForm.newPassword,
            repeatPassword: $scope.passwordForm.confirmPassword
        };
        
        // TODO: Call your backend API here
        ProfileService.changePassword(passwordData).$promise.then(
            function(response) {
                if (response.errorCode != undefined && response.errorCode != null) {
                    UtilityService.messageErrorDilog(response.resultMessage || 'Failed to change password.');
                     $rootScope.error = response.errorMessage;
                }
                else {
                    UtilityService.hideLoading();
                    UtilityService.notificationDilog('Password changed successfully!');
                    $scope.resetPasswordForm();
                    $rootScope.logout();
                }
            },
            function(error) {
                UtilityService.hideLoading();
                UtilityService.messageErrorDilog('Failed to change password. Please try again.');
            }
        );
    };
    $scope.resetPasswordForm = function() {
        $scope.passwordForm = {};
        $scope.showCurrentPassword = false;
        $scope.showNewPassword = false;
        $scope.showConfirmPassword = false;
    };
}
