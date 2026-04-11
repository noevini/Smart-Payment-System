import { useCallback, useEffect, useState } from "react";
import { listCustomers } from "../api/customerApi";
import { useBusinessSync } from "./useBusinessSync";

/**
 * useCustomers — manages customer data fetching for the selected business.
 *
 * Usage:
 *   const { customers, loading, error, reload } = useCustomers();
 */
export function useCustomers() {
  const businessId = useBusinessSync();

  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    if (!businessId) {
      setCustomers([]);
      setLoading(false);
      return;
    }
    try {
      setLoading(true);
      setError("");
      const data = await listCustomers();
      setCustomers(Array.isArray(data) ? data : (data?.content ?? []));
    } catch (e) {
      console.error("Failed to load customers:", e);
      setError("Failed to load customers.");
    } finally {
      setLoading(false);
    }
  }, [businessId]);

  useEffect(() => {
    load();
  }, [load]);

  return { customers, loading, error, businessId, reload: load };
}
