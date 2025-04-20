import React, { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { Box, Paper, Typography, CircularProgress } from '@mui/material';
import LoginComponent, { LoginFormData } from '../../components/auth/Login';
import { useAuth } from '../../context/AuthContext';
import { useNotification } from '../../context/NotificationContext';
import './Auth.css';

// Type guard for API error responses
interface ApiErrorResponse {
  response?: {
    data?: {
      message?: string;
    };
  };
}

function isApiErrorResponse(err: unknown): err is ApiErrorResponse {
  return (
    typeof err === 'object' &&
    err !== null &&
    'response' in err &&
    typeof err.response === 'object' &&
    err.response !== null
  );
}

const Login: React.FC = () => {
  const { login, loading, user, clearError } = useAuth();
  const { showNotification } = useNotification();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  // If the user is already logged in, redirect to dashboard
  if (user) {
    return <Navigate to="/dashboard" replace />;
  }

  const handleLogin = async (formData: LoginFormData): Promise<void> => {
    try {
      setIsSubmitting(true);
      clearError();
      await login(formData.email, formData.password);
      showNotification('Login successful!', 'success');
      navigate('/dashboard');
    } catch (err: unknown) {
      console.error('Login error:', err);
      
      let errorMessage = 'Login failed. Please check your credentials.';
      
      if (isApiErrorResponse(err) && 
          err.response?.data?.message) {
        errorMessage = err.response.data.message;
      }
      
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
        {loading ? (
          <Box display="flex" justifyContent="center" p={4}>
            <CircularProgress />
          </Box>
        ) : (
          <LoginComponent onSubmit={handleLogin} isSubmitting={isSubmitting} />
        )}
      </Paper>
    </Box>
  );
};

export default Login; 