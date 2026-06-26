const { count } = require("console");

/* @ngInject */
function MissionControlController($scope, $interval, $timeout, $sce, $http, RobotService, XArmService, IgusService, SymovoService, UtilityService, WebRTCService,
    $state, $cookies, $document, $window, $rootScope, AuthService, WaypointService
) {
    
    // --- CONSTANTS ---
    var ROBOT_ID = 'fahrdummy-01'; 
    var SYMOVO = '/api/v1/symovo';  
    var LIVE_CAMERA_BASE = 'https://api.techvisioncloud.pl';
    var SYMOVO_API_BASE = LIVE_CAMERA_BASE + '/api/v1/symovo/api/v1/robots/' + ROBOT_ID;       
    
    // --- LOGGING & STATE VARIABLES ---
    
    var logDebounceTimer = null; 
    var currentLogSession = null; 
    var LOG_DELAY_MS = 1000; 
    var socket = null;
    var xarmSocket = null;
    var isConnected = false;
    var isXArmConnected = false;
    var reconnectTimeout = null;
    var xarmReconnectTimeout = null;
    var movementInterval = null;
    var joystickManager = null;
    $scope.gamepadConnected = false;
    var gamepadIndex = null;

    // --- GAMEPAD HANDLERS ---
    var handleGamepadConnected = function(e) {
        console.log("Gamepad connected:", e.gamepad.id);
        gamepadIndex = e.gamepad.index;
        $scope.$applyAsync(function() { 
            $scope.gamepadConnected = true;
            UtilityService.toast.success("Gamepad Connected"); 
            $scope.startControlLoop(); // Ensure loop is running
        });
    };

    var handleGamepadDisconnected = function(e) {
        gamepadIndex = null;
        $scope.$applyAsync(function() { 
            $scope.gamepadConnected = false;
            UtilityService.toast.warning("Gamepad Disconnected"); 
        });
    };

    $window.addEventListener("gamepadconnected", handleGamepadConnected);
    $window.addEventListener("gamepaddisconnected", handleGamepadDisconnected);

    // Initial check for gamepads already plugged in
    var checkInitialGamepads = function() {
        var gps = navigator.getGamepads();
        for (var i = 0; i < gps.length; i++) {
            if (gps[i]) {
                gamepadIndex = i;
                $scope.gamepadConnected = true;
                $scope.startControlLoop();
                break;
            }
        }
    };

    $scope.updateGamepadState = function() {
        if (gamepadIndex === null) return false;
        var gamepads = navigator.getGamepads();
        var gp = gamepads[gamepadIndex];
        if (!gp) return false;

        var state = $scope.motionState;
        var threshold = 0.15; 
        var active = false;
        
        state.gpButtons = new Array(19).fill(0);

        // 1. LEFT STICK (Movement) - Only update if moved
        if (Math.abs(gp.axes[1]) > threshold || Math.abs(gp.axes[0]) > threshold) {
            state.linear = -gp.axes[1];
            state.angular = gp.axes[0];
            active = true;
        }

        // 2. RIGHT STICK (Hand Pitch/Roll) - Only update if moved
        if (Math.abs(gp.axes[3]) > threshold || Math.abs(gp.axes[2]) > threshold) {
            state[3] = -gp.axes[3]; 
            state[2] = -gp.axes[2]; 
            active = true;
        }

        // 3. TRIGGERS (RZ Rotation)
        if (gp.buttons[6].pressed) { state[2] = -1; active = true; } 
        if (gp.buttons[7].pressed) { state[2] = 1; active = true; }

        // 4. D-PAD
        if (gp.buttons[12].pressed) { state.gpButtons[12] = 1; state.linear = 1; active = true; }
        if (gp.buttons[13].pressed) { state.gpButtons[14] = 1; state.linear = -1; active = true; }
        if (gp.buttons[14].pressed) { state.gpButtons[15] = 1; state.angular = -1; active = true; }
        if (gp.buttons[15].pressed) { state.gpButtons[13] = 1; state.angular = 1; active = true; }

        // 5. LIFT CONTROL
        if (gp.buttons[3].pressed) {
            if (!state.gpLiftUp) { $scope.liftJogStart('up'); state.gpLiftUp = true; }
            active = true;
        } else if (state.gpLiftUp) {
            $scope.liftJogStop();
            state.gpLiftUp = false;
        }

        if (gp.buttons[0].pressed) {
            if (!state.gpLiftDown) { $scope.liftJogStart('down'); state.gpLiftDown = true; }
            active = true;
        } else if (state.gpLiftDown) {
            $scope.liftJogStop();
            state.gpLiftDown = false;
        }

        // 6. SPEED
        if (gp.buttons[5].pressed) { $scope.speed = Math.min(100, $scope.speed + 0.3); active = true; }
        if (gp.buttons[4].pressed) { $scope.speed = Math.max(1, $scope.speed - 0.3); active = true; }

        return active;
    };

    // --- MOTION CONTROL STATE (Scoped for reliability) ---
    $scope.motionState = {
        linear: 0,
        angular: 0,
        lastActiveInput: 0,
        isButtonActive: false,
        isJoystickActive: false, // Track if joystick is being touched
        keysPressed: {}
    };
    var CONTROL_INTERVAL_MS = 50; 
    var WATCHDOG_MS = 200; 
    
    // UtilityService.showLoading();

    // --- PERMISSIONS ---
    $scope.canControl = AuthService.hasAnyRole(['ROLE_ADMIN', 'ROLE_PILOT']);

    // --- SCOPE INITIALIZATION ---
    $scope.robot = { connected: false, status: null, mode: 'idle', batteryLevel: 0, uptime: 0, cpu: null, disk: null, ram: null };
    $scope.xarm = { status: null, position: null };
    $scope.igus = {
        status: null,
        position: 0,
        referenced: false,
        connected: false,
        isMoving: false,
        fault: false,
        enabled: false
    };
    $scope.symovo = { status: null, pose: null };
    $scope.currentTask = null;
    $scope.navigation = { active: false, target: null, progress: 0 };
    $scope.speed = 44;
    $scope.lift = {
        targetPosition: 0,
        velocityPercent: 50,
        accelerationPercent: 50,
        jogSpeed: 10
    };

    // Locations / Sections (Restored)
    $scope.sections = [
        { id: 1, name: 'Section 1', value : 'READY_SECTION_1' },
        { id: 2, name: 'Section 2', value : 'READY_SECTION_2' },
        { id: 3, name: 'Section 3', value : 'READY_SECTION_3' },
        { id: 4, name: 'Section 4', value : 'READY_SECTION_4' },
        { id: 5, name: 'Center', value : 'READY_SECTION_CENTER' }
    ];
    // $scope.selectedSection = $scope.sections[0];

    // Predefined Positions (Restored)
    $scope.predefinedPositions = [
        { id: 'product', name: 'Product Location', icon: 'P', action: 'moveToProduct' },
        { id: 'box_1', name: 'Box 1', icon: '1', action: 'moveToBox1' },
        { id: 'box_2', name: 'Box 2', icon: '2', action: 'moveToBox2' },
        { id: 'transport', name: 'Transport Position', icon: 'T', action: 'moveToTransport' }
    ];
    $scope.customPositions = [];
    $scope.selectedPosition = null;

    // --- INPUT & CONTROL MODES ---
    $scope.controlTarget = 'arm'; // 'arm' (WebRTC/Cánh tay) or 'base' (HTTP/Đế Robot)
    $scope.baseTeleop = {
        active: false,
        linear: 0.1,
        angular: 0.5,
        duration: 0.25,
        keys: { w: false, s: false, a: false, d: false }
    };
    var baseTeleopInterval = null;
    var BASE_SEND_MS = 200;

    $scope.inputMode = 'keyboard'; // 'keyboard' or 'joystick'
    $scope.keyboardActive = false;
    $scope.webRTCStatus = WebRTCService.getStatus();
    $scope.keyboardControlEnabled = true;

    $scope.setControlTarget = function(target) {
        // Stop any active movement before switching
        if ($scope.controlTarget === 'base') $scope.stopBaseTeleop();
        else $scope.stopMovement();

        $scope.controlTarget = target;
        UtilityService.toast.info('Switched to ' + target.toUpperCase() + ' Control');
    };

    $scope.toggleBaseDrive = function() {
        if (!$scope.canControl) return;
        var newState = !$scope.baseTeleop.active;

        var url = LIVE_CAMERA_BASE + SYMOVO + '/drive_mode?enable=' + newState;
        $http.put(url, {}).then(function() {
            $scope.baseTeleop.active = newState;
            UtilityService.toast.success('Base Drive ' + (newState ? 'Enabled' : 'Disabled'));
            if (!newState) $scope.stopBaseTeleop();
            $scope.finalizeLog({ action: 'TOGGLE_BASE_DRIVE', detail: { enabled: newState } });
        }, function(err) {
            UtilityService.toast.error('Failed to toggle Drive Mode');
        });
    };

    $scope.startBaseTeleop = function(key) {
        if (!$scope.baseTeleop.active) return;
        $scope.baseTeleop.keys[key] = true;

        // Session Logging for WASD
        if (!currentLogSession || currentLogSession.direction !== 'BASE_' + key) {
            if (currentLogSession) { $scope.finalizeLog(); }
            currentLogSession = { direction: 'BASE_' + key, startTime: Date.now() };
        }

        if (!baseTeleopInterval) {
            $scope.baseTeleopTick();
            baseTeleopInterval = $interval($scope.baseTeleopTick, BASE_SEND_MS);
        }
    };

    $scope.stopBaseTeleop = function(key) {
        if (key) $scope.baseTeleop.keys[key] = false;
        else $scope.baseTeleop.keys = { w: false, s: false, a: false, d: false };

        if (!$scope.baseTeleop.active) return;

        var anyPressed = Object.values($scope.baseTeleop.keys).some(k => k);
        if (!anyPressed) {
            if (baseTeleopInterval) {
                $interval.cancel(baseTeleopInterval);
                baseTeleopInterval = null;
            }
            $scope.sendBaseMoveCommand(0, 0); // Send explicit stop
            $scope.finalizeLog(); // Close base move session
        }
    };

    $scope.baseTeleopTick = function() {
        if (!$scope.baseTeleop.active) return;

        var lin = 0;
        var ang = 0;
        var speed = parseFloat($scope.baseTeleop.linear) || 0.1;
        var turn = parseFloat($scope.baseTeleop.angular) || 0.5;

        if ($scope.baseTeleop.keys.w) lin += speed;
        if ($scope.baseTeleop.keys.s) lin -= speed;
        if ($scope.baseTeleop.keys.a) ang += turn;
        if ($scope.baseTeleop.keys.d) ang -= turn;

        $scope.sendBaseMoveCommand(lin, ang);
    };

    $scope.toggleKeyboardControl = function() {
        if (!$scope.canControl) return;
        $scope.keyboardActive = !$scope.keyboardActive;

        if ($scope.keyboardActive) {
            $window.document.body.style.overflow = 'hidden';
            UtilityService.toast.info('Keyboard Control Active - Scroll Locked');
        } else {
            $window.document.body.style.overflow = 'auto';
            $scope.stopMovement();
            UtilityService.toast.warning('Keyboard Control Deactivated');
        }
    };

    // --- CAMERA SETUP ---
    $scope.camera = { x: null, y: null, depth: null };
    $scope.cameraTimestamp = Date.now();

    // Pre-trust URLs for Grid View
    $scope.trustedUrls = {
        color: $sce.trustAsResourceUrl(LIVE_CAMERA_BASE + '/api/v1/depth_camera/color_view.html'),
        depth: $sce.trustAsResourceUrl(LIVE_CAMERA_BASE + '/api/v1/depth_camera/depth_view.html'),
        nice: $sce.trustAsResourceUrl(LIVE_CAMERA_BASE + '/api/v1/color_camera/color_view.html?auto_restart=always&joy_http=1'),
        arm3d: $sce.trustAsResourceUrl('https://api.techvisioncloud.pl/arm3d_viewer?axes=all&camera_frustum&fallback=1')
    };

    $scope.liveCameraUrl = $scope.trustedUrls.nice;
    $scope.cameraViews = [
        { id: 'color', name: 'Color', path: '/api/v1/depth_camera/color_view.html' },
        { id: 'depth', name: 'Depth', path: '/api/v1/depth_camera/depth_view.html' },
        { id: 'nice', name: 'Nice', path: '/api/v1/color_camera/color_view.html?auto_restart=always&joy_http=1' },
        { id: '3d', name: 'Digital Twin', path: null },
        { id: 'grid', name: 'Grid View', path: null }
    ];
    $scope.currentCameraView = 'nice';

    // --- MQTT & WEBRTC HANDLERS ---
    $rootScope.$on('webrtc.status.change', function(event, newStatus, details) {
        $scope.$applyAsync(function() {
            $scope.webRTCStatus = newStatus;
            if (details && (details.state === 'error' || details.state === 'failed')) {
                UtilityService.toast.error(details.error, 'WebRTC Error');
            }
        });
    });

    $scope.getWebSocketUrl = function() {
        var loc = $window.location;
        var protocol = loc.protocol === 'https:' ? 'wss:' : 'ws:';
        var host = loc.host;
        var contextPath = '/gpstracker';
        if (loc.pathname.startsWith(contextPath)) {
             return protocol + '//' + host + contextPath + '/mqtt-bridge';
        }
        return protocol + '//' + host + '/mqtt-bridge';
    }

    $scope.handleSocketOpen = function() { console.log('WebSocket connected to backend bridge.'); };
    $scope.convertUptime = function(seconds) {
        var days = Math.floor(seconds / (3600 * 24));
        var hours = Math.floor((seconds % (3600 * 24)) / 3600);
        var minutes = Math.floor((seconds % 3600) / 60);
        return days + "d " + hours + "h " + minutes + "m";
    };
    $scope.handleSocketMessage = function(event) {
        try {
            var message = JSON.parse(event.data);
            if (message.topic && message.payload) {
                
                if(message.topic.endsWith('status/connection') && message.payload) {
                    if (message.payload.mqtt) {
                        UtilityService.hideLoading();
                        isConnected = true;
                        $scope.robot.connected = true;
                    }
                }
                $scope.$applyAsync(function() {
                    var topic = message.topic;
                    var payload = message.payload;
                    if (topic.endsWith('/telemetry')) {
                        if (payload.components && payload.components.igus) {

                            $scope.igus.referenced = payload.components.igus.homed;
                            $scope.igus.connected = payload.components.igus.connected;
                            $scope.igus.isMoving = payload.components.igus.is_moving;
                            $scope.igus.fault = payload.components.igus.error || payload.components.igus.fault;
                            $scope.igus.enabled = payload.components.igus.enabled || payload.components.igus.operation_enabled;
                        }
                    } else if (topic.endsWith('/status/navigation')) {
                        // $scope.robotTelemetry = payload;
                        if(payload.progress_percent) {
                            $scope.navigation.progress = payload.progress_percent;
                        }
                        if(payload.status && payload.status == 'arrived') {
                            $scope.cancelNavigation();
                        }
                        if(payload.lift_position && payload.lift_position.height) {
                            $scope.igus.position = payload.lift_position.height/1000;
                        }
                    } else if (topic.endsWith('/status/system')) {
                          $scope.robot.status = payload.status_type !== undefined ? payload.status_type : $scope.robot.status;
                          $scope.robot.batteryLevel = payload.battery !== undefined ? payload.battery : $scope.robot.batteryLevel;
                          $scope.robot.uptime = payload.uptime_seconds !== undefined ? payload.uptime_seconds : $scope.robot.uptime;
                          if (payload.cpu) $scope.robot.cpu = payload.cpu;
                          if (payload.disk) $scope.robot.disk = payload.disk;
                          if (payload.ram) $scope.robot.ram = payload.ram;
                    } else if (topic.includes('/resp/')) {
                        console.log("MQTT Response:", payload);
                    }
                });
            }
        } catch (e) { console.error("Error parsing WebSocket message:", e); }
    };
    $scope.handleSocketClose = function() {
        console.warn('WebSocket connection to backend bridge closed.');
        if (isConnected) { UtilityService.toast.warning('Real-time connection lost. Reconnecting...'); }
        isConnected = false;
        $scope.$applyAsync(function() { $scope.robot.connected = false; });
        if (reconnectTimeout) $timeout.cancel(reconnectTimeout);
        reconnectTimeout = $timeout($scope.connectMqtt, 5000);
    };
    $scope.handleSocketError = function(error) { console.error('WebSocket error:', error); };
    $scope.connectMqtt = function() {
        if (socket && (socket.readyState === WebSocket.OPEN || socket.readyState === WebSocket.CONNECTING)) { return; }
        if (reconnectTimeout) { $timeout.cancel(reconnectTimeout); reconnectTimeout = null; }
        var url = $scope.getWebSocketUrl();
        socket = new WebSocket(url);
        socket.onopen = $scope.handleSocketOpen;
        socket.onmessage = $scope.handleSocketMessage;
        socket.onclose = $scope.handleSocketClose;
        socket.onerror = $scope.handleSocketError;
    };
    $scope.disconnectMqtt = function() {
        if (reconnectTimeout) { $timeout.cancel(reconnectTimeout); reconnectTimeout = null; }
        if (socket) { socket.onclose = null; socket.close(); socket = null; isConnected = false; }
    };
    $scope.publishMqtt = function(topic, payload, qos, retained) {
        if (!isConnected || !socket || socket.readyState !== WebSocket.OPEN) { return; }
        var message = { topic: topic, payload: payload, qos: qos !== undefined ? qos : 1 };
        socket.send(JSON.stringify(message));
    };
    // --- xArm Dedicated WebSocket Connection ---
    $scope.connectXArm = function() {
        if (xarmSocket && (xarmSocket.readyState === WebSocket.OPEN || xarmSocket.readyState === WebSocket.CONNECTING)) { return; }
        if (xarmReconnectTimeout) { $timeout.cancel(xarmReconnectTimeout); xarmReconnectTimeout = null; }
        
        var timestamp = new Date().getTime();
        var url = "wss://api.techvisioncloud.pl/api/v1/xarm/ws?channel=prod&lang=en&v=1&id=" + timestamp;
        
        console.log('Connecting to xArm WebSocket:', url);
        xarmSocket = new WebSocket(url);
        
        xarmSocket.onopen = function() { 
            console.log('xArm WebSocket connected.'); 
            isXArmConnected = true; 
        };
        xarmSocket.onmessage = function(event) { 
            // console.log('xArm Message:', event.data); 
        };
        xarmSocket.onclose = function() {
            console.warn('xArm WebSocket closed. Reconnecting...');
            isXArmConnected = false;
            if (xarmReconnectTimeout) $timeout.cancel(xarmReconnectTimeout);
            xarmReconnectTimeout = $timeout($scope.connectXArm, 5000);
        };
        xarmSocket.onerror = function(error) { console.error('xArm WebSocket error:', error); };
    };
    
    $scope.sendXArmStep = function(direction, isLoop) {
        if (!isXArmConnected || !xarmSocket || xarmSocket.readyState !== WebSocket.OPEN) { 
            // console.warn("xArm Socket not ready");
            return; 
        }
        var payload = {
            cmd: "xarm_move_step",
            data: {
                acc : 500,
                axis : 1,
                direction: direction,
                isLoop : isLoop,
                isMoveTool : 1,
                mode : 0,
                userId: $rootScope.user ? $rootScope.user.username : 'test',
                version: "xarm6",
            },
            id : "6"
        };
        xarmSocket.send(JSON.stringify(payload));
    };

    $scope.sendXArmStop = function() {
        if (!isXArmConnected || !xarmSocket || xarmSocket.readyState !== WebSocket.OPEN) { return; }
        var payload = {
            cmd: "xarm_move_step_over",
            data: {
                userId: $rootScope.user ? $rootScope.user.username : 'test',
                version: "xarm6"
            },
            id : "5"
        };
        xarmSocket.send(JSON.stringify(payload));
    };

    $scope.emergencyStop = function() {
        if (!$scope.canControl) return; 
        var reason = 'User activated emergency stop from Mission Control UI';
        
        if (!isXArmConnected || !xarmSocket || xarmSocket.readyState !== WebSocket.OPEN) { return; }
        var payload = {
            cmd: "xarm_clear_error_warn",
            data: {
                mode:0,
                userId: $rootScope.user ? $rootScope.user.username : 'test',
                version: "xarm6"
            },
            id : "4"
        };
        var payloadStop = {
            cmd: "xarm_urgent_stop",
            data: {
                userId: $rootScope.user ? $rootScope.user.username : 'test',
                version: "xarm6"
            },
            id : "5"
        }
        xarmSocket.send(JSON.stringify(payload));
        xarmSocket.send(JSON.stringify(payloadStop));
        UtilityService.toast.warning('Emergency stop signal sent');
        $scope.finalizeLog({ action: 'emergencyStop', detail: { reason: reason } });
        $scope.navigation.active = false;
        $scope.robot.mode = 'idle';
        $scope.stopMovement();
    };

    // --- INPUT CONTROLS (KEYBOARD & JOYSTICK) ---
    var keysPressed = {};
    // Mapping from spec: 12: X+ (right), 14: X- (left), 13: Y+ (up), 15: Y- (down)
    var keyMap = {
        'arrowup':    { direction: 'forward',    elementId: 'dpad-up-left',    button: 12, axis: 1, val: 1 }, // up
        'arrowdown':  { direction: 'backward',   elementId: 'dpad-down-left',  button: 14, axis: 1, val: -1 },  // down
        'arrowleft':  { direction: 'turn_left',  elementId: 'dpad-left-left',  button: 15, axis: 0, val: -1 }, // left
        'arrowright': { direction: 'turn_right', elementId: 'dpad-right-left', button: 13, axis: 0, val: 1 },  // right
        
        'i': { direction: 'attitude-pitch-increase', elementId: 'dpad-up-right', button: 7, axis: 3, val: 1 },
        'k': { direction: 'attitude-pitch-decrease', elementId: 'dpad-down-right', button: null, axis: 3, val: -1 },
        'j': { direction: 'attitude-roll-increase', elementId: 'dpad-left-right', button: 4, axis: 2, val: 1 },
        'l': { direction: 'attitude-roll-decrease', elementId: 'dpad-right-right', button: 4, axis: 2, val: -1 },
        'o': { direction: 'attitude-yaw-decrease', elementId: 'btn-backward-right', button: 2, axis: 2, val: -1 },
        'u': { direction: 'attitude-yaw-increase', elementId: 'btn-forward-right', button: 2, axis: 2, val: 1 },
        'y': { direction: 'xarm-j1-plus', elementId: 'btn-xarm-j1-up', button: null, axis: null, val: null },
        'h': { direction: 'xarm-j1-minus', elementId: 'btn-xarm-j1-down', button: null, axis: null, val: null },

        'q': { direction: 'arm_forward',  elementId: 'btn-backward-left',   button: 9, axis: 3, val: -1 },
        'e': { direction: 'arm_backward', elementId: 'btn-forward-left', button: 11, axis: 3, val: 1 },
        '8': { direction: 'arm_forward',  elementId: 'btn-backward-left',   button: 9, axis: 3, val: -1 },
        '2': { direction: 'arm_backward', elementId: 'btn-forward-left', button: 11, axis: 3, val: 1 },
    };

    var handleKeyDown = function(e) {
        if (!$scope.canControl) return;
        if ($scope.inputMode !== 'keyboard') return;
        if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA') return;

        var key = e.key.toLowerCase();
        // Block all navigation keys to prevent scrolling and unintended browser actions
        if (['arrowup', 'arrowdown', 'arrowleft', 'arrowright', ' ', 'p', 'm', 'w', 'a', 's', 'd', '8', '4', '2', '6', 'i', 'k', 'j', 'l', 'o', 'u', 'y', 'h', 'q', 'e', 'enter', 'escape'].indexOf(key) > -1) {
            e.preventDefault();
        }

        if (!$scope.keyboardActive) return;

        // Arm/Lift Control Mode (Shortcuts: P = Up, M = Down)
        if (key === 'p' || key === 'm') {
            if (!$scope.motionState.keysPressed[key]) {
                $scope.motionState.keysPressed[key] = true;
                var direction = (key === 'p') ? 'up' : 'down';
                var btnId = (key === 'p') ? 'lift-jog-up' : 'lift-jog-down';

                $scope.liftJogStart(direction);

                var btn = document.getElementById(btnId);
                if (btn) btn.classList.add('pressed');
            }
            return;
        }

        // Symovo Quick Actions
        if (key === 'enter') { $scope.startJob(); return; }
        if (key === 'escape') { $scope.cancelJob(); return; }

        // Base Control Mode (WASD)
        if ($scope.controlTarget === 'base' && ['w', 'a', 's', 'd'].indexOf(key) > -1) {
            $scope.$applyAsync(function() { $scope.startBaseTeleop(key); });
            return;
        }

        if (!keyMap[key]) return;

        var state = $scope.motionState;
        state.lastActiveInput = Date.now();

        if (state.keysPressed[key]) return;

        state.keysPressed[key] = true;
        var config = keyMap[key];

        state[config.axis] = config.val;

        $scope.$applyAsync(function() {
            $scope.startMovement(config.direction);
            var dpadButton = document.getElementById(config.elementId);
            if (dpadButton) dpadButton.classList.add('pressed');
        });
    };

    var handleKeyUp = function(e) {
        if (!$scope.canControl) return;
        if ($scope.inputMode !== 'keyboard') return;

        var key = e.key.toLowerCase();

        // Base Control Mode (WASD)
        if ($scope.controlTarget === 'base' && ['w', 'a', 's', 'd'].indexOf(key) > -1) {
            if ($scope.keyboardActive) {
                $scope.$applyAsync(function() { $scope.stopBaseTeleop(key); });
            }
            return;
        }

        if (key === 'p' || key === 'm') {
            delete $scope.motionState.keysPressed[key];
            var btnId = (key === 'p') ? 'lift-jog-up' : 'lift-jog-down';
            var btn = document.getElementById(btnId);
            if (btn) btn.classList.remove('pressed');
            $scope.liftJogStop();
            return;
        }

        if (!$scope.keyboardActive || !keyMap[key]) return;

        var state = $scope.motionState;
        e.preventDefault();
        delete state.keysPressed[key];

        var config = keyMap[key];
        var dpadButton = document.getElementById(config.elementId);
        if (dpadButton) dpadButton.classList.remove('pressed');

        // Reset and recalculate based on remaining keys
        state.linear = 0;
        state.angular = 0;
        state[0] = 0; state[1] = 0; state[2] = 0; state[3] = 0;
        Object.keys(state.keysPressed).forEach(function(k) {
            var cfg = keyMap[k];
            if (cfg) state[cfg.axis] = cfg.val;
        });

        if (Object.keys(state.keysPressed).length === 0) { $scope.stopMovement(); }
        $scope.$applyAsync();
    };

    var leftJoystick = null;
    var rightJoystick = null;

    function initJoystick() {
        if (leftJoystick) { leftJoystick.destroy(); }
        if (rightJoystick) { rightJoystick.destroy(); }
        
        // LEFT JOYSTICK (Movement / Arrows)
        var leftOptions = {
            zone: document.getElementById('joystick-left'),
            mode: 'static',
            position: { left: '50%', top: '50%' },
            color: '#22d3ee',
            size: 100
        };
        leftJoystick = nipplejs.create(leftOptions);
        leftJoystick.on('move', function(evt, data) {
            if (!$scope.canControl) return;
            var angle = data.angle.degree;
            var direction = '';
            var baseKey = '';
            if (angle > 45 && angle < 135) { direction = 'forward'; baseKey = 'w'; }      
            else if (angle > 225 && angle < 315) { direction = 'backward'; baseKey = 's'; } 
            else if (angle > 135 && angle < 225) { direction = 'turn_left'; baseKey = 'a'; }
            else if (angle < 45 || angle > 315) { direction = 'turn_right'; baseKey = 'd'; }

            if ($scope.controlTarget === 'arm') {
                if (direction) $scope.startMovement(direction);
            } else {
                if (baseKey) $scope.startBaseTeleop(baseKey);
            }
        });
        leftJoystick.on('end', function() {
            if (!$scope.canControl) return;
            if ($scope.controlTarget === 'arm') $scope.stopMovement();
            else $scope.stopBaseTeleop();
        });

        // RIGHT JOYSTICK (Pitch / Roll)
        var rightOptions = {
            zone: document.getElementById('joystick-right'),
            mode: 'static',
            position: { left: '50%', top: '50%' },
            color: '#22d3ee',
            size: 100
        };
        rightJoystick = nipplejs.create(rightOptions);
        rightJoystick.on('move', function(evt, data) {
            if (!$scope.canControl || $scope.controlTarget !== 'arm') return;
            var angle = data.angle.degree;
            var direction = '';
            if (angle > 45 && angle < 135) { direction = 'attitude-pitch-increase'; }      
            else if (angle > 225 && angle < 315) { direction = 'attitude-pitch-decrease'; } 
            else if (angle > 135 && angle < 225) { direction = 'attitude-roll-increase'; }
            else if (angle < 45 || angle > 315) { direction = 'attitude-roll-decrease'; }
            if (direction) $scope.startMovement(direction);
        });
        rightJoystick.on('end', function() {
            if (!$scope.canControl || $scope.controlTarget !== 'arm') return;
            $scope.stopMovement();
        });
    }

    function destroyJoystick() {
        if (leftJoystick) { leftJoystick.destroy(); leftJoystick = null; }
        if (rightJoystick) { rightJoystick.destroy(); rightJoystick = null; }
    }

    $scope.setInputMode = function(mode) {
        if (!$scope.canControl) return;
        if ($scope.inputMode === mode) return;
        $scope.inputMode = mode;
        
        if (mode === 'joystick') {
            $scope.keyboardControlEnabled = false;
            $timeout(initJoystick, 0);
        } else {
            $scope.keyboardControlEnabled = true;
            destroyJoystick();
        }

        // Always re-bind pointer events for buttons because elements might have been recreated or visibility changed
        $timeout(function() {
            $scope.bindDPadPointerEvents();
            $scope.bindLiftPointerEvents();
            $scope.bindBaseKeyPointerEvents();
        }, 200);
    };

    $scope.finalizeLog = function(actionDetails) {
        var info = {
            username : $rootScope.user ? $rootScope.user.username : '',
            userId : $rootScope.user ? $rootScope.user.id : null,
            action: 'UNKNOWN',
            duration: 0,
            detail: null
        };

        if (currentLogSession) {
            info.action = currentLogSession.direction.toUpperCase();
            info.duration = ((Date.now() - currentLogSession.startTime) / 1000).toFixed(2);
            var detailFrames = $scope.detailActionFrames || {};
            detailFrames.speed = $scope.speed;
            info.detail = JSON.stringify(detailFrames);
            currentLogSession = null;
        } else if (actionDetails) {
            info.action = actionDetails.action.toUpperCase();
            if (actionDetails.detail) {
                 info.detail = JSON.stringify(actionDetails.detail);
            }
        } else {
            return;
        }

        console.log("ACTION LOG:", info);
        $rootScope.buildLog(info);
    };

    // --- MOVEMENT & ACTION HANDLERS ---
    

    $scope.sendJoystickFrame = function(frame) {
        if ($scope.controlTarget !== 'arm') return;
        $scope.detailActionFrames = frame;
        if ($scope.robot.connected) {
            WebRTCService.sendJoystickFrame(frame);
        }
    };

    $scope.startControlLoop = function() {
        if (movementInterval) return;

        console.log("Control Loop Started");
        movementInterval = $interval(function() {
            var now = Date.now();
            var state = $scope.motionState;

            var isGamepadActive = $scope.updateGamepadState();

            var isAnyInputHeld = Object.keys(state.keysPressed).length > 0 || state.isButtonActive || state.isJoystickActive || isGamepadActive;
            if (isAnyInputHeld) {
                state.lastActiveInput = now;
            }

            if (now - state.lastActiveInput > WATCHDOG_MS) {
                if (state.linear !== 0 || state.angular !== 0 || state.currentXArmDirection) {
                    state.linear = 0;
                    state.angular = 0;
                    state.currentXArmDirection = null;
                    $scope.sendJoystickFrame({ ts: now, axes: [0,0,0,0], buttons: new Array(19).fill(0), ttl: 200 });
                }
                if (!$scope.gamepadConnected) {
                    console.log("Control Loop Stopped (Watchdog)");
                    $interval.cancel(movementInterval);
                    movementInterval = null;
                }
                return;
            }

            if (!$scope.robot.connected || $scope.webRTCStatus.webrtc !== 'connected') return;

            var speedFactor = Math.max(0.1, $scope.speed / 100);
            var buttons = new Array(19).fill(0);

            if ($scope.gamepadConnected && state.gpButtons) {
                buttons = state.gpButtons;
            }

            Object.keys(state.keysPressed).forEach(function(key) {
                var cfg = keyMap[key];
                if (cfg) buttons[cfg.button] = 1;
            });

            if (state.currentButton !== null) {
                buttons[state.currentButton] = 1;
            }

            // Periodic WebSocket signal for xArm (Safety TTL)
            if (state.currentXArmDirection) {
                $scope.sendXArmStep(state.currentXArmDirection, true);
            } else {
                var frame = {
                    ts: now,
                    axes: [
                        state.linear * speedFactor,
                        state.angular * speedFactor,
                        (state[2] || 0) * speedFactor,
                        (state[3] || 0) * speedFactor
                    ],
                    buttons: buttons,
                    ttl: WATCHDOG_MS
                };

                $scope.sendJoystickFrame(frame);
            }
        }, CONTROL_INTERVAL_MS);
    };

    $scope.startMovement = function(direction) {
        console.log("startMovement called:", direction);
        if (!$scope.canControl) return;

        // WebSocket Control for J1- and J1+ ONLY
        if (direction === 'xarm-j1-plus') {
            $scope.motionState.currentXArmDirection = 'joint-angle-increase';
            $scope.sendXArmStep('joint-angle-increase', true);
            return;
        } else if (direction === 'xarm-j1-minus') {
            $scope.motionState.currentXArmDirection = 'joint-angle-decrease';
            $scope.sendXArmStep('joint-angle-decrease', true);
            return;
        }

        var state = $scope.motionState;
        state.lastActiveInput = Date.now();
        state.isButtonActive = true;

        state.linear = 0;
        state.angular = 0;
        state.lift = 0;
        state.currentButton = null;

        if (logDebounceTimer) { $timeout.cancel(logDebounceTimer); logDebounceTimer = null; }
        if (!currentLogSession || currentLogSession.direction !== direction) {
            if (currentLogSession) { $scope.finalizeLog(); }
            currentLogSession = { direction: direction, startTime: Date.now() };
        }

        // Spec Mapping: 12: X+ (right), 14: X- (left), 13: Y+ (up), 15: Y- (down)
        if (direction === 'turn_right') { state.angular = 1; state.currentButton = 13; }      // Up (Y+)
        else if (direction === 'turn_left') { state.angular = -1; state.currentButton = 15; } // Down (Y-)
        else if (direction === 'backward') { state.linear = -1; state.currentButton = 14; }   // Left (X-)
        else if (direction === 'forward') { state.linear = 1; state.currentButton = 12; }     // Right (X+)
        else if (direction === 'lift_up' || direction === 'arm_backward') { 
            state.lift = 1; 
            state.currentButton = 9;
        }
        else if (direction === 'lift_down' || direction === 'arm_forward') { 
            state.lift = -1; 
            state.currentButton = 11;
        }
        else if (direction === 'attitude-pitch-increase' || direction === 'xarm-j1-plus') {
            state[3] = 1;
            state.currentButton = 7;
        }
        else if (direction === 'attitude-pitch-decrease' || direction === 'xarm-j1-minus') {
            state[3] = -1;
            // state.currentButton = 3;
        }
        else if (direction === 'attitude-roll-increase') {
            state[2] = 1;
            state.currentButton = 4;
        }
        else if (direction === 'attitude-roll-decrease') {
            state[2] = -1;
            state.currentButton = 4;
        }
        else if (direction === 'attitude-yaw-decrease') {
            state[2] = -1;
            state.currentButton = 2;
        }
        else if (direction === 'attitude-yaw-increase') {
            state[2] = 1;
            state.currentButton = 2;
        }

        $scope.startControlLoop();

        if ($scope.robot.connected && !state.currentXArmDirection) {
            var speedFactor = Math.max(0.1, $scope.speed / 100);
            var buttons = new Array(19).fill(0);
            if (state.currentButton !== null) buttons[state.currentButton] = 1;
            $scope.sendJoystickFrame({
                ts: Date.now(),
                axes: [
                    state.linear * speedFactor, 
                    state.angular * speedFactor, 
                    (state[2] || 0) * speedFactor, 
                    (state[3] || 0) * speedFactor
                ],
                buttons: buttons,
                ttl: WATCHDOG_MS
            });
        }
    };

    $scope.stopMovement = function() {
        console.log("stopMovement called");

        // Send WebSocket stop for xArm
        $scope.sendXArmStop();
        $scope.motionState.currentXArmDirection = null;

        var state = $scope.motionState;
        state.linear = 0;
        state.angular = 0;
        state[0] = 0; state[1] = 0; state[2] = 0; state[3] = 0;
        state.lastActiveInput = 0;
        state.isButtonActive = false;
        state.currentButton = null;

        if (movementInterval && !$scope.gamepadConnected) {
            $interval.cancel(movementInterval);
            movementInterval = null;
        }

        var stopCount = 0;
        var neutralFrame = { ts: Date.now(), axes: [0, 0, 0, 0], buttons: new Array(19).fill(0), ttl: 200 };
        $scope.sendJoystickFrame(neutralFrame);

        var stopInterval = $interval(function() {
            stopCount++;
            neutralFrame.ts = Date.now();
            $scope.sendJoystickFrame(neutralFrame);
            if (stopCount >= 5) { $interval.cancel(stopInterval); }
        }, 20);

        if (logDebounceTimer) $timeout.cancel(logDebounceTimer);
        logDebounceTimer = $timeout($scope.finalizeLog, LOG_DELAY_MS);
    };

    // xArm Control (Restored)
    $scope.moveXArm = function(action) {
        // ...
    };

    // Lift (Igus) Control
    var IGUS_API_BASE = 'rest/robot-api/api/v1/igus';

    $scope.moveLift = function(position) {
        if (!$scope.canControl || !$scope.robot.connected) return;
        
        var body = {
            position: parseFloat(position*1000),
            velocity_percent: parseFloat($scope.lift.velocityPercent),
            acceleration_percent: parseFloat($scope.lift.accelerationPercent)
        };
        
        $http.post(IGUS_API_BASE + '/move', body).then(function() {
            UtilityService.toast.success('Command Move sent');
            $scope.finalizeLog({ action: 'MOVE_LIFT', detail: body });
        }, function(err) {
            UtilityService.toast.error('Move failed: ' + (err.data ? err.data.detail : 'Unknown error'));
        });
    };

    // --- JOG CONTROL (Hold to move) ---
    var liftJogInterval = null;

    $scope.liftJogStart = function(direction) {
        console.log("liftJogStart called:", direction);
        if (!$scope.canControl || !$scope.robot.connected) return;

        if (!currentLogSession || currentLogSession.direction !== 'LIFT_JOG_' + direction) {
            if (currentLogSession) { $scope.finalizeLog(); }
            currentLogSession = { direction: 'LIFT_JOG_' + direction, startTime: Date.now() };
        }

        var speedVal = (parseFloat($scope.lift.jogSpeed) / 100.0) * 10000;
        var jogData = {
            direction: direction === 'up' ? 'positive' : 'negative',
            speed: speedVal,
            ttl_ms: 1000
        };

        $http.post(IGUS_API_BASE + '/drive/jog_start', jogData)
        .then(function() {
            if (liftJogInterval) $interval.cancel(liftJogInterval);
            
            liftJogInterval = $interval(function() {
                $http.post(IGUS_API_BASE + '/drive/jog_update', jogData)
                    .catch(function(updateError) {
                        console.error('Jog update error:', updateError);
                        $interval.cancel(liftJogInterval); // Stop spamming API
                        UtilityService.toast.error('Lost connection during movement!');
                    });
            }, 150);
        })
        .catch(function(startError) {
            if (startError.data && startError.data.message) {
                    UtilityService.toast.error(startError.data.message);
            }
        });
    };

    $scope.liftJogStop = function() {
        console.log("liftJogStop called");
        if (liftJogInterval) {
            $interval.cancel(liftJogInterval);
            liftJogInterval = null;
        }
        $http.post(IGUS_API_BASE + '/drive/jog_stop', {});
        $scope.finalizeLog();
    };

    $scope.liftReference = function() {
        if (!$scope.canControl || !$scope.robot.connected) return;
        $http.post(IGUS_API_BASE + '/reference', {}).then(function() {
            UtilityService.toast.info('Reference started');
            $scope.finalizeLog({ action: 'LIFT_REFERENCE' });
        });
    };

    $scope.liftFaultReset = function() {
        if (!$scope.canControl || !$scope.robot.connected) return;
        $http.post(IGUS_API_BASE + '/fault_reset', {}).then(function() {
            UtilityService.toast.success('Fault reset sent');
            $scope.finalizeLog({ action: 'LIFT_FAULT_RESET' });
        });
    };

    $scope.stopDrive = function() {
        $http.post(IGUS_API_BASE + '/drive/stop', { mode: 'quick_stop', timeout_ms: 5000 }).then(function() {
            $scope.finalizeLog({ action: 'LIFT_STOP' });
        });
    };

    $scope.faultReset = function(component) {
        if (!$scope.canControl) return;
        $scope.finalizeLog({ action: 'faultReset', detail: { component: component } });
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
        });
    };

    // ... (UI/Camera helpers remain same) ...

    $scope.setCameraView = function(id) {
        var view = $scope.cameraViews.find(function(v) { return v.id === id; });
        if (!view) return;
        $scope.currentCameraView = id;
        if (id === '3d') {
            $scope.liveCameraUrl = $scope.trustedUrls.arm3d;
        } else if (id !== 'grid' && view.path) {
            $scope.liveCameraUrl = $sce.trustAsResourceUrl(LIVE_CAMERA_BASE + view.path);
        }
    };

    $scope.updateSnapshotUrls = function() {
        $scope.cameraTimestamp = Date.now();
        $scope.colorSnapshotUrl = 'rest/robot-api/api/v1/color_camera/snapshot.jpg?t=' + $scope.cameraTimestamp;
        $scope.depthSnapshotUrl = 'rest/robot-api/api/v1/depth_camera/snapshot.jpg?t=' + $scope.cameraTimestamp;
    };

    $scope.refreshCamera = function() {
        $scope.updateSnapshotUrls();
        UtilityService.toast.info('Camera refreshed');
    };

    $scope.toggleFullscreenCamera = function() {
        var container = document.querySelector('.camera-live-container');
        if (container) {
            if (container.requestFullscreen) container.requestFullscreen();
            else if (container.webkitRequestFullscreen) container.webkitRequestFullscreen();
            else if (container.msRequestFullscreen) container.msRequestFullscreen();
        }
    };

    // Fix for 3D Digital Twin staying big after exiting fullscreen
    var handleFullscreenChange = function() {
        if (!document.fullscreenElement && !document.webkitFullscreenElement && !document.mozFullScreenElement) {
            // Hard reset: momentarily toggle the view to force re-render
            var savedView = $scope.currentCameraView;
            $scope.$apply(function() {
                $scope.currentCameraView = null;
            });

            $timeout(function() {
                $scope.currentCameraView = savedView;
                window.dispatchEvent(new Event('resize'));
            }, 50);
        }
    };
    document.addEventListener('fullscreenchange', handleFullscreenChange);
    document.addEventListener('webkitfullscreenchange', handleFullscreenChange);
    document.addEventListener('mozfullscreenchange', handleFullscreenChange);

    $scope.refreshStatus = function() {
        UtilityService.showLoading();
        RobotService.getStatus().$promise.then(function(response) {
            $scope.robot.mode = response.mode || 'idle';
            if (response && response.symovo) {
                if(response.symovo.battery_level_percent) $scope.robot.batteryLevel = response.symovo.battery_level_percent;
                if(response.symovo.online) $scope.robot.connected = response.symovo.online;
            }
            UtilityService.hideLoading();
        }, function(error) {
            $scope.robot.connected = false;
            UtilityService.hideLoading();
        });

        // Also refresh Igus status explicitly
        $http.get(IGUS_API_BASE + '/status').then(function(res) {
            var status = res.data;
            if (status) {
                $scope.igus.connected = status.connected !== undefined ? status.connected : false;
                $scope.igus.enabled = status.enabled !== undefined ? status.enabled : (status.operation_enabled !== undefined ? status.operation_enabled : false);
                $scope.igus.position = status.position !== undefined ? status.position : 0;
                $scope.igus.isMoving = status.is_moving !== undefined ? status.is_moving : false;
                $scope.igus.referenced = status.homed !== undefined ? status.homed : false;
                $scope.igus.fault = status.error !== undefined ? status.error : false;
            }
        });
    };

    $scope.quickAction = function(action) {
        if (!$scope.canControl) return;
        if (!$scope.robot.connected) return;

        $scope.finalizeLog({
            action: 'quickAction_' + action,
            detail: { action: action, speed: $scope.speed }
        });

        UtilityService.showLoading();
        var promise;
        var velocityBody = { velocity_percent: $scope.speed };

        switch(action) {
            case 'autotake': promise = RobotService.autotake(velocityBody).$promise; break;
            case 'box1': promise = RobotService.moveToBox1(velocityBody).$promise; break;
            case 'box2': promise = RobotService.moveToBox2(velocityBody).$promise; break;
            case 'transport': promise = RobotService.moveToTransport(velocityBody).$promise; break;
            case 'product': promise = RobotService.moveToProduct(velocityBody).$promise; break;
            case 'charging':
                // Updated per MQTT_CLIENT_ros2.md to ensure strict UUID-like format
                var topic = 'gpstracker/robot/' + ROBOT_ID + '/commands/navigateTo';
                // var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
                //     var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
                //     return v.toString(16);
                // });
                var uuid = 'aaaaaaaa-aaaa-aaaa-aaaa-bbbbbbbbbbbс';
                var payload = {
                    command_id: uuid,
                    timestamp: new Date().toISOString(),
                    target_id: 'CHARGER'
                };
                $scope.publishMqtt(topic, payload, 1);
                promise = $timeout(function() { return { status: 'sent' }; }, 200);
                break;
            default: UtilityService.hideLoading(); return;
        }

        promise.then(function(response) {
            UtilityService.hideLoading();
            UtilityService.toast.success('Action started: ' + action);
        }, function(error) {
            UtilityService.hideLoading();
            UtilityService.toast.error('Action failed');
        });
    };

    $scope.safetyRecover = function() {
        if (!$scope.canControl) return;
        UtilityService.showLoading();
        RobotService.recover({}).$promise.then(function() {
            UtilityService.hideLoading();
            UtilityService.toast.success('Safety recover signal sent');
            $scope.finalizeLog({ action: 'SAFETY_RECOVER' });
        }, function(err) {
            UtilityService.hideLoading();
            UtilityService.toast.error('Failed to send Safety Recover');
        });
    };

    $scope.gripperDrop = function() {
        if (!$scope.canControl) return;
        UtilityService.showLoading();
        XArmService.gripperDrop({}).$promise.then(function() {
            UtilityService.hideLoading();
            UtilityService.toast.success('Gripper Drop command sent');
            $scope.finalizeLog({ action: 'GRIPPER_DROP' });
        }, function(err) {
            UtilityService.hideLoading();
            UtilityService.toast.error('Failed to send Gripper Drop');
        });
    };

    $scope.setReady = function() {
        if (!$scope.canControl) return;

        UtilityService.showLoading();
        RobotService.setReady().$promise.then(function(response) {
            UtilityService.hideLoading();
            UtilityService.toast.success('Robot enabled and ready');
            $scope.refreshStatus();
            $scope.finalizeLog({ action: 'ENABLE_ROBOT', detail: { status: 'success' } });
        }, function(error) {
            UtilityService.hideLoading();
            UtilityService.toast.error('Failed to enable robot. Check physical E-Stop.');
            console.error("Set Ready Error:", error);
        });
    };

    // --- POSITIONS MANAGEMENT (Using SymovoService) ---
    $scope.posForm = { visible: false, editId: null, name: '', description: '', params: '{}' };
    $scope.chargingStations = [];
