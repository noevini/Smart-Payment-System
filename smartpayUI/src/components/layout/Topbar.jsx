import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { clearToken, getToken, decodeJwtPayload } from "../../auth/tokenStorage";
import { listMyBusinesses, createBusiness } from "../../api/businessApi";
import {
  clearSelectedBusinessId,
  getSelectedBusinessId,
  setSelectedBusinessId,
} from "../../state/businessStorage";

/**
 * Decodes the JWT payload to extract user information (role, name).
 * Used to show the correct role label in the topbar.
 * Does NOT verify the signature — backend handles that.
 */
function getUserFromToken() {
  try {
    const token = getToken();
    if (!token) return null;
    return decodeJwtPayload(token);
  } catch {
    return null;
  }
}

/**
 * Topbar — top navigation bar.
 * Responsibilities:
 * - Show current user role
 * - Business selector dropdown
 * - Create new business (via inline modal, not window.prompt)
 * - Logout button
 */
export default function Topbar() {
  const navigate = useNavigate();

  // Business list and selected business state
  const [businesses, setBusinesses] = useState([]);
  const [selectedId, setSelectedId] = useState("");
  const [loading, setLoading] = useState(true);

  // Inline create business modal state
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newBusinessName, setNewBusinessName] = useState("");
  const [createError, setCreateError] = useState("");
  const [creating, setCreating] = useState(false);

  // User info decoded from JWT
  const userPayload = getUserFromToken();
  const userRole = userPayload?.role ?? "USER";

  /**
   * Loads the list of businesses and sets the selected one.
   * Prefers the previously saved businessId from localStorage.
   */
  async function loadBusinesses() {
    const data = await listMyBusinesses();
    const normalized = Array.isArray(data)
      ? data
      : Array.isArray(data?.content)
        ? data.content
        : Array.isArray(data?.items)
          ? data.items
          : [];
    setBusinesses(normalized);

    const saved = getSelectedBusinessId();
    const validSaved = normalized.some((b) => b.id === saved) ? saved : null;
    const initial = validSaved ?? normalized?.[0]?.id ?? null;

    if (initial) {
      setSelectedId(String(initial));
      setSelectedBusinessId(initial);
    } else {
      setSelectedId("");
    }
  }

  // Load businesses on mount
  useEffect(() => {
    async function init() {
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

    init();
  }, [navigate]);

  /**
   * Handles business selection change.
   * Updates localStorage and fires a focus event so pages react.
   */
  function handleChange(e) {
    const id = Number(e.target.value);
    setSelectedId(String(id));
    setSelectedBusinessId(id);
    // Notify pages listening to window focus/storage events
    window.dispatchEvent(new Event("focus"));
  }

  /**
   * Handles creating a new business via the inline modal.
   * Validates name length to match backend constraints (10-140 chars).
   */
  async function handleCreateBusiness(e) {
    e.preventDefault();
    setCreateError("");

    const trimmed = newBusinessName.trim();

    if (!trimmed) {
      return setCreateError("Business name is required.");
    }
    if (trimmed.length < 10) {
      return setCreateError("Business name must be at least 10 characters.");
    }
    if (trimmed.length > 140) {
      return setCreateError("Business name must be at most 140 characters.");
    }

    try {
      setCreating(true);
      const created = await createBusiness({ name: trimmed });

      // Select the newly created business automatically
      if (created?.id != null) {
        setSelectedBusinessId(created.id);
        window.dispatchEvent(new Event("focus"));
      }

      setShowCreateModal(false);
      setNewBusinessName("");
      await loadBusinesses();
    } catch (e2) {
      console.error(e2);
      setCreateError(
        e2?.response?.data?.message ||
          e2?.response?.data?.error ||
          "Failed to create business.",
      );
    } finally {
      setCreating(false);
    }
  }

  /** Clears auth state and redirects to login */
  function handleLogout() {
    clearToken();
    clearSelectedBusinessId();
    navigate("/login", { replace: true });
  }

  return (
    <>
      {/* ── Main topbar ── */}
      <header className="topbar-shell">
        <div className="flex items-center gap-3">
          {/* User role badge */}
          <span className="text-sm font-medium text-slate-700 capitalize">
            {userRole.toLowerCase()}
          </span>

          {/* Business selector or loading state */}
          {loading ? (
            <span className="text-sm text-slate-500">Loading...</span>
          ) : businesses.length === 0 ? (
            // No businesses yet — show create button
            <button
              onClick={() => setShowCreateModal(true)}
              className="btn-primary"
            >
              Create business
            </button>
          ) : (
            // Business dropdown
            <div className="flex items-center gap-2">
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

              {/* Add another business */}
              <button
                onClick={() => setShowCreateModal(true)}
                className="btn-secondary"
                title="Add business"
              >
                +
              </button>
            </div>
          )}
        </div>

        {/* Logout */}
        <button onClick={handleLogout} className="btn-danger">
          Logout
        </button>
      </header>

      {/* ── Create business modal ── */}
      {showCreateModal ? (
        <div className="fixed inset-0 z-50 flex items-center justify-center px-4">
          {/* Backdrop */}
          <div
            className="absolute inset-0 bg-slate-900/50 backdrop-blur-sm"
            onClick={() => {
              setShowCreateModal(false);
              setNewBusinessName("");
              setCreateError("");
            }}
          />

          {/* Modal */}
          <div className="relative w-full max-w-md card-surface overflow-hidden">
            <div className="border-b border-slate-200 px-6 py-4">
              <h2 className="text-lg font-semibold text-slate-900">
                Create business
              </h2>
              <p className="text-sm text-slate-500 mt-1">
                Add a new business to your account.
              </p>
            </div>

            <form onSubmit={handleCreateBusiness} className="p-6 space-y-4">
              <div className="space-y-2">
                <label className="text-sm font-medium text-slate-700">
                  Business name
                </label>
                <input
                  type="text"
                  value={newBusinessName}
                  onChange={(e) => setNewBusinessName(e.target.value)}
                  className="input-field"
                  placeholder="e.g. Westminster Legal Services Ltd"
                  autoFocus
                />
                <p className="text-xs text-slate-500">10–140 characters.</p>
              </div>

              {createError ? (
                <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
                  {createError}
                </div>
              ) : null}

              <div className="flex justify-end gap-3">
                <button
                  type="button"
                  className="btn-secondary"
                  disabled={creating}
                  onClick={() => {
                    setShowCreateModal(false);
                    setNewBusinessName("");
                    setCreateError("");
                  }}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn-primary"
                  disabled={creating}
                >
                  {creating ? "Creating..." : "Create"}
                </button>
              </div>
            </form>
          </div>
        </div>
      ) : null}
    </>
  );
}
