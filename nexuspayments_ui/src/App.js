import React, { useState, useEffect, useRef } from 'react';
import { CreditCard, PlusCircle, FileText, ChevronDown, User, Package, DollarSign, AlertCircle, Download, Loader2 } from 'lucide-react';

// --- MOCK DATA ---
const mockCompany = {
    name: 'IntelliBill Inc.',
    address: '123 Tech Avenue, Silicon Valley, CA 94043',
    logo: 'https://placehold.co/150x50/6366f1/ffffff?text=IntelliBill'
};

const mockCustomers = [
    { id: 1, name: 'Innovate Inc.', email: 'contact@innovate.com', address: '456 Innovation Drive, Suite 100', joined: '2023-01-15', planId: 1, status: 'Active' },
    { id: 2, name: 'Data Solutions Ltd.', email: 'support@datasolutions.com', address: '789 Data Point, Building B', joined: '2023-02-20', planId: 2, status: 'Active' },
    { id: 3, name: 'CloudNet Services', email: 'admin@cloudnet.io', address: '101 Cloud Way, Floor 32', joined: '2023-03-10', planId: 3, status: 'Past Due' },
    { id: 4, name: 'QuantumLeap Co.', email: 'ceo@quantumleap.com', address: '234 Quantum Realm', joined: '2023-04-05', planId: 2, status: 'Canceled' },
];

const mockPlans = [
    { id: 1, name: 'Basic Tier', price: 49, frequency: '/month' },
    { id: 2, name: 'Pro Tier', price: 99, frequency: '/month' },
    { id: 3, name: 'Enterprise', price: 299, frequency: '/month' },
];

const mockInvoices = {
    1: [{ id: 101, date: '2025-08-01', amount: 49.00, status: 'Paid', dueDate: '2025-08-15' }, { id: 102, date: '2025-07-01', amount: 49.00, status: 'Paid', dueDate: '2025-07-15' }],
    2: [{ id: 201, date: '2025-08-15', amount: 99.00, status: 'Paid', dueDate: '2025-09-01' }, { id: 202, date: '2025-07-15', amount: 99.00, status: 'Paid', dueDate: '2025-08-01' }, { id: 203, date: '2025-06-15', amount: 99.00, status: 'Paid', dueDate: '2025-07-01' }],
    3: [{ id: 301, date: '2025-08-20', amount: 299.00, status: 'Due', dueDate: '2025-09-05' }, { id: 302, date: '2025-07-20', amount: 299.00, status: 'Paid', dueDate: '2025-08-05' }],
    4: []
};


// --- INVOICE PDF COMPONENT ---
// This component is rendered off-screen and used for PDF generation
const InvoicePDF = React.forwardRef(({ invoice, customer, plan, company }, ref) => {
    if (!invoice || !customer || !plan) return null;

    return (
        <div ref={ref} className="p-8 bg-white text-gray-800" style={{ width: '210mm', minHeight: '297mm', fontFamily: 'sans-serif' }}>
            {/* Header */}
            <div className="flex justify-between items-start mb-12">
                <img src={company.logo} alt="Company Logo" className="h-12" />
                <div className="text-right">
                    <h1 className="text-4xl font-bold text-gray-900">INVOICE</h1>
                    <p className="text-gray-500">Invoice #: {invoice.id}</p>
                    <p className="text-gray-500">Date Issued: {invoice.date}</p>
                </div>
            </div>

            {/* Billed To and From */}
            <div className="grid grid-cols-2 gap-8 mb-12">
                <div>
                    <p className="font-bold text-gray-600 mb-2">Billed From</p>
                    <p className="font-semibold text-gray-800">{company.name}</p>
                    <p className="text-gray-600">{company.address}</p>
                </div>
                <div className="text-right">
                    <p className="font-bold text-gray-600 mb-2">Billed To</p>
                    <p className="font-semibold text-gray-800">{customer.name}</p>
                    <p className="text-gray-600">{customer.address}</p>
                    <p className="text-gray-600">{customer.email}</p>
                </div>
            </div>

            {/* Invoice Items Table */}
            <table className="w-full mb-12">
                <thead className="bg-gray-100">
                    <tr>
                        <th className="text-left p-3 font-semibold text-gray-700">Description</th>
                        <th className="text-center p-3 font-semibold text-gray-700">Quantity</th>
                        <th className="text-right p-3 font-semibold text-gray-700">Unit Price</th>
                        <th className="text-right p-3 font-semibold text-gray-700">Total</th>
                    </tr>
                </thead>
                <tbody>
                    <tr className="border-b">
                        <td className="p-3">Subscription to {plan.name}</td>
                        <td className="text-center p-3">1</td>
                        <td className="text-right p-3">${plan.price.toFixed(2)}</td>
                        <td className="text-right p-3">${plan.price.toFixed(2)}</td>
                    </tr>
                </tbody>
            </table>

            {/* Totals */}
            <div className="flex justify-end mb-12">
                <div className="w-1/3">
                    <div className="flex justify-between mb-2">
                        <span className="text-gray-600">Subtotal</span>
                        <span className="font-semibold">${invoice.amount.toFixed(2)}</span>
                    </div>
                    <div className="flex justify-between mb-4">
                        <span className="text-gray-600">Tax (0%)</span>
                        <span className="font-semibold">$0.00</span>
                    </div>
                    <div className="flex justify-between border-t-2 pt-4">
                        <span className="font-bold text-xl">Total Amount Due</span>
                        <span className="font-bold text-xl">${invoice.amount.toFixed(2)}</span>
                    </div>
                </div>
            </div>

            {/* Footer Notes */}
            <div className="border-t pt-8 text-gray-500 text-sm">
                <p className="font-bold mb-2">Notes</p>
                <p>Payment is due by {invoice.dueDate}.</p>
                <p>Thank you for your business! For any questions concerning this invoice, please contact billing@intellibill.com.</p>
            </div>
        </div>
    );
});


