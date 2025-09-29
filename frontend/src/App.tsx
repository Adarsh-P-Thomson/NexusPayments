import { useState } from 'react'
import { CreditCard, Users, BarChart3 } from 'lucide-react'

function App() {
  const [isConnected, setIsConnected] = useState(false)

  const checkBackendConnection = async () => {
    try {
      const response = await fetch('/api/v1/health')
      const data = await response.json()
      setIsConnected(data.status === 'UP')
    } catch (error) {
      setIsConnected(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="pt-20 pb-16">
          {/* Header */}
          <div className="text-center">
            <h1 className="text-4xl font-bold text-gray-900 sm:text-5xl md:text-6xl">
              NexusPay
            </h1>
            <p className="mt-3 max-w-md mx-auto text-base text-gray-500 sm:text-lg md:mt-5 md:text-xl md:max-w-3xl">
              Multi-tenant SaaS billing platform with real-time automation
            </p>
          </div>

          {/* Features Grid */}
          <div className="mt-16">
            <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-3">
              <div className="pt-6">
                <div className="flow-root bg-white rounded-lg px-6 pb-8">
                  <div className="-mt-6">
                    <div>
                      <span className="inline-flex items-center justify-center p-3 bg-blue-500 rounded-md shadow-lg">
                        <CreditCard className="h-6 w-6 text-white" />
                      </span>
                    </div>
                    <h3 className="mt-8 text-lg font-medium text-gray-900 tracking-tight">
                      Smart Billing Engine
                    </h3>
                    <p className="mt-5 text-base text-gray-500">
                      Automated subscription and usage-based billing with real-time calculation and fraud protection.
                    </p>
                  </div>
                </div>
              </div>

              <div className="pt-6">
                <div className="flow-root bg-white rounded-lg px-6 pb-8">
                  <div className="-mt-6">
                    <div>
                      <span className="inline-flex items-center justify-center p-3 bg-green-500 rounded-md shadow-lg">
                        <Users className="h-6 w-6 text-white" />
                      </span>
                    </div>
                    <h3 className="mt-8 text-lg font-medium text-gray-900 tracking-tight">
                      Multi-Tenant SaaS
                    </h3>
                    <p className="mt-5 text-base text-gray-500">
                      Complete tenant isolation with role-based access control and scalable architecture.
                    </p>
                  </div>
                </div>
              </div>

              <div className="pt-6">
                <div className="flow-root bg-white rounded-lg px-6 pb-8">
                  <div className="-mt-6">
                    <div>
                      <span className="inline-flex items-center justify-center p-3 bg-purple-500 rounded-md shadow-lg">
                        <BarChart3 className="h-6 w-6 text-white" />
                      </span>
                    </div>
                    <h3 className="mt-8 text-lg font-medium text-gray-900 tracking-tight">
                      Adaptive Intelligence
                    </h3>
                    <p className="mt-5 text-base text-gray-500">
                      ML-powered insights for revenue optimization and anomaly detection.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Backend Connection Status */}
          <div className="mt-16 text-center">
            <button
              onClick={checkBackendConnection}
              className="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              Check Backend Connection
            </button>
            {isConnected !== null && (
              <div className={`mt-4 p-4 rounded-md ${isConnected ? 'bg-green-50 text-green-800' : 'bg-red-50 text-red-800'}`}>
                Backend Status: {isConnected ? 'Connected ✅' : 'Disconnected ❌'}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

export default App
