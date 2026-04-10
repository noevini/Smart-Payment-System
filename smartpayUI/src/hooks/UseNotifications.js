import { useCallback, useEffect, useState } from "react";
import { listNotifications } from "../api/notificationApi";

/**
 * useNotifications — manages notification data fetching.
 *
 * Usage:
 *   const { notifications, loading, error, reload } = useNotifications();
 */
export function useNotifications() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    try {
      setLoading(true);
      setError("");
      const data = await listNotifications();
      setNotifications(Array.isArray(data) ? data : []);
    } catch (e) {
      console.error("Failed to load notifications:", e);
      setError("Failed to load notifications.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  return { notifications, loading, error, reload: load };
}
