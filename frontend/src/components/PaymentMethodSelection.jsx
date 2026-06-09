import { useState } from 'react'
import '../styles/checkout.css'

const PaymentMethodSelection = ({ onSelect, total }) => {
  const paymentMethods = [
    { id: 'PIX', name: '💳 PIX', description: 'Pagamento instantâneo' },
    { id: 'BOLETO', name: '📄 BOLETO', description: 'Transferência bancária' },
    { id: 'DEBIT_CARD', name: '💳 Débito', description: 'Cartão de débito' },
    { id: 'CREDIT_CARD', name: '💳 Crédito', description: 'Cartão de crédito' },
  ]

  return (
    <div className="checkout-container">
      <div className="checkout-header">
        <h2>Escolha a forma de pagamento</h2>
        <p className="checkout-total">Total a pagar: <strong>R$ {total.toFixed(2)}</strong></p>
      </div>

      <div className="payment-methods-grid">
        {paymentMethods.map((method) => (
          <div
            key={method.id}
            className="payment-method-card"
            onClick={() => onSelect(method.id)}
          >
            <div className="payment-method-icon">{method.name.split(' ')[0]}</div>
            <h3>{method.name}</h3>
            <p>{method.description}</p>
          </div>
        ))}
      </div>
    </div>
  )
}

export default PaymentMethodSelection
