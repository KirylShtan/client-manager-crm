/**
 * API base URL for browser fetches.
 * - Production (Docker / HTTPS reverse-proxy): leave REACT_APP_API_ORIGIN unset → relative /api and /auth (same scheme as the page).
 * - Local `npm start`: use .env.development → http://localhost:8080
 */
const ORIGIN = (process.env.REACT_APP_API_ORIGIN || "").replace(/\/$/, "");

export const BASE_URL = ORIGIN ? `${ORIGIN}/api` : "/api";
export const AUTH_LOGIN_URL = ORIGIN ? `${ORIGIN}/auth/login` : "/auth/login";
