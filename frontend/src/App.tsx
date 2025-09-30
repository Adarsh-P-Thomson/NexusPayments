import { useState } from 'react'
import Navigation from './components/Navigation'
import Dashboard from './components/Dashboard'
import Subscriptions from './components/Subscriptions'
import Invoices from './components/Invoices'

function App() {
  const [activeView, setActiveView] = useState('dashboard')

  const renderActiveView = () => {
    switch (activeView) {
      case 'dashboard':
        return <Dashboard />
      case 'subscriptions':
        return <Subscriptions />
      case 'invoices':
        return <Invoices />
      default:
        return <Dashboard />
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navigation activeView={activeView} onViewChange={setActiveView} />
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {renderActiveView()}
      </main>
    </div>
  )
}

export default App
