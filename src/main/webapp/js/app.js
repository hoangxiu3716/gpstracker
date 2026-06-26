/**
 * gpstracker.Hub - AngularJS Application
 */
(function() {
    angular.module('gpstrackerHub', [
        'ui.router',
        'ngCookies',
        'ngResource',
        'pascalprecht.translate',
        'gpstrackerHub.services'
    ]);

    var services = angular.module('gpstrackerHub.services', ['ngResource']);
    
    // App State Service
    services.factory('AppStateService', ['$window', '$rootScope', function($window, $rootScope) {
        var appState = {};
        
        // Dark Mode
        var savedTheme = $window.localStorage.getItem('theme');
        appState.darkMode = savedTheme !== 'light';

        appState.toggleDarkMode = function() {
            appState.darkMode = !appState.darkMode;
            $window.localStorage.setItem('theme', appState.darkMode ? 'dark' : 'light');
            if (appState.darkMode) {
                $window.document.body.classList.add('dark');
            } else {
                $window.document.body.classList.remove('dark');
            }
        };

        // Sidebar state
        appState.sidebarOpen = $window.innerWidth >= 1025;
        appState.sidebarCollapsed = $window.innerWidth < 1025;

        appState.toggleSidebar = function() {
            if ($window.innerWidth < 1025) {
                appState.sidebarOpen = !appState.sidebarOpen;
            } else {
                appState.sidebarCollapsed = !appState.sidebarCollapsed;
            }
        };

        appState.closeSidebar = function() {
            if ($window.innerWidth < 1025) {
                appState.sidebarOpen = false;
            }
        };
        
        angular.element($window).bind('resize', function() {
            var width = $window.innerWidth;
            if (width < 1025) {
                appState.sidebarOpen = false;
                appState.sidebarCollapsed = true;
            } else {
                appState.sidebarOpen = true;
                appState.sidebarCollapsed = false;
            }
            $rootScope.$applyAsync();
        });

        // Submenu states
        appState.profileSubmenuOpen = false;
        appState.adminSubmenuOpen = false;

        appState.toggleProfileSubmenu = function() {
            appState.profileSubmenuOpen = !appState.profileSubmenuOpen;
        };

        appState.toggleAdminSubmenu = function() {
            appState.adminSubmenuOpen = !appState.adminSubmenuOpen;
        };

        // Initialize dark mode on load
        if (appState.darkMode) {
            $window.document.body.classList.add('dark');
        }

        // Expose state via getter functions for components to bind to
        appState.getDarkMode = function() { return appState.darkMode; };
        appState.getSidebarOpen = function() { return appState.sidebarOpen; };
        appState.getSidebarCollapsed = function() { return appState.sidebarCollapsed; };
        appState.getProfileSubmenuOpen = function() { return appState.profileSubmenuOpen; };
        appState.getAdminSubmenuOpen = function() { return appState.adminSubmenuOpen; };
        
        return appState;
    }]);
    
    // Auth Service
    services.factory('AuthService', ['$cookies', '$state', '$rootScope', 'SecurityService', function($cookies, $state, $rootScope, SecurityService) {
        var authService = {};

        var _user = null;
        var _currentUser = null;

        authService.setUser = function(user) {
            _user = user;
            if (user) {
                _currentUser = {
                    name: user.fullname || user.username,
                    email: user.email || user.username || '',
                    role: user.roles ? Object.keys(user.roles)[0] : 'User',
                    avatar: (user.fullname || user.username || 'U').substring(0, 2).toUpperCase(),
                    firstName : user.firstName || ''
                };
                $cookies.putObject('gpstracker_hub_user', user);
            } else {
                _currentUser = null;
                $cookies.remove('gpstracker_hub_user');
            }
        };

        authService.getUser = function() {
            return _user;
        };

        authService.getCurrentUser = function() {
            return _currentUser;
        };

        authService.getAuthToken = function() {
            try {
                return $cookies.get('gpstracker_hub_authToken');
            } catch (e) {
                return null;
            }
        };

        authService.hasRole = function(role) {
            if (!_user || !_user.roles) {
                return false;
            }
            // Check if roles is an array
            if (angular.isArray(_user.roles)) {
                return _user.roles.indexOf(role) !== -1;
            }
            // Check if roles is an object
            if (_user.roles[role] === undefined) {
                return false;
            }
            return _user.roles[role];
        };

        authService.hasAnyRole = function(roles) {
            var hasRole = false;
            roles.forEach(function(role) {
                if (authService.hasRole(role)) {
                    hasRole = true;
                }
            });
            return hasRole;
        };

        authService.logout = function() {
            authService.setUser(null); // Clear user data
            $cookies.remove('gpstracker_hub_authToken');
            $cookies.remove('gpstracker_hub_user'); // Ensure cookie is removed
            $state.go('login', {}, { reload: true });
        };

        // Initialize from cookie on service load
        var savedUser = $cookies.getObject('gpstracker_hub_user');
        if (savedUser && authService.getAuthToken()) {
            authService.setUser(savedUser);
        }
        
        return authService;
    }]);

    // Settings factory
    services.factory('settings', ['$rootScope', function($rootScope) {
        var settings = {
            appName: 'AE.Hub',
            companyName: 'gpstracker Technologies'
        };
        return settings;
    }]);

    // Security Service
    services.factory('SecurityService', /* @ngInject */ function($resource) {
        return $resource(
            'rest/security/:action/:id',
            { id: '@id' },
            {
                authenticate: {
                    method: 'POST',
                    params: { 'action': 'authenticate' },
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                }
            }
        );
    });

    // Profile Service
    services.factory('ProfileService', /* @ngInject */ function($resource) {
        return $resource(
            'rest/profile/:action',
            { },
            {
                updateProfile: {
                    method: 'POST',
                    params: { 'action': 'updateProfile' }
                },
                changePassword: {
                    method: 'POST',
                    params: { 'action': 'changePassword' }
                }
            }
        );
    });
    // Profile Service
    services.factory('MetadataService', /* @ngInject */ function($resource) {
        return $resource(
            'rest/metadata/:action',
            { },
            {
                updateProfile: {
                    method: 'POST',
                    params: { 'action': 'updateProfile' }
                },
                getAllUser: {
                    method: 'GET',
                    params: { 'action': 'getAllUser' }
                }
            }
        );
    });

    // Fleet Service - Shared robot data
    services.factory('FleetService', ['$rootScope', function($rootScope) {
        var fleetService = {};
        
        // The "Real" Robot
        var realRobot = {
            id: 'fahrdummy-01',
            name: 'AE.01 Fahrdummy',
            model: 'AE-X1 Pro',
            connected: false,
            status: 'Offline',
            statusClass: 'danger',
            battery: 0,
            location: 'Warehouse A - Zone 1',
            lastActive: 'N/A',
            mode: 'N/A',
            uptime: 0,
            isMoving: false,
            currentTask: 'Disconnected'
        };

        // Static Maintenance Robots
        var staticRobots = [
            { id: 'AE-1002', name: 'AE.02 Picker', model: 'AE-X1 Pro', status: 'Maintenance', statusClass: 'warning', battery: 45, location: 'Service Bay 1', lastActive: '1h ago', type: 'warning' },
            { id: 'AE-1005', name: 'AE.05 Lifter', model: 'AE-X2 Standard', status: 'Maintenance', statusClass: 'danger', battery: 12, location: 'Service Bay 2', lastActive: '5h ago', type: 'danger' },
            { id: 'AE-1012', name: 'AE.12 Carrier', model: 'AE-X1 Pro', status: 'Maintenance', statusClass: 'warning', battery: 88, location: 'Storage Zone C', lastActive: '20m ago', type: 'warning' }
        ];

        fleetService.getRealRobot = function() {
            return realRobot;
        };

        fleetService.getStaticRobots = function() {
            return staticRobots;
        };

        fleetService.getAllUnits = function() {
            var units = [realRobot];
            return units.concat(staticRobots);
        };

        fleetService.updateRealRobot = function(newData) {
            angular.extend(realRobot, newData);
            // Update status class based on status
            if (realRobot.connected) {
                realRobot.statusClass = 'success';
                realRobot.lastActive = 'Just now';
            } else {
                realRobot.statusClass = 'danger';
            }
            $rootScope.$broadcast('fleet.updated', realRobot);
        };

        return fleetService;
    }]);

    // UserManager Service
    services.factory('UserManagerService', /* @ngInject */ function($resource) {
        return $resource(
            'rest/manage/user/:action',
            { },
            {
                getAllEmployee: {
                    method: 'GET',
                    params: { 'action': 'getAllEmployee' }
                },
                resetPassword: {
                    method: 'POST',
                    params: { 'action': 'resetPassword' }
                },
                deleteUser: {
                    method: 'POST',
                    params: { 'action': 'deleteUser' }
                },
                create: {
                    method: 'POST'
                }
            }
        );
    });

    services.factory('RoleManagementService', /* @ngInject */ function ($resource) {

        return $resource(
            'rest/manage/role/:action/:id', // ws url
            {}, //parameters default
            {

                list: {
                    method: 'GET',
                    params: {'action': 'list'},
                    isArray: false
                },

                listManagementRoles: {
                    method: 'GET',
                    params: {'action': 'listManagementRoles'},
                    isArray: true
                }
            }
        );
    });

    // Action Log Service
    services.factory('gpstrackerActionLogService', /* @ngInject */ function($resource) {
        return $resource(
            'rest/gpstrackeractionlog/:action',
            { },
            {
                list: {
                    method: 'GET',
                    params: {'action': 'list'},
                    isArray: false
                },
                create: {
                    method: 'POST',
                    params: {'action': 'create'}
                },
                getAllgpstrackerActionLog: {
                    method: 'GET',
                    params: {'action': 'getAllgpstrackerActionLog'},
                    isArray: false
                }
            }
        );
    });

    // API Base URL for Robot Services
    // Uses local backend proxy to forward requests to https://docs.techvisioncloud.pl
    var ROBOT_API_BASE = 'rest/robot-api';

    // Robot Service - Main robot controls
    services.factory('RobotService', /* @ngInject */ function($resource) {
        return $resource(
            ROBOT_API_BASE + '/api/v1/robot/:action/:subaction/:id',
            { },
            {
                getStatus: { method: 'GET', params: { action: 'status' } },
                getHealthDetails: { method: 'GET', params: { action: 'health', subaction: 'details' } },
                setReady: { method: 'POST', params: { action: 'set_ready' } },
                joystickFrame: { method: 'POST', params: { action: 'joystick', subaction: 'frame' } },
                moveToProduct: { method: 'POST', params: { action: 'move', subaction: 'to_product' } },
                moveToBox1: { method: 'POST', params: { action: 'move', subaction: 'to_box_1' } },
                moveToBox2: { method: 'POST', params: { action: 'move', subaction: 'to_box_2' } },
                moveToTransport: { method: 'POST', params: { action: 'move', subaction: 'to_transport_position' } },
                autotake: { method: 'POST', params: { action: 'move', subaction: 'autotake' } },
                getPositionsList: { method: 'GET', params: { action: 'robot_positions', subaction: 'list' }, isArray: true },
                savePosition: { method: 'POST', params: { action: 'robot_positions', subaction: 'save' } },
                runPosition: { method: 'POST', params: { action: 'robot_positions', subaction: 'run', position_id: '@position_id' } },
                deletePosition: { method: 'POST', params: { action: 'robot_positions', subaction: 'delete' } },
                getTaskStatus: { method: 'GET', params: { action: 'tasks', subaction: 'status' } },
                getCurrentTask: { method: 'GET', params: { action: 'tasks', subaction: 'current' } },
                cancelTask: { method: 'POST', params: { action: 'tasks', subaction: 'cancel' } },
                cancelCurrentTask: { method: 'POST', params: { action: 'tasks', subaction: 'cancel_current' } },
                estop: { method: 'POST', params: { action: 'tasks', subaction: 'estop' } },
                recover: { method: 'POST', params: { action: 'safety', subaction: 'recover' } }
            }
        );
    });

    // XArm Service - Robotic arm controls
    services.factory('XArmService', /* @ngInject */ function($resource) {
        return $resource(
            ROBOT_API_BASE + '/api/v1/xarm/:action/:subaction',
            { },
            {
                getStatus: { method: 'GET', params: { action: 'status' } },
                getCurrentPosition: { method: 'GET', params: { action: 'current_position' } },
                getJointsPosition: { method: 'GET', params: { action: 'joints_position' } },
                changeJoints: { method: 'POST', params: { action: 'move', subaction: 'change_joints' } },
                
                changePose: { method: 'POST', params: { action: 'move', subaction: 'change_pose' } },
                changeToolPosition: { method: 'POST', params: { action: 'move', subaction: 'change_tool_position' } },
                complexMove: { method: 'POST', params: { action: 'complex_move', subaction: 'with_joints_dict' } },
                gripperTake: { method: 'POST', params: { action: 'gripper', subaction: 'take' } },
                gripperDrop: { method: 'POST', params: { action: 'gripper', subaction: 'drop' } },
                faultReset: { method: 'POST', params: { action: 'fault_reset' } }
            }
        );
    });

    // Igus Lift Service
    services.factory('IgusService', /* @ngInject */ function($resource) {
        return $resource(
            ROBOT_API_BASE + '/api/v1/igus/drive/:action',
            { },
            {
                getStatus: { method: 'GET', params: { action: 'status' } },
                telemetry: { method: 'GET', params: { action: 'telemetry' } },
                lastestTrace: { method: 'GET', params: { action: 'trace/lastest' } },
                getPosition: { method: 'GET', params: { action: 'position' } },
                isMotion: { method: 'GET', params: { action: 'is_motion' } },
                move: { method: 'POST', params: { action: 'move_to_position' } },
                reference: { method: 'POST', params: { action: 'reference' } },
                faultReset: { method: 'POST', params: { action: 'fault_reset' } }
            }
        );
    });

    // Symovo AGV Service
    services.factory('SymovoService', /* @ngInject */ function($resource) {
        return $resource(
            ROBOT_API_BASE + '/api/v1/symovo/:action/:subaction/:id',
            { id: '@id' },
            {
                getStatus: { method: 'GET', params: { action: 'status' } },
                getStatusNavigation: {
                    method: 'GET', 
                    url: ROBOT_API_BASE + '/api/v1/symovo/api/v1/robots/:robotId/status/navigation',
                    params: { robotId: '@robotId' }
                },
                getPose: { method: 'GET', params: { action: 'pose' } },
                getMap: { method: 'GET', params: { action: 'map' }, isArray: true },
                getChargingStations: { method: 'GET', params: { action: 'charging_stations' } },
                goToPose: { method: 'POST', params: { action: 'go_to_pose' } },
                goToChargingStation: { method: 'POST', params: { action: 'go_to_charging_station' } },
                getTransport: { method: 'GET', params: { action: 'transport' } },
                faultReset: { method: 'GET', params: { action: 'fault_reset' } },
                // New Position Management for Symovo
                getPositionsList: { method: 'GET', params: { action: 'robot_positions', subaction: 'list' }, isArray: true },
                savePosition: { method: 'POST', params: { action: 'robot_positions', subaction: 'save' } },
                deletePosition: { method: 'DELETE', params: { action: 'robot_positions' } },
                
                // Customer specific Navigation Commands (Plural 'robots' and ID in middle)
                navigateTo: { 
                    method: 'POST', 
                    url: ROBOT_API_BASE + '/api/v1/symovo/api/v1/robots/:robotId/commands/navigateTo',
                    params: { robotId: '@robotId' }
                },
                cancelNavigation: {
                    method: 'POST', 
                    url: ROBOT_API_BASE + '/api/v1/symovo/api/v1/robots/:robotId/commands/cancel',
                    params: { robotId: '@robotId' }
                }
            }
        );
    });

    // waypoints Service
    services.factory('WaypointService', /* @ngInject */ function($resource) {
        return $resource(
            ROBOT_API_BASE + '/api/v1/robot/waypoints/:action',
            { },
            {
                list: { 
                method: 'GET', 
                params: { action: 'list' },
                isArray: true 
            },
            run: { 
                method: 'POST', 
                params: { action: 'run' }
            }
                
            }
        );
    });

    // Camera Service
    services.factory('CameraService', /* @ngInject */ function($resource) {
        return $resource(
            ROBOT_API_BASE + '/api/v1/:camera/:action/:subaction',
            { },
            {
                getColorSnapshot: { method: 'GET', params: { camera: 'color_camera', action: 'snapshot.jpg' } },
                getDepthSnapshot: { method: 'GET', params: { camera: 'depth_camera', action: 'snapshot.jpg' } },
                getColorConfig: { method: 'GET', params: { camera: 'color_camera', action: 'config' } },
                getDepthConfig: { method: 'GET', params: { camera: 'depth_camera', action: 'config' } },
                getClientConfig: { method: 'GET', params: { camera: 'color_camera', action: 'client-config' } },
                getDepth: { method: 'GET', params: { camera: 'depth_camera', action: 'depth' } }
            }
        );
    });

    // Hub Config Service
    services.factory('HubService', /* @ngInject */ function($resource) {
        return $resource(
            ROBOT_API_BASE + '/api/v1/hub/:action/:subaction',
            { },
            {
                getConfig: { method: 'GET', params: { action: 'config' } },
                updateConfig: { method: 'PUT', params: { action: 'config' } },
                getRobot: { method: 'GET', params: { action: 'robot' } },
                setRobot: { method: 'PUT', params: { action: 'robot' } },
                getCredentials: { method: 'GET', params: { action: 'credentials' } },
                updateCredentials: { method: 'PUT', params: { action: 'credentials' } },
                getAuthStatus: { method: 'GET', params: { action: 'auth', subaction: 'status' } },
                connectionTest: { method: 'POST', params: { action: 'connection-test' } },
                requestAuth: { method: 'POST', params: { action: 'auth' } }
            }
        );
    });


    

    // WebRTC Service for Janus DataChannel
    services.factory('WebRTCService', ['$window', '$rootScope', '$timeout', '$q', 'UtilityService', function($window, $rootScope, $timeout, $q, UtilityService) {
        var service = {};
        
        // --- Constants from Specification ---
        const SERVER_URL = "wss://api.techvisioncloud.pl/api/v1/color_camera/janus-ws";
        const ROOM_ID = 1000;

        // --- Private variables ---
        var janus = null;
        var textroom = null;
        var opaqueId = "gpstracker-hub-" + Math.random().toString(36).substr(2, 9);
        var myId = "hub-user-" + Math.random().toString(16).substr(2, 6);

        var status = {
            webrtc: 'disconnected', // 'disconnected', 'connecting', 'connected', 'failed', 'error'
            janus: 'down', // 'down', 'connecting', 'connected', 'error'
            textroom: 'detached' // 'detached', 'attaching', 'attached', 'joining', 'joined', 'error'
        };

        var connectionPromise = null;

        function setStatus(component, state, errorMessage) {
            status[component] = state;
            $rootScope.$broadcast('webrtc.status.change', status, { component: component, state: state, error: errorMessage });
            console.log(`[WebRTCService] Status Change: ${component} -> ${state}`, errorMessage || '');
        }

        // --- Public API ---
        service.connect = function() {
            if (connectionPromise) {
                return connectionPromise;
            }

            var deferred = $q.defer();
            connectionPromise = deferred.promise;
            
            if (typeof $window.Janus === 'undefined') {
                setStatus('janus', 'error', 'Janus library not loaded.');
                deferred.reject('Janus library not loaded.');
                connectionPromise = null;
                return deferred.promise;
            }

            Janus.init({
                debug: "all",
                callback: function() {
                    if (!Janus.isWebrtcSupported()) {
                        setStatus('webrtc', 'failed', 'WebRTC not supported by this browser.');
                        deferred.reject('WebRTC not supported.');
                        connectionPromise = null;
                        return;
                    }
                    
                    setStatus('janus', 'connecting');
                    janus = new Janus({
                        server: SERVER_URL,
                        success: function() {
                            setStatus('janus', 'connected');
                            attachTextRoom(deferred);
                        },
                        error: function(cause) {
                            setStatus('janus', 'error', cause);
                            setStatus('webrtc', 'failed', 'Janus server connection failed.');
                            deferred.reject(cause);
                            connectionPromise = null;
                        },
                        destroyed: function() {
                            setStatus('janus', 'down');
                            setStatus('webrtc', 'disconnected');
                        }
                    });
                }
            });

            return connectionPromise;
        };

        service.disconnect = function() {
            if (janus) {
                janus.destroy();
                janus = null;
            }
            textroom = null;
            connectionPromise = null;
            setStatus('webrtc', 'disconnected');
        };

        service.getStatus = function() {
            return status;
        };
        
        service.sendJoystickFrame = function(frame) {
            if (status.webrtc !== 'connected' || !textroom) {
                // console.warn("WebRTC not connected. Cannot send frame.");
                return;
            }

            // Double JSON serialization as per spec
            var envelope = {
                textroom: "message",
                room: ROOM_ID,
                text: JSON.stringify(frame), // Inner stringify
                transaction: Janus.randomString(12)
            };
            
            try {
                // Outer stringify
                textroom.data({ text: JSON.stringify(envelope) });
            } catch(e) {
                console.error("[WebRTCService] Error sending data:", e);
                setStatus('webrtc', 'error', 'Failed to send data.');
            }
        };

        // --- Private Methods ---
        function attachTextRoom(deferred) {
            setStatus('textroom', 'attaching');
            janus.attach({
                plugin: "janus.plugin.textroom",
                opaqueId: opaqueId,
                success: function(pluginHandle) {
                    textroom = pluginHandle;
                    setStatus('textroom', 'attached');
                    textroom.send({ message: { request: "setup" } });
                },
                error: function(error) {
                    setStatus('textroom', 'error', error);
                    deferred.reject('Failed to attach TextRoom plugin.');
                    connectionPromise = null;
                },
                onmessage: function(msg, jsep) {
                    if (msg["error"]) {
                        setStatus('textroom', 'error', 'Janus Error: ' + msg["error"]);
                        deferred.reject(msg["error"]);
                        connectionPromise = null;
                        return;
                    }
                    if (jsep) {
                        textroom.createAnswer({
                            jsep: jsep,
                            media: { audio: false, video: false, data: true },
                            success: function(jsepAnswer) {
                                textroom.send({ message: { request: "ack" }, jsep: jsepAnswer });
                            },
                            error: function(error) {
                                setStatus('webrtc', 'error', 'WebRTC Answer Error: ' + error);
                                deferred.reject(error);
                                connectionPromise = null;
                            }
                        });
                    }
                },
                ondataopen: function() {
                    setStatus('textroom', 'joining');
                    joinRoom(deferred);
                },
                oncleanup: function() {
                    setStatus('textroom', 'detached');
                }
            });
        }

        function joinRoom(deferred) {
            var joinRequest = {
                textroom: "join",
                room: ROOM_ID,
                username: myId,
                display: myId,
                transaction: Janus.randomString(12)
            };
            textroom.data({
                text: JSON.stringify(joinRequest),
                success: function() {
                    setStatus('textroom', 'joined');
                    setStatus('webrtc', 'connected');
                    deferred.resolve();
                },
                error: function(error) {
                    setStatus('textroom', 'error', 'Could not join room: ' + error);
                    deferred.reject(error);
                    connectionPromise = null;
                }
            });
        }
        
        return service;
    }]);

    // Utility Service
    services.factory('UtilityService', ['$rootScope', '$timeout', function($rootScope, $timeout) {
        var utilityService = {};
        var toastContainer = null;

        var icons = {
            success: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>',
            error: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>',
            warning: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>',
            info: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>'
        };

        var titles = {
            success: 'Success',
            error: 'Error',
            warning: 'Warning',
            info: 'Info'
        };

        function getContainer() {
            if (!toastContainer) {
                toastContainer = document.createElement('div');
                toastContainer.className = 'toast-container';
                document.body.appendChild(toastContainer);
            }
            return toastContainer;
        }

        function showToast(type, message, title, duration) {
            duration = duration || 4000;
            title = title || titles[type];
            
            var container = getContainer();
            var toast = document.createElement('div');
            toast.className = 'toast toast-' + type;
            toast.style.position = 'relative';
            
            toast.innerHTML = 
                '<div class="toast-icon">' + icons[type] + '</div>' +
                '<div class="toast-content">' +
                    '<div class="toast-title">' + title + '</div>' +
                    '<div class="toast-message">' + message + '</div>' +
                '</div>' +
                '<button class="toast-close">' +
                    '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>' +
                '</button>' +
                '<div class="toast-progress" style="animation-duration: ' + duration + 'ms;"></div>';

            container.appendChild(toast);

            var closeBtn = toast.querySelector('.toast-close');
            var timeoutId;

            function removeToast() {
                if (timeoutId) clearTimeout(timeoutId);
                toast.classList.add('hiding');
                setTimeout(function() {
                    if (toast.parentNode) {
                        toast.parentNode.removeChild(toast);
                    }
                }, 300);
            }

            closeBtn.addEventListener('click', removeToast);
            timeoutId = setTimeout(removeToast, duration);
        }

        utilityService.showLoading = function() {
            $rootScope.showCustomLoading = true;
        };

        utilityService.hideLoading = function() {
            $rootScope.showCustomLoading = false;
        };

        utilityService.notificationDilog = function(message, title) {
            if (!message || message.length == 0)
                message = "Operation completed successfully!";
            showToast('success', message, title);
        };

        utilityService.messageErrorDilog = function(message, title) {
            if (!message || message.length == 0)
                message = "An error occurred. Please try again!";
            showToast('error', message, title);
        };

        utilityService.toast = {
            success: function(message, title, duration) {
                showToast('success', message, title, duration);
            },
            error: function(message, title, duration) {
                showToast('error', message, title, duration);
            },
            warning: function(message, title, duration) {
                showToast('warning', message, title, duration);
            },
            info: function(message, title, duration) {
                showToast('info', message, title, duration);
            }
        };

        return utilityService;
    }]);

    // MQTT over WebSocket Bridge Service - Functionality moved to MissionControlController
    services.factory('MqttService', ['$rootScope', '$timeout', 'UtilityService', '$window', function($rootScope, $timeout, UtilityService, $window) {
        console.warn("MqttService functionality has been moved to MissionControlController. This service is now a dummy.");
        return {
            connect: function() { console.warn("MqttService.connect() called, but functionality is now handled by MissionControlController."); },
            disconnect: function() { console.warn("MqttService.disconnect() called, but functionality is now handled by MissionControlController."); },
            isConnected: function() { console.warn("MqttService.isConnected() called, but functionality is now handled by MissionControlController."); return false; },
            publish: function(topic, payload, qos, retained) { console.warn("MqttService.publish() called, but functionality is now handled by MissionControlController."); }
        };
    }]);

    // App Run Block
    angular.module('gpstrackerHub')
        .run(['$rootScope', '$state', '$cookies', 'settings', 'AppStateService', '$window',
            function($rootScope, $state, $cookies, settings, AppStateService, $window) {

            // Global app state
            $rootScope.appName = settings.appName;
            $rootScope.companyName = settings.companyName;
            $rootScope.showCustomLoading = false;

            // Expose AppStateService properties and methods to $rootScope
            $rootScope.darkMode = AppStateService.getDarkMode();
            $rootScope.toggleDarkMode = AppStateService.toggleDarkMode;
            
            $rootScope.sidebarOpen = AppStateService.getSidebarOpen();
            $rootScope.sidebarCollapsed = AppStateService.getSidebarCollapsed();
            $rootScope.toggleSidebar = AppStateService.toggleSidebar;
            $rootScope.closeSidebar = AppStateService.closeSidebar;

            $rootScope.profileSubmenuOpen = AppStateService.getProfileSubmenuOpen();
            $rootScope.toggleProfileSubmenu = AppStateService.toggleProfileSubmenu;
            
            $rootScope.adminSubmenuOpen = AppStateService.getAdminSubmenuOpen();
            $rootScope.toggleAdminSubmenu = AppStateService.toggleAdminSubmenu;
            
            // Watch for changes in AppStateService and update $rootScope
            // This is a common pattern when services manage state and $rootScope needs to react
            $rootScope.$watch(function() { return AppStateService.getDarkMode(); }, function(newVal) {
                $rootScope.darkMode = newVal;
            });
            $rootScope.$watch(function() { return AppStateService.getSidebarOpen(); }, function(newVal) {
                $rootScope.sidebarOpen = newVal;
            });
            $rootScope.$watch(function() { return AppStateService.getSidebarCollapsed(); }, function(newVal) {
                $rootScope.sidebarCollapsed = newVal;
            });
            $rootScope.$watch(function() { return AppStateService.getProfileSubmenuOpen(); }, function(newVal) {
                $rootScope.profileSubmenuOpen = newVal;
            });
            $rootScope.$watch(function() { return AppStateService.getAdminSubmenuOpen(); }, function(newVal) {
                $rootScope.adminSubmenuOpen = newVal;
            });

            // Initial application of dark mode (if needed after initial load)
            if (AppStateService.getDarkMode()) {
                $window.document.body.classList.add('dark');
            }

        }]);

    // Translation Configuration (angular-translate - MIT License, free for commercial use)
    angular.module('gpstrackerHub').config(['$translateProvider', function($translateProvider) {
        // Translation tables will be loaded dynamically
        $translateProvider.useStaticFilesLoader({
            prefix: 'langs/',
            suffix: '.json'
        });

        // Set default language
        var savedLanguage = localStorage.getItem('language') || 'en';
        $translateProvider.preferredLanguage(savedLanguage);
        
        // Fallback language
        $translateProvider.fallbackLanguage('en');
        
        // Security: sanitize translations
        $translateProvider.useSanitizeValueStrategy('escape');
    }]);

    // Translation Service for dynamic language switching
    angular.module('gpstrackerHub.services').factory('TranslationService', ['$translate', '$rootScope', function($translate, $rootScope) {
        var service = {};

        service.changeLanguage = function(langKey) {
            $translate.use(langKey);
            localStorage.setItem('language', langKey);
            $rootScope.$broadcast('languageChanged', langKey);
        };

        service.getCurrentLanguage = function() {
            return $translate.use() || localStorage.getItem('language') || 'en';
        };

        service.getAvailableLanguages = function() {
            return [
                { code: 'en', name: 'English', nativeName: 'English' },
                { code: 'de', name: 'German', nativeName: 'Deutsch' },
                { code: 'fr', name: 'French', nativeName: 'Français' },
                { code: 'es', name: 'Spanish', nativeName: 'Español' },
                { code: 'zh', name: 'Chinese', nativeName: '中文' },
                { code: 'ja', name: 'Japanese', nativeName: '日本語' }
            ];
        };

        return service;
    }]);

})();
