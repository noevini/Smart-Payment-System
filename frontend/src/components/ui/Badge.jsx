export default function Badge({ status }) {
  const styles = {
    PAID: "bg-emerald-100 text-emerald-700 border border-emerald-200",
    PENDING: "bg-amber-100 text-amber-700 border border-amber-200",
    OVERDUE: "bg-red-100 text-red-700 border border-red-200",
    CANCELED: "bg-slate-100 text-slate-600 border border-slate-200",
  };

  return (
    <span
      className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-medium ${
        styles[status] ?? "bg-slate-100 text-slate-700 border border-slate-200"
      }`}
    >
      {status}
    </span>
  );
}
