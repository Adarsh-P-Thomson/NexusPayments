import React, { useState } from 'react';
import { FileText, Download, Eye, Calendar, DollarSign } from 'lucide-react';
import mockData from '../data/mockData.json';

const Invoices: React.FC = () => {
  const { invoices, tenants } = mockData;
  const [selectedInvoice, setSelectedInvoice] = useState<string | null>(null);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'paid':
        return 'bg-green-100 text-green-800';
      case 'pending':
        return 'bg-yellow-100 text-yellow-800';
      case 'overdue':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getTenantName = (tenantId: string) => {
    const tenant = tenants.find(t => t.id === tenantId);
    return tenant ? tenant.name : 'Unknown Tenant';
  };

  const selectedInvoiceData = selectedInvoice ? invoices.find(inv => inv.id === selectedInvoice) : null;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-900">Invoices</h2>
        <button className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium">
          Create Invoice
        </button>
      </div>

      {!selectedInvoice ? (
        <>
          {/* Invoice Summary Cards */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center">
                <div className="p-2 bg-green-100 rounded-lg">
                  <DollarSign className="h-6 w-6 text-green-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Total Revenue</p>
                  <p className="text-2xl font-semibold text-gray-900">
                    ${invoices.reduce((sum, inv) => sum + inv.amount, 0).toFixed(2)}
                  </p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center">
                <div className="p-2 bg-blue-100 rounded-lg">
                  <FileText className="h-6 w-6 text-blue-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Total Invoices</p>
                  <p className="text-2xl font-semibold text-gray-900">{invoices.length}</p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center">
                <div className="p-2 bg-yellow-100 rounded-lg">
                  <Calendar className="h-6 w-6 text-yellow-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Pending</p>
                  <p className="text-2xl font-semibold text-gray-900">
                    {invoices.filter(inv => inv.status === 'pending').length}
                  </p>
                </div>
              </div>
            </div>
          </div>

          {/* Invoices Table */}
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="px-6 py-4 border-b border-gray-200">
              <h3 className="text-lg font-medium text-gray-900">All Invoices</h3>
            </div>
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Invoice
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Tenant
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Amount
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Status
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Issue Date
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Due Date
                    </th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {invoices.map((invoice) => (
                    <tr key={invoice.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-medium text-gray-900">{invoice.number}</div>
                        <div className="text-sm text-gray-500">ID: {invoice.id}</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {getTenantName(invoice.tenantId)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        ${invoice.amount.toFixed(2)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(invoice.status)}`}>
                          {invoice.status}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {invoice.issueDate}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {invoice.dueDate}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-2">
                        <button
                          onClick={() => setSelectedInvoice(invoice.id)}
                          className="text-blue-600 hover:text-blue-900"
                        >
                          <Eye className="h-4 w-4" />
                        </button>
                        <button className="text-gray-600 hover:text-gray-900">
                          <Download className="h-4 w-4" />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </>
      ) : (
        /* Invoice Detail View */
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between mb-6">
            <button
              onClick={() => setSelectedInvoice(null)}
              className="text-blue-600 hover:text-blue-800 font-medium"
            >
              ← Back to Invoices
            </button>
            <div className="flex space-x-2">
              <button className="bg-gray-100 hover:bg-gray-200 text-gray-800 px-4 py-2 rounded-lg font-medium">
                Download PDF
              </button>
              <button className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium">
                Send Email
              </button>
            </div>
          </div>

          {selectedInvoiceData && (
            <div className="space-y-6">
              {/* Invoice Header */}
              <div className="border-b pb-6">
                <div className="flex justify-between items-start">
                  <div>
                    <h1 className="text-3xl font-bold text-gray-900">{selectedInvoiceData.number}</h1>
                    <p className="text-gray-600 mt-1">Invoice ID: {selectedInvoiceData.id}</p>
                  </div>
                  <div className="text-right">
                    <span className={`inline-flex px-3 py-1 text-sm font-semibold rounded-full ${getStatusColor(selectedInvoiceData.status)}`}>
                      {selectedInvoiceData.status}
                    </span>
                    <p className="text-2xl font-bold text-gray-900 mt-2">${selectedInvoiceData.amount.toFixed(2)}</p>
                  </div>
                </div>
              </div>

              {/* Invoice Details */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <h3 className="text-lg font-medium text-gray-900 mb-3">Bill To</h3>
                  <p className="text-gray-900 font-medium">{getTenantName(selectedInvoiceData.tenantId)}</p>
                  <p className="text-gray-600">Customer ID: {selectedInvoiceData.tenantId}</p>
                </div>
                <div>
                  <h3 className="text-lg font-medium text-gray-900 mb-3">Invoice Details</h3>
                  <div className="space-y-1">
                    <p className="text-gray-600">Issue Date: <span className="text-gray-900">{selectedInvoiceData.issueDate}</span></p>
                    <p className="text-gray-600">Due Date: <span className="text-gray-900">{selectedInvoiceData.dueDate}</span></p>
                    {selectedInvoiceData.paidDate && (
                      <p className="text-gray-600">Paid Date: <span className="text-gray-900">{selectedInvoiceData.paidDate}</span></p>
                    )}
                  </div>
                </div>
              </div>

              {/* Invoice Items */}
              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-3">Invoice Items</h3>
                <div className="overflow-x-auto">
                  <table className="min-w-full">
                    <thead>
                      <tr className="border-b">
                        <th className="text-left py-2 text-gray-600">Description</th>
                        <th className="text-right py-2 text-gray-600">Quantity</th>
                        <th className="text-right py-2 text-gray-600">Unit Price</th>
                        <th className="text-right py-2 text-gray-600">Total</th>
                      </tr>
                    </thead>
                    <tbody>
                      {selectedInvoiceData.items.map((item, index) => (
                        <tr key={index} className="border-b">
                          <td className="py-3 text-gray-900">{item.description}</td>
                          <td className="py-3 text-right text-gray-900">{item.quantity}</td>
                          <td className="py-3 text-right text-gray-900">${item.unitPrice.toFixed(2)}</td>
                          <td className="py-3 text-right text-gray-900 font-medium">${item.total.toFixed(2)}</td>
                        </tr>
                      ))}
                      <tr className="border-t-2 border-gray-300">
                        <td colSpan={3} className="py-3 text-right font-medium text-gray-900">Total Amount:</td>
                        <td className="py-3 text-right font-bold text-gray-900 text-lg">${selectedInvoiceData.amount.toFixed(2)}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default Invoices;