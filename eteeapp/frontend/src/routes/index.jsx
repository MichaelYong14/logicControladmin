// src/routes/AppRoutes.jsx
import React from "react";
import { useRoutes } from "react-router-dom";
import LoginPage from "../pages/LoginPage";
import SetUpProfilePage from "../pages/SetUpProfile";
import Homepage from "../pages/HomePage";
import ProtectedRoute from "./ProtectedRoutes";
import ApplicationTrack from "../pages/ApplicationTrack";
import ApplicationForm from "../pages/AppCoursePreference";
import ProgramShowcase from "../pages/ProgramShowcase";

const AppRoutes = () => {
  return useRoutes([
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
    {
      path: "/program-showcase",
      element: (
        <ProtectedRoute>
          <ProgramShowcase />
        </ProtectedRoute>
      ),
    },
    {
      path: "/AppCoursePreference",
      element: (
        <ProtectedRoute>
          <ApplicationForm />
        </ProtectedRoute>
      ),
    },
    {
      path: "/ApplicationTrack",
      element: (
        <ProtectedRoute>
          <ApplicationTrack />
        </ProtectedRoute>
      ),
    },
    // Add a fallback route if needed
    // {
    //   path: "*",
    //   element: <NotFoundPage />,
    // },
  ]);
};

export default AppRoutes;
