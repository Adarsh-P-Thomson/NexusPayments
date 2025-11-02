import { useState, useEffect } from 'react';
import { subscriptionPlanAPI, subscriptionAPI } from '../services/api';

function SubscriptionManagement({ userId = 1 }) { // Default userId for testing
  const [defaultPlans, setDefaultPlans] = useState([]);
  const [offerPlans, setOfferPlans] = useState([]);
  const [userSubscriptions, setUserSubscriptions] = useState([]);
  const [selectedPlan, setSelectedPlan] = useState(null);
  const [billingCycle, setBillingCycle] = useState('MONTHLY');
  const [hasActiveSubscription, setHasActiveSubscription] = useState(false);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('plans'); // 'plans' or 'my-subscriptions'

  useEffect(() => {
    loadData();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId]);

  const loadData = async () => {
    try {
      setLoading(true);
      
      // Load default and offer plans
      const [defaultResponse, offersResponse] = await Promise.all([
        subscriptionPlanAPI.getDefaultPlans(),
        subscriptionPlanAPI.getOfferPlans(),
      ]);
      
      setDefaultPlans(defaultResponse.data);
      setOfferPlans(offersResponse.data);
      
      // Try to load user subscriptions (may fail if not implemented yet)
      try {
        const subscriptionsResponse = await subscriptionAPI.getUserSubscriptions(userId);
        setUserSubscriptions(subscriptionsResponse.data || []);
        
        const activeResponse = await subscriptionAPI.checkActiveSubscription(userId);
        setHasActiveSubscription(activeResponse.data?.hasActiveSubscription || false);
      } catch (subError) {
        console.log('Subscription API not fully implemented yet:', subError.message);
        setUserSubscriptions([]);
        setHasActiveSubscription(false);
      }
      
    } catch (error) {
      console.error('Error loading subscription plans:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubscribe = async () => {
    if (!selectedPlan) return;

    try {
      await subscriptionAPI.createSubscription({
        userId: userId,
        planId: selectedPlan.id,
        billingCycle: billingCycle,
      });
      
      alert('Subscription created successfully!');
      loadData();
      setSelectedPlan(null);
    } catch (error) {
      console.error('Error creating subscription:', error);
      alert('Failed to create subscription');
    }
  };

  const handleCancelSubscription = async (subscriptionId) => {
    if (!confirm('Are you sure you want to cancel this subscription?')) return;

    try {
      await subscriptionAPI.cancelSubscription(subscriptionId);
      alert('Subscription cancelled successfully');
      loadData();
    } catch (error) {
      console.error('Error cancelling subscription:', error);
      alert('Failed to cancel subscription');
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading subscription plans...</p>
        </div>
      </div>
    );
  }

  const parseFeatures = (features) => {
    try {
      return JSON.parse(features);
    } catch {
      return features?.split(',') || [];
    }
  };

  const renderPlanCard = (plan, isOffer = false) => {
    const features = parseFeatures(plan.features);
    const isSelected = selectedPlan?.id === plan.id;
    
    return (
      <div 
        key={plan.id} 
        className={`relative border-2 rounded-xl p-6 cursor-pointer transition-all duration-300 ${
          isSelected 
            ? 'border-blue-500 bg-blue-50 shadow-xl transform scale-105' 
            : 'border-gray-200 bg-white hover:border-blue-300 hover:shadow-lg'
        } ${isOffer ? 'ring-2 ring-orange-400 ring-offset-2' : ''}`}
        onClick={() => setSelectedPlan(plan)}
      >
        {/* Offer Badge */}
        {isOffer && plan.discountPercentage > 0 && (
          <div className="absolute -top-3 -right-3 bg-gradient-to-r from-orange-500 to-red-500 text-white px-4 py-2 rounded-full font-bold shadow-lg transform rotate-12">
            {plan.discountPercentage}% OFF
          </div>
        )}
        
        {/* Plan Type Badge */}
        <div className="flex items-center justify-between mb-3">
          <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
            plan.planType === 'BASIC' ? 'bg-gray-200 text-gray-700' :
            plan.planType === 'PREMIUM' ? 'bg-purple-200 text-purple-700' :
            'bg-gradient-to-r from-yellow-400 to-orange-400 text-white'
          }`}>
            {plan.planType}
          </span>
          {plan.isDefault && (
            <span className="text-xs text-gray-500">⭐ Popular</span>
          )}
        </div>

        {/* Plan Name & Description */}
        <h3 className="text-2xl font-bold mb-2 text-gray-800">{plan.name}</h3>
        <p className="text-gray-600 text-sm mb-4 h-12">{plan.description}</p>

        {/* Pricing */}
        <div className="mb-4 pb-4 border-b border-gray-200">
          <div className="flex items-baseline gap-2">
            <span className="text-4xl font-bold text-blue-600">${plan.monthlyPrice}</span>
            <span className="text-gray-500 text-sm">/month</span>
          </div>
          {plan.yearlyPrice && (
            <p className="text-sm text-gray-500 mt-1">
              or ${plan.yearlyPrice}/year
              {plan.discountPercentage > 0 && billingCycle === 'YEARLY' && (
                <span className="ml-2 text-green-600 font-semibold">
                  (Save {plan.discountPercentage}%)
                </span>
              )}
            </p>
          )}
        </div>

        {/* Features */}
        <div className="space-y-2 mb-4">
          <p className="font-semibold text-sm text-gray-700 mb-2">Features:</p>
          {features.slice(0, 5).map((feature, idx) => (
            <div key={idx} className="flex items-start gap-2">
              <svg className="w-5 h-5 text-green-500 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
              </svg>
              <span className="text-sm text-gray-700">{feature}</span>
            </div>
          ))}
          {features.length > 5 && (
            <p className="text-xs text-gray-500 italic">+{features.length - 5} more features</p>
          )}
        </div>

        {/* Plan Details */}
        <div className="text-xs text-gray-500 space-y-1 pt-3 border-t border-gray-100">
          {plan.maxUsers && plan.maxUsers > 0 && (
            <p>👥 Up to {plan.maxUsers} users</p>
          )}
          {plan.maxUsers === -1 && <p>👥 Unlimited users</p>}
          
          {plan.maxBillsPerMonth && plan.maxBillsPerMonth > 0 && (
            <p>📄 {plan.maxBillsPerMonth} bills per month</p>
          )}
          {plan.maxBillsPerMonth === -1 && <p>📄 Unlimited bills</p>}
          
          {plan.prioritySupport && <p>⚡ Priority support</p>}
          {plan.customBranding && <p>🎨 Custom branding</p>}
          
          {plan.offerValidUntil && (
            <p className="text-orange-600 font-semibold">
              ⏰ Offer valid until {new Date(plan.offerValidUntil).toLocaleDateString()}
            </p>
          )}
        </div>

        {/* Select Button */}
        {isSelected && (
          <div className="mt-4">
            <div className="w-full bg-blue-500 text-white text-center py-2 rounded-lg font-semibold">
              ✓ Selected
            </div>
          </div>
        )}
      </div>
    );
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-blue-50 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-800 mb-2">Subscription Management</h1>
          <p className="text-gray-600">Choose the perfect plan for your needs</p>
        </div>

        {/* Tabs */}
        <div className="flex gap-4 mb-6 border-b border-gray-300">
          <button
            onClick={() => setActiveTab('plans')}
            className={`px-6 py-3 font-semibold transition-colors ${
              activeTab === 'plans'
                ? 'text-blue-600 border-b-2 border-blue-600'
                : 'text-gray-600 hover:text-blue-600'
            }`}
          >
            📋 Available Plans
          </button>
          <button
            onClick={() => setActiveTab('my-subscriptions')}
            className={`px-6 py-3 font-semibold transition-colors ${
              activeTab === 'my-subscriptions'
                ? 'text-blue-600 border-b-2 border-blue-600'
                : 'text-gray-600 hover:text-blue-600'
            }`}
          >
            ⚙️ My Subscriptions
            {userSubscriptions.length > 0 && (
              <span className="ml-2 bg-blue-500 text-white text-xs px-2 py-1 rounded-full">
                {userSubscriptions.length}
              </span>
            )}
          </button>
        </div>

        {/* Plans Tab */}
        {activeTab === 'plans' && (
          <>
            {/* Active Subscription Status */}
            {hasActiveSubscription && (
              <div className="mb-6 p-4 rounded-lg bg-green-100 border-2 border-green-300 flex items-center gap-3">
                <svg className="w-6 h-6 text-green-600" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                </svg>
                <p className="font-semibold text-green-800">✓ You have an active subscription</p>
              </div>
            )}

            {/* Special Offers Section */}
            {offerPlans.length > 0 && (
              <div className="mb-8">
                <div className="flex items-center gap-3 mb-4">
                  <h2 className="text-2xl font-bold text-gray-800">🎉 Limited Time Offers</h2>
                  <span className="px-3 py-1 bg-red-500 text-white text-xs font-bold rounded-full animate-pulse">
                    SPECIAL
                  </span>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {offerPlans.map((plan) => renderPlanCard(plan, true))}
                </div>
              </div>
            )}

            {/* Default Plans Section */}
            <div className="mb-8">
              <h2 className="text-2xl font-bold text-gray-800 mb-4">💎 Standard Plans</h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {defaultPlans.map((plan) => renderPlanCard(plan, false))}
              </div>
            </div>

            {/* Subscribe Section */}
            {selectedPlan && (
              <div className="fixed bottom-0 left-0 right-0 bg-white border-t-2 border-gray-200 shadow-2xl p-6 z-50">
                <div className="max-w-7xl mx-auto">
                  <div className="flex flex-col md:flex-row items-center justify-between gap-4">
                    <div>
                      <h3 className="text-xl font-bold text-gray-800">
                        Subscribe to {selectedPlan.name}
                      </h3>
                      <p className="text-gray-600 text-sm">
                        {billingCycle === 'MONTHLY' 
                          ? `$${selectedPlan.monthlyPrice}/month` 
                          : `$${selectedPlan.yearlyPrice}/year`}
                      </p>
                    </div>
                    
                    <div className="flex items-center gap-4">
                      <div className="flex gap-2 bg-gray-100 rounded-lg p-1">
                        <button
                          onClick={() => setBillingCycle('MONTHLY')}
                          className={`px-4 py-2 rounded-md font-semibold transition-colors ${
                            billingCycle === 'MONTHLY'
                              ? 'bg-white text-blue-600 shadow'
                              : 'text-gray-600 hover:text-blue-600'
                          }`}
                        >
                          Monthly
                        </button>
                        <button
                          onClick={() => setBillingCycle('YEARLY')}
                          className={`px-4 py-2 rounded-md font-semibold transition-colors ${
                            billingCycle === 'YEARLY'
                              ? 'bg-white text-blue-600 shadow'
                              : 'text-gray-600 hover:text-blue-600'
                          }`}
                        >
                          Yearly
                          {selectedPlan.discountPercentage > 0 && (
                            <span className="ml-1 text-xs text-green-600">
                              (Save {selectedPlan.discountPercentage}%)
                            </span>
                          )}
                        </button>
                      </div>

                      <button
                        onClick={handleSubscribe}
                        className="bg-gradient-to-r from-blue-500 to-blue-600 text-white px-8 py-3 rounded-lg font-bold hover:from-blue-600 hover:to-blue-700 shadow-lg transform hover:scale-105 transition-all"
                      >
                        Subscribe Now →
                      </button>

                      <button
                        onClick={() => setSelectedPlan(null)}
                        className="text-gray-500 hover:text-gray-700 font-semibold"
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            )}
          </>
        )}

        {/* My Subscriptions Tab */}
        {activeTab === 'my-subscriptions' && (
          <div>
            <h2 className="text-2xl font-bold text-gray-800 mb-6">Your Active Subscriptions</h2>
            {userSubscriptions.length === 0 ? (
              <div className="text-center py-12 bg-white rounded-lg border-2 border-dashed border-gray-300">
                <svg className="w-16 h-16 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                <p className="text-gray-500 text-lg mb-2">No active subscriptions</p>
                <p className="text-gray-400 text-sm">Subscribe to a plan to get started!</p>
                <button
                  onClick={() => setActiveTab('plans')}
                  className="mt-4 bg-blue-500 text-white px-6 py-2 rounded-lg hover:bg-blue-600 transition-colors"
                >
                  Browse Plans
                </button>
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {userSubscriptions.map((subscription) => (
                  <div key={subscription.id} className="border-2 rounded-xl p-6 bg-white shadow-lg">
                    <div className="flex items-center justify-between mb-4">
                      <h3 className="text-xl font-bold text-gray-800">
                        {subscription.subscriptionPlan?.name || 'Subscription'}
                      </h3>
                      <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                        subscription.status === 'ACTIVE' 
                          ? 'bg-green-100 text-green-700' 
                          : subscription.status === 'CANCELLED'
                          ? 'bg-red-100 text-red-700'
                          : 'bg-yellow-100 text-yellow-700'
                      }`}>
                        {subscription.status}
                      </span>
                    </div>

                    <div className="space-y-2 text-sm text-gray-600 mb-4">
                      <p className="flex justify-between">
                        <span>Billing Cycle:</span>
                        <span className="font-semibold">{subscription.billingCycle}</span>
                      </p>
                      <p className="flex justify-between">
                        <span>Amount:</span>
                        <span className="font-semibold text-blue-600">${subscription.amount}</span>
                      </p>
                      <p className="flex justify-between">
                        <span>Next Billing:</span>
                        <span className="font-semibold">
                          {subscription.nextBillingDate 
                            ? new Date(subscription.nextBillingDate).toLocaleDateString()
                            : 'N/A'}
                        </span>
                      </p>
                      <p className="flex justify-between">
                        <span>Start Date:</span>
                        <span className="font-semibold">
                          {subscription.startDate 
                            ? new Date(subscription.startDate).toLocaleDateString()
                            : 'N/A'}
                        </span>
                      </p>
                    </div>

                    {subscription.status === 'ACTIVE' && (
                      <button
                        onClick={() => handleCancelSubscription(subscription.id)}
                        className="w-full bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition-colors font-semibold"
                      >
                        Cancel Subscription
                      </button>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default SubscriptionManagement;
