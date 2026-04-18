import { useCallback, useEffect, useState } from "react";
import { listStaff } from "../api/staffApi";
import { useBusinessSync } from "./useBusinessSync";

export function useStaff() {
  const businessId = useBusinessSync();

  const [staff, setStaff] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    if (!businessId) {
      setStaff([]);
      setLoading(false);
      return;
    }
    try {
      setLoading(true);
      setError("");
      const data = await listStaff();
      setStaff(Array.isArray(data) ? data : []);
    } catch (e) {
      console.error("Failed to load staff:", e);
      setError("Failed to load staff.");
    } finally {
      setLoading(false);
    }
  }, [businessId]);

  useEffect(() => {
    load();
  }, [load]);

  return { staff, loading, error, businessId, reload: load };
}
