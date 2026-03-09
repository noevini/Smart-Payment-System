export default function Card({ title, value, subtitle }) {
  return (
    <div className="bg-white border rounded p-4">
      <div className="text-sm text-gray-500">{title}</div>
      <div className="text-2xl font-bold mt-1">{value}</div>
      {subtitle ? (
        <div className="text-xs text-gray-500 mt-1">{subtitle}</div>
      ) : null}
    </div>
  );
}
