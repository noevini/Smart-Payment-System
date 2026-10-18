import { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { loginUser } from "../api/authApi";
import { listMyBusinesses } from "../api/businessApi";
import { setToken, getToken } from "../auth/tokenStorage";
import {
  getSelectedBusinessId,
  setSelectedBusinessId,
} from "../state/businessStorage";

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
    } catch (err) {
      console.error(err);
      setError("Invalid email or password.");
      setLoading(false);
      return;
    }

    try {
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
      setError("Login succeeded but failed to load businesses. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-6">
      <div className="w-full max-w-md card-surface overflow-hidden">
        <div className="bg-slate-900 px-8 py-8 text-white">
          <h1 className="text-2xl font-bold tracking-tight">Welcome back</h1>
          <p className="mt-2 text-sm text-slate-300">
            Sign in to access your SmartPay dashboard.
          </p>
        </div>

        <form onSubmit={handleSubmit} className="p-8 space-y-5">
          <div className="space-y-2">
            <label className="text-sm font-medium text-slate-700">Email</label>
            <input
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="input-field"
              placeholder="user@email.com"
            />
          </div>

          <div className="space-y-2">
            <label className="text-sm font-medium text-slate-700">
              Password
            </label>
            <input
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="input-field"
              placeholder="••••••••"
            />
          </div>

          {error ? (
            <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
              {error}
            </div>
          ) : null}

          <button
            type="submit"
            disabled={loading}
            className="btn-primary w-full"
          >
            {loading ? "Logging in..." : "Login"}
          </button>

          <p className="text-sm text-center text-slate-500">
            Don’t have an account?{" "}
            <Link
              to="/register"
              className="font-medium text-emerald-600 hover:text-emerald-700"
            >
              Register
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
}
