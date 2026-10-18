import { useEffect, useState } from "react";
import { getPredictionSummary } from "../api/predictionApi";
import { getSelectedBusinessId } from "../state/businessStorage";

export default function Predictions() {
  const [businessId, setBusinessId] = useState(getSelectedBusinessId());
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    function sync() {
      setBusinessId(getSelectedBusinessId());
    }
    window.addEventListener("storage", sync);
    window.addEventListener("focus", sync);
    sync();
    return () => {
      window.removeEventListener("storage", sync);
      window.removeEventListener("focus", sync);
    };
  }, []);

  useEffect(() => {
    if (!businessId) {
      setData(null);
      return;
    }

    async function fetchPredictions() {
      try {
        setLoading(true);
        setError("");
        const result = await getPredictionSummary(businessId);
        setData(result);
      } catch (err) {
        console.error(err);
        setError("Failed to load predictions.");
      } finally {
        setLoading(false);
      }
    }

    fetchPredictions();
  }, [businessId]);

  if (!businessId) {
    return (
      <div className="page-shell">
        <h1 className="page-title">Predictions</h1>
        <div className="text-sm text-slate-500">
          Select a business to view predictions.
        </div>
      </div>
    );
  }

  return (
    <div className="page-shell">
      <div>
        <h1 className="page-title">Predictions</h1>
        <p className="page-subtitle">
          Payment risk predictions for your selected business.
        </p>
      </div>

      {loading ? (
        <div className="text-sm text-slate-500">Loading predictions...</div>
      ) : error ? (
        <div className="text-sm text-red-600">{error}</div>
      ) : data ? (
        <div className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
            <PredictionCard
              title="Pending Payments"
              value={data.pendingReceivablePayments}
            />
            <PredictionCard
              title="High Risk"
              value={data.highRiskPayments}
              color="red"
            />
            <PredictionCard
              title="Medium Risk"
              value={data.mediumRiskPayments}
              color="amber"
            />
            <PredictionCard
              title="Low Risk"
              value={data.lowRiskPayments}
              color="emerald"
            />
          </div>

          <div className="card-surface card-content">
            <h2 className="font-semibold text-slate-900 mb-2">
              Overall Risk Level
            </h2>
            <span
              className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-medium capitalize
              ${
                data.overallRiskLevel === "high"
                  ? "bg-red-100 text-red-700 border border-red-200"
                  : data.overallRiskLevel === "medium"
                    ? "bg-amber-100 text-amber-700 border border-amber-200"
                    : "bg-emerald-100 text-emerald-700 border border-emerald-200"
              }`}
            >
              {data.overallRiskLevel}
            </span>
          </div>

          <div className="card-surface card-content">
            <h2 className="font-semibold text-slate-900 mb-2">Summary</h2>
            <p className="text-sm text-slate-700">{data.predictionSummary}</p>
          </div>
        </div>
      ) : null}
    </div>
  );
}

function PredictionCard({ title, value, color }) {
  const colors = {
    red: "text-red-600",
    amber: "text-amber-600",
    emerald: "text-emerald-600",
  };

  return (
    <div className="card-surface card-content">
      <div className="text-sm font-medium text-slate-500">{title}</div>
      <div
        className={`mt-2 text-3xl font-bold tracking-tight ${colors[color] ?? "text-slate-900"}`}
      >
        {value}
      </div>
    </div>
  );
}
