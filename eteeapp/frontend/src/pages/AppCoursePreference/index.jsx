import React, { useState, useEffect } from "react";
import { 
  Typography, 
  Box, 
  Button, 
  TextField, 
  Grid, 
  Paper, 
  Stack,
  MenuItem,
  styled,
  List,
  ListItem,
  ListItemText,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Modal
} from "@mui/material";
import { UploadFile } from "@mui/icons-material";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import MinimalLayout from "../../templates/MinimalLayout";
import backgroundImage from "../../assets/login-bg.png";
import logo from "../../assets/logo.png";
import useResponseHandler from "../../utils/useResponseHandler";

// Styled components
const ApplicationPaper = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(4),
  borderRadius: 16,
  backgroundColor: "rgba(255, 255, 255, 0.9)",
  width: "100%",
  maxWidth: 800,
}));

const CourseButton = styled(Button)(({ theme }) => ({
  backgroundColor: "#800000",
  color: "white",
  borderRadius: 4,
  textTransform: "none",
  "&:hover": {
    backgroundColor: "#600000",
  }
}));

const SubmitButton = styled(Button)(({ theme }) => ({
  backgroundColor: "#800000",
  color: "white",
  borderRadius: 20,
  padding: "10px 40px",
  fontSize: 16,
  textTransform: "none",
  "&:hover": {
    backgroundColor: "#600000",
  }
}));

const UploadButton = styled(Button)(({ theme }) => ({
  backgroundColor: "#222222",
  color: "white",
  borderRadius: 4,
  textTransform: "none",
  "&:hover": {
    backgroundColor: "#000000",
  }
}));

const StyledTextField = styled(TextField)({
  backgroundColor: "#f5f5f5",
  borderRadius: "4px",
  "& .MuiOutlinedInput-root": {
    borderRadius: "4px",
  }
});

// Styled components for the success modal
const SuccessModal = styled(Modal)({
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
});

const SuccessModalContent = styled(Box)(({ theme }) => ({
  backgroundColor: "white",
  borderRadius: 16,
  boxShadow: theme.shadows[5],
  padding: theme.spacing(4),
  display: "flex",
  flexDirection: "column",
  alignItems: "center",
  width: "100%",
  maxWidth: 400,
  textAlign: "center",
}));

const TrackButton = styled(Button)(({ theme }) => ({
  backgroundColor: "#ffde00",
  color: "black",
  borderRadius: 20,
  padding: "10px 30px",
  fontSize: 16,
  fontWeight: "bold",
  textTransform: "none",
  "&:hover": {
    backgroundColor: "#e6c800",
  }
}));

