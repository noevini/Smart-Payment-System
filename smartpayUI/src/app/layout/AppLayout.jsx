import { NavLink, Outlet } from "react-router-dom";
import Topbar from "./Topbar";

export default function AppLayout() {
  return (
    <div className="min-h-screen flex bg-slate-50">
      <aside className="w-64 bg-slate-900 text-white p-4 space-y-2">
        <div className="px-3 py-4 mb-4">
          <h1 className="text-xl font-bold tracking-tight">SmartPay</h1>
          <p className="text-xs text-slate-400 mt-1">Payment Tracking System</p>
        </div>

        <NavLink
          to="/dashboard"
          className={({ isActive }) =>
            `sidebar-link ${isActive ? "sidebar-link-active" : ""}`
          }
        >
          Dashboard
        </NavLink>

        <NavLink
          to="/payments"
          className={({ isActive }) =>
            `sidebar-link ${isActive ? "sidebar-link-active" : ""}`
          }
        >
          Payments
        </NavLink>

        <NavLink
          to="/customers"
          className={({ isActive }) =>
            `sidebar-link ${isActive ? "sidebar-link-active" : ""}`
          }
        >
          Customers
        </NavLink>

        <NavLink
          to="/reports"
          className={({ isActive }) =>
            `sidebar-link ${isActive ? "sidebar-link-active" : ""}`
          }
        >
          Reports
        </NavLink>

        <NavLink
          to="/notifications"
          className={({ isActive }) =>
            `sidebar-link ${isActive ? "sidebar-link-active" : ""}`
          }
        >
          Notifications
        </NavLink>
      </aside>

      <div className="flex-1 flex flex-col">
        <Topbar />
        <main className="flex-1 p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
