import { useEffect, useMemo, useState } from "react";
import {
  listNotifications,
  markNotificationAsRead,
} from "../api/notificationApi";
import NotificationTable from "../components/notifications/NotificationTable";

export default function Notifications() {
  const [filter, setFilter] = useState("ALL");
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function load() {
    try {
      setLoading(true);
      setError("");

      const data = await listNotifications();
      setRows(Array.isArray(data) ? data : (data?.content ?? []));
    } catch (e) {
      console.error("Failed to load notifications:", e);
      setError("Failed to load notifications.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  const filtered = useMemo(() => {
    if (filter === "ALL") return rows;
    if (filter === "READ") return rows.filter((n) => n.isRead);
    return rows.filter((n) => !n.isRead);
  }, [rows, filter]);

  async function handleMarkRead(id) {
    try {
      await markNotificationAsRead(id);
      await load();
    } catch (e) {
      console.error("Failed to mark notification as read:", e);
      alert("Failed to mark notification as read.");
    }
  }

  return (
    <div className="page-shell">
      <div>
        <h1 className="page-title">Notifications</h1>
        <p className="page-subtitle">
          Track alerts and updates for your selected business.
        </p>
      </div>

      <div className="flex flex-wrap gap-2">
        {["ALL", "UNREAD", "READ"].map((k) => (
          <button
            key={k}
            onClick={() => setFilter(k)}
            className={filter === k ? "btn-primary" : "btn-secondary"}
          >
            {k === "ALL" ? "All" : k}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="text-sm text-slate-500">Loading notifications...</div>
      ) : error ? (
        <div className="text-sm text-red-600">{error}</div>
      ) : (
        <NotificationTable rows={filtered} onMarkRead={handleMarkRead} />
      )}
    </div>
  );
}
