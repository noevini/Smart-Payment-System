import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createBusiness } from "../app/api/businessApi";
import { setSelectedBusinessId } from "../app/business/businessStorage";

export default function BusinessSetup() {
  const [name, setName] = useState("");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  async function onSubmit(e) {
    e.preventDefault();
    setError("");

    const trimmed = name.trim();
    if (!trimmed) return setError("Business name is required.");
    if (trimmed.length < 10)
      return setError("Business name must be at least 10 characters.");
    if (trimmed.length > 140)
      return setError("Business name must be at most 140 characters.");

    try {
      setSaving(true);
      const created = await createBusiness({ name: trimmed });
      if (created?.id != null) setSelectedBusinessId(created.id);
      navigate("/dashboard");
    } catch (e2) {
      console.error(e2);
      setError(
        e2?.response?.data?.message ||
          e2?.response?.data?.error ||
          "Failed to create business.",
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="min-h-[70vh] flex items-center justify-center px-4">
      <div className="w-full max-w-xl bg-white border rounded shadow-sm">
        <div className="p-6 border-b">
          <h1 className="text-2xl font-bold">Set up your business</h1>
          <p className="text-gray-600 mt-1">Create your first business.</p>
        </div>

        <form onSubmit={onSubmit} className="p-6 space-y-4">
          <div className="space-y-1">
            <label className="text-sm text-gray-700">Business name</label>
            <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full border rounded px-3 py-2 text-sm"
              placeholder="e.g. Westminster Legal Services Ltd"
            />
            <div className="text-xs text-gray-500">10–140 characters.</div>
          </div>

          {error ? <div className="text-sm text-red-600">{error}</div> : null}

          <div className="flex justify-end">
            <button
              type="submit"
              disabled={saving}
              className="px-4 py-2 rounded bg-black text-white text-sm hover:opacity-90 disabled:opacity-60"
            >
              {saving ? "Creating..." : "Create business"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
