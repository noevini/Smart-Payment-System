import { useNavigate } from "react-router-dom";
import { clearToken } from "../auth/tokenStorage";

export default function Topbar() {
  const navigate = useNavigate();

  function handleLogout() {
    clearToken();
    navigate("/login");
  }

  return (
    <header className="h-14 bg-white border-b px-6 flex items-center justify-between">
      <span className="text-sm font-medium">Owner</span>

      <button
        onClick={handleLogout}
        className="text-sm bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded"
      >
        Logout
      </button>
    </header>
  );
}
