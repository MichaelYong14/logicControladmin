import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { 
  Typography, 
  Button, 
  Stack, 
  Box,
  styled 
} from "@mui/material";
import MinimalLayout from "../../templates/MinimalLayout";
import backgroundImage from "../../assets/login-bg.png"; // Use the same background as login
import logo from "../../assets/logo.png";
import useResponseHandler from "../../utils/useResponseHandler";

// Styled components
const StartButton = styled(Button)(({ theme }) => ({
  backgroundColor: "#FFD700",
  color: "#000",
  fontWeight: "bold",
  borderRadius: "20px",
  padding: "12px 24px",
  fontSize: 18,
  textTransform: "none",
  width: 256,
  "&:hover": {
    backgroundColor: "#F0C800",
  }
}));

const Homepage = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState("Username");
  const { handleSuccess, handleError, snackbar } = useResponseHandler();
  
  useEffect(() => {
    const applicantId = localStorage.getItem("applicantId");
    if (!applicantId) {
      handleError("Please login to continue");
      navigate("/login");
    }
    // In a real app, you might fetch user data here based on applicantId
  }, [navigate, handleError]);

  const handleStartApplication = () => {
    navigate("/AppCoursePreference");
    handleSuccess("Navigated to course preference page!");
  };

  return (
    <MinimalLayout backgroundImage={backgroundImage}>
      <Stack alignItems="center" spacing={4} sx={{ position: "relative" }}>
        {/* Logo at the top */}
        <img src={logo} alt="Logo" />
        
        {/* Main content */}
        <Box 
          sx={{ 
            textAlign: "center", 
            backgroundColor: "rgba(255, 255, 255, 0.8)",
            borderRadius: 4,
            p: 4,
            maxWidth: 600,
            width: "100%"
          }}
        >
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            Hi, {username}. Your ETEEAP journey begins here!
          </Typography>
          
          <Stack 
            direction="column" 
            spacing={2} 
            alignItems="center" 
            sx={{ mt: 4 }}
          >
            <Typography variant="body1">Click here to</Typography>
            <StartButton 
              onClick={handleStartApplication}
              variant="contained"
            >
              Start your Application
            </StartButton>
          </Stack>
        </Box>
      </Stack>
      {snackbar}
    </MinimalLayout>
  );
};

export default Homepage;