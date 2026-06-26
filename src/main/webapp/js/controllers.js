/**
 * gpstracker.Hub - Base Controllers
 * Layout controller for main template (htdocs style - plain function)
 */
/* @ngInject */
function LayoutController($scope, $state, $rootScope) {
    
    // Get current page title
    $scope.getPageTitle = function() {
        var state = $state.current;
        var title = state.data ? state.data.pageTitle : 'Dashboard';
        return $rootScope.appName + ': ' + title;
    };
    
    // Navigation items
    $scope.mainNavItems = [
        { name: 'Home', icon: 'home', state: 'main.dashboard' },
        { name: 'Mission Control', icon: 'rocket', state: 'main.missionControl' },
        { name: 'Pilot Academy', icon: 'award', state: 'main.academy' },
        { name: 'Data Center', icon: 'pie-chart', state: 'main.dataCenter' },
        { name: 'AE Units', icon: 'bot', state: 'main.aeUnits' }
    ];
    
    $scope.profileNavItems = [
        { name: 'Profile', icon: 'user', state: 'main.profile' },
        { name: 'Settings', icon: 'settings', state: 'main.settings' }
    ];
    
    $scope.adminNavItems = [
        { name: 'Administration', icon: 'shield-check', state: 'main.administration' },
        { name: 'Server Health', icon: 'activity', state: 'main.serverHealth' },
        { name: 'Interfaces', icon: 'link', state: 'main.interfaces' },
        { name: 'Subscription', icon: 'credit-card', state: 'main.subscription' }
    ];
    
    // Check if state is active
    $scope.isActive = function(stateName) {
        return $state.includes(stateName);
    };
    
    // Navigate to state
    $scope.goTo = function(stateName) {
        $state.go(stateName);
        $rootScope.closeSidebar();
    };
}
