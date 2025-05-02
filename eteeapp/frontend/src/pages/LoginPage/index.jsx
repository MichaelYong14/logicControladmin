import React, { useState } from "react";
import LoginForm from "../../components/Login/LoginForm";
import logo from "../../assets/logo.png";
import { Stack, Box } from "@mui/material";
import useResponseHandler from "../../utils/useResponseHandler";
import SplashLayout from "../../templates/SplashLayout";

const LoginPage = () => {
  const [view, setView] = useState("login"); // login | signup | setupProfile
  const { handleSuccess, handleError, snackbar } = useResponseHandler();

  return (
    <SplashLayout>
      {/* Logo at top-left */}
      <Box
        sx={{
          position: "absolute",
          top: 32,
          left: 32,
          width: 100, // adjust size
        }}
      >
        <img src={logo} alt="Logo" style={{ width: "100%" }} />
      </Box>

      {/* Centered content */}
      <Stack alignItems="center" spacing={4} maxWidth={"60%"}>
        {view === "login" && (
          <LoginForm
            setView={setView}
            handleSuccess={handleSuccess}
            handleError={handleError}
          />
        )}
        {view === "signup" && (
          <LoginForm
            formType="signup"
            setView={setView}
            handleSuccess={handleSuccess}
            handleError={handleError}
          />
        )}
      </Stack>

      {snackbar}
    </SplashLayout>
  );
};

export default LoginPage;
