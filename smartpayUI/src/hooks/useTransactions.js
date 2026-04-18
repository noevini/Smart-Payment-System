import { useCallback, useEffect, useState } from "react";
import { listTransactions } from "../api/transactionApi";
import { useBusinessSync } from "./useBusinessSync";

export function useTransactions() {
  const businessId = useBusinessSync();

  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    if (!businessId) {
      setTransactions([]);
      setLoading(false);
      return;
    }
    try {
      setLoading(true);
      setError("");
      const data = await listTransactions();
      setTransactions(Array.isArray(data) ? data : (data?.content ?? []));
    } catch (e) {
      console.error("Failed to load transactions:", e);
      setError("Failed to load transactions.");
    } finally {
      setLoading(false);
    }
  }, [businessId]);

  useEffect(() => {
    load();
  }, [load]);

  return { transactions, loading, error, businessId, reload: load };
}
