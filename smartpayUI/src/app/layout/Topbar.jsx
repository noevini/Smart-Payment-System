import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { clearToken } from "../auth/tokenStorage";
import { listMyBusinesses, createBusiness } from "../api/businessApi";
import {
  clearSelectedBusinessId,
  getSelectedBusinessId,
  setSelectedBusinessId,
} from "../state/businessStorage";

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
    window.dispatchEvent(new Event("focus"));
  }

  async function handleCreateBusiness() {
    const name = window.prompt("Business name:");
    if (!name || !name.trim()) return;

    try {
      setLoading(true);
      await createBusiness({ name: name.trim() });
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
    clearSelectedBusinessId();
    navigate("/login", { replace: true });
  }

  return (
    <header className="topbar-shell">
      <div className="flex items-center gap-3">
        <span className="text-sm font-medium text-slate-700">Owner</span>

        {loading ? (
          <span className="text-sm text-slate-500">Loading...</span>
        ) : businesses.length === 0 ? (
          <button onClick={handleCreateBusiness} className="btn-primary">
            Create business
          </button>
        ) : (
          <select
            value={selectedId}
            onChange={handleChange}
            className="select-field min-w-[260px]"
          >
            {businesses.map((b) => (
              <option key={b.id} value={b.id}>
                {b.name}
              </option>
            ))}
          </select>
        )}
      </div>

      <button onClick={handleLogout} className="btn-danger">
        Logout
      </button>
    </header>
  );
}