// --- MAIN APPLICATION COMPONENT ---
export default function App() {
    const [customers, setCustomers] = useState([]);
    const [selectedCustomer, setSelectedCustomer] = useState(null);
    const [invoices, setInvoices] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [invoiceForPDF, setInvoiceForPDF] = useState(null);
    const [scriptsLoaded, setScriptsLoaded] = useState(false);
    const [isGeneratingPDF, setIsGeneratingPDF] = useState(false);
    const pdfRef = useRef();

    // Dynamically load external scripts for PDF generation
    useEffect(() => {
        const loadScript = (src, onLoad) => {
            if (document.querySelector(`script[src="${src}"]`)) {
                onLoad();
                return;
            }
            const script = document.createElement('script');
            script.src = src;
            script.onload = onLoad;
            script.onerror = () => console.error(`Failed to load script: ${src}`);
            document.body.appendChild(script);
        };

        let loadedCount = 0;
        const onScriptLoad = () => {
            loadedCount++;
            if (loadedCount === 2) {
                setScriptsLoaded(true);
            }
        };

        loadScript('https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.4.1/html2canvas.min.js', onScriptLoad);
        loadScript('https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js', onScriptLoad);

    }, []);

    useEffect(() => {
        setCustomers(mockCustomers);
        setSelectedCustomer(mockCustomers[0]);
    }, []);

    useEffect(() => {
        if (selectedCustomer) {
            setInvoices(mockInvoices[selectedCustomer.id] || []);
        }
    }, [selectedCustomer]);

    const handleGenerateInvoice = () => {
        if (!selectedCustomer) return;
        const plan = getPlan(selectedCustomer.planId);
        const newInvoice = {
            id: Math.floor(Math.random() * 1000) + 400,
            date: new Date().toISOString().split('T')[0],
            amount: plan?.price || 0,
            status: 'Due',
            dueDate: new Date(new Date().setDate(new Date().getDate() + 15)).toISOString().split('T')[0]
        };
        const updatedInvoices = [newInvoice, ...invoices];
        setInvoices(updatedInvoices);
        mockInvoices[selectedCustomer.id] = updatedInvoices;
        setShowModal(false);
    };

    const handleDownloadPDF = async (invoice) => {
        if (!scriptsLoaded || isGeneratingPDF) {
            console.error("PDF generation scripts not loaded or already in progress.");
            return;
        }
        setIsGeneratingPDF(true);
        setInvoiceForPDF(invoice);
        
        await new Promise(resolve => setTimeout(resolve, 100));

        const input = pdfRef.current;
        if (input && window.html2canvas && window.jspdf) {
            const { jsPDF } = window.jspdf;
            const html2canvas = window.html2canvas;

            html2canvas(input, { scale: 2 }).then((canvas) => {
                const imgData = canvas.toDataURL('image/png');
                const pdf = new jsPDF('p', 'mm', 'a4');
                const pdfWidth = pdf.internal.pageSize.getWidth();
                const canvasWidth = canvas.width;
                const canvasHeight = canvas.height;
                const ratio = canvasWidth / canvasHeight;
                const height = pdfWidth / ratio;
                
                pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, height);
                pdf.save(`invoice-${invoice.id}-${customer.name}.pdf`);
                
                setInvoiceForPDF(null);
                setIsGeneratingPDF(false);
            });
        } else {
            console.error("PDF generation libraries not found on window object.");
            setIsGeneratingPDF(false);
        }
    };
    
    const getPlan = (planId) => mockPlans.find(p => p.id === planId);
    const customer = selectedCustomer;
    const plan = customer ? getPlan(customer.planId) : null;

    const getStatusChip = (status) => {
        switch (status) {
            case 'Active': return 'bg-green-100 text-green-800';
            case 'Paid': return 'bg-green-100 text-green-800';
            case 'Past Due': return 'bg-yellow-100 text-yellow-800';
            case 'Due': return 'bg-blue-100 text-blue-800';
            case 'Canceled': return 'bg-red-100 text-red-800';
            default: return 'bg-gray-100 text-gray-800';
        }
    };

    return (
        <>
            <div style={{ position: 'absolute', left: '-9999px', top: 0 }}>
                <InvoicePDF ref={pdfRef} invoice={invoiceForPDF} customer={customer} plan={plan} company={mockCompany} />
            </div>

            <div className="bg-gray-50 font-sans min-h-screen">
                <div className="container mx-auto p-4 md:p-8">
                    <header className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8">
                        <div>
                            <h1 className="text-3xl font-bold text-gray-800">Billing Management</h1>
                            <p className="text-gray-500 mt-1">Manage customers, subscriptions, and invoices.</p>
                        </div>
                        <div className="mt-4 md:mt-0">
                            <button className="bg-indigo-600 text-white font-semibold py-2 px-4 rounded-lg shadow-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-opacity-75 flex items-center">
                                <PlusCircle className="w-5 h-5 mr-2" />
                                Add New Customer
                            </button>
                        </div>
                    </header>
                    <div className="flex flex-col lg:flex-row gap-8">
                        <div className="lg:w-1/3 bg-white p-6 rounded-xl shadow-lg h-fit">
                            <h2 className="text-xl font-semibold text-gray-700 mb-4">Customers</h2>
                            <div className="space-y-3">
                                {customers.map(cust => (
                                    <div key={cust.id} className={`p-4 rounded-lg cursor-pointer border-2 ${selectedCustomer?.id === cust.id ? 'bg-indigo-50 border-indigo-500' : 'bg-white hover:bg-gray-100 border-transparent'}`} onClick={() => setSelectedCustomer(cust)}>
                                        <div className="flex justify-between items-center">
                                            <div>
                                                <p className="font-semibold text-gray-800">{cust.name}</p>
                                                <p className="text-sm text-gray-500">{cust.email}</p>
                                            </div>
                                            <span className={`text-xs font-bold px-2 py-1 rounded-full ${getStatusChip(cust.status)}`}>{cust.status}</span>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                        <div className="lg:w-2/3">
                            {selectedCustomer ? (
                                <div className="bg-white p-6 rounded-xl shadow-lg">
                                    <div className="flex flex-col md:flex-row justify-between items-start border-b pb-4 mb-6">
                                        <div>
                                            <h2 className="text-2xl font-bold text-gray-800">{selectedCustomer.name}</h2>
                                            <p className="text-gray-500 flex items-center mt-1"><User className="w-4 h-4 mr-2" /> Joined on {selectedCustomer.joined}</p>
                                        </div>
                                        <div className="mt-4 md:mt-0 text-right"><div className={`text-lg font-bold px-3 py-1.5 rounded-full inline-block ${getStatusChip(selectedCustomer.status)}`}>{selectedCustomer.status}</div></div>
                                    </div>
                                    <div className="grid md:grid-cols-2 gap-6 mb-8">
                                        <div className="bg-gray-50 p-4 rounded-lg"><h3 className="font-semibold text-gray-600 mb-2 flex items-center"><Package className="w-5 h-5 mr-2 text-indigo-500" /> Current Plan</h3><p className="text-2xl font-bold text-gray-800">{getPlan(selectedCustomer.planId)?.name}</p></div>
                                        <div className="bg-gray-50 p-4 rounded-lg"><h3 className="font-semibold text-gray-600 mb-2 flex items-center"><DollarSign className="w-5 h-5 mr-2 text-green-500" /> Monthly Rate</h3><p className="text-2xl font-bold text-gray-800">${getPlan(selectedCustomer.planId)?.price}<span className="text-base font-normal text-gray-500">{getPlan(selectedCustomer.planId)?.frequency}</span></p></div>
                                    </div>
                                    <div>
                                        <div className="flex justify-between items-center mb-4"><h3 className="text-xl font-semibold text-gray-700">Invoice History</h3><button onClick={() => setShowModal(true)} className="bg-green-500 text-white font-semibold py-2 px-4 rounded-lg shadow-md hover:bg-green-600 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-opacity-75 flex items-center"><PlusCircle className="w-5 h-5 mr-2" />Generate Invoice</button></div>
                                        <div className="flow-root">
                                            <ul role="list" className="-mb-8">
                                                {invoices.length > 0 ? invoices.map((invoice, invoiceIdx) => (
                                                    <li key={invoice.id}>
                                                        <div className="relative pb-8">
                                                            {invoiceIdx !== invoices.length - 1 ? (<span className="absolute top-4 left-4 -ml-px h-full w-0.5 bg-gray-200" aria-hidden="true" />) : null}
                                                            <div className="relative flex space-x-3 items-center">
                                                                <div><span className={`h-8 w-8 rounded-full flex items-center justify-center ring-8 ring-white ${getStatusChip(invoice.status)}`}><FileText className="h-5 w-5 text-white" /></span></div>
                                                                <div className="min-w-0 flex-1 flex justify-between items-center space-x-4">
                                                                    <div>
                                                                        <p className="text-sm text-gray-500">Invoice #{invoice.id} - {invoice.date}</p>
                                                                        <p className="font-semibold text-gray-800">${invoice.amount.toFixed(2)}</p>
                                                                    </div>
                                                                    <div className="text-right text-sm whitespace-nowrap flex items-center gap-4">
                                                                        <span className={`px-2.5 py-1 rounded-full font-medium text-xs ${getStatusChip(invoice.status)}`}>{invoice.status}</span>
                                                                        <button onClick={() => handleDownloadPDF(invoice)} disabled={!scriptsLoaded || isGeneratingPDF} className="p-2 text-gray-500 hover:text-indigo-600 hover:bg-indigo-50 rounded-full focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed" title={scriptsLoaded ? "Download PDF" : "PDF library loading..."}>
                                                                            {isGeneratingPDF && invoiceForPDF?.id === invoice.id ? <Loader2 className="w-5 h-5 animate-spin" /> : <Download className="w-5 h-5" />}
                                                                        </button>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </li>
                                                )) : (
                                                    <div className="text-center py-8 px-4 border-2 border-dashed rounded-lg"><FileText className="mx-auto h-12 w-12 text-gray-400" /><h3 className="mt-2 text-sm font-medium text-gray-900">No invoices</h3><p className="mt-1 text-sm text-gray-500">There is no billing history for this customer yet.</p></div>
                                                )}
                                            </ul>
                                        </div>
                                    </div>
                                </div>
                            ) : (
                                <div className="bg-white p-8 rounded-xl shadow-lg text-center"><User className="mx-auto h-12 w-12 text-gray-400" /><h3 className="mt-2 text-lg font-medium text-gray-900">Select a Customer</h3><p className="mt-1 text-sm text-gray-500">Choose a customer from the list to view their billing details.</p></div>
                            )}
                        </div>
                    </div>
                </div>

                {showModal && (
                    <div className="fixed inset-0 bg-gray-600 bg-opacity-75 overflow-y-auto h-full w-full z-50 flex items-center justify-center">
                        <div className="relative mx-auto p-8 border w-full max-w-md shadow-lg rounded-xl bg-white">
                            <div className="text-center">
                                <AlertCircle className="mx-auto h-12 w-12 text-blue-500" />
                                <h3 className="text-lg leading-6 font-medium text-gray-900 mt-4">Confirm Invoice Generation</h3>
                                <div className="mt-2 px-7 py-3"><p className="text-sm text-gray-500">This will generate a new invoice for <span className="font-bold">{selectedCustomer?.name}</span> for the amount of <span className="font-bold">${getPlan(selectedCustomer?.planId)?.price.toFixed(2)}</span>.</p></div>
                                <div className="items-center px-4 py-3 space-x-4">
                                    <button onClick={() => setShowModal(false)} className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 font-semibold">Cancel</button>
                                    <button onClick={handleGenerateInvoice} className="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 font-semibold shadow-md">Confirm & Generate</button>
                                </div>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </>
    );
}


