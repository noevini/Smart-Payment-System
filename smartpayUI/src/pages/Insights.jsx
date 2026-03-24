import { useEffect, useState } from "react";
import { getInsightSummary } from "../app/api/insightsApi";
import { getSelectedBusinessId } from "../app/business/businessStorage";

export default function Insights() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const businessId = getSelectedBusinessId();

  useEffect(() => {
    if (!businessId) return;

    const fetchInsights = async () => {
      try {
        setLoading(true);
        setError("");

        const result = await getInsightSummary(businessId);
        setData(result);
      } catch (err) {
        console.error(err);
        setError("Failed to load insights");
      } finally {
        setLoading(false);
      }
    };

    fetchInsights();
  }, [businessId]);

  if (!businessId) {
    return <div className="p-4">No business selected</div>;
  }

  if (loading) {
    return <div className="p-4">Loading insights...</div>;
  }

  if (error) {
    return <div className="p-4 text-red-500">{error}</div>;
  }

  if (!data) {
    return null;
  }

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold">AI Insights</h1>

      {/* Summary */}
      <div className="bg-white p-4 rounded-xl shadow">
        <h2 className="font-semibold mb-2">Summary</h2>
        <p>{data.summary}</p>
      </div>

      {/* Risks */}
      <div className="bg-white p-4 rounded-xl shadow">
        <h2 className="font-semibold mb-2">Risks</h2>
        <ul className="list-disc pl-5">
          {data.risks?.map((risk, index) => (
            <li key={index}>{risk}</li>
          ))}
        </ul>
      </div>

      {/* Recommendations */}
      <div className="bg-white p-4 rounded-xl shadow">
        <h2 className="font-semibold mb-2">Recommendations</h2>
        <ul className="list-disc pl-5">
          {data.recommendations?.map((rec, index) => (
            <li key={index}>{rec}</li>
          ))}
        </ul>
      </div>

      {/* Confidence */}
      <div className="bg-white p-4 rounded-xl shadow">
        <h2 className="font-semibold mb-2">Confidence</h2>
        <p className="capitalize">{data.confidence}</p>
      </div>
    </div>
  );
}
