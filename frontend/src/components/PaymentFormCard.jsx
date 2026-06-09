import { useState } from 'react'

const PaymentFormCard = ({ total, paymentMethod, onSubmit, isLoading }) => {
  const [cardNumber, setCardNumber] = useState('')
  const [cardHolder, setCardHolder] = useState('')
  const [expirationDate, setExpirationDate] = useState('')
  const [cvv, setCvv] = useState('')
  const [installments, setInstallments] = useState('1')

  const handleCardNumberChange = (e) => {
    let value = e.target.value.replace(/\s/g, '')
    if (value.length <= 16) {
      value = value.replace(/(\d{4})/g, '$1 ').trim()
      setCardNumber(value)
    }
  }

  const handleExpirationChange = (e) => {
    let value = e.target.value.replace(/\D/g, '')
    if (value.length <= 4) {
      if (value.length >= 2) {
        value = value.substring(0, 2) + '/' + value.substring(2)
      }
      setExpirationDate(value)
    }
  }

  const handleCvvChange = (e) => {
    let value = e.target.value.replace(/\D/g, '')
    if (value.length <= 3) {
      setCvv(value)
    }
  }

  const handleSubmit = (e) => {
    e.preventDefault()

    if (!cardNumber.replace(/\s/g, '') || cardNumber.replace(/\s/g, '').length !== 16) {
      alert('Número do cartão inválido (16 dígitos)')
      return
    }

    if (!cardHolder.trim()) {
      alert('Nome do titular é obrigatório')
      return
    }

    if (!expirationDate || expirationDate.length !== 5) {
      alert('Data de expiração inválida (MM/YY)')
      return
    }

    if (!cvv || cvv.length !== 3) {
      alert('CVV inválido (3 dígitos)')
      return
    }

    const isCredit = paymentMethod === 'CREDIT_CARD'
    
    onSubmit({
      paymentMethod: paymentMethod,
      cardNumber: cardNumber.replace(/\s/g, ''),
      cardHolderName: cardHolder.trim().toUpperCase(),
      expirationDate: expirationDate,
      cvv: cvv,
      installments: isCredit ? parseInt(installments) : 1,
    })
  }

  const isCredit = paymentMethod === 'CREDIT_CARD'
  const maxInstallments = isCredit ? 12 : 1

  return (
    <form onSubmit={handleSubmit} className="payment-form">
      <div className="form-section">
        <h3>💳 Pagamento com {isCredit ? 'Cartão de Crédito' : 'Cartão de Débito'}</h3>
        <p className="form-info">Insira os dados do seu cartão com segurança</p>
      </div>

      <div className="form-group">
        <label>Número do cartão:</label>
        <input
          type="text"
          value={cardNumber}
          onChange={handleCardNumberChange}
          placeholder="0000 0000 0000 0000"
          className="form-input"
          maxLength="19"
          disabled={isLoading}
          required
        />
      </div>

      <div className="form-group">
        <label>Nome do titular:</label>
        <input
          type="text"
          value={cardHolder}
          onChange={(e) => setCardHolder(e.target.value)}
          placeholder="NOME COMPLETO"
          className="form-input"
          disabled={isLoading}
          required
        />
      </div>

      <div className="form-row">
        <div className="form-group">
          <label>Validade (MM/YY):</label>
          <input
            type="text"
            value={expirationDate}
            onChange={handleExpirationChange}
            placeholder="MM/YY"
            className="form-input"
            maxLength="5"
            disabled={isLoading}
            required
          />
        </div>

        <div className="form-group">
          <label>CVV:</label>
          <input
            type="text"
            value={cvv}
            onChange={handleCvvChange}
            placeholder="000"
            className="form-input"
            maxLength="3"
            disabled={isLoading}
            required
          />
        </div>
      </div>

      {isCredit && (
        <div className="form-group">
          <label>Parcelamento:</label>
          <select
            value={installments}
            onChange={(e) => setInstallments(e.target.value)}
            className="form-input"
            disabled={isLoading}
          >
            {Array.from({ length: maxInstallments }, (_, i) => i + 1).map((num) => (
              <option key={num} value={num}>
                {num}x de R$ {(total / num).toFixed(2)}
              </option>
            ))}
          </select>
        </div>
      )}

      <div className="form-info-box">
        <p>✓ Sua transação é segura com criptografia SSL</p>
        <p>✓ Dados do cartão não são armazenados</p>
      </div>

      <button type="submit" className="btn-payment" disabled={isLoading}>
        {isLoading ? 'Processando...' : `Confirmar pagamento - R$ ${total.toFixed(2)}`}
      </button>
    </form>
  )
}

export default PaymentFormCard
