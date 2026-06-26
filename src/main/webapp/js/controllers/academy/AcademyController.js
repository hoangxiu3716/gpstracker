/**
 * gpstracker.Hub - Academy Controller
 */
/* @ngInject */
function AcademyController($scope, UtilityService) {
    
    // Stats for the hero section
    $scope.academyStats = {
        activePilots: '2,450+',
        modules: '180+',
        successRate: '95%',
        support: '24/7'
    };

    // Courses Mock Data
    $scope.courses = [
        {
            id: 'ae01-basics',
            title: 'AE.01 Fundamentals',
            description: 'Learn the basics of AE.01 control and navigation.',
            duration: '4 hours',
            level: 'Beginner',
            completionRate: 85,
            enrolled: 248,
            progress: 0,
            statusClass: 'success' 
        },
        {
            id: 'advanced-navigation',
            title: 'Advanced Navigation',
            description: 'Master complex navigation scenarios and obstacle avoidance.',
            duration: '6 hours',
            level: 'Advanced',
            completionRate: 72,
            enrolled: 156,
            progress: 45,
            statusClass: 'warning'
        },
        {
            id: 'multi-robot',
            title: 'Multi-Robot Coordination',
            description: 'Learn effective coordination of multiple robots in operation.',
            duration: '8 hours',
            level: 'Expert',
            completionRate: 64,
            enrolled: 92,
            progress: 100,
            statusClass: 'danger'
        }
    ];

    // Achievements Mock Data
    $scope.achievements = [
        { name: 'First Steps', description: 'Completed 10 training sessions', progress: 80 },
        { name: 'Navigation Artist', description: '50 successful navigations', progress: 45 },
        { name: 'Team Player', description: '25 multi-robot missions', progress: 30 }
    ];

    // Leaderboard Mock Data
    $scope.leaderboard = [
        { rank: 1, name: 'Sarah Schmidt', points: 2840, missions: 156 },
        { rank: 2, name: 'Marcus Weber', points: 2720, missions: 143 },
        { rank: 3, name: 'Laura Meyer', points: 2650, missions: 138 },
        { rank: 4, name: 'Thomas Bauer', points: 2580, missions: 132 },
        { rank: 5, name: 'Anna Schulz', points: 2510, missions: 129 }
    ];
    
    // Actions
    $scope.startCourse = function(course) {
        console.log('Starting course: ' + course.title);
    };
    $scope.testFunction = function() {
        UtilityService.toast.info("This feature will be implemented soon.");
    }
    $scope.onAchievementClick = function(achievement) {
        achievement.clicked = !achievement.clicked;
        console.log('Achievement toggled:', achievement.name, 'Clicked:', achievement.clicked);
        // if (achievement.clicked) {
        //     UtilityService.toast.success("Highlighting: " + achievement.name);
        // }
    }
}
