import { useState, useEffect } from 'react';
import { subscriptionPlanAPI, subscriptionAPI } from '../services/api';

function SubscriptionManagement({ userId }) {
  const [plans, setPlans] = useState([]);
  const [userSubscriptions, setUserSubscriptions] = useState([]);
  const [selectedPlan, setSelectedPlan] = useState(null);
  const [billingCycle, setBillingCycle] = useState('MONTHLY');
  const [hasActiveSubscription, setHasActiveSubscription] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId]);

  const loadData = async () => {
    try {
      const [plansResponse, subscriptionsResponse, activeResponse] = await Promise.all([
        subscriptionPlanAPI.getActivePlans(),
        subscriptionAPI.getUserSubscriptions(userId),
        subscriptionAPI.checkActiveSubscription(userId),
      ]);
      
      setPlans(plansResponse.data);
      setUserSubscriptions(subscriptionsResponse.data);
      setHasActiveSubscription(activeResponse.data.hasActiveSubscription);
    } catch (error) {
      console.error('Error loading data:', error);
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
    return <div className="flex justify-center p-8">Loading...</div>;
  }

  return (
    <div className="max-w-6xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Subscription Management</h1>

      {/* Active Subscription Status */}
      <div className={`mb-6 p-4 rounded-lg ${hasActiveSubscription ? 'bg-green-100 border border-green-300' : 'bg-yellow-100 border border-yellow-300'}`}>
        <p className="font-semibold">
          {hasActiveSubscription ? '✓ Active Subscription' : '⚠ No Active Subscription'}
        </p>
      </div>

      {/* Current Subscriptions */}
      <div className="mb-8">
        <h2 className="text-2xl font-semibold mb-4">Your Subscriptions</h2>
        {userSubscriptions.length === 0 ? (
          <p className="text-gray-500">No subscriptions found</p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {userSubscriptions.map((subscription) => (
              <div key={subscription.id} className="border rounded-lg p-4 bg-white shadow">
                <h3 className="font-bold text-lg">{subscription.subscriptionPlan.name}</h3>
                <p className="text-gray-600">Billing Cycle: {subscription.billingCycle}</p>
                <p className="text-gray-600">Status: <span className={`font-semibold ${subscription.status === 'ACTIVE' ? 'text-green-600' : 'text-red-600'}`}>{subscription.status}</span></p>
                <p className="text-gray-600">Amount: ${subscription.amount}</p>
                <p className="text-gray-600">Next Billing: {new Date(subscription.nextBillingDate).toLocaleDateString()}</p>
                {subscription.status === 'ACTIVE' && (
                  <button
                    onClick={() => handleCancelSubscription(subscription.id)}
                    className="mt-3 bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
                  >
                    Cancel Subscription
                  </button>
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Available Plans */}
      <div>
        <h2 className="text-2xl font-semibold mb-4">Available Plans</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {plans.map((plan) => (
            <div 
              key={plan.id} 
              className={`border rounded-lg p-4 cursor-pointer transition ${
                selectedPlan?.id === plan.id ? 'border-blue-500 bg-blue-50' : 'bg-white hover:shadow-lg'
              }`}
              onClick={() => setSelectedPlan(plan)}
            >
              <h3 className="font-bold text-xl mb-2">{plan.name}</h3>
              <p className="text-gray-600 mb-3">{plan.description}</p>
              <div className="mb-3">
                <p className="text-2xl font-bold text-blue-600">${plan.monthlyPrice}/mo</p>
                <p className="text-sm text-gray-500">or ${plan.yearlyPrice}/year</p>
              </div>
              <p className="text-sm text-gray-600">{plan.features}</p>
            </div>
          ))}
        </div>

        {selectedPlan && (
          <div className="mt-6 p-4 border rounded-lg bg-gray-50">
            <h3 className="font-semibold text-lg mb-3">Subscribe to {selectedPlan.name}</h3>
            <div className="mb-4">
              <label className="block mb-2 font-medium">Billing Cycle:</label>
              <select 
                value={billingCycle} 
                onChange={(e) => setBillingCycle(e.target.value)}
                className="border rounded px-3 py-2 w-full"
              >
                <option value="MONTHLY">Monthly - ${selectedPlan.monthlyPrice}</option>
                <option value="YEARLY">Yearly - ${selectedPlan.yearlyPrice}</option>
              </select>
            </div>
            <button
              onClick={handleSubscribe}
              className="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600"
            >
              Subscribe Now
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default SubscriptionManagement;
