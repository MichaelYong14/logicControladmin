import React, { useState } from "react";
import axios from "axios"; // make sure to import this at the top
import { useNavigate } from "react-router-dom"; // Add this at the top

import {
  TextField,
  Button,
  Typography,
  Link,
  Paper,
  Stack,
  ToggleButtonGroup,
  ToggleButton,
  styled,
} from "@mui/material";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import useResponseHandler from "../../../utils/useResponseHandler";

const getValidationSchema = (formType) =>
  yup.object().shape({
    email: yup.string().email("Invalid email").required("Email is required"),
    password: yup
      .string()
      .min(8, "Minimum 8 characters")
      .required("Password is required"),
    ...(formType === "signup" && {
      reEnterPassword: yup
        .string()
        .oneOf([yup.ref("password")], "Passwords must match")
        .required("Please re-enter password"),
    }),
  });

const LoginForm = ({
  formType = "login",
  setView,
  handleSuccess,
  handleError,
}) => {
  const [currentFormType, setCurrentFormType] = useState(formType);
  const schema = getValidationSchema(currentFormType);
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(schema),
  });

  const onSubmit = async (data) => {
    if (currentFormType === "signup") {
      try {
        const response = await axios.post(
          "http://localhost:8080/api/applicants/register",
          {
            email: data.email,
            password: data.password,
          }
        );

        console.log("Signup response:", response.data);
        handleSuccess("Successfully signed up! Please complete your profile.");
        localStorage.setItem("applicantId", response.data.applicantId);
        navigate("/setup-profile", {
          state: {
            applicantId: response.data.applicantId,
          },
        });
      } catch (error) {
        console.error("Signup error:", error);
        handleError("Signup failed. Please try again.");
      }
    } else {
      try {
        const response = await axios.post(
          "http://localhost:8080/api/applicants/login",
          {
            email: data.email,
            password: data.password,
          },
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        );

        localStorage.setItem("applicantId", response.data.applicantId);
        console.log("Login response:", response.data);
        handleSuccess("Login successful!");
        navigate("/homepage");
      } catch (error) {
        console.error("Login error:", error.response?.data || error.message);
        const errorMessage = error.response?.data || error.message;
        handleError(errorMessage);
      }
    }
  };

  const handleToggle = (event, newFormType) => {
    if (newFormType) {
      setCurrentFormType(newFormType);
    }
  };

  return (
    <>
      {" "}
      <StyledPaper elevation={6}>
        <Typography variant="h5" textAlign="center" fontWeight="bold">
          {currentFormType === "login" ? "Login Form" : "Signup Form"}
        </Typography>

        <Stack direction="row" justifyContent="center">
          <ToggleButtonGroup
            value={currentFormType}
            exclusive
            onChange={handleToggle}
            aria-label="Login or Signup"
          >
            <StyledToggleButton value="login">Login</StyledToggleButton>
            <StyledToggleButton value="signup">Signup</StyledToggleButton>
          </ToggleButtonGroup>
        </Stack>
        <Stack gap={2}>
          <form onSubmit={handleSubmit(onSubmit)} noValidate>
            <StyledTextField
              type="email"
              fullWidth
              placeholder="Enter your email"
              variant="outlined"
              size="small"
              {...register("email")}
              error={!!errors.email}
              helperText={errors.email?.message}
            />

            <StyledTextField
              type="password"
              fullWidth
              placeholder="Enter your password"
              variant="outlined"
              size="small"
              {...register("password")}
              error={!!errors.password}
              helperText={errors.password?.message}
            />

            {currentFormType === "signup" && (
              <StyledTextField
                type="password"
                placeholder="Re-enter your password"
                variant="outlined"
                size="small"
                fullWidth
                {...register("reEnterPassword")}
                error={!!errors.reEnterPassword}
                helperText={errors.reEnterPassword?.message}
              />
            )}

            <Stack direction="row" justifyContent="flex-start">
              {currentFormType === "login" && (
                <Link href="#" variant="body2" sx={{ color: "black" }}>
                  Forgot password?
                </Link>
              )}
            </Stack>

            <Button
              type="submit"
              variant="contained"
              fullWidth
              sx={{ backgroundColor: "#800000", borderRadius: "20px" }}
            >
              {currentFormType === "login" ? "Login" : "Signup"}
            </Button>
          </form>
        </Stack>
      </StyledPaper>
    </>
  );
};

export const StyledTextField = styled(TextField)({
  marginBottom: 8,
  backgroundColor: "#D9D9D9",
  borderRadius: "12px",
  "& .MuiOutlinedInput-root": {
    borderRadius: "12px",
  },
  "& .MuiOutlinedInput-notchedOutline": {
    borderRadius: "12px",
  },
});

export const StyledToggleButton = styled(ToggleButton)(({ theme }) => ({
  backgroundColor: "#f5f5f5",
  color: "black",
  "&.Mui-selected": {
    backgroundColor: "#800000",
    color: "white",
  },
  "&:hover": {
    backgroundColor: "#800000",
    color: "white",
  },
}));

export const StyledPaper = styled(Paper)({
  padding: 32,
  width: "100%",
  maxWidth: 500,
  display: "flex",
  flexDirection: "column",
  gap: 16,
  borderRadius: 16,
  background:
    "linear-gradient(145deg, rgba(255, 255, 255, 0.11), rgba(128, 128, 128, 0.6))",
  boxShadow: "inset 0px 0px 10px rgba(255, 255, 255, 0.5)",
});

export default LoginForm;
