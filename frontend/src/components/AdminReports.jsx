import React, { useState, useEffect } from 'react'
import '../styles/admin-reports.css'

const AdminReports = ({ setCurrentPage, handleLogout, authToken }) => {
  const [reports, setReports] = useState([])
  const [summary, setSummary] = useState(null)
  const [loading, setLoading] = useState(false)
  const [filterType, setFilterType] = useState('all')
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [statusFilter, setStatusFilter] = useState('')
  const [paymentMethodFilter, setPaymentMethodFilter] = useState('')
  const [exportFormat, setExportFormat] = useState('pdf')

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

  useEffect(() => {
    loadReports()
    loadSummary()
  }, [])

  const loadReports = async (filters = {}) => {
    setLoading(true)
    try {
      let url = `${API_BASE_URL}/reports/orders`

      if (filters.startDate && filters.endDate) {
        url = `${API_BASE_URL}/reports/orders/date-range?startDate=${filters.startDate}&endDate=${filters.endDate}`
      } else if (filters.status) {
        url = `${API_BASE_URL}/reports/orders/status?status=${filters.status}`
      } else if (filters.paymentMethod) {
        url = `${API_BASE_URL}/reports/orders/payment-method?paymentMethod=${filters.paymentMethod}`
      }

      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${authToken}`,
          'Content-Type': 'application/json',
        },
      })

      if (response.ok) {
        const data = await response.json()
        setReports(data)
      } else {
        console.error('Erro ao carregar relatórios')
      }
    } catch (error) {
      console.error('Erro:', error)
    } finally {
      setLoading(false)
    }
  }

  const loadSummary = async (filters = {}) => {
    try {
      let url = `${API_BASE_URL}/reports/summary`

      if (filters.startDate && filters.endDate) {
        url = `${API_BASE_URL}/reports/summary/date-range?startDate=${filters.startDate}&endDate=${filters.endDate}`
      }

      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${authToken}`,
          'Content-Type': 'application/json',
        },
      })

      if (response.ok) {
        const data = await response.json()
        setSummary(data)
      }
    } catch (error) {
      console.error('Erro ao carregar resumo:', error)
    }
  }

  const handleApplyFilters = () => {
    const filters = {}

    if (filterType === 'date' && startDate && endDate) {
      filters.startDate = startDate
      filters.endDate = endDate
    } else if (filterType === 'status' && statusFilter) {
      filters.status = statusFilter
    } else if (filterType === 'payment' && paymentMethodFilter) {
      filters.paymentMethod = paymentMethodFilter
    }

    loadReports(filters)
    loadSummary(filters)
  }

  const handleClearFilters = () => {
    setFilterType('all')
    setStartDate('')
    setEndDate('')
    setStatusFilter('')
    setPaymentMethodFilter('')
    loadReports()
    loadSummary()
  }

  const handleExport = async () => {
    try {
      let url = `${API_BASE_URL}/reports/export/${exportFormat}`

      if (startDate && endDate) {
        url += `?startDate=${startDate}&endDate=${endDate}`
      }

      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${authToken}`,
        },
      })

      if (response.ok) {
        const blob = await response.blob()
        const urlBlob = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = urlBlob
        link.download = `relatorio_vendas.${exportFormat}`
        link.click()
        window.URL.revokeObjectURL(urlBlob)
      } else {
        alert('Erro ao exportar relatório')
      }
    } catch (error) {
      console.error('Erro na exportação:', error)
      alert('Erro ao exportar relatório')
    }
  }

  const getStatusColor = (status) => {
    switch (status) {
      case 'PAID':
        return '#27ae60'
      case 'PENDING':
        return '#f39c12'
      case 'FAILED':
        return '#e74c3c'
      case 'CANCELLED':
        return '#95a5a6'
      default:
        return '#34495e'
    }
  }

  const getStatusLabel = (status) => {
    const labels = {
      PAID: 'Pago',
      PENDING: 'Pendente',
      FAILED: 'Falhou',
      CANCELLED: 'Cancelado',
      PROCESSING: 'Processando',
    }
    return labels[status] || status
  }

  const getPaymentMethodLabel = (method) => {
    const labels = {
      PIX: 'PIX',
      BOLETO: 'Boleto',
      DEBIT_CARD: 'Débito',
      CREDIT_CARD: 'Crédito',
    }
    return labels[method] || method
  }

  return (
    <div className="admin-layout">
      <aside className="sidebar">
        <div className="sidebar-logo">📊 Relatórios</div>
        <nav className="sidebar-menu">
          <button className="menu-item" onClick={() => setCurrentPage('admin-dashboard')}>Gestão de Peças</button>
          <button className="menu-item" onClick={() => setCurrentPage('admin-employees')}>Funcionários</button>
          <button className="menu-item" onClick={() => setCurrentPage('admin-clients')}>Clientes</button>
          <button className="menu-item active">Relatórios</button>
          <button className="menu-item logout" onClick={handleLogout}>🚪 Sair</button>
        </nav>
      </aside>

      <main className="admin-main">
        <div className="admin-top">
          <h1>Relatórios de Vendas</h1>
          {summary && (
            <div className="stats">
              <div className="stat-card">
                <p className="stat-value">R$ {(summary.totalRevenue || 0).toFixed(2)}</p>
                <p className="stat-label">Receita Total</p>
                <span className="stat-percent">
                  {summary.percentageIncrease >= 0 ? '↑' : '↓'} {Math.abs(summary.percentageIncrease).toFixed(1)}%
                </span>
              </div>
              <div className="stat-card">
                <p className="stat-value">{summary.totalOrders}</p>
                <p className="stat-label">Total de Pedidos</p>
              </div>
              <div className="stat-card">
                <p className="stat-value">{summary.paidOrders}</p>
                <p className="stat-label">Pedidos Pagos</p>
              </div>
              <div className="stat-card">
                <p className="stat-value">R$ {(summary.averageOrderValue || 0).toFixed(2)}</p>
                <p className="stat-label">Ticket Médio</p>
              </div>
            </div>
          )}
        </div>

        <div className="admin-content">
          <section className="admin-section">
            <h2>Filtros e Exportação</h2>

            <div className="filters-container">
              <div className="filter-group">
                <label>Tipo de Filtro:</label>
                <select value={filterType} onChange={(e) => setFilterType(e.target.value)}>
                  <option value="all">Todos os pedidos</option>
                  <option value="date">Por Data</option>
                  <option value="status">Por Status</option>
                  <option value="payment">Por Método de Pagamento</option>
                </select>
              </div>

              {filterType === 'date' && (
                <>
                  <div className="filter-group">
                    <label>Data Inicial:</label>
                    <input
                      type="date"
                      value={startDate}
                      onChange={(e) => setStartDate(e.target.value)}
                    />
                  </div>
                  <div className="filter-group">
                    <label>Data Final:</label>
                    <input
                      type="date"
                      value={endDate}
                      onChange={(e) => setEndDate(e.target.value)}
                    />
                  </div>
                </>
              )}

              {filterType === 'status' && (
                <div className="filter-group">
                  <label>Status:</label>
                  <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
                    <option value="">Selecione um status</option>
                    <option value="PAID">Pago</option>
                    <option value="PENDING">Pendente</option>
                    <option value="FAILED">Falhou</option>
                    <option value="CANCELLED">Cancelado</option>
                    <option value="PROCESSING">Processando</option>
                  </select>
                </div>
              )}

              {filterType === 'payment' && (
                <div className="filter-group">
                  <label>Método de Pagamento:</label>
                  <select value={paymentMethodFilter} onChange={(e) => setPaymentMethodFilter(e.target.value)}>
                    <option value="">Selecione um método</option>
                    <option value="PIX">PIX</option>
                    <option value="BOLETO">Boleto</option>
                    <option value="DEBIT_CARD">Débito</option>
                    <option value="CREDIT_CARD">Crédito</option>
                  </select>
                </div>
              )}

              <div className="filter-buttons">
                <button className="btn-apply" onClick={handleApplyFilters}>Aplicar Filtros</button>
                <button className="btn-clear" onClick={handleClearFilters}>Limpar</button>
              </div>
            </div>

            <div className="export-container">
              <h3>Exportar Relatório</h3>
              <div className="export-controls">
                <select value={exportFormat} onChange={(e) => setExportFormat(e.target.value)}>
                  <option value="pdf">PDF</option>
                  <option value="csv">CSV</option>
                </select>
                <button className="btn-export" onClick={handleExport}>
                  📥 Exportar {exportFormat.toUpperCase()}
                </button>
              </div>
            </div>
          </section>

          <section className="admin-section">
            <h2>Pedidos ({reports.length})</h2>

            {loading ? (
              <p className="loading">Carregando relatórios...</p>
            ) : reports.length === 0 ? (
              <p className="no-data">Nenhum pedido encontrado com os filtros selecionados.</p>
            ) : (
              <div className="reports-table-container">
                <table className="reports-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Cliente</th>
                      <th>Email</th>
                      <th>Valor</th>
                      <th>Status</th>
                      <th>Método</th>
                      <th>Data</th>
                      <th>Detalhes</th>
                    </tr>
                  </thead>
                  <tbody>
                    {reports.map((report) => (
                      <tr key={report.orderId}>
                        <td>#{report.orderId}</td>
                        <td>{report.clientName}</td>
                        <td>{report.clientEmail}</td>
                        <td>R$ {report.totalValue.toFixed(2)}</td>
                        <td>
                          <span
                            className="status-badge"
                            style={{ backgroundColor: getStatusColor(report.orderStatus) }}
                          >
                            {getStatusLabel(report.orderStatus)}
                          </span>
                        </td>
                        <td>{getPaymentMethodLabel(report.paymentMethod)}</td>
                        <td>{new Date(report.createdAt).toLocaleDateString('pt-BR', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })}</td>
                        <td>
                          <details className="details-cell">
                            <summary>Ver</summary>
                            <div className="details-content">
                              <p><strong>ID Transação:</strong> {report.transactionId || '-'}</p>
                              {report.pixCode && <p><strong>Código PIX:</strong> {report.pixCode}</p>}
                              {report.boletoCode && <p><strong>Código Boleto:</strong> {report.boletoCode}</p>}
                              {report.cardLastDigits && <p><strong>Cartão:</strong> ****{report.cardLastDigits}</p>}
                            </div>
                          </details>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        </div>
      </main>
    </div>
  )
}

export default AdminReports