//    $scope.loadCustomPositions = function() {
//         if (!$scope.canControl) return;

//         SymovoService.getPositionsList().$promise
//             .then(function(response) {
//                 $scope.customPositions = response || [];
//             })
//             .catch(function(error) {
//                 // UtilityService.toast.error('Error loading waypoints:');
//                 $scope.customPositions = [];
//             });
//     };
    $scope.loadCustomPositions = function() {
        if (!$scope.canControl) return;

        WaypointService.list().$promise
            .then(function(response) {
                $scope.customPositions = response || [];
            })
            .catch(function(error) {
                UtilityService.toast.error('Error loading waypoints:');
                // $scope.customPositions = [];
            });
    };
    $scope.togglePosForm = function() {
        $scope.posForm.visible = !$scope.posForm.visible;
        if (!$scope.posForm.visible) {
            $scope.posForm.editId = null; $scope.posForm.name = ''; $scope.posForm.description = ''; $scope.posForm.params = '{}';
        }
    };

    $scope.captureCurrentPos = function() {
        var pose = $scope.symovo.pose || { x_m: 0, y_m: 0, theta_deg: 0 };
        try {
            var params = JSON.parse($scope.posForm.params || '{}');
            params.x_m = parseFloat(pose.x_m.toFixed(3));
            params.y_m = parseFloat(pose.y_m.toFixed(3));
            params.theta_deg = parseFloat(pose.theta_deg.toFixed(1));
            $scope.posForm.params = JSON.stringify(params, null, 2);
        } catch (e) {
            $scope.posForm.params = JSON.stringify({ x_m: pose.x_m, y_m: pose.y_m, theta_deg: pose.theta_deg }, null, 2);
        }
    };

    $scope.savePosition = function() {
        if (!$scope.posForm.name) return UtilityService.toast.error('Name is required');
        var params; try { params = JSON.parse($scope.posForm.params || '{}'); } catch (e) { return UtilityService.toast.error('Invalid JSON'); }
        if ($scope.posForm.description) params.description = $scope.posForm.description;

        var body = { name: $scope.posForm.name, params: params };
        if ($scope.posForm.editId) body.id = $scope.posForm.editId;

        SymovoService.savePosition(body).$promise.then(function() {
            UtilityService.toast.success('Saved'); $scope.togglePosForm(); $scope.loadCustomPositions();
            $scope.finalizeLog({ action: 'SAVE_POSITION_DETAIL', detail: body });
        });
    };

    $scope.editSelectedPos = function() {
        if (!$scope.selectedPosition) return UtilityService.toast.warning('Select a position first');
        var p = $scope.selectedPosition;
        $scope.posForm = { visible: true, editId: p.id, name: p.name, description: (p.params && p.params.description) || '', params: JSON.stringify(p.params || {}, null, 2) };
    };

    $scope.deleteSelectedPos = function() {
        if (!$scope.selectedPosition) return;
        if (!confirm('Delete position "' + $scope.selectedPosition.name + '"?')) return;
        SymovoService.deletePosition({ id: $scope.selectedPosition.id }).$promise.then(function() {
            UtilityService.toast.success('Deleted'); $scope.loadCustomPositions(); $scope.selectedPosition = null;
            $scope.finalizeLog({ action: 'DELETE_POSITION_DETAIL' });
        });
    };

    $scope.refreshCharging = function() {
        SymovoService.getChargingStations().$promise.then(function(res) {
            $scope.chargingStations = res.stations || res.charging_stations || res || [];
            var dockedStation = null;
            if($scope.chargingStations && $scope.chargingStations.length > 0) {
                dockedStation = $scope.chargingStations[0]; 
                
            }
            if (dockedStation && dockedStation.state) {
                $scope.currentJob.status = dockedStation.state;
            }
            if($scope.currentJob && $scope.currentJob.id && $scope.currentJob.status === 'OK') {
                UtilityService.toast.success('Docked. Charging in progress.');
                $scope.resetCurrentJob();
            }
        });
    };

    $scope.goToCharging = function(id) {
        $scope.currentJob.name = "Go To Charging Station";
        $scope.currentJob.id = id;
        SymovoService.goToChargingStation({ id: id }, {}).$promise.then(function() {
            UtilityService.toast.success('Heading to station ' + id);
            $scope.startStatusPolling();
            $scope.finalizeLog({ action: 'GO_TO_CHARGING', detail: { id: id } });
        });
    };

    $scope.savePosition = function() {
        if (!$scope.canControl) return;
        if (!$scope.robot.connected) return;
        var name = prompt('Enter position name:');
        if (!name) return;

        $scope.finalizeLog({ action: 'savePosition', detail: { name: name } });
        RobotService.savePosition({ name: name }).$promise.then(function() {
            UtilityService.toast.success('Position saved: ' + name);
            $scope.loadCustomPositions();
        });
    };

    $scope.deletePosition = function(position) {
        if (!$scope.canControl) return;
        if (!confirm('Delete position: ' + position.name + '?')) return;

        $scope.finalizeLog({ action: 'deletePosition', detail: { position_id: position.id, name: position.name } });
        RobotService.deletePosition(position).$promise.then(function() {
            UtilityService.toast.success('Position deleted');
            $scope.loadCustomPositions();
        });
    };

    function generateUUID() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random()*16|0, v = c==='x'?r:(r&0x3|0x8);
            return v.toString(16);
        });
    }
    $scope.resetCurrentJob = function() {
        $scope.currentJob = {
            id : null,
            name : '',
            status : '',
            progress : 0
        }
    }
    $scope.resetCurrentJob();
    $scope.startJob = function() {
        if (!$scope.canControl) return;
        if (!$scope.selectedPosition) { UtilityService.toast.warning('Please select a position first'); return; }

        var posId = $scope.selectedPosition.id;
        $scope.currentJob.name = $scope.selectedPosition.name;
        $scope.currentJob.id = $scope.selectedPosition.id;
        WaypointService.run({ position_id: posId }, {}).$promise.then(function() {
            UtilityService.toast.success('Command sent to ' + posId);
            $scope.navigation.active = true;
            $scope.navigation.target = $scope.selectedPosition;
            $scope.startStatusPolling();
            $scope.finalizeLog({ action: 'NAVIGATE_TO', detail: { position_id: posId } });
        }, function(err) {
            UtilityService.toast.error('Failed to send navigation command');
        });
    };

    $scope.openMission = function() {
        if (!$scope.canControl) return;
        if (!$scope.robot.connected) return;
        if (!$scope.selectedPosition) { UtilityService.toast.warning('Please select a position first'); return; }
        var pass = prompt('Security Check: Enter passcode');
        if (pass) $scope.startJob();
    };

    $scope.runCustomPosition = function(position) {
        $scope.startJob();
    };

    $scope.cancelJob = function() {
        if (!$scope.canControl) return;

        var payload = {
            command_id: $scope.currentStatus ? $scope.currentStatus.goal_id : generateUUID(),
            timestamp: new Date().toISOString()
        };

        SymovoService.cancelNavigation({ robotId: ROBOT_ID }, payload).$promise.then(function() {
            UtilityService.toast.warning('Mission cancelled');
            $scope.navigation.active = false;
            $scope.resetCurrentJob();
            $scope.finalizeLog({ action: 'CANCEL_NAVIGATION' });
        }, function(err) {
            UtilityService.toast.error('Failed to cancel mission');
        });
    };

    $scope.cancelNavigation = function() {
        $scope.navigation.active = false;
        $scope.cancelJob(); };
    $scope.loadCurrentStatusIgus = function() {
        if (!$scope.canControl) return;

        SymovoService.getStatusNavigation({ robotId: ROBOT_ID }).$promise
            .then(function(response) {
                $scope.currentStatus = response || [];
                if ($scope.currentStatus && $scope.currentStatus.status) {
                    $scope.currentJob.status = $scope.currentStatus.status;
                }
                if($scope.currentJob && $scope.currentJob.id && $scope.currentJob.status === 'arrived') {
                    UtilityService.toast.success('Robot positioned. You can now take control.');
                    $scope.resetCurrentJob();
                }
            })
            .catch(function(error) {
                UtilityService.toast.error('Error loading status:');
                // $scope.customPositions = [];
            });
    };
    var statusUpdateInterval = null;
    $scope.startStatusPolling = function() {
        if (statusUpdateInterval) return;
        statusUpdateInterval = $interval(function() {
            if (!$scope.currentJob || !$scope.currentJob.id) {
                $scope.stopStatusPolling();
                return;
            }
            $scope.loadCurrentStatusIgus();
            $scope.refreshCharging();
        }, 1000);
    };

    $scope.stopStatusPolling = function() {
        if (statusUpdateInterval) {
            $interval.cancel(statusUpdateInterval);
            statusUpdateInterval = null;
        }
    };

    $scope.connectWebRTC = function() {
        WebRTCService.connect().then(function() {
            $scope.$applyAsync(function() { UtilityService.toast.success('WebRTC connected!'); });
            UtilityService.hideLoading();
        }).catch(function(error) {
            $scope.$applyAsync(function() { UtilityService.toast.error('WebRTC auto-connect failed.', 'Connection Error'); });
        });
    }
    // --- INITIALIZATION ---
    $scope.init = function() {
        // $scope.loadCustomWaypoints();
        $scope.loadCurrentStatusIgus();
        $scope.loadCustomPositions();
        $scope.refreshCharging();
        $scope.updateSnapshotUrls();
        $document.on('keydown', handleKeyDown);
        $document.on('keyup', handleKeyUp);
        $scope.connectMqtt();
        $scope.connectXArm();
        checkInitialGamepads();
        $scope.connectWebRTC();

        // Use a small timeout to ensure DOM elements are rendered
        $timeout(function() {
            $scope.bindDPadPointerEvents();
            $scope.bindLiftPointerEvents();
            $scope.bindBaseKeyPointerEvents();
        }, 200);

        // Auto-refresh navigation status only if a job is active
        if ($scope.currentJob && $scope.currentJob.id) {
            $scope.startStatusPolling();
        }
    };

    // Cleanup on controller destroy
    $scope.$on('$destroy', function() {
        if (statusUpdateInterval) {
            $interval.cancel(statusUpdateInterval);
            statusUpdateInterval = null;
        }
        $document.off('keydown', handleKeyDown);
        $document.off('keyup', handleKeyUp);
        $window.removeEventListener("gamepadconnected", handleGamepadConnected);
        $window.removeEventListener("gamepaddisconnected", handleGamepadDisconnected);
        $scope.disconnectMqtt();
        // WebRTCService.disconnect(); // Optional: keep connected if needed
    });

    // --- BIND LIFT EVENTS (For professional hold-to-move) ---
    $scope.bindLiftPointerEvents = function() {
        var buttons = [
            { id: 'lift-jog-up', dir: 'up' },
            { id: 'lift-jog-down', dir: 'down' },
            { id: 'btn-liftup-left', dir: 'up' },
            { id: 'btn-liftdown-left', dir: 'down' }
        ];

        buttons.forEach(function(item) {
            var btn = document.getElementById(item.id);
            if (!btn) return;

            btn.addEventListener('pointerdown', function(e) {
                if (!$scope.canControl) return;
                e.preventDefault();
                btn.setPointerCapture(e.pointerId);
                $scope.$applyAsync(function() {
                    $scope.liftJogStart(item.dir);
                    btn.classList.add('pressed');
                });
            });

            var stopEvents = ['pointerup', 'pointercancel'];
            stopEvents.forEach(function(evt) {
                btn.addEventListener(evt, function(e) {
                    if (!$scope.canControl) return;
                    e.preventDefault();
                    $scope.$applyAsync(function() {
                        $scope.liftJogStop();
                        btn.classList.remove('pressed');
                    });
                });
            });
        });
    }

    // --- BIND DPAD EVENTS (Unified for ARM and BASE) ---
    $scope.bindDPadPointerEvents = function() {
        var directions = [
            { id: 'dpad-up-left', armDir: 'forward', baseKey: 'w' },
            { id: 'dpad-down-left', armDir: 'backward', baseKey: 's' },
            { id: 'dpad-left-left', armDir: 'turn_left', baseKey: 'a' },
            { id: 'dpad-right-left', armDir: 'turn_right', baseKey: 'd' },
            
            // Right Control Group (Arm/Hand)
            { id: 'dpad-up-right', armDir: 'attitude-pitch-increase', baseKey: 'i' },
            { id: 'dpad-down-right', armDir: 'attitude-pitch-decrease', baseKey: 'k' },
            { id: 'dpad-left-right', armDir: 'attitude-roll-increase', baseKey: 'j' },
            { id: 'dpad-right-right', armDir: 'attitude-roll-decrease', baseKey: 'l' },
            
            { id: 'btn-backward-right', armDir: 'attitude-yaw-decrease', baseKey: 'o' }, // RZ-
            { id: 'btn-forward-right', armDir: 'attitude-yaw-increase', baseKey: 'u' },  // RZ+
            { id: 'btn-xarm-j1-up', armDir: 'xarm-j1-plus', baseKey: 'y' }, // J1+ (Unique)
            { id: 'btn-xarm-j1-down', armDir: 'xarm-j1-minus', baseKey: 'h' }, // J1- (Unique)

            // Extra Move Buttons
            { id: 'btn-backward-left', armDir: 'lift_up', baseKey: 's' },
            { id: 'btn-forward-left', armDir: 'lift_down', baseKey: 'w' }
        ];

        directions.forEach(function(item) {
            var btn = document.getElementById(item.id);
            if (!btn) return;

            btn.addEventListener('pointerdown', function(e) {
                if (!$scope.canControl) return;
                e.preventDefault();
                btn.setPointerCapture(e.pointerId);
                $scope.$applyAsync(function() {
                    if ($scope.controlTarget === 'arm') {
                        $scope.startMovement(item.armDir);
                    } else {
                        $scope.startBaseTeleop(item.baseKey);
                    }
                    btn.classList.add('pressed'); 
                });
            });

            var stopEvents = ['pointerup', 'pointercancel'];
            stopEvents.forEach(function(evt) {
                btn.addEventListener(evt, function(e) {
                    if (!$scope.canControl) return;
                    e.preventDefault();
                    $scope.$applyAsync(function() {
                        if ($scope.controlTarget === 'arm') {
                            $scope.stopMovement();
                        } else {
                            $scope.stopBaseTeleop(item.baseKey);
                        }
                        btn.classList.remove('pressed'); 
                    });
                });
            });
        });
    }

    // --- BIND BASE KEY EVENTS (For WASD clickable buttons) ---
    $scope.bindBaseKeyPointerEvents = function() {
        var keys = ['w', 'a', 's', 'd'];
        keys.forEach(function(key) {
            var btn = document.getElementById('base-key-' + key);
            if (!btn) return;

            btn.addEventListener('pointerdown', function(e) {
                if (!$scope.canControl || !$scope.baseTeleop.active) return;
                e.preventDefault();
                btn.setPointerCapture(e.pointerId);
                $scope.$applyAsync(function() {
                    $scope.startBaseTeleop(key);
                    btn.classList.add('pressed');
                });
            });

            var stopEvents = ['pointerup', 'pointercancel'];
            stopEvents.forEach(function(evt) {
                btn.addEventListener(evt, function(e) {
                    if (!$scope.canControl) return;
                    e.preventDefault();
                    $scope.$applyAsync(function() {
                        $scope.stopBaseTeleop(key);
                        btn.classList.remove('pressed');
                    });
                });
            });
        });
    }

    $scope.init();

    var robotMoveInterval = null;
    $scope.sendBaseMoveCommand = function(linear, angular) {
        if (!$scope.canControl) return;

        var duration = $scope.baseTeleop.duration || 0.25;
        
        var payload = {
            speed: linear,
            angular_speed: angular,
            duration: duration
        };

        var url = SYMOVO_API_BASE  + '/move/speed';
        
        $http.post(url, payload).then(function(response) {
            // Success
        }).catch(function(error) {
            console.error("Base movement error:", error);
        });
    };

    $scope.startBaseMovement = function(linear, angular) {
        if (!$scope.canControl) return;
        
        $scope.sendBaseMoveCommand(linear, angular);
        
        if (robotMoveInterval) clearInterval(robotMoveInterval);
        robotMoveInterval = setInterval(function() {
            $scope.sendBaseMoveCommand(linear, angular);
        }, 200);
    };
    $scope.stopBaseMovement = function() {
        if (robotMoveInterval) {
            clearInterval(robotMoveInterval);
            robotMoveInterval = null;
        }
        $scope.sendBaseMoveCommand(0, 0);
    };
    $scope.changeSection = function() {
       
        var payload = { 
            velocity_percent: parseFloat($scope.speed) || 50,
            name: $scope.selectedSection ? $scope.selectedSection.value : 'Default Section',
            reset_faults: false 
        };
        UtilityService.showLoading();
        XArmService.changePose(payload).$promise.then(function() {
            UtilityService.hideLoading();
            UtilityService.toast.success('Change Pose command sent');
            $scope.finalizeLog({ action: 'CHANGE_POSE', detail: payload });
        }, function(err) {
            UtilityService.hideLoading();
            UtilityService.toast.error('Failed to change pose');
        });
    };
}