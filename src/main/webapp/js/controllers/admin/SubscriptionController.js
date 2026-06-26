/**
 * gpstracker.Hub - Subscription Controller
 */
/* @ngInject */
function SubscriptionController($scope, UtilityService) {
    
    // Current plan
    $scope.currentPlan = {
        name: 'Business',
        price: '$99',
        period: 'month',
        renewDate: 'March 15, 2026',
        description: 'Up to 10 robots, 200GB storage, Priority support'
    };

    // Available Plans
    $scope.availablePlans = [
        {
          id: 'basic',
          name: 'Basic',
          price: 49,
          features: [
            'Up to 3 robots',
            '50GB storage',
            'Email support',
            'Basic features'
          ]
        },
        {
          id: 'business',
          name: 'Business',
          price: 99,
          features: [
            'Up to 10 robots',
            '200GB storage',
            'Priority support',
            'Advanced features',
            'API access'
          ],
          isActive: true
        },
        {
          id: 'enterprise',
          name: 'Enterprise',
          price: 249,
          features: [
            'Unlimited robots',
            'Unlimited storage',
            '24/7 support',
            'All features',
            'Custom integrations',
            'On-premise option'
          ]
        }
    ];
    
    // Plan features
    $scope.features = [
        'Unlimited AE Units',
        'Unlimited Pilots',
        'Priority 24/7 Support',
        'Custom Integrations',
        'Advanced Analytics',
        'Dedicated Account Manager'
    ];
    
    // Usage data
    $scope.usage = [
        { label: 'AE Units', current: 15, max: 'Unlimited', percentage: 15 },
        { label: 'Active Pilots', current: 24, max: 'Unlimited', percentage: 24 },
        { label: 'Missions', current: 1247, max: null, percentage: 60 },
        { label: 'API Calls', current: '45,230', max: '100,000', percentage: 45 }
    ];
    
    // Payment method
    $scope.paymentMethod = {
        type: 'VISA',
        last4: '4242',
        expires: '12/2027'
    };
    
    // Billing history
    $scope.billingHistory = [
        { invoice: 'INV-2024-012', date: 'Dec 15, 2024', amount: '$2,499.00', status: 'Paid', statusClass: 'success' },
        { invoice: 'INV-2024-011', date: 'Nov 15, 2024', amount: '$2,499.00', status: 'Paid', statusClass: 'success' },
        { invoice: 'INV-2024-010', date: 'Oct 15, 2024', amount: '$2,499.00', status: 'Paid', statusClass: 'success' }
    ];
    
    // Select Plan
    $scope.selectPlan = function(plan) {
        UtilityService.toast.success("Switched to " + plan.name + " plan!");
        $scope.currentPlan.name = plan.name;
        $scope.currentPlan.price = '$' + plan.price;
        // In a real app, we would update the description and other details too
    };

    // Update payment method
    $scope.updatePayment = function() {
        UtilityService.toast.info("This feature will be implemented soon.");
    };
    
    // Download invoice
    $scope.downloadInvoice = function(invoice) {
       UtilityService.toast.info("This feature will be implemented soon.");
    };
    
    // Download all invoices
    $scope.downloadAll = function() {
        UtilityService.toast.info("This feature will be implemented soon.");
    };
}
