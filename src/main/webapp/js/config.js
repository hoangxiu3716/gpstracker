/**
 * gpstracker.Hub - Router Configuration
 * UI-Router state definitions
 */

function config($stateProvider, $urlRouterProvider, $locationProvider, $httpProvider) {
    
    // Default route
    $urlRouterProvider.otherwise('/login');
    
    // Remove '!' from hash URLs
    $locationProvider.hashPrefix('');
    
    var adhsAppConfig = {
        useAuthTokenHeader: true
    };
    
    // Error handling interceptor
    $httpProvider.interceptors.push(['$q', '$cookies', '$location', '$injector', function($q, $cookies, $location, $injector) {
        return {
            'responseError': function(rejection) {
                var status = rejection.status;
                if (status === 401) {
                    // Inject AuthService lazily to avoid circular dependency
                    var AuthService = $injector.get('AuthService');
                    AuthService.logout(); // Use AuthService to clear state and redirect
                }
                return $q.reject(rejection);
            }
        };
    }]);
    
    // Auth token interceptor
    $httpProvider.interceptors.push(['$q', '$location', '$injector', function($q, $location, $injector) {
        return {
            'request': function(config) {
                var isRestCall = config.url.indexOf('rest') >= 0;
                // Inject AuthService lazily to avoid circular dependency
                var AuthService = $injector.get('AuthService');
                var authToken = AuthService.getAuthToken();
                
                if (isRestCall && authToken != null && authToken != undefined) {
                    if (adhsAppConfig.useAuthTokenHeader) {
                        config.headers['X-Auth-Token'] = authToken;
                    } else {
                        config.url = config.url + '?token=' + authToken;
                    }
                    
                    // Disable IE ajax request caching
                    if (config.method == 'GET') {
                        var separator = config.url.indexOf('?') === -1 ? '?' : '&';
                        config.url = config.url + separator + 'noCache=' + new Date().getTime();
                    }
                }
                return config || $q.when(config);
            }
        };
    }]);
    
    $stateProvider
        // Login state
        .state('login', {
            url: '/login',
            templateUrl: 'views/login.html',
            controller: LoginController
        })
        
        // Main layout state
        .state('main', {
            abstract: true,
            templateUrl: 'views/layout.html',
            controller: LayoutController,
            resolve: {
                authRequired: ['AuthService', '$q', '$state', function(AuthService, $q, $state) {
                    var deferred = $q.defer();
                    if (AuthService.getAuthToken()) {
                        deferred.resolve();
                    } else {
                        $state.go('login');
                        deferred.reject();
                    }
                    return deferred.promise;
                }]
            }
        })
        
        // Dashboard
        .state('main.dashboard', {
            url: '/home',
            templateUrl: 'views/dashboard.html',
            controller: DashboardController,
            data: { pageTitle: 'Home' }
        })
        
        // Mission Control
        .state('main.missioncontrol', {
            url: '/missioncontrol',
            templateUrl: 'views/missioncontrol.html',
            controller: MissionControlController,
            data: { pageTitle: 'Mission Control' }
        })

        // Mission Details
        .state('main.missiondetail', {
            url: '/missiondetail/:missionId',
            templateUrl: 'views/missiondetail.html',
            controller: MissionDetailController,
            params: {
            	missionId : null
            },
            data: { pageTitle: 'Mission Details' }
        })

        // Pilot Academy
        .state('main.academy', {
            url: '/academy',
            templateUrl: 'views/academy.html',
            controller: AcademyController,
            data: { pageTitle: 'Pilot Academy' }
        })
        
        // Data Center
        .state('main.datacenter', {
            url: '/datacenter',
            templateUrl: 'views/datacenter.html',
            controller: DataCenterController,
            data: { pageTitle: 'Data Center' }
        })
        
        // AE Units
        .state('main.aeunits', {
            url: '/aeunits',
            templateUrl: 'views/aeunits.html',
            controller: AEUnitsController,
            data: { pageTitle: 'AE Units' }
        })
        
        // Profile
        .state('main.profile', {
            url: '/profile',
            templateUrl: 'views/profile.html',
            controller: ProfileController,
            data: { pageTitle: 'Profile' }
        })
        
        // Settings
        .state('main.settings', {
            url: '/settings',
            templateUrl: 'views/settings.html',
            controller: SettingsController,
            data: { pageTitle: 'Settings' }
        })
        
        // Administration
        .state('main.administration', {
            url: '/administration',
            templateUrl: 'views/administration.html',
            controller: AdministrationController,
            data: { pageTitle: 'Administration' }
        })
        
        // Server Health
        .state('main.serverhealth', {
            url: '/serverhealth',
            templateUrl: 'views/serverhealth.html',
            controller: ServerHealthController,
            data: { pageTitle: 'Server Health' }
        })
        
        // Interfaces
        .state('main.interfaces', {
            url: '/interfaces',
            templateUrl: 'views/interfaces.html',
            controller: InterfacesController,
            data: { pageTitle: 'Interfaces' }
        })
        
        // Subscription
        .state('main.subscription', {
            url: '/subscription',
            templateUrl: 'views/subscription.html',
            controller: SubscriptionController,
            data: { pageTitle: 'Subscription' }
        })

        // About
        .state('main.about', {
            url: '/about',
            templateUrl: 'views/about.html',
            controller: 'AboutController',
            data: { pageTitle: 'About AE.HUB' }
        });
}

var app = angular.module('gpstrackerHub');
app.config(['$stateProvider', '$urlRouterProvider', '$locationProvider', '$httpProvider', config]);

app.run(function($rootScope, $location, $state, $cookies, SecurityService, AuthService, gpstrackerActionLogService) {
    
    $rootScope.$state = $state;

    $rootScope.hasRole = function(role) {
        return AuthService.hasRole(role);
    };
    $rootScope.hasAnyRole = function(roles) {
        return AuthService.hasAnyRole(roles);
    };
    $rootScope.getAuthToken = AuthService.getAuthToken;
    $rootScope.logout = AuthService.logout;
    $rootScope.getUser = AuthService.getUser;
    $rootScope.getCurrentUser = AuthService.getCurrentUser;

    $rootScope.$watch(AuthService.getUser, function(newUser) {
        $rootScope.user = newUser;
    }, true); // Deep watch for user object
    
    $rootScope.$watch(AuthService.getCurrentUser, function(newCurrentUser) {
        $rootScope.currentUser = newCurrentUser;
    }, true); // Deep watch for currentUser object
    
    $rootScope.go = function(path) {
        $location.path(path);
    };
    
    $rootScope.buildLog = function(gpstrackerActionLogInfo) {
        gpstrackerActionLogService.create(gpstrackerActionLogInfo).$promise.then(function (data) {
            if (data != undefined && data != null){
            }
        }, function (error) {
        });
    };
    $rootScope.resetForm = function(){
    	$state.go($state.current.name, {}, {reload: true});
    }
    // Initialize - check if already logged in
    var authToken = AuthService.getAuthToken();
    if (authToken !== undefined && authToken != null) {
        // First try to restore user from cookies (already handled by AuthService constructor)
        
        // Then try to get fresh user data from server
        SecurityService.get(function(user) {
            AuthService.setUser(user);
        }, function(error) {
            // If API fails but we have saved user, continue with saved data
            if (!AuthService.getUser()) {
                AuthService.logout(); // If no user data (from cookie or API), log out
            }
        });
    }
});
