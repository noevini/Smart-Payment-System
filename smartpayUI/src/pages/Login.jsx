import { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { loginUser } from "../app/api/authApi";
import { listMyBusinesses } from "../app/api/businessApi";

import { setToken, getToken } from "../app/auth/tokenStorage";
import {
  getSelectedBusinessId,
  setSelectedBusinessId,
} from "../app/state/businessStorage";

export default function Login() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let cancelled = false;

    async function go() {
      const token = getToken();
      if (!token) return;

      try {
        const businesses = await listMyBusinesses();
        if (cancelled) return;

        if (!Array.isArray(businesses) || businesses.length === 0) {
          navigate("/business-setup", { replace: true });
          return;
        }

        const current = getSelectedBusinessId();

        if (!current && businesses[0]?.id != null) {
          setSelectedBusinessId(businesses[0].id);
        }

        navigate("/dashboard", { replace: true });
      } catch (e) {
        console.error(e);
        setError("Failed to load your businesses. Please login again.");
      }
    }

    go();

    return () => {
      cancelled = true;
    };
  }, [navigate]);

  async function handleSubmit(e) {
    e.preventDefault();

    setError("");
    setLoading(true);

    try {
      const data = await loginUser({ email, password });

      setToken(data.token);

      const businesses = await listMyBusinesses();

      if (!Array.isArray(businesses) || businesses.length === 0) {
        navigate("/business-setup");
        return;
      }

      const current = getSelectedBusinessId();

      if (!current && businesses[0]?.id != null) {
        setSelectedBusinessId(businesses[0].id);
      }

      navigate("/dashboard");
    } catch (err) {
      console.error(err);
      setError("Invalid email or password.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-6">
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-sm bg-white border rounded p-6 space-y-4"
      >
        <div>
          <h1 className="text-xl font-bold">Login</h1>
          <p className="text-sm text-gray-600">
            Sign in to access your dashboard
          </p>
        </div>

        <div>
          <label className="text-sm">Email</label>
          <input
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="mt-1 w-full border rounded px-3 py-2"
            placeholder="user@email.com"
          />
        </div>

        <div>
          <label className="text-sm">Password</label>
          <input
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="mt-1 w-full border rounded px-3 py-2"
            placeholder="••••••••"
          />
        </div>

        {error && <p className="text-sm text-red-600">{error}</p>}

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-blue-600 text-white rounded py-2 font-medium disabled:opacity-70"
        >
          {loading ? "Logging in..." : "Login"}
        </button>

        <p className="text-sm text-center text-gray-600">
          Don’t have an account?{" "}
          <Link to="/register" className="text-blue-600 hover:underline">
            Register
          </Link>
        </p>
      </form>
    </div>
  );
}
