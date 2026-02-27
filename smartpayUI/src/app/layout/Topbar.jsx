import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { clearToken } from "../auth/tokenStorage";
import { listMyBusinesses } from "../api/businessApi";
import {
  getSelectedBusinessId,
  setSelectedBusinessId,
} from "../business/businessStorage";

export default function Topbar() {
  const navigate = useNavigate();

  const [businesses, setBusinesses] = useState([]);
  const [selectedId, setSelectedId] = useState("");

  useEffect(() => {
    async function load() {
      try {
        const data = await listMyBusinesses();
        setBusinesses(data);

        const saved = getSelectedBusinessId();
        const first = data?.[0]?.id;

        const initial =
          saved && data.some((b) => b.id === saved) ? saved : first;

        if (initial) {
          setSelectedId(String(initial));
          setSelectedBusinessId(initial);
        }
      } catch {
        navigate("/login", { replace: true });
      }
    }

    load();
  }, [navigate]);

  function handleChange(e) {
    const id = Number(e.target.value);
    setSelectedId(String(id));
    setSelectedBusinessId(id);
  }

  function handleLogout() {
    clearToken();
    navigate("/login", { replace: true });
  }

  return (
    <header className="h-14 bg-white border-b px-6 flex items-center justify-between">
      <div className="flex items-center gap-3">
        <span className="text-sm font-medium">Owner</span>

        <select
          value={selectedId}
          onChange={handleChange}
          className="text-sm border rounded px-2 py-1 bg-white"
          disabled={businesses.length === 0}
        >
          {businesses.length === 0 ? (
            <option value="">No businesses</option>
          ) : (
            businesses.map((b) => (
              <option key={b.id} value={b.id}>
                {b.name}
              </option>
            ))
          )}
        </select>
      </div>

      <button
        onClick={handleLogout}
        className="text-sm bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded"
      >
        Logout
      </button>
    </header>
  );
}
