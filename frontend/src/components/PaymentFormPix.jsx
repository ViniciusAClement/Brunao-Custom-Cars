import { useState, useEffect } from 'react'
import { QRCodeSVG } from 'qrcode.react'

const PaymentFormPix = ({ total, onSubmit, isLoading }) => {
  const [pixCode, setPixCode] = useState('')
  const [copied, setCopied] = useState(false)
  const [confirmed, setConfirmed] = useState(false)

  // Gerar código PIX aleatório quando o componente montar
  useEffect(() => {
    const generatePixCode = () => {
      const timestamp = Date.now()
      const random = Math.floor(Math.random() * 1000000)
      return `${timestamp}-${random}`
    }
    setPixCode(generatePixCode())
  }, [])

  const handleCopyCode = async () => {
    await navigator.clipboard.writeText(pixCode)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    
    if (!confirmed) {
      alert('Por favor, confirme que copiou o código PIX')
      return
    }

    onSubmit({
      paymentMethod: 'PIX',
      pixKey: pixCode,
    })
  }

  return (
    <form onSubmit={handleSubmit} className="payment-form pix-form">
      <div className="form-section">
        <h3>📱 Pagamento via PIX</h3>
        <p className="form-info">Escaneie o QR code ou copie o código para fazer a transferência</p>
      </div>

      <div className="pix-container">
        <div className="qr-code-section">
          <div className="qr-code-wrapper">
          <QRCodeSVG
            value={pixCode}
            size={200}
            level="H"
            includeMargin={true}
            fgColor="#000000"
            bgColor="#ffffff"
          />
          </div>
          <p className="qr-label">Escaneie o código acima</p>
        </div>

        <div className="pix-code-section">
          <p className="code-label">Ou copie o código:</p>
          <div className="code-display">
            <span className="code-text">{pixCode}</span>
            <button 
              type="button"
              className="copy-btn"
              onClick={handleCopyCode}
              disabled={isLoading}
            >
              {copied ? '✓ Copiado!' : '📋 Copiar'}
            </button>
          </div>
        </div>
      </div>

      <div className="form-group confirmation-group">
        <label className="checkbox-label">
          <input
            type="checkbox"
            checked={confirmed}
            onChange={(e) => setConfirmed(e.target.checked)}
            disabled={isLoading}
          />
          <span>Confirmei que copiei/escaneei o código PIX</span>
        </label>
      </div>

      <div className="form-info-box">
        <p>✓ PIX é instantâneo e seguro</p>
        <p>✓ Sem taxas adicionais</p>
        <p>⏱️ Você tem 10 minutos para fazer a transferência</p>
      </div>

      <button type="submit" className="btn-payment" disabled={isLoading || !confirmed}>
        {isLoading ? 'Confirmando pagamento...' : 'Confirmar pagamento PIX'}
      </button>
    </form>
  )
}

export default PaymentFormPix
