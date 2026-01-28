import { Navigate } from "react-router-dom";
import AppLayout from "./layout/AppLayout";
import RequireAuth from "./RequireAuth";

import Login from "../pages/Login";
import Register from "../pages/Register";
import Dashboard from "../pages/Dashboard";
import Payments from "../pages/Payments";
import Customers from "../pages/Customers";

export const routes = [
  { path: "/login", element: <Login /> },
  { path: "/register", element: <Register /> },

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
      { path: "customers", element: <Customers /> },
    ],
  },
];
