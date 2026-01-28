import { NavLink } from "react-router-dom";

const links = [
  { to: "/dashboard", label: "Dashboard" },
  { to: "/payments", label: "Payments" },
  { to: "/customers", label: "Customers" },
];

export default function Sidebar() {
  return (
    <aside className="w-64 bg-white border-r p-4">
      <h1 className="text-lg font-bold">SmartPay</h1>
      <p className="text-sm text-gray-500 mb-4">IPD demo</p>

      <nav className="space-y-1">
        {links.map((l) => (
          <NavLink
            key={l.to}
            to={l.to}
            className={({ isActive }) =>
              `block px-3 py-2 rounded text-sm ${
                isActive ? "bg-gray-200 font-medium" : "hover:bg-gray-100"
              }`
            }
          >
            {l.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
