import { useState, useEffect } from 'react'

const PaymentConfirmation = ({ paymentResponse, onClose, onNewOrder }) => {
  const [displayedResponse, setDisplayedResponse] = useState(paymentResponse)
  const isSuccess = paymentResponse?.status === 'PAID'

  useEffect(() => {
    setDisplayedResponse(paymentResponse)
  }, [paymentResponse])

  const handleCopyToClipboard = (text, label) => {
    if (!text) return
    navigator.clipboard.writeText(text)
    alert(`${label} copiado para a área de transferência!`)
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content payment-confirmation-modal" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>✕</button>

        <div className={`confirmation-header ${isSuccess ? 'success' : 'pending'}`}>
          <div className="confirmation-icon">
            {isSuccess ? '✓' : paymentResponse?.status === 'FAILED' ? '✗' : '⏳'}
          </div>
          <h2>
            {isSuccess 
              ? 'Pagamento realizado com sucesso!' 
              : paymentResponse?.status === 'FAILED'
              ? 'Falha no pagamento'
              : 'Pagamento em processamento'}
          </h2>
          <p>{paymentResponse?.message}</p>
        </div>

        <div className="confirmation-details">
          <div className="detail-row">
            <span className="detail-label">ID do Pedido:</span>
            <span className="detail-value">{displayedResponse?.orderId}</span>
          </div>

          <div className="detail-row">
            <span className="detail-label">Valor Total:</span>
            <span className="detail-value price">R$ {displayedResponse?.totalValue?.toFixed(2)}</span>
          </div>

          <div className="detail-row">
            <span className="detail-label">Método de Pagamento:</span>
            <span className="detail-value">{displayedResponse?.paymentMethod}</span>
          </div>

          {displayedResponse?.transactionId && (
            <div className="detail-row">
              <span className="detail-label">ID da Transação:</span>
              <span className="detail-value">{displayedResponse.transactionId}</span>
            </div>
          )}

          {displayedResponse?.paymentMethod === 'PIX' && displayedResponse?.pixCode && (
            <div className="payment-details-section">
              <h3>📱 Dados do PIX</h3>
              <div className="detail-box">
                <p className="detail-label">Chave PIX registrada:</p>
                <p className="detail-value">{displayedResponse.pixKey}</p>
              </div>
              <div className="detail-box">
                <p className="detail-label">Código PIX:</p>
                <div className="copy-container">
                  <code className="copy-text">{displayedResponse.pixCode}</code>
                  <button 
                    className="copy-btn" 
                    onClick={() => handleCopyToClipboard(displayedResponse.pixCode, 'Código PIX')}
                  >
                    Copiar
                  </button>
                </div>
              </div>
            </div>
          )}

          {displayedResponse?.paymentMethod === 'BOLETO' && displayedResponse?.barcode && (
            <div className="payment-details-section">
              <h3>📄 Dados do Boleto</h3>
              <div className="detail-box">
                <p className="detail-label">Código do Boleto:</p>
                <div className="copy-container">
                  <code className="copy-text">{displayedResponse.boletoCode}</code>
                  <button 
                    className="copy-btn" 
                    onClick={() => handleCopyToClipboard(displayedResponse.boletoCode, 'Código do Boleto')}
                  >
                    Copiar
                  </button>
                </div>
              </div>
              <div className="detail-box">
                <p className="detail-label">Código de barras:</p>
                <div className="copy-container">
                  <code className="copy-text">{displayedResponse.barcode}</code>
                  <button 
                    className="copy-btn" 
                    onClick={() => handleCopyToClipboard(displayedResponse.barcode, 'Código de barras')}
                  >
                    Copiar
                  </button>
                </div>
              </div>
            </div>
          )}

          {(displayedResponse?.paymentMethod === 'DEBIT_CARD' || displayedResponse?.paymentMethod === 'CREDIT_CARD') && displayedResponse?.cardLastDigits && (
            <div className="payment-details-section">
              <h3>💳 Dados do Cartão</h3>
              <div className="detail-box">
                <p className="detail-label">Cartão terminado em:</p>
                <p className="detail-value">**** **** **** {displayedResponse.cardLastDigits}</p>
              </div>
              <div className="detail-box">
                <p className="detail-label">Titular:</p>
                <p className="detail-value">{displayedResponse.cardHolderName}</p>
              </div>
            </div>
          )}
        </div>

        <div className="confirmation-actions">
          {isSuccess ? (
            <>
              <button className="btn-primary" onClick={onNewOrder}>
                Fazer novo pedido
              </button>
              <button className="btn-secondary" onClick={onClose}>
                Fechar
              </button>
            </>
          ) : paymentResponse?.status === 'FAILED' ? (
            <button className="btn-primary" onClick={onClose}>
              Tentar novamente
            </button>
          ) : (
            <button className="btn-secondary" onClick={onClose}>
              Fechar
            </button>
          )}
        </div>
      </div>
    </div>
  )
}

export default PaymentConfirmation
