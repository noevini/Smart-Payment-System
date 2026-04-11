import { useEffect, useState } from "react";
import { getSelectedBusinessId } from "../state/businessStorage";

/**
 * useBusinessSync — reusable hook that keeps businessId in sync
 * with the value stored in localStorage.
 *
 * Listens to:
 * - "storage" event — when another tab changes the business
 * - "focus" event — when Topbar dispatches a business change in the same tab
 *
 * Without this hook, pages would show stale data after switching business.
 *
 * Usage:
 *   const businessId = useBusinessSync();
 */
export function useBusinessSync() {
  const [businessId, setBusinessId] = useState(getSelectedBusinessId());

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

  return businessId;
}
