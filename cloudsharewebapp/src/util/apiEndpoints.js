// ✅ Fallback to localhost if env var is missing
const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api/v1.0";

console.log("🌐 API Base URL:", BASE_URL); // Debug - remove after fix

export const apiEndpoints = {
    FETCH_FILES: `${BASE_URL}/files/my`,
    TOGGLE_FILE: (id) => `${BASE_URL}/files/${id}/toggle-public`,
    DOWNLOAD_FILE: (id) => `${BASE_URL}/files/download/${id}`,
    DELETE_FILE: (id) => `${BASE_URL}/files/${id}`,
    UPLOAD_FILE: `${BASE_URL}/files/upload`,
    PUBLIC_FILE_VIEW: (id) => `${BASE_URL}/files/public/${id}`,
    PUBLIC_DOWNLOAD_FILE: (id) => `${BASE_URL}/files/public/download/${id}`
};