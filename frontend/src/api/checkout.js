let accessTokenGetter = null

export const setCheckoutTokenGetter = (getter) => {
  accessTokenGetter = getter
}

const checkoutFetch = async (url, options = {}) => {
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  const fullUrl = url.startsWith('http') ? url : `${baseUrl}${url}`

  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  }

  if (options.auth !== false && accessTokenGetter) {
    const token = accessTokenGetter()
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }
  }

  const response = await fetch(fullUrl, {
    ...options,
    headers,
  })

  return response
}

export const processPayment = async (paymentData) => {
  try {
    const res = await checkoutFetch('/payments/process', {
      method: 'POST',
      body: JSON.stringify(paymentData),
    })

    if (!res.ok) {
      const errorText = await res.text()
      throw new Error(errorText || 'Erro ao processar pagamento')
    }

    return await res.json()
  } catch (error) {
    console.error('Erro no pagamento:', error)
    throw error
  }
}

export const getOrderStatus = async (orderId) => {
  try {
    const res = await checkoutFetch(`/payments/${orderId}`, {
      method: 'GET',
    })

    if (!res.ok) {
      throw new Error('Erro ao obter status do pedido')
    }

    return await res.json()
  } catch (error) {
    console.error('Erro ao buscar status:', error)
    throw error
  }
}
