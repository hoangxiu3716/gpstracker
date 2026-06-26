/**
 * gpstracker.Hub - Server Health Controller
 */
/* @ngInject */
function ServerHealthController($scope) {
    
    // System status
    $scope.systemStatus = {
        status: 'operational',
        message: 'All Systems Operational',
        lastChecked: 'Just now'
    };
    
    // Stats
    $scope.stats = [
        { label: 'Uptime', value: '99.98%', icon: 'server', colorClass: 'green' },
        { label: 'Avg. Response', value: '42ms', icon: 'activity', colorClass: 'cyan' },
        { label: 'CPU Usage', value: '32%', icon: 'cpu', colorClass: 'amber' },
        { label: 'Memory Used', value: '2.4GB', icon: 'database', colorClass: 'purple' }
    ];
    
    // Services
    $scope.services = [
        { name: 'API Gateway', status: 'Operational', statusClass: 'success', responseTime: '28ms', uptime: '99.99%', lastCheck: 'Just now' },
        { name: 'Database (PostgreSQL)', status: 'Operational', statusClass: 'success', responseTime: '12ms', uptime: '99.98%', lastCheck: 'Just now' },
        { name: 'Authentication Service', status: 'Operational', statusClass: 'success', responseTime: '45ms', uptime: '99.97%', lastCheck: 'Just now' },
        { name: 'Real-time Engine', status: 'Operational', statusClass: 'success', responseTime: '8ms', uptime: '99.99%', lastCheck: 'Just now' },
        { name: 'Storage Service', status: 'Operational', statusClass: 'success', responseTime: '156ms', uptime: '99.95%', lastCheck: 'Just now' },
        { name: 'Edge Functions', status: 'Degraded', statusClass: 'warning', responseTime: '320ms', uptime: '99.85%', lastCheck: '2 min ago' }
    ];
    
    // Recent incidents
    $scope.incidents = [];
    $scope.hasNoIncidents = true;
    
    // Refresh status
    $scope.refreshStatus = function() {
        alert('Refreshing status - to be implemented');
    };
}
