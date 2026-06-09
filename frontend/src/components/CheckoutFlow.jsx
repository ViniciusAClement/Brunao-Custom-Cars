import { useState } from 'react'
import PaymentMethodSelection from './PaymentMethodSelection'
import PaymentFormPix from './PaymentFormPix'
import PaymentFormBoleto from './PaymentFormBoleto'
import PaymentFormCard from './PaymentFormCard'
import PaymentConfirmation from './PaymentConfirmation'
import { processPayment } from '../api/checkout'

const CheckoutFlow = ({ marketCarId, cartTotal, onClose, onSuccess }) => {
  const [step, setStep] = useState('method') // method, form, confirmation
  const [selectedMethod, setSelectedMethod] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const [paymentResponse, setPaymentResponse] = useState(null)

  const handleSelectMethod = (method) => {
    setSelectedMethod(method)
    setStep('form')
  }

  const handleSubmitPayment = async (paymentData) => {
    setIsLoading(true)
    try {
      const response = await processPayment({
        ...paymentData,
        marketCarId,
      })
      setPaymentResponse(response)
      setStep('confirmation')
      
      if (response.status === 'PAID') {
        if (onSuccess) {
          // Aguarda 4 segundos para o usuário ver a confirmação
          setTimeout(() => {
            onSuccess()
          }, 4000)
        }
      }
    } catch (error) {
      alert(error.message || 'Erro ao processar pagamento. Tente novamente.')
      setStep('form')
    } finally {
      setIsLoading(false)
    }
  }

  const handleBackToMethod = () => {
    setStep('method')
    setSelectedMethod(null)
  }

  const handleNewOrder = () => {
    onClose()
    if (onSuccess) {
      onSuccess()
    }
  }

  return (
    <div className="checkout-overlay">
      <div className="checkout-modal">
        {step === 'method' && (
          <>
            <button 
              className="checkout-back-btn" 
              onClick={onClose}
            >
              ← Voltar ao carrinho
            </button>
            <PaymentMethodSelection 
              onSelect={handleSelectMethod}
              total={cartTotal}
            />
          </>
        )}

        {step === 'form' && selectedMethod === 'PIX' && (
          <>
            <button 
              className="checkout-back-btn" 
              onClick={handleBackToMethod}
            >
              ← Voltar aos métodos de pagamento
            </button>
            <PaymentFormPix 
              total={cartTotal}
              onSubmit={handleSubmitPayment}
              isLoading={isLoading}
            />
          </>
        )}

        {step === 'form' && selectedMethod === 'BOLETO' && (
          <>
            <button 
              className="checkout-back-btn" 
              onClick={handleBackToMethod}
            >
              ← Voltar aos métodos de pagamento
            </button>
            <PaymentFormBoleto 
              total={cartTotal}
              onSubmit={handleSubmitPayment}
              isLoading={isLoading}
            />
          </>
        )}

        {step === 'form' && (selectedMethod === 'DEBIT_CARD' || selectedMethod === 'CREDIT_CARD') && (
          <>
            <button 
              className="checkout-back-btn" 
              onClick={handleBackToMethod}
            >
              ← Voltar aos métodos de pagamento
            </button>
            <PaymentFormCard 
              paymentMethod={selectedMethod}
              total={cartTotal}
              onSubmit={handleSubmitPayment}
              isLoading={isLoading}
            />
          </>
        )}

        {step === 'confirmation' && paymentResponse && (
          <PaymentConfirmation 
            paymentResponse={paymentResponse}
            onClose={onClose}
            onNewOrder={handleNewOrder}
          />
        )}
      </div>
    </div>
  )
}

export default CheckoutFlow
