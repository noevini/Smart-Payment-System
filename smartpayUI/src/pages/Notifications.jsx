import { useMemo, useState } from "react";
import { markNotificationAsRead } from "../api/notificationApi";
import { useNotifications } from "../hooks/useNotifications";
import NotificationTable from "../components/notifications/NotificationTable";

/**
 * Notifications page — lists and manages notifications for the current business.
 *
 * Uses useNotifications hook for data fetching.
 * Supports filtering by read/unread status.
 */
export default function Notifications() {
  const { notifications, loading, error, reload } = useNotifications();

  // Filter — ALL, UNREAD, READ
  const [filter, setFilter] = useState("ALL");

  // Filter notifications by read status
  const filtered = useMemo(() => {
    if (filter === "ALL") return notifications;
    if (filter === "READ") return notifications.filter((n) => n.isRead);
    return notifications.filter((n) => !n.isRead);
  }, [notifications, filter]);

  /**
   * Marks a notification as read and reloads the list.
   * @param {number} id - notification ID
   */
  async function handleMarkRead(id) {
    try {
      await markNotificationAsRead(id);
      await reload();
    } catch (e) {
      console.error(e);
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

      {/* Filter buttons */}
      <div className="flex flex-wrap gap-2">
        {["ALL", "UNREAD", "READ"].map((k) => (
          <button
            key={k}
            onClick={() => setFilter(k)}
            className={filter === k ? "btn-primary" : "btn-secondary"}
          >
            {k}
          </button>
        ))}
      </div>

      {/* Loading / error states */}
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
