import { useState, useEffect } from 'react';
import { reviewCode, logout } from './services/api.js';
import AuthForm from './components/AuthForm.jsx';
import './App.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [language, setLanguage] = useState('Java');
  const [code, setCode] = useState('');
  const [review, setReview] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Check if token exists on load
    if (localStorage.getItem('token')) {
      setIsAuthenticated(true);
    }
  }, []);

  const handleLoginSuccess = () => {
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    logout();
    setIsAuthenticated(false);
    setReview(null);
    setCode('');
    setError(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!code.trim()) {
      setError('Please provide some code to review.');
      return;
    }

    setLoading(true);
    setError(null);
    setReview(null);

    try {
      const response = await reviewCode(language, code);
      setReview(response);
    } catch (err) {
      if (err.message.includes("Session expired")) {
        setIsAuthenticated(false);
      }
      setError(err.message || 'An error occurred during review.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <header>
        <div className="header-content">
          <div>
            <h1>AI Code Reviewer</h1>
            <p>Get instant feedback on your code using Gemini AI.</p>
          </div>
          {isAuthenticated && (
            <button onClick={handleLogout} className="logout-btn">Logout</button>
          )}
        </div>
      </header>

      <main>
        {!isAuthenticated ? (
          <AuthForm onLoginSuccess={handleLoginSuccess} />
        ) : (
          <>
            <form onSubmit={handleSubmit} className="review-form">
              <div className="form-group">
                <label htmlFor="language">Select Language:</label>
                <select
                  id="language"
                  value={language}
                  onChange={(e) => setLanguage(e.target.value)}
                >
                  <option value="Java">Java</option>
                  <option value="JavaScript">JavaScript</option>
                  <option value="Python">Python</option>
                  <option value="C++">C++</option>
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="code">Code to review:</label>
                <textarea
                  id="code"
                  placeholder="Paste your code here..."
                  value={code}
                  onChange={(e) => setCode(e.target.value)}
                  rows="12"
                />
              </div>

              <button type="submit" disabled={loading}>
                {loading ? 'Reviewing...' : 'Submit for Review'}
              </button>
            </form>

            {loading && <p className="loading">Analyzing your code...</p>}
            
            {error && <p className="error">{error}</p>}

            {review && (
              <div className="results">
                <h2>Review Results</h2>
                <div className="card-grid">
                  <div className="card">
                    <h3>Bugs & Issues</h3>
                    <p>{review.bugs}</p>
                  </div>
                  <div className="card">
                    <h3>Performance Suggestions</h3>
                    <p>{review.performance}</p>
                  </div>
                  <div className="card">
                    <h3>Best Practices</h3>
                    <p>{review.bestPractices}</p>
                  </div>
                  <div className="card">
                    <h3>Improved Code</h3>
                    <pre><code>{review.improvedCode}</code></pre>
                  </div>
                </div>
              </div>
            )}
          </>
        )}
      </main>
    </div>
  );
}

export default App;
