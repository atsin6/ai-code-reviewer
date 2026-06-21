const RAW_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";
const BASE_URL = RAW_BASE_URL.replace(/\/$/, ""); // Remove trailing slash if present

function getAuthHeaders() {
  const token = localStorage.getItem('token');
  const headers = { "Content-Type": "application/json" };
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }
  return headers;
}

export async function login(email, password) {
  const response = await fetch(`${BASE_URL}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password })
  });
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || "Login failed");
  }
  const data = await response.json();
  localStorage.setItem('token', data.token);
  return data;
}

export async function register(email, password) {
  const response = await fetch(`${BASE_URL}/api/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password })
  });
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || "Registration failed");
  }
  const data = await response.json();
  localStorage.setItem('token', data.token);
  return data;
}

export function logout() {
  localStorage.removeItem('token');
}

export async function reviewCode(language, code) {
  const response = await fetch(`${BASE_URL}/api/review`, {
    method: "POST",
    headers: getAuthHeaders(),
    body: JSON.stringify({ language, code })
  });
  if (!response.ok) {
    if (response.status === 401 || response.status === 403) {
      logout();
      throw new Error("Session expired. Please log in again.");
    }
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || "Review failed");
  }
  return response.json();
}