const ApplicationForm = () => {
  const navigate = useNavigate();
  const { handleSuccess, handleError, snackbar } = useResponseHandler();
  const [applicantId, setApplicantId] = useState(null);
  const [applicationId, setApplicationId] = useState(null);
  const [userData, setUserData] = useState({
    name: "",
    email: "",
  });
  const [coursePreferences, setCoursePreferences] = useState([]);
  const [files, setFiles] = useState([]);
  const [availableCourses, setAvailableCourses] = useState([]);
  const [courseDialogOpen, setCourseDialogOpen] = useState(false);
  const [currentPriorityIndex, setCurrentPriorityIndex] = useState(null);
  const [selectedCourse, setSelectedCourse] = useState(null);
  // Add state for success modal
  const [successModalOpen, setSuccessModalOpen] = useState(false);

  const priorityOrders = ["FIRST", "SECOND", "THIRD"];

  useEffect(() => {
    const storedApplicantId = localStorage.getItem("applicantId");
    if (!storedApplicantId) {
      handleError("Please login to continue");
      navigate("/login");
      return;
    }

    setApplicantId(storedApplicantId);

    // Fetch applicant data
    fetchApplicantData(storedApplicantId);

    // Fetch available courses
    fetchCourses();
  }, [navigate, handleError]);

  const fetchApplicantData = async (id) => {
    try {
      // Fetch applicant profile
      const profileResponse = await axios.get(`http://localhost:8080/api/applicants/${id}`);
      
      // Set user data
      setUserData({
        name: `${profileResponse.data.firstName} ${profileResponse.data.middleInitial ? profileResponse.data.middleInitial + '.' : ''} ${profileResponse.data.lastName}`,
        email: profileResponse.data.email
      });
      
      // Check if applicant already has an application
      const applicationsResponse = await axios.get(`http://localhost:8080/api/applications/applicant/${id}`);
      
      if (applicationsResponse.data && applicationsResponse.data.length > 0) {
        const appId = applicationsResponse.data[0].applicationId;
        setApplicationId(appId);
        
        // Fetch course preferences for this application
        fetchCoursePreferences(appId);
      } else {
        // Create a new application for this applicant
        createNewApplication(id);
      }
    } catch (error) {
      console.error("Error fetching applicant data:", error);
      handleError("Failed to load applicant data");
    }
  };
  
  const createNewApplication = async (applicantId) => {
    try {
      const response = await axios.post("http://localhost:8080/api/applications", {
        applicantId: applicantId,
        status: "DRAFT"
      });
      
      setApplicationId(response.data.applicationId);
    } catch (error) {
      console.error("Error creating application:", error);
      handleError("Failed to create application");
    }
  };
  
  const fetchCourses = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/courses");
      setAvailableCourses(response.data);
    } catch (error) {
      console.error("Error fetching courses:", error);
      handleError("Failed to load available courses");
    }
  };
  
  const fetchCoursePreferences = async (appId) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/preferences/application/${appId}`);
      setCoursePreferences(response.data);
    } catch (error) {
      console.error("Error fetching course preferences:", error);
      // If no preferences yet, that's okay, we'll start with empty
      setCoursePreferences([]);
    }
  };

  const openCourseDialog = (priorityIndex) => {
    setCurrentPriorityIndex(priorityIndex);
    setCourseDialogOpen(true);
  };

  const handleCourseSelection = (course) => {
    setSelectedCourse(course);
  };

  const handleDialogClose = () => {
    setCourseDialogOpen(false);
    setSelectedCourse(null);
  };

  const handleDialogConfirm = async () => {
    if (!selectedCourse || currentPriorityIndex === null) {
      return;
    }

    try {
      // Check if we're updating an existing preference or creating a new one
      const existingPreference = coursePreferences.find(
        pref => pref.priorityOrder === priorityOrders[currentPriorityIndex]
      );

      if (existingPreference) {
        // Delete existing preference first
        await axios.delete(`http://localhost:8080/api/preferences/${existingPreference.preferenceId}`);
      }

      // Create new preference
      const newPreference = {
        application: { applicationId: applicationId },
        course: { courseId: selectedCourse.courseId },
        priorityOrder: priorityOrders[currentPriorityIndex]
      };

      const response = await axios.post("http://localhost:8080/api/preferences", newPreference);
      
      // Update local state with the new/updated preference
      const updatedPreferences = [...coursePreferences];
      
      // Remove the old preference if it existed
      const filteredPreferences = updatedPreferences.filter(
        pref => pref.priorityOrder !== priorityOrders[currentPriorityIndex]
      );
      
      // Add the new preference
      filteredPreferences.push(response.data);
      
      setCoursePreferences(filteredPreferences);
      handleSuccess("Course preference updated!");
    } catch (error) {
      console.error("Error saving course preference:", error);
      handleError("Failed to save course preference");
    }

    setCourseDialogOpen(false);
    setSelectedCourse(null);
  };

  const handleFileUpload = (event) => {
    const fileList = Array.from(event.target.files);
    setFiles([...files, ...fileList]);
    
    // In a real app, you would upload these files to your server
    // For example:
    /*
    const formData = new FormData();
    fileList.forEach(file => {
      formData.append('files', file);
    });
    formData.append('applicationId', applicationId);
    
    axios.post('http://localhost:8080/api/documents/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }).then(response => {
      handleSuccess("Files uploaded successfully!");
    }).catch(error => {
      handleError("Failed to upload files");
    });
    */
    
    handleSuccess("File uploaded successfully!");
  };

  const handleSubmit = () => {
    navigate("/ApplicationTrack");
  };

  const handleTrackApplication = () => {
    setSuccessModalOpen(false);
    navigate("/ApplicationTrack");
  };

  // Get the course name for a given priority
  const getCourseNameByPriority = (priority) => {
    const preference = coursePreferences.find(pref => pref.priorityOrder === priority);
    if (preference) {
      const course = availableCourses.find(c => c.courseId === preference.course.courseId);
      return course ? course.courseName : "Course not found";
    }
    return "";
  };

  return (
    <MinimalLayout backgroundImage={backgroundImage}>
      <Stack alignItems="center" spacing={2} sx={{ width: "100%" }}>
        <Box sx={{ display: "flex", justifyContent: "space-between", width: "100%", maxWidth: 800 }}>
          <img src={logo} alt="Logo" style={{ height: 40 }} />
          <Box sx={{ display: "flex", alignItems: "center" }}>
            <Typography variant="body2" color="text.secondary" sx={{ mr: 1 }}>
              {userData.email}
            </Typography>
            <Box sx={{ width: 32, height: 32, borderRadius: "50%", bgcolor: "#333" }} />
          </Box>
        </Box>
        
        <Typography variant="h5" fontWeight="bold" color="text.primary">
          Application
        </Typography>
        
        <ApplicationPaper elevation={3}>
          <Grid container spacing={4}>
            <Grid item xs={12} md={7}>
              <Stack spacing={3}>
                <Box>
                  <Typography variant="subtitle2" gutterBottom>Name:</Typography>
                  <Typography variant="body1">{userData.name}</Typography>
                </Box>
                
                <Box>
                  <Typography variant="subtitle2" gutterBottom>Email:</Typography>
                  <Typography variant="body1">{userData.email}</Typography>
                </Box>
                
                <Box>
                  <Typography variant="subtitle2" gutterBottom>Course Preference(s):</Typography>
                  <Stack spacing={2} sx={{ mt: 1 }}>
                    {priorityOrders.map((priority, index) => (
                      <Box key={index} sx={{ display: "flex", alignItems: "center" }}>
                        <StyledTextField
                          size="small"
                          value={getCourseNameByPriority(priority)}
                          placeholder={`Course ${index + 1}`}
                          variant="outlined"
                          sx={{ width: "60%", mr: 1 }}
                          InputProps={{
                            readOnly: true,
                          }}
                        />
                        <CourseButton 
                          size="small"
                          onClick={() => openCourseDialog(index)}
                        >
                          Choose
                        </CourseButton>
                      </Box>
                    ))}
                  </Stack>
                </Box>
              </Stack>
            </Grid>
            
            <Grid item xs={12} md={5}>
              <Stack spacing={3}>
                <Typography variant="subtitle1">Upload Documents</Typography>
                
                <UploadButton
                  variant="contained"
                  component="label"
                  startIcon={<UploadFile />}
                  size="small"
                >
                  Select File
                  <input
                    type="file"
                    hidden
                    onChange={handleFileUpload}
                    multiple
                  />
                </UploadButton>
                
                <Box>
                  <Typography variant="subtitle2" gutterBottom>Files Uploaded</Typography>
                  {files.length > 0 ? (
                    <List dense sx={{ bgcolor: "#f5f5f5", borderRadius: 1 }}>
                      {files.map((file, index) => (
                        <ListItem key={index}>
                          <ListItemText primary={file.name} />
                        </ListItem>
                      ))}
                    </List>
                  ) : (
                    <Typography variant="body2" color="text.secondary">
                      No files uploaded yet
                    </Typography>
                  )}
                </Box>
              </Stack>
            </Grid>
          </Grid>
          
          <Box sx={{ display: "flex", justifyContent: "center", mt: 4 }}>
            <SubmitButton 
              variant="contained"
              onClick={handleSubmit}
            >
              Submit
            </SubmitButton>
          </Box>
        </ApplicationPaper>
      </Stack>

      {/* Course Selection Dialog */}
      <Dialog 
        open={courseDialogOpen} 
        onClose={handleDialogClose}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Select Course</DialogTitle>
        <DialogContent>
          <List>
            {availableCourses.map((course) => (
              <ListItem 
                button 
                key={course.courseId}
                onClick={() => handleCourseSelection(course)}
                selected={selectedCourse && selectedCourse.courseId === course.courseId}
                sx={{
                  bgcolor: selectedCourse && selectedCourse.courseId === course.courseId ? '#f0f0f0' : 'transparent'
                }}
              >
                <ListItemText primary={course.courseName} />
              </ListItem>
            ))}
          </List>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDialogClose}>Cancel</Button>
          <Button 
            onClick={handleDialogConfirm}
            disabled={!selectedCourse}
            sx={{ color: "#800000" }}
          >
            Confirm
          </Button>
        </DialogActions>
      </Dialog>
      
      {/* Success Modal */}
      <SuccessModal
        open={successModalOpen}
        aria-labelledby="success-modal-title"
        aria-describedby="success-modal-description"
      >
        <SuccessModalContent>
          <img src={logo} alt="Logo" style={{ height: 40, marginBottom: 16 }} />
          <Typography variant="h6" id="success-modal-title" sx={{ mb: 2 }}>
            Hi, {userData.name.split(' ')[0]}. You already submitted an application.
          </Typography>
          <Typography variant="body2" id="success-modal-description" sx={{ mb: 3 }}>
            Click here to
          </Typography>
          <TrackButton onClick={handleTrackApplication}>
            Track your Application
          </TrackButton>
        </SuccessModalContent>
      </SuccessModal>
      
      {snackbar}
    </MinimalLayout>
  );
};

export default ApplicationForm;