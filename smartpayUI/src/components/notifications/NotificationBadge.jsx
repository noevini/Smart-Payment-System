export default function NotificationBadge({ read }) {
  return (
    <span
      className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-medium ${
        read
          ? "border border-slate-200 bg-slate-100 text-slate-600"
          : "border border-emerald-200 bg-emerald-100 text-emerald-700"
      }`}
    >
      {read ? "Read" : "Unread"}
    </span>
  );
}
