import { useEffect, useState } from "react";
import {
  listNotifications,
  markNotificationAsRead,
} from "../app/api/notificationsApi";
import NotificationTable from "../components/notifications/NotificationTable";

export default function Notifications() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function load() {
    try {
      setLoading(true);
      setError("");

      const data = await listNotifications();
      setRows(data);
    } catch (e) {
      console.error(e);
      setError("Failed to load notifications.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function handleMarkRead(id) {
    try {
      await markNotificationAsRead(id);
      await load();
    } catch (e) {
      console.error(e);
      alert("Failed to mark notification as read.");
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Notifications</h1>
        <p className="text-gray-600">System updates and payment alerts.</p>
      </div>

      {loading && (
        <div className="text-sm text-gray-600">Loading notifications...</div>
      )}

      {error && <div className="text-sm text-red-600">{error}</div>}

      {!loading && !error && (
        <NotificationTable rows={rows} onMarkRead={handleMarkRead} />
      )}
    </div>
  );
}
