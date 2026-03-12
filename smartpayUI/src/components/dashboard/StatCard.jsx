export default function StatCard({ title, value }) {
  return (
    <div className="card-surface card-content">
      <div className="text-sm font-medium text-slate-500">{title}</div>
      <div className="mt-2 text-3xl font-bold tracking-tight text-slate-900">
        {value}
      </div>
    </div>
  );
}
