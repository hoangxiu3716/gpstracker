angular.module('gpstrackerHub').controller('AboutController', ['$scope', '$rootScope', function($scope, $rootScope) {
    // Controller logic here if needed
    // The page title is handled by the router config, but we can set specific scope variables here
    /* @ngInject */
    $scope.projectInfo = {
        name: "AE.HUB",
        version: "MVP - Driving Dummy",
        description: "A cloud-native platform for on-demand 'Human-in-the-Loop' robot control."
    };

    $scope.features = [
        {
            title: "Secure Remote Control",
            description: "Low-latency WebRTC video streaming and joystick control (< 200ms latency).",
            icon: "video"
        },
        {
            title: "Task-Based Navigation",
            description: "Command robots to specific positions using MQTT for reliable asynchronous communication.",
            icon: "map"
        },
        {
            title: "Real-time Telemetry",
            description: "Monitor robot status, battery levels, and operational metrics in real-time.",
            icon: "activity"
        }
    ];

    $scope.architecture = {
        hotPath: {
            name: "Hot Path (Real-time)",
            tech: "WebRTC",
            usage: "Live video streaming (H.264) and direct joystick control via Data Channel."
        },
        coldPath: {
            name: "Cold Path (Asynchronous)",
            tech: "MQTT",
            usage: "Task commands (navigation), status updates, and telemetry data."
        }
    };

}]);
