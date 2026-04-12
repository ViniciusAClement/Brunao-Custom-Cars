/** Retorna o JWT atual (ex.: lido do estado React após login). */
let accessTokenGetter = () => null

export function setAccessTokenGetter(getter) {
  accessTokenGetter = typeof getter === 'function' ? getter : () => null
}

export function getAccessToken() {
  try {
    const t = accessTokenGetter()
    return t && String(t).trim() ? String(t).trim() : null
  } catch {
    return null
  }
}
