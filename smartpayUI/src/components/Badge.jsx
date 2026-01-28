const styles = {
  PAID: "bg-green-100 text-green-700",
  PENDING: "bg-yellow-100 text-yellow-700",
  OVERDUE: "bg-red-100 text-red-700",
};

export default function Badge({ status }) {
  const cls = styles[status] ?? "bg-gray-100 text-gray-700";
  return (
    <span className={`px-2 py-1 rounded text-xs font-medium ${cls}`}>
      {status}
    </span>
  );
}
