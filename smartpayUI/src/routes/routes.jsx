import { Navigate } from "react-router-dom";
import AppLayout from "../components/layout/AppLayout";
import RequireAuth from "./RequireAuth";

import Login from "../pages/Login";
import Register from "../pages/Register";
import Dashboard from "../pages/Dashboard";
import Payments from "../pages/Payments";
import Customers from "../pages/Customers";
import Notifications from "../pages/Notifications";
import Reports from "../pages/Reports";
import BusinessSetup from "../pages/BusinessSetup";
import Insights from "../pages/Insights";
import Analytics from "../pages/Analytics";
import Predictions from "../pages/Predictions";
import Transactions from "../pages/Transactions";

export const routes = [
  { path: "/login", element: <Login /> },
  { path: "/register", element: <Register /> },

  {
    path: "/business-setup",
    element: (
      <RequireAuth>
        <BusinessSetup />
      </RequireAuth>
    ),
  },

  {
    path: "/",
    element: (
      <RequireAuth>
        <AppLayout />
      </RequireAuth>
    ),
    children: [
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: "dashboard", element: <Dashboard /> },
      { path: "payments", element: <Payments /> },
      { path: "transactions", element: <Transactions /> },
      { path: "customers", element: <Customers /> },
      { path: "business-setup", element: <BusinessSetup /> },
      { path: "notifications", element: <Notifications /> },
      { path: "reports", element: <Reports /> },
      { path: "insights", element: <Insights /> },
      { path: "analytics", element: <Analytics /> },
      { path: "predictions", element: <Predictions /> },
    ],
  },
];
