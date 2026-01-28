import { Navigate } from "react-router-dom";
import AppLayout from "./layout/AppLayout";

import Dashboard from "../pages/Dashboard";
import Payments from "../pages/Payments";
import Customers from "../pages/Customers";

export const routes = [
  {
    path: "/",
    element: <AppLayout />,
    children: [
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: "dashboard", element: <Dashboard /> },
      { path: "payments", element: <Payments /> },
      { path: "customers", element: <Customers /> },
    ],
  },
];
