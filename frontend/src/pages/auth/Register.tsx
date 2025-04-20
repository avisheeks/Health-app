import React, { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { Box, Paper, Typography, CircularProgress, Alert } from '@mui/material';
import RegisterComponent, { RegisterFormData } from '../../components/auth/Register';
import { useAuth } from '../../context/AuthContext';
import { useNotification } from '../../context/NotificationContext';
import './Auth.css';

// Define interfaces for API data and errors
interface UserData {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  roles: string[];
}

// Type guard for API error responses
interface ApiErrorResponse {
  response?: {
    data?: {
      message?: string;
    };
    status?: number;
  };
  message?: string;
}

function isApiErrorResponse(err: unknown): err is ApiErrorResponse {
  return (
    typeof err === 'object' &&
    err !== null &&
    ('response' in err || 'message' in err)
  );
}

const Register: React.FC = () => {
  const { register, loading, user, clearError } = useAuth();
  const { showNotification } = useNotification();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  // If the user is already logged in, redirect to dashboard
  if (user) {
    return <Navigate to="/dashboard" replace />;
  }

  const handleRegister = async (formData: RegisterFormData): Promise<void> => {
    // Validate passwords match
    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      showNotification('Passwords do not match', 'error');
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);
      clearError();

      // Create user data object
      const userData: UserData = {
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        password: formData.password,
        roles: ['PATIENT'] // Default role for new registrations
      };

      await register(userData);
      showNotification('Registration successful! Welcome to Hospital Management System.', 'success');
      navigate('/dashboard');
    } catch (err: unknown) {
      console.error('Registration error:', err);
      
      let errorMessage = 'Registration failed. Please try again.';
      
      if (isApiErrorResponse(err)) {
        if (err.response?.status === 500) {
          errorMessage = 'Server error. The database might be unavailable. Please try again later or contact support.';
        } else if (err.response?.data?.message) {
          errorMessage = err.response.data.message;
        } else if (err.message) {
          errorMessage = err.message;
        }
      }
      
      setError(errorMessage);
      showNotification(errorMessage, 'error');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Box className="auth-page">
      <Paper elevation={3} className="auth-paper">
        <Typography variant="h4" className="auth-page-title">
          Hospital Management System
        </Typography>
        
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}
        
        {loading ? (
          <Box display="flex" justifyContent="center" p={4}>
            <CircularProgress />
          </Box>
        ) : (
          <RegisterComponent onSubmit={handleRegister} isSubmitting={isSubmitting} />
        )}
      </Paper>
    </Box>
  );
};

export default Register; 