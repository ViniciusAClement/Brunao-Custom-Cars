import { useState } from 'react'

const PaymentFormBoleto = ({ total, onSubmit, isLoading }) => {
  const [boletoEmail, setBoletoEmail] = useState('')

  const handleSubmit = (e) => {
    e.preventDefault()
    
    if (!boletoEmail.trim()) {
      alert('Informe seu email para receber o boleto')
      return
    }

    // Validação básica de email
    if (!boletoEmail.includes('@')) {
      alert('Email inválido')
      return
    }

    onSubmit({
      paymentMethod: 'BOLETO',
      boletoEmail: boletoEmail.trim(),
    })
  }

  return (
    <form onSubmit={handleSubmit} className="payment-form">
      <div className="form-section">
        <h3>📄 Pagamento via BOLETO</h3>
        <p className="form-info">Você receberá o boleto por email para pagar em sua instituição bancária</p>
      </div>

      <div className="form-group">
        <label>Email para receber o boleto:</label>
        <input
          type="email"
          value={boletoEmail}
          onChange={(e) => setBoletoEmail(e.target.value)}
          placeholder="seu.email@exemplo.com"
          className="form-input"
          disabled={isLoading}
          required
        />
      </div>

      <div className="form-info-box">
        <p>✓ Vencimento em 3 dias úteis</p>
        <p>✓ Pague em qualquer banco ou via internet banking</p>
        <p>✓ Receba confirmação por email</p>
      </div>

      <button type="submit" className="btn-payment" disabled={isLoading}>
        {isLoading ? 'Gerando boleto...' : 'Gerar boleto'}
      </button>
    </form>
  )
}

export default PaymentFormBoleto
