/**
 * gpstracker.Hub - Login Controller
 * Similar to gpstracker LoginController
 */
/* @ngInject */
function LoginController($scope, $rootScope, $state, $cookies, SecurityService, UtilityService, AuthService) {

    $scope.rememberMe = true;
    $scope.username = '';
    $scope.password = '';
    $scope.error = '';

    // Check if already logged in
    var authToken = AuthService.getAuthToken();
    if (authToken) {
        // Restore user from cookies if available - AuthService does this on init, but we can double check
        var savedUser = $cookies.getObject('gpstracker_hub_user');
        if (savedUser && !AuthService.getUser()) {
            AuthService.setUser(savedUser);
        }
        
        if (AuthService.getUser()) {
             $state.go('main.dashboard');
             return;
        }
    }

    // Login function - similar to gpstracker
    $scope.login = function() {
        if (!$scope.username || !$scope.password) {
            $scope.error = 'Please enter username and password';
            return;
        }

        $scope.error = '';
        UtilityService.showLoading();

        SecurityService.authenticate(
            $.param({
                username: $scope.username,
                password: $scope.password
            }),
            function(authenticationResult) {
                UtilityService.hideLoading();

                var authToken = (authenticationResult && authenticationResult.left) 
                    ? authenticationResult.left.token 
                    : null;

                if (authToken == null) {
                    $scope.error = 'Login failed. Please check your username and password.';
                    return;
                }

                // Save token to cookies
                if ($scope.rememberMe) {
                    $cookies.put('gpstracker_hub_authToken', authToken);
                } else {
                    $cookies.put('gpstracker_hub_authToken', authToken); // Session cookie if expiration not set, but angular-cookies usually sets it.
                }

                // Get user info and navigate
                SecurityService.get(function(user) {
                    // Use AuthService to set user state centrally
                    AuthService.setUser(user);
                    
                    // Navigate to dashboard
                    $state.go('main.dashboard');
                }, function(error) {
                    // If get user fails, still navigate but might have limited access
                    $state.go('main.dashboard');
                });
            },
            function(error) {
                UtilityService.hideLoading();
                $scope.error = 'Login failed. Please try again.';
            }
        );
    };

    // Demo login (for testing without backend)
    $scope.demoLogin = function() {
        $scope.error = '';
        UtilityService.showLoading();

        setTimeout(function() {
            $scope.$apply(function() {
                UtilityService.hideLoading();

                var mockUser = {
                    username: $scope.username || 'demo@gpstracker-tech.com',
                    fullname: 'Demo User',
                    roles: { 'ROLE_ADMIN': true }
                };

                var mockToken = 'mock_token_' + Date.now();
                $cookies.put('gpstracker_hub_authToken', mockToken);
                
                // Use AuthService
                AuthService.setUser(mockUser);

                $state.go('main.dashboard');
            });
        }, 500);
    };

    // Toggle password visibility
    $scope.changeTypeInput = function(id) {
        var input = document.getElementById(id);
        if (input) {
            input.type = input.type === 'password' ? 'text' : 'password';
        }
    };
}
