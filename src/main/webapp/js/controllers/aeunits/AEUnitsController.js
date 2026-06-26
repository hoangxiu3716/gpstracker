/**
 * gpstracker.Hub - AE Units Controller
 */
/* @ngInject */
function AEUnitsController($scope, FleetService, UtilityService, $state) {
    
    // Stats
    $scope.updateStats = function() {
        var allUnits = FleetService.getAllUnits();
        var onlineCount = allUnits.filter(u => u.connected || u.status === 'Online').length;
        var maintenanceCount = allUnits.filter(u => u.status === 'Maintenance').length;

        $scope.stats = [
            { label: 'Total Units', value: allUnits.length, icon: 'bot', colorClass: 'cyan' },
            { label: 'Online', value: onlineCount, icon: 'check', colorClass: 'green' },
            { label: 'Charging', value: 0, icon: 'battery', colorClass: 'amber' },
            { label: 'Maintenance', value: maintenanceCount, icon: 'tool', colorClass: 'purple' }
        ];
    };
    
    // Search query
    $scope.searchQuery = '';
    
    // Load units from FleetService
    $scope.units = FleetService.getAllUnits();
    $scope.updateStats();

    // Listen for updates
    $scope.$on('fleet.updated', function() {
        $scope.units = FleetService.getAllUnits();
        $scope.updateStats();
    });
    
    // Get battery class
    $scope.getBatteryClass = function(battery) {
        if (battery >= 50) return '';
        if (battery >= 20) return 'warning';
        return 'danger';
    };
    
    // Add new unit
    $scope.addUnit = function() {
        UtilityService.toast.info("This feature will be implemented soon.");
    };
    
    // View unit details
    $scope.viewDetails = function(unit) {
        if(unit && unit.id == 'fahrdummy-01') {
            $state.go("main.missioncontrol");
            return;
        }
        UtilityService.toast.error("Robot Is not Ready");
    };
    
    // Filter units
    $scope.filterUnits = function() {
        UtilityService.toast.info("This feature will be implemented soon.");
    };
}
