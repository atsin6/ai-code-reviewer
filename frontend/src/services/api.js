const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

export async function reviewCode(language, code) {
  const response = await fetch(`${BASE_URL}/api/review`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ language, code })
  });
  if (!response.ok) throw new Error("Review failed");
  return response.json();
}
