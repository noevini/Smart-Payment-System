import { useEffect, useState } from "react";
import { getInsightSummary } from "../api/insightApi";
import { getSelectedBusinessId } from "../state/businessStorage";

export default function Insights() {
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

    async function fetchInsights() {
      try {
        setLoading(true);
        setError("");
        const result = await getInsightSummary(businessId);
        setData(result);
      } catch (err) {
        console.error(err);
        setError("Failed to load insights.");
      } finally {
        setLoading(false);
      }
    }

    fetchInsights();
  }, [businessId]);

  if (!businessId) {
    return (
      <div className="page-shell">
        <h1 className="page-title">AI Insights</h1>
        <div className="text-sm text-slate-500">
          Select a business to view insights.
        </div>
      </div>
    );
  }

  return (
    <div className="page-shell">
      <div>
        <h1 className="page-title">AI Insights</h1>
        <p className="page-subtitle">
          AI-generated insights based on your payment data.
        </p>
      </div>

      {loading ? (
        <div className="text-sm text-slate-500">Loading insights...</div>
      ) : error ? (
        <div className="text-sm text-red-600">{error}</div>
      ) : data ? (
        <div className="space-y-4">
          <div className="card-surface card-content">
            <h2 className="font-semibold text-slate-900 mb-2">Summary</h2>
            <p className="text-sm text-slate-700">{data.summary}</p>
          </div>

          <div className="card-surface card-content">
            <h2 className="font-semibold text-slate-900 mb-2">Risks</h2>
            <ul className="list-disc pl-5 space-y-1">
              {data.risks?.map((risk, i) => (
                <li key={i} className="text-sm text-slate-700">
                  {risk}
                </li>
              ))}
            </ul>
          </div>

          <div className="card-surface card-content">
            <h2 className="font-semibold text-slate-900 mb-2">
              Recommendations
            </h2>
            <ul className="list-disc pl-5 space-y-1">
              {data.recommendations?.map((rec, i) => (
                <li key={i} className="text-sm text-slate-700">
                  {rec}
                </li>
              ))}
            </ul>
          </div>

          <div className="card-surface card-content">
            <h2 className="font-semibold text-slate-900 mb-2">Confidence</h2>
            <span
              className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-medium capitalize
              ${
                data.confidence === "high"
                  ? "bg-emerald-100 text-emerald-700 border border-emerald-200"
                  : data.confidence === "medium"
                    ? "bg-amber-100 text-amber-700 border border-amber-200"
                    : "bg-red-100 text-red-700 border border-red-200"
              }`}
            >
              {data.confidence}
            </span>
          </div>
        </div>
      ) : null}
    </div>
  );
}
