import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { clearToken } from "../auth/tokenStorage";
import { listMyBusinesses, createBusiness } from "../api/businessApi";
import {
  getSelectedBusinessId,
  setSelectedBusinessId,
} from "../business/businessStorage";

export default function Topbar() {
  const navigate = useNavigate();

  const [businesses, setBusinesses] = useState([]);
  const [selectedId, setSelectedId] = useState("");
  const [loading, setLoading] = useState(true);

  async function loadBusinesses() {
    const data = await listMyBusinesses();
    setBusinesses(data);

    const saved = getSelectedBusinessId();
    const first = data?.[0]?.id;

    const initial = saved && data.some((b) => b.id === saved) ? saved : first;

    if (initial) {
      setSelectedId(String(initial));
      setSelectedBusinessId(initial);
    } else {
      setSelectedId("");
    }
  }

  useEffect(() => {
    async function load() {
      try {
        setLoading(true);
        await loadBusinesses();
      } catch (e) {
        console.error("Failed to load businesses:", e);
        navigate("/login", { replace: true });
      } finally {
        setLoading(false);
      }
    }

    load();
  }, [navigate]);

  function handleChange(e) {
    const id = Number(e.target.value);
    setSelectedId(String(id));
    setSelectedBusinessId(id);
  }

  async function handleCreateBusiness() {
    const name = window.prompt("Business name:");
    if (!name || !name.trim()) return;

    try {
      setLoading(true);
      await createBusiness(name.trim());
      await loadBusinesses();
    } catch (e) {
      console.error("Failed to create business:", e);
      alert("Failed to create business. Try again.");
    } finally {
      setLoading(false);
    }
  }

  function handleLogout() {
    clearToken();
    navigate("/login", { replace: true });
  }

  return (
    <header className="h-14 bg-white border-b px-6 flex items-center justify-between">
      <div className="flex items-center gap-3">
        <span className="text-sm font-medium">Owner</span>

        {loading ? (
          <span className="text-sm text-gray-500">Loading...</span>
        ) : businesses.length === 0 ? (
          <button
            onClick={handleCreateBusiness}
            className="text-sm bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded"
          >
            Create business
          </button>
        ) : (
          <select
            value={selectedId}
            onChange={handleChange}
            className="text-sm border rounded px-2 py-1 bg-white"
          >
            {businesses.map((b) => (
              <option key={b.id} value={b.id}>
                {b.name}
              </option>
            ))}
          </select>
        )}
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
