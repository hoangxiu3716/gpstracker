/**
 * AROC.Hub - Dashboard Controller
 */
function DashboardController($scope, $state) {

    // Stats data
    $scope.stats = [
        {
            label: 'Active AE Units',
            value: 12,
            icon: 'bot',
            colorClass: 'cyan'
        },
        {
            label: 'Active Missions',
            value: 8,
            icon: 'rocket',
            colorClass: 'green'
        },
        {
            label: 'Certified Pilots',
            value: 24,
            icon: 'user',
            colorClass: 'amber'
        },
        {
            label: 'System Uptime',
            value: '99.8%',
            icon: 'activity',
            colorClass: 'purple'
        }
    ];

    // Recent missions
    $scope.recentMissions = [
        { id: 'MSN-001', status: 'Active', statusClass: 'success', pilot: 'John Doe', progress: 75 },
        { id: 'MSN-002', status: 'Pending', statusClass: 'warning', pilot: 'Jane Smith', progress: 30 },
        { id: 'MSN-003', status: 'Active', statusClass: 'success', pilot: 'Mike Johnson', progress: 90 },
        { id: 'MSN-004', status: 'Completed', statusClass: 'info', pilot: 'Sarah Wilson', progress: 100 }
    ];

    // AE Units status
    $scope.aeUnits = [
        { id: 'AE-001', status: 'Online', statusClass: 'success', battery: 85, location: 'Warehouse A' },
        { id: 'AE-002', status: 'Online', statusClass: 'success', battery: 92, location: 'Warehouse B' },
        { id: 'AE-003', status: 'Charging', statusClass: 'warning', battery: 23, location: 'Dock 1' },
        { id: 'AE-004', status: 'Offline', statusClass: 'danger', battery: 0, location: 'Maintenance' }
    ];

    // Quick actions
    $scope.newMission = function() {
        $state.go('main.missionControl');
    };

    $scope.addAEUnit = function() {
        $state.go('main.aeUnits');
    };

    $scope.viewSystemStatus = function() {
        $state.go('main.serverHealth');
    };
}
