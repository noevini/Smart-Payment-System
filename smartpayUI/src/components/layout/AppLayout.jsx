import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";
import Topbar from "../../app/layout/Topbar";

/**
 * AppLayout — main application shell.
 * Responsible ONLY for the overall page structure:
 * - Sidebar (navigation)
 * - Topbar (business selector + logout)
 * - Main content area (rendered via <Outlet />)
 *
 * Navigation links live in Sidebar.jsx
 * Business switching and logout live in Topbar.jsx
 */
export default function AppLayout() {
  return (
    <div className="min-h-screen flex bg-slate-50">
      {/* Left sidebar — navigation */}
      <Sidebar />

      {/* Right side — topbar + page content */}
      <div className="flex-1 flex flex-col min-w-0">
        <Topbar />
        <main className="flex-1 p-6 overflow-auto">
          {/* Child routes render here */}
          <Outlet />
        </main>
      </div>
    </div>
  );
}
