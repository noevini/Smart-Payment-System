import { useEffect, useState } from "react";
import { getPredictionSummary } from "../app/api/predictionsApi";
import { getSelectedBusinessId } from "../app/business/businessStorage";

export default function Predictions() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const businessId = getSelectedBusinessId();

  useEffect(() => {
    if (!businessId) return;

    const fetchPredictions = async () => {
      try {
        setLoading(true);
        setError("");

        const result = await getPredictionSummary(businessId);
        setData(result);
      } catch (err) {
        console.error(err);
        setError("Failed to load predictions");
      } finally {
        setLoading(false);
      }
    };

    fetchPredictions();
  }, [businessId]);

  if (!businessId) {
    return <div className="p-4">No business selected</div>;
  }

  if (loading) {
    return <div className="p-4">Loading predictions...</div>;
  }

  if (error) {
    return <div className="p-4 text-red-500">{error}</div>;
  }

  if (!data) return null;

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold">Predictions</h1>

      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        <Card title="Pending Payments" value={data.pendingReceivablePayments} />
        <Card title="High Risk" value={data.highRiskPayments} />
        <Card title="Medium Risk" value={data.mediumRiskPayments} />
        <Card title="Low Risk" value={data.lowRiskPayments} />
      </div>

      {/* Overall Risk */}
      <div className="bg-white p-4 rounded-xl shadow">
        <h2 className="font-semibold mb-2">Overall Risk Level</h2>
        <p className="capitalize text-lg font-semibold">
          {data.overallRiskLevel}
        </p>
      </div>

      {/* Summary */}
      <div className="bg-white p-4 rounded-xl shadow">
        <h2 className="font-semibold mb-2">Summary</h2>
        <p>{data.predictionSummary}</p>
      </div>
    </div>
  );
}

function Card({ title, value }) {
  return (
    <div className="bg-white p-4 rounded-xl shadow">
      <p className="text-sm text-gray-500">{title}</p>
      <p className="text-xl font-semibold mt-1">{value}</p>
    </div>
  );
}
