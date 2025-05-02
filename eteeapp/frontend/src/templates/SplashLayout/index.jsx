import React from "react";
import { Stack, Box } from "@mui/material";
import backgroundImage from "../../assets/login-bg.png";

const SplashLayout = ({ children }) => {
  return (
    <Stack direction="row" sx={{ height: "100vh", width: "100vw" }}>
      {/* Left side: Login form */}
      <Box
        sx={{
          flex: 1,
          backgroundColor: "#fff",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          p: 4,
        }}
      >
        {children}
      </Box>

      {/* Right side: Fixed image */}
      <Box
        sx={{
          flex: 1,
        }}
      >
        <Box
          component="img"
          src={backgroundImage}
          alt="Splash"
          sx={{
            width: "100%",
            height: "100%",
            objectFit: "cover",
          }}
        />
      </Box>
    </Stack>
  );
};

export default SplashLayout;
