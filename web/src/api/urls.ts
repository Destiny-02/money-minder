/**
 * API URLs accessed from various frontend locations
 */

const BASE_URL = import.meta.env.VITE_API_BASE_URL;
const API_BASE_URL = `${BASE_URL}/api`;

export const SHEETS_URL = `${API_BASE_URL}/sheets`;
export const TRANSACTIONS_URL = `${API_BASE_URL}/transactions`;
