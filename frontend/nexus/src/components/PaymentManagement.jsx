import { useState, useEffect } from 'react';
import { billAPI, paymentAPI, transactionAPI } from '../services/api';

function PaymentManagement({ userId }) {
  const [bills, setBills] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [processingPayment, setProcessingPayment] = useState(false);

  useEffect(() => {
    loadData();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId]);

  const loadData = async () => {
    try {
      const [billsResponse, transactionsResponse] = await Promise.all([
        billAPI.getUserBills(userId),
        transactionAPI.getUserTransactions(userId),
      ]);
      
      setBills(billsResponse.data);
      setTransactions(transactionsResponse.data);
    } catch (error) {
      console.error('Error loading payment data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handlePayment = async (billId) => {
    setProcessingPayment(true);
    try {
      const response = await paymentAPI.initiatePayment({
        billId: billId,
        paymentMethod: 'CREDIT_CARD',
      });
      
      if (response.data.success) {
        alert(`Payment successful! Transaction ID: ${response.data.transactionId}`);
      } else {
        alert(`Payment failed: ${response.data.message}`);
      }
      
      loadData();
    } catch (error) {
      console.error('Error processing payment:', error);
      alert('Payment processing error');
    } finally {
      setProcessingPayment(false);
    }
  };

  const handleRetryPayment = async (transactionId) => {
    setProcessingPayment(true);
    try {
      const response = await paymentAPI.retryPayment(transactionId);
      
      if (response.data.success) {
        alert(`Payment retry successful! Transaction ID: ${response.data.transactionId}`);
      } else {
        alert(`Payment retry failed: ${response.data.message}`);
      }
      
      loadData();
    } catch (error) {
      console.error('Error retrying payment:', error);
      alert('Payment retry error');
    } finally {
      setProcessingPayment(false);
    }
  };

  const getStatusColor = (status) => {
    const colors = {
      PENDING: 'bg-yellow-100 text-yellow-800',
      PAID: 'bg-green-100 text-green-800',
      FAILED: 'bg-red-100 text-red-800',
      SUCCESS: 'bg-green-100 text-green-800',
      RETRYING: 'bg-blue-100 text-blue-800',
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  };

  if (loading) {
    return <div className="flex justify-center p-8">Loading...</div>;
  }

  return (
    <div className="max-w-6xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Payment Management</h1>

      {/* Bills Section */}
      <div className="mb-8">
        <h2 className="text-2xl font-semibold mb-4">Bills</h2>
        {bills.length === 0 ? (
          <p className="text-gray-500">No bills found</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full bg-white border rounded-lg">
              <thead className="bg-gray-100">
                <tr>
                  <th className="px-4 py-2 text-left">Bill Number</th>
                  <th className="px-4 py-2 text-left">Amount</th>
                  <th className="px-4 py-2 text-left">Status</th>
                  <th className="px-4 py-2 text-left">Due Date</th>
                  <th className="px-4 py-2 text-left">Action</th>
                </tr>
              </thead>
              <tbody>
                {bills.map((bill) => (
                  <tr key={bill.id} className="border-t">
                    <td className="px-4 py-2">{bill.billNumber}</td>
                    <td className="px-4 py-2">${bill.amount}</td>
                    <td className="px-4 py-2">
                      <span className={`px-2 py-1 rounded ${getStatusColor(bill.status)}`}>
                        {bill.status}
                      </span>
                    </td>
                    <td className="px-4 py-2">
                      {new Date(bill.dueDate).toLocaleDateString()}
                    </td>
                    <td className="px-4 py-2">
                      {bill.status === 'PENDING' && (
                        <button
                          onClick={() => handlePayment(bill.id)}
                          disabled={processingPayment}
                          className="bg-blue-500 text-white px-4 py-1 rounded hover:bg-blue-600 disabled:bg-gray-400"
                        >
                          Pay Now
                        </button>
                      )}
                      {bill.status === 'PAID' && (
                        <span className="text-green-600">✓ Paid</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Transaction History */}
      <div>
        <h2 className="text-2xl font-semibold mb-4">Transaction History</h2>
        {transactions.length === 0 ? (
          <p className="text-gray-500">No transactions found</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full bg-white border rounded-lg">
              <thead className="bg-gray-100">
                <tr>
                  <th className="px-4 py-2 text-left">Transaction ID</th>
                  <th className="px-4 py-2 text-left">Amount</th>
                  <th className="px-4 py-2 text-left">Status</th>
                  <th className="px-4 py-2 text-left">Date</th>
                  <th className="px-4 py-2 text-left">Retry</th>
                  <th className="px-4 py-2 text-left">Action</th>
                </tr>
              </thead>
              <tbody>
                {transactions.map((transaction) => (
                  <tr key={transaction.id} className="border-t">
                    <td className="px-4 py-2">{transaction.transactionId}</td>
                    <td className="px-4 py-2">${transaction.amount}</td>
                    <td className="px-4 py-2">
                      <span className={`px-2 py-1 rounded ${getStatusColor(transaction.status)}`}>
                        {transaction.status}
                      </span>
                    </td>
                    <td className="px-4 py-2">
                      {new Date(transaction.transactionDate).toLocaleString()}
                    </td>
                    <td className="px-4 py-2">
                      {transaction.scheduledRetryDate && (
                        <span className="text-sm text-gray-600">
                          {new Date(transaction.scheduledRetryDate).toLocaleDateString()}
                        </span>
                      )}
                    </td>
                    <td className="px-4 py-2">
                      {transaction.status === 'FAILED' && (
                        <button
                          onClick={() => handleRetryPayment(transaction.id)}
                          disabled={processingPayment}
                          className="bg-orange-500 text-white px-4 py-1 rounded hover:bg-orange-600 disabled:bg-gray-400"
                        >
                          Retry
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

export default PaymentManagement;
