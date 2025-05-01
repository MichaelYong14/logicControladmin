// src/routes/AppRoutes.jsx
import React from "react";
import { useRoutes } from "react-router-dom";
import LoginPage from "../pages/LoginPage";
import SetUpProfilePage from "../pages/SetUpProfile";
import Homepage from "../pages/HomePage";
import ProtectedRoute from "./ProtectedRoutes";

const AppRoutes = () => {
  const routes = useRoutes([
    {
      path: "/login",
      element: <LoginPage />,
    },
    {
      path: "/setup-profile",
      element: (
        <ProtectedRoute>
          <SetUpProfilePage />
        </ProtectedRoute>
      ),
    },
    {
      path: "/homepage",
      element: (
        <ProtectedRoute>
          <Homepage />
        </ProtectedRoute>
      ),
    },
    // {
    //   path: "*",
    //   element: <NotFoundPage />,
    // },
  ]);

  return routes;
};

export default AppRoutes;
