import { useCallback, useEffect, useState } from "react";
import { listPayments } from "../api/paymentApi";
import { useBusinessSync } from "./useBusinessSync";

/**
 * usePayments — manages payment data fetching for the selected business.
 *
 * Encapsulates:
 * - Business sync (reacts to business changes)
 * - Loading and error states
 * - Fetch and refresh logic
 *
 * Usage:
 *   const { payments, loading, error, reload } = usePayments();
 */
export function usePayments() {
  const businessId = useBusinessSync();

  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  /**
   * useCallback gives load() a stable reference so useEffect
   * only re-runs when businessId actually changes.
   */
  const load = useCallback(async () => {
    if (!businessId) {
      setPayments([]);
      setLoading(false);
      return;
    }
    try {
      setLoading(true);
      setError("");
      const data = await listPayments();
      setPayments(Array.isArray(data) ? data : (data?.content ?? []));
    } catch (e) {
      console.error("Failed to load payments:", e);
      setError("Failed to load payments.");
    } finally {
      setLoading(false);
    }
  }, [businessId]);

  useEffect(() => {
    load();
  }, [load]);

  return { payments, loading, error, businessId, reload: load };
}
