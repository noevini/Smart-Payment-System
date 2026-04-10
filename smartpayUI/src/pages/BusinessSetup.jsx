import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createBusiness } from "../api/businessApi";
import { setSelectedBusinessId } from "../state/businessStorage";

export default function BusinessSetup() {
  const [name, setName] = useState("");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  async function onSubmit(e) {
    e.preventDefault();
    setError("");

    const trimmed = name.trim();

    if (!trimmed) {
      return setError("Business name is required.");
    }

    if (trimmed.length < 10) {
      return setError("Business name must be at least 10 characters.");
    }

    if (trimmed.length > 140) {
      return setError("Business name must be at most 140 characters.");
    }

    try {
      setSaving(true);
      const created = await createBusiness({ name: trimmed });

      if (created?.id != null) {
        setSelectedBusinessId(created.id);
      }

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
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-6">
      <div className="w-full max-w-2xl card-surface overflow-hidden">
        <div className="bg-slate-900 px-8 py-8 text-white">
          <h1 className="text-2xl font-bold tracking-tight">
            Set up your business
          </h1>
          <p className="mt-2 text-sm text-slate-300">
            Create your first business to start using SmartPay.
          </p>
        </div>

        <form onSubmit={onSubmit} className="p-8 space-y-6">
          <div className="space-y-2">
            <label className="text-sm font-medium text-slate-700">
              Business name
            </label>
            <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="input-field"
              placeholder="e.g. Westminster Legal Services Ltd"
            />
            <div className="text-xs text-slate-500">10–140 characters.</div>
          </div>

          {error ? (
            <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
              {error}
            </div>
          ) : null}

          <div className="flex justify-end">
            <button type="submit" disabled={saving} className="btn-primary">
              {saving ? "Creating..." : "Create business"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
