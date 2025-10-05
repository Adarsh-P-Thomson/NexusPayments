import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Health Check API
export const healthAPI = {
  checkHealth: () => api.get('/health'),
  checkPostgres: () => api.get('/health/postgres'),
  checkMongoDB: () => api.get('/health/mongodb'),
};

// Data Initialization API
export const initAPI = {
  initializeData: () => api.post('/init/data'),
  getInitStatus: () => api.get('/init/status'),
};

// User API
export const userAPI = {
  getAllUsers: () => api.get('/users'),
  getUserById: (id) => api.get(`/users/${id}`),
  createUser: (userData) => api.post('/users', userData),
};

// Card API
export const cardAPI = {
  getUserCards: (userId) => api.get(`/cards/user/${userId}`),
  getCardById: (id) => api.get(`/cards/${id}`),
  createCard: (cardData) => api.post('/cards', cardData),
  updateCard: (id, cardData) => api.put(`/cards/${id}`, cardData),
  deleteCard: (id) => api.delete(`/cards/${id}`),
};

// Subscription Plan API
export const subscriptionPlanAPI = {
  getAllPlans: () => api.get('/subscription-plans'),
  getActivePlans: () => api.get('/subscription-plans/active'),
  getPlanById: (id) => api.get(`/subscription-plans/${id}`),
  createPlan: (planData) => api.post('/subscription-plans', planData),
  updatePlan: (id, planData) => api.put(`/subscription-plans/${id}`, planData),
  deletePlan: (id) => api.delete(`/subscription-plans/${id}`),
};

// Subscription API
export const subscriptionAPI = {
  createSubscription: (subscriptionData) => api.post('/subscriptions', subscriptionData),
  getUserSubscriptions: (userId) => api.get(`/subscriptions/user/${userId}`),
  checkActiveSubscription: (userId) => api.get(`/subscriptions/user/${userId}/has-active`),
  cancelSubscription: (subscriptionId) => api.delete(`/subscriptions/${subscriptionId}`),
};

// Bill API
export const billAPI = {
  getUserBills: (userId) => api.get(`/bills/user/${userId}`),
  getPendingBills: () => api.get('/bills/pending'),
};

// Payment API
export const paymentAPI = {
  initiatePayment: (paymentData) => api.post('/payments/initiate', paymentData),
  retryPayment: (transactionId) => api.post(`/payments/retry/${transactionId}`),
};

// Transaction API
export const transactionAPI = {
  getUserTransactions: (userId) => api.get(`/transactions/user/${userId}`),
  getBillTransactions: (billId) => api.get(`/transactions/bill/${billId}`),
  getTransactionsByStatus: (status) => api.get(`/transactions/status/${status}`),
};

export default api;
