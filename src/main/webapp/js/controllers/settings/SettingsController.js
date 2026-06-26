/**
 * gpstracker.Hub - Settings Controller
 */
/* @ngInject */
function SettingsController($scope, $rootScope, $translate, TranslationService, UtilityService) {
    
    // Available languages
    $scope.availableLanguages = TranslationService.getAvailableLanguages();
    
    // Settings
    $scope.settings = {
        language: TranslationService.getCurrentLanguage()
    };
    
    // Change language
    $scope.changeLanguage = function() {
        TranslationService.changeLanguage($scope.settings.language);
        var langName = getLanguageName($scope.settings.language);
        
        // Use translated message
        $translate('MESSAGES.LANGUAGE_CHANGED').then(function(translation) {
            UtilityService.notificationDilog(translation + ' (' + langName + ')');
        });
    };
    
    function getLanguageName(code) {
        for (var i = 0; i < $scope.availableLanguages.length; i++) {
            if ($scope.availableLanguages[i].code === code) {
                return $scope.availableLanguages[i].nativeName;
            }
        }
        return code;
    }
}
