import { useNavigate, Link } from "react-router-dom";

export default function Login() {
  const navigate = useNavigate();

  function handleSubmit(e) {
    e.preventDefault();
    // mock auth
    localStorage.setItem("demo_auth", "true");
    navigate("/dashboard");
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
            Demo authentication (no backend)
          </p>
        </div>

        <div>
          <label className="text-sm">Email</label>
          <input
            type="email"
            required
            className="mt-1 w-full border rounded px-3 py-2"
            placeholder="user@email.com"
          />
        </div>

        <div>
          <label className="text-sm">Password</label>
          <input
            type="password"
            required
            className="mt-1 w-full border rounded px-3 py-2"
            placeholder="••••••••"
          />
        </div>

        <button
          type="submit"
          className="w-full bg-blue-600 text-white rounded py-2 font-medium"
        >
          Login
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
