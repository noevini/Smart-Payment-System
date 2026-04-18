import { NavLink } from "react-router-dom";

/**
 * Navigation links configuration.
 * Add new pages here — no need to touch AppLayout.
 */
const NAV_LINKS = [
  { to: "/dashboard", label: "Dashboard" },
  { to: "/payments", label: "Payments" },
  { to: "/transactions", label: "Transactions" },
  { to: "/customers", label: "Customers" },
  { to: "/reports", label: "Reports" },
  { to: "/notifications", label: "Notifications" },
  { to: "/analytics", label: "Analytics" },
  { to: "/insights", label: "Insights" },
  { to: "/predictions", label: "Predictions" },
];

/**
 * Sidebar component — handles all navigation links.
 * Extracted from AppLayout to keep layout and navigation concerns separate.
 */
export default function Sidebar() {
  return (
    <aside className="w-64 bg-slate-900 text-white p-4 space-y-2 flex flex-col">
      {/* Branding */}
      <div className="px-3 py-4 mb-4">
        <h1 className="text-xl font-bold tracking-tight">SmartPay</h1>
        <p className="text-xs text-slate-400 mt-1">Payment Tracking System</p>
      </div>

      {/* Navigation links */}
      <nav className="flex-1 space-y-1">
        {NAV_LINKS.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) =>
              `sidebar-link ${isActive ? "sidebar-link-active" : ""}`
            }
          >
            {link.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
