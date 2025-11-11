import { useState, useEffect } from 'react';
import { billAPI, paymentAPI, transactionAPI } from '../services/api';

function PaymentManagement({ userId }) {
  const [bills, setBills] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [processingPayment, setProcessingPayment] = useState(false);
  const [showBillGenerator, setShowBillGenerator] = useState(false);
  const [generatedBill, setGeneratedBill] = useState(null);
  const [billRequest, setBillRequest] = useState({
    period: 'MONTH',
    startDate: '',
    endDate: '',
    customerName: '',
  });

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

  const handleGenerateBill = async () => {
    try {
      const response = await billAPI.generateFromSales(billRequest);
      setGeneratedBill(response.data);
    } catch (error) {
      console.error('Error generating bill:', error);
      alert('Failed to generate bill');
    }
  };

  const handlePrintBill = () => {
    window.print();
  };

  const resetBillGenerator = () => {
    setGeneratedBill(null);
    setBillRequest({
      period: 'MONTH',
      startDate: '',
      endDate: '',
      customerName: '',
    });
  };

  if (loading) {
    return <div className="flex justify-center p-8">Loading...</div>;
  }

  return (
    <div className="max-w-6xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Payment Management</h1>

      {/* Bill Generator Section */}
      <div className="mb-8 bg-white rounded-lg shadow p-6">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-2xl font-semibold">Generate Bill from Sales</h2>
          <button
            onClick={() => setShowBillGenerator(!showBillGenerator)}
            className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
          >
            {showBillGenerator ? 'Hide' : 'Show'} Generator
          </button>
        </div>

        {showBillGenerator && (
          <div className="space-y-4">
            {!generatedBill ? (
              <div className="space-y-4">
                {/* Period Selection */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Time Period
                    </label>
                    <select
                      value={billRequest.period}
                      onChange={(e) => setBillRequest({...billRequest, period: e.target.value})}
                      className="w-full border border-gray-300 rounded px-3 py-2 focus:ring-2 focus:ring-blue-500"
                    >
                      <option value="WEEK">This Week</option>
                      <option value="MONTH">This Month</option>
                      <option value="YEAR">This Year</option>
                      <option value="CUSTOM">Custom Range</option>
                    </select>
                  </div>

                  {billRequest.period === 'CUSTOM' && (
                    <>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Start Date
                        </label>
                        <input
                          type="date"
                          value={billRequest.startDate}
                          onChange={(e) => setBillRequest({...billRequest, startDate: e.target.value})}
                          className="w-full border border-gray-300 rounded px-3 py-2 focus:ring-2 focus:ring-blue-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          End Date
                        </label>
                        <input
                          type="date"
                          value={billRequest.endDate}
                          onChange={(e) => setBillRequest({...billRequest, endDate: e.target.value})}
                          className="w-full border border-gray-300 rounded px-3 py-2 focus:ring-2 focus:ring-blue-500"
                        />
                      </div>
                    </>
                  )}

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Customer Name (Optional)
                    </label>
                    <input
                      type="text"
                      value={billRequest.customerName}
                      onChange={(e) => setBillRequest({...billRequest, customerName: e.target.value})}
                      placeholder="Leave empty for all customers"
                      className="w-full border border-gray-300 rounded px-3 py-2 focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>

                <button
                  onClick={handleGenerateBill}
                  className="bg-green-500 text-white px-6 py-2 rounded hover:bg-green-600"
                >
                  Generate Bill
                </button>
              </div>
            ) : (
              <div className="space-y-4">
                {/* Bill Preview */}
                <div className="border border-gray-300 rounded-lg p-6 bg-gray-50">
                  <div className="flex justify-between items-start mb-6">
                    <div>
                      <h3 className="text-2xl font-bold text-gray-800">SALES BILL</h3>
                      <p className="text-gray-600">Bill No: {generatedBill.billNumber}</p>
                      <p className="text-gray-600">Date: {new Date(generatedBill.generatedDate).toLocaleDateString()}</p>
                    </div>
                    <div className="text-right">
                      <p className="text-gray-700 font-medium">Period: {generatedBill.period}</p>
                      <p className="text-sm text-gray-600">
                        {new Date(generatedBill.periodStartDate).toLocaleDateString()} - {new Date(generatedBill.periodEndDate).toLocaleDateString()}
                      </p>
                      {generatedBill.customerName && (
                        <p className="text-gray-700 mt-2">Customer: {generatedBill.customerName}</p>
                      )}
                    </div>
                  </div>

                  {/* Line Items Table */}
                  <div className="mb-6">
                    <table className="w-full border-collapse">
                      <thead>
                        <tr className="bg-gray-200">
                          <th className="border border-gray-300 px-4 py-2 text-left">Product</th>
                          <th className="border border-gray-300 px-4 py-2 text-right">Qty</th>
                          <th className="border border-gray-300 px-4 py-2 text-right">Unit Price</th>
                          <th className="border border-gray-300 px-4 py-2 text-right">Subtotal</th>
                          <th className="border border-gray-300 px-4 py-2 text-right">Discount</th>
                          <th className="border border-gray-300 px-4 py-2 text-right">Total</th>
                        </tr>
                      </thead>
                      <tbody>
                        {generatedBill.lineItems.map((item, index) => (
                          <tr key={index}>
                            <td className="border border-gray-300 px-4 py-2">{item.productName}</td>
                            <td className="border border-gray-300 px-4 py-2 text-right">{item.quantity}</td>
                            <td className="border border-gray-300 px-4 py-2 text-right">${item.unitPrice.toFixed(2)}</td>
                            <td className="border border-gray-300 px-4 py-2 text-right">${item.subtotal.toFixed(2)}</td>
                            <td className="border border-gray-300 px-4 py-2 text-right">${item.discount.toFixed(2)}</td>
                            <td className="border border-gray-300 px-4 py-2 text-right font-medium">${item.total.toFixed(2)}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>

                  {/* Totals */}
                  <div className="flex justify-end">
                    <div className="w-64 space-y-2">
                      <div className="flex justify-between">
                        <span>Subtotal:</span>
                        <span>${generatedBill.subtotal.toFixed(2)}</span>
                      </div>
                      <div className="flex justify-between text-green-600">
                        <span>Total Discount:</span>
                        <span>-${generatedBill.totalDiscount.toFixed(2)}</span>
                      </div>
                      <div className="flex justify-between border-t pt-2">
                        <span>Taxable Amount:</span>
                        <span>${generatedBill.taxableAmount.toFixed(2)}</span>
                      </div>
                      <div className="flex justify-between">
                        <span>Tax ({(generatedBill.taxRate * 100).toFixed(0)}%):</span>
                        <span>${generatedBill.taxAmount.toFixed(2)}</span>
                      </div>
                      <div className="flex justify-between font-bold text-lg border-t-2 border-gray-800 pt-2">
                        <span>Grand Total:</span>
                        <span>${generatedBill.grandTotal.toFixed(2)}</span>
                      </div>
                    </div>
                  </div>

                  {/* Statistics */}
                  <div className="mt-6 pt-6 border-t border-gray-300 grid grid-cols-3 gap-4 text-sm text-gray-600">
                    <div>
                      <p className="font-medium">Total Transactions</p>
                      <p className="text-2xl font-bold text-gray-800">{generatedBill.totalTransactions}</p>
                    </div>
                    <div>
                      <p className="font-medium">Items Sold</p>
                      <p className="text-2xl font-bold text-gray-800">{generatedBill.totalItemsSold}</p>
                    </div>
                    <div>
                      <p className="font-medium">Payment Method</p>
                      <p className="text-2xl font-bold text-gray-800">{generatedBill.paymentMethod}</p>
                    </div>
                  </div>
                </div>

                {/* Actions */}
                <div className="flex gap-4">
                  <button
                    onClick={handlePrintBill}
                    className="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600"
                  >
                    Print Bill
                  </button>
                  <button
                    onClick={resetBillGenerator}
                    className="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600"
                  >
                    Generate New Bill
                  </button>
                </div>
              </div>
            )}
          </div>
        )}
      </div>

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
