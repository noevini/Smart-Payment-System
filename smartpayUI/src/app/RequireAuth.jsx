import { Navigate } from "react-router-dom";

export default function RequireAuth({ children }) {
  const ok = localStorage.getItem("demo_auth") === "true";
  return ok ? children : <Navigate to="/login" replace />;
}
