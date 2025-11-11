import React, { useState, useEffect } from 'react';
import { suggestionsAPI } from '../services/api';

const BusinessSuggestions = () => {
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeFilter, setActiveFilter] = useState('all');
  const [priorityFilter, setPriorityFilter] = useState('all');

  useEffect(() => {
    fetchSuggestions();
  }, [activeFilter, priorityFilter]);

  const fetchSuggestions = async () => {
    setLoading(true);
    try {
      const params = {};
      if (activeFilter !== 'all') params.category = activeFilter;
      if (priorityFilter !== 'all') params.priority = priorityFilter;
      
      const response = await suggestionsAPI.getAllSuggestions(params);
      setSuggestions(response.data);
    } catch (error) {
      console.error('Error fetching suggestions:', error);
    } finally {
      setLoading(false);
    }
  };

  const getCategoryIcon = (category) => {
    const icons = {
      INVENTORY: (
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
        </svg>
      ),
      PRICING: (
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1" />
        </svg>
      ),
      MARKETING: (
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M11 5.882V19.24a1.76 1.76 0 01-3.417.592l-2.147-6.15M18 13a3 3 0 100-6M5.436 13.683A4.001 4.001 0 017 6h1.832c4.1 0 7.625-1.234 9.168-3v14c-1.543-1.766-5.067-3-9.168-3H7a3.988 3.988 0 01-1.564-.317z" />
        </svg>
      ),
      REGIONAL: (
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      ),
      PRODUCT: (
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
        </svg>
      ),
    };
    return icons[category] || icons.PRODUCT;
  };

  const getCategoryColor = (category) => {
    const colors = {
      INVENTORY: 'bg-blue-100 text-blue-800 border-blue-200',
      PRICING: 'bg-green-100 text-green-800 border-green-200',
      MARKETING: 'bg-purple-100 text-purple-800 border-purple-200',
      REGIONAL: 'bg-yellow-100 text-yellow-800 border-yellow-200',
      PRODUCT: 'bg-pink-100 text-pink-800 border-pink-200',
    };
    return colors[category] || colors.PRODUCT;
  };

  const getPriorityBadge = (priority) => {
    const styles = {
      HIGH: 'bg-red-100 text-red-800 border-red-300',
      MEDIUM: 'bg-orange-100 text-orange-800 border-orange-300',
      LOW: 'bg-gray-100 text-gray-800 border-gray-300',
    };
    return (
      <span className={`px-3 py-1 rounded-full text-xs font-semibold border ${styles[priority]}`}>
        {priority}
      </span>
    );
  };

  const getImpactBar = (score) => {
    const width = Math.min(score, 100);
    let colorClass = 'bg-gray-400';
    if (score >= 80) colorClass = 'bg-green-500';
    else if (score >= 60) colorClass = 'bg-blue-500';
    else if (score >= 40) colorClass = 'bg-yellow-500';
    
    return (
      <div className="w-full bg-gray-200 rounded-full h-2 mt-2">
        <div className={`${colorClass} h-2 rounded-full transition-all duration-300`} style={{ width: `${width}%` }}></div>
      </div>
    );
  };

  const categories = [
    { value: 'all', label: 'All Categories', count: suggestions.length },
    { value: 'INVENTORY', label: 'Inventory', count: suggestions.filter(s => s.category === 'INVENTORY').length },
    { value: 'PRICING', label: 'Pricing', count: suggestions.filter(s => s.category === 'PRICING').length },
    { value: 'MARKETING', label: 'Marketing', count: suggestions.filter(s => s.category === 'MARKETING').length },
    { value: 'REGIONAL', label: 'Regional', count: suggestions.filter(s => s.category === 'REGIONAL').length },
    { value: 'PRODUCT', label: 'Product', count: suggestions.filter(s => s.category === 'PRODUCT').length },
  ];

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Business Improvement Suggestions</h1>
          <p className="text-gray-600">AI-powered recommendations based on your sales data analysis</p>
        </div>

        {/* Summary Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
          <div className="bg-white rounded-lg shadow p-6">
            <div className="text-sm text-gray-600 mb-1">Total Suggestions</div>
            <div className="text-3xl font-bold text-gray-900">{suggestions.length}</div>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <div className="text-sm text-gray-600 mb-1">High Priority</div>
            <div className="text-3xl font-bold text-red-600">
              {suggestions.filter(s => s.priority === 'HIGH').length}
            </div>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <div className="text-sm text-gray-600 mb-1">Avg Impact Score</div>
            <div className="text-3xl font-bold text-blue-600">
              {suggestions.length > 0 
                ? Math.round(suggestions.reduce((sum, s) => sum + s.impactScore, 0) / suggestions.length)
                : 0}
            </div>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <div className="text-sm text-gray-600 mb-1">Categories</div>
            <div className="text-3xl font-bold text-purple-600">
              {new Set(suggestions.map(s => s.category)).size}
            </div>
          </div>
        </div>

        {/* Filters */}
        <div className="bg-white rounded-lg shadow p-4 mb-6">
          <div className="flex flex-wrap gap-4 items-center">
            <div className="flex-1 min-w-[200px]">
              <label className="text-sm font-medium text-gray-700 mb-2 block">Category</label>
              <div className="flex flex-wrap gap-2">
                {categories.map(cat => (
                  <button
                    key={cat.value}
                    onClick={() => setActiveFilter(cat.value)}
                    className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                      activeFilter === cat.value
                        ? 'bg-blue-600 text-white'
                        : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                    }`}
                  >
                    {cat.label} {cat.count > 0 && `(${cat.count})`}
                  </button>
                ))}
              </div>
            </div>
            <div>
              <label className="text-sm font-medium text-gray-700 mb-2 block">Priority</label>
              <select
                value={priorityFilter}
                onChange={(e) => setPriorityFilter(e.target.value)}
                className="border border-gray-300 rounded-lg px-4 py-2 text-sm focus:ring-2 focus:ring-blue-500"
              >
                <option value="all">All Priorities</option>
                <option value="HIGH">High Only</option>
                <option value="MEDIUM">Medium Only</option>
                <option value="LOW">Low Only</option>
              </select>
            </div>
          </div>
        </div>

        {/* Suggestions List */}
        {suggestions.length === 0 ? (
          <div className="bg-white rounded-lg shadow p-12 text-center">
            <div className="text-gray-400 mb-4">
              <svg className="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">No Suggestions Found</h3>
            <p className="text-gray-600">Try adjusting your filters or check back later for new recommendations.</p>
          </div>
        ) : (
          <div className="space-y-4">
            {suggestions.map((suggestion, index) => (
              <div key={index} className="bg-white rounded-lg shadow hover:shadow-lg transition-shadow">
                <div className="p-6">
                  <div className="flex items-start justify-between mb-4">
                    <div className="flex items-start space-x-4 flex-1">
                      <div className={`p-3 rounded-lg ${getCategoryColor(suggestion.category)}`}>
                        {getCategoryIcon(suggestion.category)}
                      </div>
                      <div className="flex-1">
                        <div className="flex items-center gap-3 mb-2">
                          <h3 className="text-lg font-semibold text-gray-900">{suggestion.title}</h3>
                          {getPriorityBadge(suggestion.priority)}
                        </div>
                        <p className="text-gray-600 mb-3">{suggestion.description}</p>
                        
                        {/* Actionable Item */}
                        <div className="bg-blue-50 border-l-4 border-blue-500 p-4 rounded mb-4">
                          <div className="flex items-start">
                            <svg className="w-5 h-5 text-blue-500 mr-2 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                            </svg>
                            <div>
                              <div className="text-sm font-semibold text-blue-900 mb-1">Recommended Action</div>
                              <div className="text-sm text-blue-800">{suggestion.actionable}</div>
                            </div>
                          </div>
                        </div>

                        {/* Metrics */}
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                          <div>
                            <div className="text-xs text-gray-500 mb-1">Impact Score</div>
                            <div className="text-lg font-bold text-gray-900">{Math.round(suggestion.impactScore)}/100</div>
                            {getImpactBar(suggestion.impactScore)}
                          </div>
                          <div>
                            <div className="text-xs text-gray-500 mb-1">{suggestion.metric} (Current)</div>
                            <div className="text-lg font-semibold text-gray-700">
                              {suggestion.currentValue != null 
                                ? (suggestion.metric.includes('Revenue') || suggestion.metric.includes('Value')
                                  ? `$${suggestion.currentValue.toFixed(2)}`
                                  : Math.round(suggestion.currentValue))
                                : 'N/A'}
                            </div>
                          </div>
                          <div>
                            <div className="text-xs text-gray-500 mb-1">{suggestion.metric} (Potential)</div>
                            <div className="text-lg font-semibold text-green-600">
                              {suggestion.potentialValue != null
                                ? (suggestion.metric.includes('Revenue') || suggestion.metric.includes('Value')
                                  ? `$${suggestion.potentialValue.toFixed(2)}`
                                  : Math.round(suggestion.potentialValue))
                                : 'N/A'}
                            </div>
                          </div>
                        </div>

                        {/* Action Buttons */}
                        <div className="flex gap-2">
                          <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium">
                            Implement
                          </button>
                          <button className="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors text-sm font-medium">
                            Learn More
                          </button>
                          <button className="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors text-sm font-medium">
                            Dismiss
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default BusinessSuggestions;
