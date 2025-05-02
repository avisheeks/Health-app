import React, { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { Box, Paper, Typography, CircularProgress, Alert } from '@mui/material';
import RegisterComponent, { RegisterFormData } from '../../components/auth/Register';
import { useAuth } from '../../context/AuthContext';
import { useNotification } from '../../context/NotificationContext';
import './Auth.css';
import { UserMetadata, Role } from '../../types/user';
import axios from 'axios';
import { supabase } from '../../config/supabase';

// Define interfaces for API data and errors
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
  const { signUp, loading, user, clearError } = useAuth();
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

    // If doctor, ensure location is selected
    if (formData.role === 'DOCTOR' && (!formData.latitude || !formData.longitude)) {
      setError('Please select your location on the map.');
      showNotification('Please select your location on the map.', 'error');
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);
      clearError();

      const fullName = `${formData.firstName} ${formData.lastName}`;
      
      // Create user data object for Supabase
      const metadata: UserMetadata = {
        full_name: fullName,
        firstName: formData.firstName,
        lastName: formData.lastName,
        role: formData.role as Role,
        // Add additional fields based on role
        ...(formData.role === 'DOCTOR' ? {
          specialization: '',
          licenseNumber: ''
        } : {})
      };

      console.log('Registering with metadata:', metadata); // Debug log

      // Sign up the user
      const result = await signUp(
        formData.email,
        formData.password,
        metadata
      );

      let userId = result?.user?.id;

      // If userId is not available, try to get it from the current session
      if (!userId) {
        const { data: { session } } = await supabase.auth.getSession();
        userId = session?.user?.id;
      }

      // If doctor, register in doctors table
      if (formData.role === 'DOCTOR' && userId) {
        try {
          await axios.post('http://localhost:8000/doctors/register', {
            id: userId,
            name: fullName,
            specialty: 'General', // You can update this to use a real specialty field
            latitude: formData.latitude,
            longitude: formData.longitude
          });
        } catch (err) {
          setError('Doctor registration failed. Please try again.');
          showNotification('Doctor registration failed. Please try again.', 'error');
          setIsSubmitting(false);
          return;
        }
      }

      if (result?.requiresEmailConfirmation) {
        showNotification(
          'Registration successful! Please check your email to confirm your account.',
          'success'
        );
      } else {
        showNotification('Registration successful!', 'success');
      }
      
      navigate('/login');
    } catch (err: unknown) {
      console.error('Registration error:', err);
      
      let errorMessage = 'Registration failed. Please try again.';
      
      if (isApiErrorResponse(err)) {
        errorMessage = err.message || err.response?.data?.message || 'Registration failed. Please try again.';
      } else if (err instanceof Error) {
        errorMessage = err.message;
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