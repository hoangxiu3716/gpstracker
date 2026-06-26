/* @ngInject */
function MissionDetailController($scope, $interval, $timeout, $sce, $http, RobotService, UtilityService,
    $stateParams, $cookies
) {
    $scope.robot = {
        connected: false,
        status: null,
        batteryLevel: null
    };
     // Polling interval
    var statusInterval = null;
    var movementInterval = null;
    var JOYSTICK_API_URL = 'rest/robot-api/api/v1/robot/joystick/frame';
    $scope.refreshStatus = function() {
        RobotService.getStatus().$promise.then(function(response) {
            $scope.robot.status = response.ready;
            if (response && response.symovo) {
                if(response.symovo.battery_level_percent) {
                    $scope.robot.batteryLevel = response.symovo.battery_level_percent;
                }
                if(response.symovo.online) {
                    $scope.robot.connected = response.symovo.online;
                }
            }
        }, function(error) {
            $scope.robot.connected = false;
            $scope.robot.status = null;
        });
    };
    $scope.missionId = $stateParams && $stateParams.missionId ? $stateParams.missionId : null;
    $scope.cameraTimestamp = Date.now();
    $scope.user = $cookies.getObject('gpstracker_hub_user');

    // Component statuses
    $scope.xarm = { status: null, position: null };
    $scope.igus = { status: null, position: 0, referenced: false };
    $scope.symovo = { status: null, pose: null };

    // Current task
    $scope.currentTask = null;

    // Manual mode
    $scope.manualMode = false;

    // Speed control (0-100%)
    $scope.speed = 11;

    // Section selector
    $scope.sections = [
        { id: 1, name: 'Section 1' },
        { id: 2, name: 'Section 2' },
        { id: 3, name: 'Section 3' },
        { id: 4, name: 'Section 4' },
        { id: 'center', name: 'Center' }
    ];
    $scope.selectedSection = $scope.sections[0];

    // Predefined positions from API
    $scope.predefinedPositions = [
        { id: 'product', name: 'Product Location', icon: 'P', action: 'moveToProduct' },
        { id: 'box_1', name: 'Box 1', icon: '1', action: 'moveToBox1' },
        { id: 'box_2', name: 'Box 2', icon: '2', action: 'moveToBox2' },
        { id: 'transport', name: 'Transport Position', icon: 'T', action: 'moveToTransport' }
    ];

    // Custom positions list
    $scope.customPositions = [];
    $scope.selectedPosition = null;

    // Navigation state
    $scope.navigation = {
        active: false,
        target: null,
        progress: 0
    };

    // Joystick state
    $scope.joystick = {
        x: 0,
        y: 0,
        active: false
    };

    // Lift control
    $scope.liftTarget = 0;

    // Camera coordinates
    $scope.camera = {
        x: null,
        y: null,
        depth: null
    };

    // Camera settings
    $scope.cameraMode = 'live'; // 'live' or 'snapshot'
    $scope.cameraTimestamp = Date.now();
    
    // Live camera URL (external - direct access to robot API)
    var LIVE_CAMERA_BASE = 'https://api.techvisioncloud.pl';
    $scope.liveCameraUrl = $sce.trustAsResourceUrl(LIVE_CAMERA_BASE + '/api/v1/depth_camera/color_view.html');
    
    // Snapshot URLs (through proxy)
    $scope.colorSnapshotUrl = '';
    $scope.depthSnapshotUrl = '';

    // Active tab
    $scope.activeTab = 'control';

    // Polling interval
    var statusInterval = null;
    var movementInterval = null;

    // Initialize
    $scope.init = function() {
        $scope.refreshStatus();
        $scope.loadCustomPositions();
        $scope.updateSnapshotUrls();
        // Removed auto-polling - use manual refresh button instead
    };

    // Camera mode switching
    $scope.setCameraMode = function(mode) {
        $scope.cameraMode = mode;
        if (mode === 'snapshot') {
            $scope.updateSnapshotUrls();
        }
    };

    // Update snapshot URLs with timestamp to prevent caching
    $scope.updateSnapshotUrls = function() {
        $scope.cameraTimestamp = Date.now();
        $scope.colorSnapshotUrl = 'rest/robot-api/api/v1/color_camera/snapshot.jpg?t=' + $scope.cameraTimestamp;
        $scope.depthSnapshotUrl = 'rest/robot-api/api/v1/depth_camera/snapshot.jpg?t=' + $scope.cameraTimestamp;
    };

    // Refresh camera snapshots
    $scope.refreshCamera = function() {
        $scope.updateSnapshotUrls();
        UtilityService.toast.info('Camera refreshed');
    };

    // Toggle fullscreen camera
    $scope.toggleFullscreenCamera = function() {
        var iframe = document.querySelector('.camera-live-iframe');
        if (iframe) {
            if (iframe.requestFullscreen) {
                iframe.requestFullscreen();
            } else if (iframe.webkitRequestFullscreen) {
                iframe.webkitRequestFullscreen();
            } else if (iframe.msRequestFullscreen) {
                iframe.msRequestFullscreen();
            }
        }
    };

    // Load custom positions
    $scope.loadCustomPositions = function() {
        RobotService.getPositionsList().$promise.then(function(response) {
            $scope.customPositions = response || [];
        }, function(error) {
            $scope.customPositions = [];
        });
    };

    // Toggle manual mode
    $scope.toggleManualMode = function() {
        $scope.manualMode = !$scope.manualMode;
        if ($scope.manualMode) {
            UtilityService.toast.info('Manual mode enabled');
        } else {
            UtilityService.toast.info('Manual mode disabled');
        }
    };

    // Emergency stop
    $scope.emergencyStop = function() {
        RobotService.cancelCurrentTask().$promise.then(function() {
            $scope.navigation.active = false;
            $scope.robot.mode = 'idle';
            UtilityService.toast.warning('Emergency stop activated');
        });
        $scope.stopMovement();
    };

    // Joystick API URL
    var JOYSTICK_API_URL = 'rest/robot-api/api/v1/robot/joystick/frame';

    // Send joystick frame using $http
    $scope.sendJoystickFrame = function(axes) {
        var frame = {
            ts: Date.now(),
            axes: axes,
            buttons: [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
            ttl: 200
        };
        
        $http.post(JOYSTICK_API_URL, frame).then(
            function(response) {},
            function(error) {
                console.error('Joystick error:', error);
            }
        );
    };


    // Section change
    $scope.changeSection = function() {
        if (!$scope.robot.connected) return;
        UtilityService.toast.info('Changing to ' + $scope.selectedSection.name);
    };

    // Quick actions
    $scope.quickAction = function(action) {
        if (!$scope.robot.connected) return;

        UtilityService.showLoading();
        var promise;
        var velocityBody = { velocity_percent: $scope.speed };

        switch(action) {
            case 'autotake':
                promise = RobotService.autotake(velocityBody).$promise;
                break;
            case 'box1':
                promise = RobotService.moveToBox1(velocityBody).$promise;
                break;
            case 'box2':
                promise = RobotService.moveToBox2(velocityBody).$promise;
                break;
            case 'transport':
                promise = RobotService.moveToTransport(velocityBody).$promise;
                break;
            case 'product':
                promise = RobotService.moveToProduct(velocityBody).$promise;
                break;
            case 'charging':
                promise = SymovoService.goToChargingStation({ id: 1 }).$promise;
                break;
            case 'align':
            case 'put':
            case 'trajectory':
            case 'measure':
            case 'unlock':
                UtilityService.hideLoading();
                UtilityService.toast.info(action.charAt(0).toUpperCase() + action.slice(1) + ' - Not implemented yet');
                return;
            default:
                UtilityService.hideLoading();
                return;
        }

        promise.then(function(response) {
            UtilityService.hideLoading();
            UtilityService.toast.success(action.charAt(0).toUpperCase() + action.slice(1) + ' started');
            if (response && response.task_id) {
                $scope.pollTaskStatus(response.task_id);
            }
        }, function(error) {
            UtilityService.hideLoading();
            // UtilityService.toast.error(action + ' failed: ' + (error.data?.message || error.statusText || 'Unknown error'));
        });
    };

    // Save current position
    $scope.savePosition = function() {
        if (!$scope.robot.connected) return;

        var name = prompt('Enter position name:');
        if (!name) return;

        RobotService.savePosition({ name: name }).$promise.then(function() {
            UtilityService.toast.success('Position saved: ' + name);
            $scope.loadCustomPositions();
        }, function(error) {
            UtilityService.toast.error('Failed to save position');
        });
    };

    // Delete position
    $scope.deletePosition = function(position) {
        if (!confirm('Delete position: ' + position.name + '?')) return;

        RobotService.deletePosition(position).$promise.then(function() {
            UtilityService.toast.success('Position deleted');
            $scope.loadCustomPositions();
        }, function(error) {
            UtilityService.toast.error('Failed to delete position');
        });
    };

    // Start job with selected position
    $scope.startJob = function() {
        if (!$scope.selectedPosition) {
            UtilityService.toast.warning('Please select a position first');
            return;
        }
       $state.go('main.missiondetail', { missionId : $scope.selectedPosition.id}, { reload : true });
        // $scope.runCustomPosition($scope.selectedPosition);
    };

    // Open mission with security check (passcode via SweetAlert)
    $scope.openMission = function() {
        if (!$scope.robot.connected) return;
        if (!$scope.selectedPosition) {
            UtilityService.toast.warning('Please select a position first');
            return;
        }

        var showSwalV1 = function() {
            try {
                // pick primary color from CSS variables to match theme (use dark variant when body.dark)
                var root = document.documentElement || document.querySelector(':root');
                var primaryColor = '#06b6d4';
                try {
                    var isDark = document.body && document.body.classList && document.body.classList.contains('dark');
                    var varName = isDark ? '--primary-cyan-dark' : '--primary-cyan';
                    var computed = window.getComputedStyle(root).getPropertyValue(varName);
                    if (computed) primaryColor = computed.trim();
                } catch (e) {}

                swal({
                    title: 'Security Check',
                    text: 'Enter passcode to start mission',
                    type: 'input',
                    inputType: 'password',
                    inputPlaceholder: 'Passcode',
                    showCancelButton: true,
                    confirmButtonText: 'Run',
                    closeOnConfirm: true,
                    confirmButtonColor: primaryColor,
                    customClass: 'swal-theme-gpstracker'
                }, function(passcode) {
                    if (passcode === false || passcode === null || passcode === '') return;
                    $scope.$applyAsync(function() {
                        $scope.startJob();
                    });
                });
                return true;
            } catch (e) {
                return false;
            }
        };

        var showSwalV2 = function() {
            // SweetAlert2 (Swal) fallback
            if (window.Swal && typeof window.Swal.fire === 'function') {
                // pick primary color from CSS variables (use dark variant when body.dark)
                var primary = '#06b6d4';
                try {
                    var isDarkMode = document.body && document.body.classList && document.body.classList.contains('dark');
                    var rootEl = document.documentElement || document.querySelector(':root');
                    var varKey = isDarkMode ? '--primary-cyan-dark' : '--primary-cyan';
                    var val = window.getComputedStyle(rootEl).getPropertyValue(varKey);
                    if (val) primary = val.trim();
                } catch (e) {}

                window.Swal.fire({
                    title: 'Security Check',
                    input: 'password',
                    inputPlaceholder: 'Passcode',
                    showCancelButton: true,
                    confirmButtonText: 'Run',
                    confirmButtonColor: primary,
                    customClass: 'swal-theme-gpstracker'
                }).then(function(result) {
                    if (!result || !result.isConfirmed) return;
                    $scope.$applyAsync(function() {
                        $scope.startJob();
                    });
                });
                return true;
            }
            return false;
        };

        // Try SweetAlert v1, then SweetAlert2, finally fallback to window.prompt
        if (typeof swal === 'function') {
            if (!showSwalV1()) {
                if (!showSwalV2()) {
                    var p = prompt('Enter passcode:');
                    if (p) $scope.startJob();
                }
            }
        } else if (window.Swal && typeof window.Swal.fire === 'function') {
            showSwalV2();
        } else {
            var pass = prompt('Enter passcode:');
            if (pass) $scope.startJob();
        }
    };

    // Navigate to predefined position
    $scope.navigateTo = function(position) {
        if (!$scope.robot.connected || $scope.navigation.active) return;

        UtilityService.showLoading();
        $scope.navigation.active = true;
        $scope.navigation.target = position;
        $scope.robot.mode = 'navigating';

        var velocityBody = { velocity_percent: $scope.speed };
        var serviceMethod = RobotService[position.action];
        if (serviceMethod) {
            serviceMethod(velocityBody).$promise.then(function(response) {
                UtilityService.hideLoading();
                UtilityService.toast.success('Moving to ' + position.name);
                if (response.task_id) {
                    $scope.pollTaskStatus(response.task_id);
                }
            }, function(error) {
                UtilityService.hideLoading();
                // UtilityService.toast.error('Failed to navigate: ' + (error.data?.message || error.statusText || 'Unknown error'));
                $scope.navigation.active = false;
                $scope.robot.mode = 'idle';
            });
        }
    };

    // Run custom position
    $scope.runCustomPosition = function(position) {
        if (!$scope.robot.connected || $scope.navigation.active) return;

        UtilityService.showLoading();
        
        RobotService.runPosition({ position_id: position.id }).$promise.then(function(response) {
            UtilityService.hideLoading();
            UtilityService.toast.success('Running position: ' + position.name);
        }, function(error) {
            UtilityService.hideLoading();
            UtilityService.toast.error('Failed to run position');
        });
    };

     $scope.cancelJob = function() {
        if (!$scope.robot.connected || $scope.navigation.active) return;

        UtilityService.showLoading();
        
        RobotService.cancelCurrentTask().$promise.then(function(response) {
            UtilityService.hideLoading();
            UtilityService.toast.success('Cancelled job');
        }, function(error) {
            UtilityService.hideLoading();
            UtilityService.toast.error('Failed to cancel job');
        });
    };

    // Poll task status
    $scope.pollTaskStatus = function(taskId) {
        // store interval on scope so it can be cancelled from other places (e.g. destroy)
        $scope.pollInterval = $interval(function() {
            RobotService.getTaskStatus({ id: taskId }).$promise.then(function(response) {
                if (response.status === 'completed') {
                    if ($scope.pollInterval) {
                        $interval.cancel($scope.pollInterval);
                        $scope.pollInterval = null;
                    }
                    $scope.navigation.active = false;
                    $scope.robot.mode = 'arrived';
                    UtilityService.toast.success('Task completed');
                } else if (response.status === 'failed' || response.status === 'cancelled') {
                    if ($scope.pollInterval) {
                        $interval.cancel($scope.pollInterval);
                        $scope.pollInterval = null;
                    }
                    $scope.navigation.active = false;
                    $scope.robot.mode = 'idle';
                    UtilityService.toast.warning('Task ' + response.status);
                } else {
                    $scope.navigation.progress = response.progress || 0;
                }
            });
        }, 500);
    };

    // Cancel navigation
    $scope.cancelNavigation = function() {
        RobotService.cancelCurrentTask().$promise.then(function() {
            // stop any polling loop for task status
            if ($scope.pollInterval) {
                $interval.cancel($scope.pollInterval);
                $scope.pollInterval = null;
            }
            $scope.navigation.active = false;
            $scope.navigation.progress = 0;
            $scope.robot.mode = 'idle';
            UtilityService.toast.info('Navigation cancelled');
        }, function(error) {
            UtilityService.toast.error('Failed to cancel');
        });
    };

    // Gripper controls
    $scope.gripperTake = function() {
        if (!$scope.robot.connected) return;
        UtilityService.showLoading();
        XArmService.gripperTake().$promise.then(function() {
            UtilityService.hideLoading();
            UtilityService.toast.success('Gripper TAKE');
        }, function() {
            UtilityService.hideLoading();
            UtilityService.toast.error('Gripper take failed');
        });
    };

    $scope.gripperDrop = function() {
        if (!$scope.robot.connected) return;
        UtilityService.showLoading();
        XArmService.gripperDrop().$promise.then(function() {
            UtilityService.hideLoading();
            UtilityService.toast.success('Gripper DROP');
        }, function() {
            UtilityService.hideLoading();
            UtilityService.toast.error('Gripper drop failed');
        });
    };

    // Lift controls
    $scope.moveLift = function(position) {
        if (!$scope.robot.connected) return;
        IgusService.move({ position: position }).$promise.then(function() {
            UtilityService.toast.success('Lift moving to ' + position + 'mm');
        }, function() {
            UtilityService.toast.error('Lift move failed');
        });
    };

    $scope.liftUp = function() {
        var newPos = Math.min(500, $scope.igus.position + 10);
        $scope.moveLift(newPos);
    };

    $scope.liftDown = function() {
        var newPos = Math.max(0, $scope.igus.position - 10);
        $scope.moveLift(newPos);
    };

    $scope.liftReference = function() {
        IgusService.reference().$promise.then(function() {
            UtilityService.toast.success('Lift referencing');
        }, function() {
            UtilityService.toast.error('Lift reference failed');
        });
    };

    // Fault reset
    $scope.faultReset = function(component) {
        var service;
        switch (component) {
            case 'xarm': service = XArmService; break;
            case 'igus': service = IgusService; break;
            case 'symovo': service = SymovoService; break;
            default: return;
        }

        service.faultReset().$promise.then(function() {
            UtilityService.toast.success(component.toUpperCase() + ' fault reset');
            $scope.refreshStatus();
        }, function() {
            UtilityService.toast.error('Fault reset failed');
        });
    };

    // Set robot ready
    $scope.setReady = function() {
        RobotService.setReady().$promise.then(function() {
            UtilityService.toast.success('Robot set to ready');
            $scope.refreshStatus();
        }, function() {
            UtilityService.toast.error('Failed to set ready');
        });
    };

    // Old joystick handlers removed - using D-pad buttons instead

    // Tab switching
    $scope.setTab = function(tab) {
        $scope.activeTab = tab;
    };

    // Cleanup
    $scope.$on('$destroy', function() {
        if (statusInterval) $interval.cancel(statusInterval);
        if (movementInterval) $interval.cancel(movementInterval);
        if ($scope.pollInterval) {
            $interval.cancel($scope.pollInterval);
            $scope.pollInterval = null;
        }
    });

    // Initialize
    $scope.init();
}
