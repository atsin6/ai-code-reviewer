export async function reviewCode(language, code) {
  const response = await fetch('/api/review', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ language, code }),
  });

  if (!response.ok) {
    throw new Error('Review failed');
  }

  return response.json();
}
