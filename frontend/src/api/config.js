/**
 * Base URL da API Spring Boot (sem barra final).
 * Defina em .env: VITE_API_BASE_URL=http://localhost:8080
 */
const raw = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export const API_BASE_URL = String(raw).replace(/\/+$/, '')
