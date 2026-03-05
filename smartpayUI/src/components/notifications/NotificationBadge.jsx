export default function NotificationBadge({ read }) {
  return (
    <span
      className={`px-2 py-1 text-xs rounded border ${
        read ? "bg-gray-100 text-gray-600" : "bg-black text-white border-black"
      }`}
    >
      {read ? "Read" : "Unread"}
    </span>
  );
}
