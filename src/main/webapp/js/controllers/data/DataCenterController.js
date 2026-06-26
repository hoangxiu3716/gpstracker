/**
 * gpstracker.Hub - Data Center Controller
 */
/* @ngInject */
function DataCenterController($scope, $timeout, UtilityService, AuthService) {
    $scope.showOnlyAdmin = AuthService.hasAnyRole(['ROLE_ADMIN']);
    // Stats Summary
    $scope.summaryStats = [];
    if($scope.showOnlyAdmin) {
        $scope.summaryStats.push({ label: 'AE Units in Action', value: '42', change: '+3', changeType: 'positive', icon: 'bot' },
            { label: 'Operating Time', value: '1,248 hrs', change: '+124', changeType: 'positive', icon: 'clock' },
            { label: 'Picks Today', value: '1,876', change: '+12%', changeType: 'positive', icon: 'package' },
            { label: 'Autonomous %', value: '87%', change: '+2%', changeType: 'positive', icon: 'cpu' },
            { label: 'Locations', value: '12', change: '0', changeType: 'neutral', icon: 'map-pin' },
            { label: 'Avg. Pick Time', value: '42s', change: '-3s', changeType: 'positive', icon: 'timer' });
    }else {
        $scope.summaryStats.push({ label: 'Operating Time', value: '4,3 hrs', change: '+124', changeType: 'positive', icon: 'clock' },
            { label: 'Picks Today', value: '12', change: '+12%', changeType: 'positive', icon: 'package' },
            { label: 'Autonomous %', value: '87%', change: '+2%', changeType: 'positive', icon: 'cpu' });
    }
    // Data Insights
    $scope.insights = [
        { 
            title: 'Efficiency Improvement', 
            description: 'System efficiency has improved by 2.7% compared to the previous period. This is likely due to the recent software updates and optimized routing algorithms.', 
            type: 'success', 
            icon: 'trending-up' 
        },
        { 
            title: 'Peak Activity Pattern', 
            description: 'Peak activity consistently occurs between 10:00 and 11:00. Consider scheduling maintenance outside of this window to minimize operational impact.', 
            type: 'info', 
            icon: 'activity' 
        },
        { 
            title: 'Battery Optimization Needed', 
            description: '10% of units are showing critical battery usage patterns. Recommend reviewing charging schedules for units AE-1005, AE-1012, and AE-1018.', 
            type: 'warning', 
            icon: 'zap' 
        }
    ];
            // Date range filter
    
            $scope.dateRange = 'Day';
    
            $scope.unitFilter = 'All Units';
            // Dynamic chart labels
    
            $scope.chartTitles = {
    
                primary: 'Picks by Hour',
    
                secondary: 'Activity Level',
    
                primarySubtitle: 'Today',
    
                secondarySubtitle: 'Today'
    
            };
            $scope.changeDateRange = function(range) {
    
                $scope.dateRange = range;
    
                console.log("Changing date range to: " + range);
                // Update labels and data based on range
    
                if (range === 'Day') {
    
                    $scope.chartTitles.primary = 'Picks by Hour';
    
                    $scope.chartTitles.secondary = 'Activity Level';
    
                    $scope.chartTitles.primarySubtitle = 'Today';
    
                    $scope.chartTitles.secondarySubtitle = 'Today';
    
                } else if (range === 'Week') {
    
                    $scope.chartTitles.primary = 'Picks by Day';
    
                    $scope.chartTitles.secondary = 'Efficiency';
    
                    $scope.chartTitles.primarySubtitle = 'This Week';
    
                    $scope.chartTitles.secondarySubtitle = 'This Week';
    
                } else if (range === 'Month') {
    
                    $scope.chartTitles.primary = 'Picks by Week';
    
                    $scope.chartTitles.secondary = 'Monthly Growth';
    
                    $scope.chartTitles.primarySubtitle = 'This Month';
    
                    $scope.chartTitles.secondarySubtitle = 'This Month';
    
                } else if (range === 'Year') {
    
                    $scope.chartTitles.primary = 'Picks by Month';
    
                    $scope.chartTitles.secondary = 'Annual Performance';
    
                    $scope.chartTitles.primarySubtitle = '2026';
    
                    $scope.chartTitles.secondarySubtitle = '2026';
    
                }
                $scope.initCharts(range);
    
            };
        // Analytics data (Expanded for more rows)
        $scope.analyticsData = [];
        if($scope.showOnlyAdmin) {
            $scope.analyticsData.push({ date: 'Feb 4, 2026', totalMissions: 42, completed: 41, failed: 1, avgDuration: '38 min', efficiency: '97.6%', efficiencyClass: 'success' },
    
            { date: 'Feb 3, 2026', totalMissions: 38, completed: 38, failed: 0, avgDuration: '35 min', efficiency: '100%', efficiencyClass: 'success' },
    
            { date: 'Feb 2, 2026', totalMissions: 45, completed: 43, failed: 2, avgDuration: '40 min', efficiency: '95.5%', efficiencyClass: 'success' },
    
            { date: 'Feb 1, 2026', totalMissions: 30, completed: 28, failed: 2, avgDuration: '45 min', efficiency: '93.3%', efficiencyClass: 'success' },
    
            { date: 'Jan 31, 2026', totalMissions: 22, completed: 20, failed: 2, avgDuration: '51 min', efficiency: '90.9%', efficiencyClass: 'warning' },
    
            { date: 'Jan 30, 2026', totalMissions: 26, completed: 26, failed: 0, avgDuration: '40 min', efficiency: '100%', efficiencyClass: 'success' });
        }else {
            $scope.analyticsData.push({ date: 'Feb 4, 2026', totalMissions: 22, completed: 21, failed: 1, avgDuration: '28 min', efficiency: '97.6%', efficiencyClass: 'success' },
    
            { date: 'Feb 3, 2026', totalMissions: 18, completed: 18, failed: 0, avgDuration: '15 min', efficiency: '100%', efficiencyClass: 'success' },
    
            { date: 'Feb 2, 2026', totalMissions: 25, completed: 23, failed: 2, avgDuration: '20 min', efficiency: '95.5%', efficiencyClass: 'success' },
    
            { date: 'Feb 1, 2026', totalMissions: 10, completed: 8, failed: 2, avgDuration: '25 min', efficiency: '93.3%', efficiencyClass: 'success' },
    
            { date: 'Jan 31, 2026', totalMissions: 2, completed: 0, failed: 2, avgDuration: '31 min', efficiency: '100%', efficiencyClass: 'danger' },
    
            { date: 'Jan 30, 2026', totalMissions: 6, completed: 6, failed: 0, avgDuration: '20 min', efficiency: '100%', efficiencyClass: 'success' });
        }
            // Charts
            var charts = {};
           $scope.initCharts = function(range) {
    
                // Use provided range or current dateRange
    
                var activeRange = range || $scope.dateRange;
    
                console.log("Initializing Data Center Charts for range: " + activeRange);
                // Destroy existing charts if refreshing
                Object.values(charts).forEach(function(chart) { if (chart) chart.destroy(); });
                // dramatic grow animation
                var growAnimation = {
                    y: {
                        from: 0,
                        duration: 2000,
                        easing: 'easeOutQuart'
                    },
                    opacity: {
                        from: 0,
                        to: 1,
                        duration: 1000
                    }
                };
                // Mock data generator based on range
    
                var getChartData = function(range, chartType) {
    
                    if (range === 'Day') {
    
                        if (chartType === 'primary') return { labels: ['08:00', '10:00', '12:00', '14:00', '16:00', '18:00'], data: [120, 320, 180, 210, 190, 80] };
    
                        return { labels: ['08:00', '10:00', '12:00', '14:00', '16:00', '18:00'], data: [65, 88, 72, 91, 84, 76] };
    
                    } else if (range === 'Week') {
    
                        if (chartType === 'primary') return { labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'], data: [1800, 2100, 1876, 2300, 2200, 900, 850] };
    
                        return { labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'], data: [85, 92, 88, 94, 91, 78, 75] };
    
                    } else if (range === 'Month') {
    
                        if (chartType === 'primary') return { labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'], data: [8500, 9200, 7800, 8900] };
    
                        return { labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'], data: [88, 91, 84, 90] };
    
                    } else { // Year
    
                        if (chartType === 'primary') return { labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'], data: [32000, 35000, 31000, 38000, 42000, 39000] };
    
                        return { labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'], data: [82, 85, 81, 89, 92, 88] };
    
                    }
    
                };
                var primaryData = getChartData(activeRange, 'primary');
                var secondaryData = getChartData(activeRange, 'secondary');
                // Picks by Hour (Bar Chart)
                var ctxHour = document.getElementById('picksByHourChart');
                if (ctxHour) {
                    charts.hour = new Chart(ctxHour, {
                        type: 'bar',
                        data: {
                            labels: primaryData.labels,
                            datasets: [{
                                label: 'Picks',
                                data: primaryData.data,
                                backgroundColor: 'rgba(6, 182, 212, 0.6)',
                                borderColor: '#06b6d4',
                                borderWidth: 1,
                                borderRadius: 4
                            }]
                        },
                        options: { 
                            responsive: true, 
                            maintainAspectRatio: false,
                            animations: growAnimation
                        }
                    });
                }
                // Picks by Day (Line Chart)
                var ctxDay = document.getElementById('picksByDayChart');
                if (ctxDay) {
                    charts.day = new Chart(ctxDay, {
                        type: 'line',
                        data: {
                            labels: secondaryData.labels,
                            datasets: [{
                                    label: activeRange === 'Day' ? 'Activity %' : 'Efficiency %',
                                    data: secondaryData.data,
                                    borderColor: '#8b5cf6',
                                    backgroundColor: 'rgba(139, 92, 246, 0.1)',
                                    fill: true,
                                    tension: 0
    
                                }]
    
                                },
    
                        options: { 
        
                            responsive: true, 
                            maintainAspectRatio: false,
                            animations: {
                                y: {
                                    from: 500,
                                    duration: 2000,
                                    easing: 'easeOutQuart'
                                    }
                                }}});
                             }
                     var ctxType = document.getElementById('pickTypesChart');
                                            if (ctxType) {
                                                charts.type = new Chart(ctxType, {
                                                    type: 'doughnut',
                                                    data: {
                                                        labels: ['Standard', 'Express', 'Fragile', 'Bulk'],
                                                        datasets: [{
                                                            data: [65, 15, 12, 8],
                                                            backgroundColor: ['#06b6d4', '#8b5cf6', '#f59e0b', '#ef4444']
                                                        }]
                                                    },
                                                    options: { 
                                                        responsive: true,
                                                        maintainAspectRatio: false,
                                                        plugins: { legend: { position: 'bottom' } },
                                                        animation: {
                                                            animateRotate: true,
                                                            animateScale: true,
                                                            duration: 2000,
                                                            easing: 'easeOutQuart'
                                                        }
                                                    }
                                                });
                                            }
                                            // Error Rates (Line Chart)
                                            var ctxError = document.getElementById('errorRatesChart');
                                            if (ctxError) {
                                                charts.error = new Chart(ctxError, {
                                                    type: 'line',
                                                    data: {
                                                        labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                                                        datasets: [{
                                                            label: 'Error Rate %',
                                                            data: [1.2, 0.8, 1.5, 0.9, 0.5, 1.1, 0.7],
                                                            borderColor: '#ef4444',
                                                            backgroundColor: 'rgba(239, 68, 68, 0.1)',
                                                            fill: true,
                                                            tension: 0.4
                                                        }]
                                                    },
                                                    options: { 
                                                        responsive: true,
                                                        maintainAspectRatio: false,
                                                        animations: growAnimation
                                                    }
                                                });
                                            }
                                            // Warehouse Utilization (Bar Chart)
                                            var ctxUtil = document.getElementById('utilizationChart');
                                            if (ctxUtil) {
                                                charts.util = new Chart(ctxUtil, {
                                                    type: 'bar',
                                                    data: {
                                                        labels: ['Zone A', 'Zone B', 'Zone C', 'Zone D', 'Zone E'],
                                                        datasets: [{
                                                            label: 'Utilization %',
                                                            data: [85, 72, 94, 61, 78],
                                                            backgroundColor: '#f59e0b'
                                                        }]
                                                    },
                                                    options: { 
                                                        responsive: true, 
                                                        maintainAspectRatio: false,
                                                        indexAxis: 'y',
                                                        animations: {
                                                            x: {
                                                                from: 0,
                                                                duration: 1500,
                                                                easing: 'easeOutQuart'
                                                            }
                                                        }
                                                    }
                                                });
                                            }
    
        }
    
        // Initial load: Match the default selection
        $timeout(function() {
            $scope.initCharts($scope.dateRange);
        }, 500);

    // Export functions
    $scope.exportCSV = function() {
        UtilityService.toast.info("This feature will be implemented soon.");
    };
    
    $scope.exportPDF = function() {
        UtilityService.toast.info("This feature will be implemented soon.");
    };
}