import { NavLink } from "react-router-dom";
import { getToken, decodeJwtPayload } from "../../auth/tokenStorage";

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

const OWNER_ONLY_LINKS = [
  { to: "/staff", label: "Staff" },
];

function getUserRole() {
  try {
    const token = getToken();
    if (!token) return null;
    return decodeJwtPayload(token)?.role ?? null;
  } catch {
    return null;
  }
}

export default function Sidebar() {
  const role = getUserRole();
  const isOwner = role === "OWNER";

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

        {isOwner ? (
          <>
            <div className="pt-3 pb-1 px-3">
              <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                Management
              </span>
            </div>
            {OWNER_ONLY_LINKS.map((link) => (
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
          </>
        ) : null}
      </nav>
    </aside>
  );
}
