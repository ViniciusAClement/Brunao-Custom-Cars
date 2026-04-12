import { API_BASE_URL } from './config.js'
import { getAccessToken } from './session.js'

function resolveUrl(path) {
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }
  const p = path.startsWith('/') ? path : `/${path}`
  return `${API_BASE_URL}${p}`
}

/**
 * fetch com URL já resolvida a partir de VITE_API_BASE_URL.
 * Objetos em `body` são serializados como JSON e recebem Content-Type application/json.
 *
 * @param {string} path - ex: "/auth/login" ou "/products"
 * @param {RequestInit & { body?: object | BodyInit; auth?: boolean }} options
 *   auth: false — não envia Bearer (ex.: POST /auth/login). Padrão: envia se houver token.
 * @returns {Promise<Response>}
 */
export function apiFetch(path, options = {}) {
  const { body, headers: initHeaders, auth = true, ...rest } = options
  const headers = new Headers(initHeaders ?? undefined)

  if (auth !== false && !headers.has('Authorization')) {
    const token = getAccessToken()
    if (token) {
      headers.set('Authorization', `Bearer ${token}`)
    }
  }

  let resolvedBody = body
  if (body != null && typeof body === 'object' && !(body instanceof FormData) && !(body instanceof Blob)) {
    if (!headers.has('Content-Type')) {
      headers.set('Content-Type', 'application/json')
    }
    resolvedBody = JSON.stringify(body)
  }

  return fetch(resolveUrl(path), {
    ...rest,
    headers,
    body: resolvedBody,
  })
}
