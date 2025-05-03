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

const ShowcaseButton = styled(Button)(({ theme }) => ({
  backgroundColor: "#1976D2",
  color: "#fff",
  fontWeight: "bold",
  borderRadius: "20px",
  padding: "12px 24px",
  fontSize: 16,
  textTransform: "none",
  width: 256,
  "&:hover": {
    backgroundColor: "#115293",
  }
}));

const Homepage = () => {
  const navigate = useNavigate();
  const [userFullName, setUserFullName] = useState("User");
  const { handleSuccess, handleError, snackbar } = useResponseHandler();
  
  useEffect(() => {
    const applicantId = localStorage.getItem("applicantId");
    if (!applicantId) {
      handleError("Please login to continue");
      navigate("/login");
      return;
    }

    // Fetch user data using the ApplicantController endpoint
    const fetchUserData = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/applicants/${applicantId}`);
        if (!response.ok) {
          throw new Error("Failed to fetch user data");
        }
        const data = await response.json();
        setUserFullName(`${data.firstName} ${data.lastName}`);
      } catch (error) {
        handleError("Failed to fetch user data");
      }
    };

    fetchUserData();
  }, [navigate, handleError]);

  const handleStartApplication = () => {
    navigate("/AppCoursePreference");
    handleSuccess("Navigated to course preference page!");
  };

  const handleProgramShowcase = () => {
    navigate("/program-showcase");
    handleSuccess("Viewing program showcase!");
  };

  return (
    <MinimalLayout backgroundImage={backgroundImage}>
      <Stack alignItems="center" spacing={4} sx={{ position: "relative" }}>
        {/* Logo */}
        <img src={logo} alt="Logo" />
        
        {/* Welcome Section */}
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
            Hi, {userFullName}. Your ETEEAP journey begins here!
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

        {/* Program Showcase Section */}
        <Box 
          sx={{ 
            textAlign: "center",
            backgroundColor: "rgba(25, 118, 210, 0.2)",
            borderRadius: 4,
            p: 4,
            maxWidth: 600,
            width: "100%"
          }}
        >
          <Typography variant="h5" fontWeight="bold" gutterBottom color="black">
            🎓 Explore Our Program Showcase
          </Typography>
          <Typography variant="body2" gutterBottom>
            Learn more about ETEEAP and how it helps working professionals earn a degree through competency-based education.
          </Typography>
          <ShowcaseButton 
            onClick={handleProgramShowcase}
            variant="contained"
          >
            View Program Showcase
          </ShowcaseButton>
        </Box>
      </Stack>
      {snackbar}
    </MinimalLayout>
  );
};

export default Homepage;
