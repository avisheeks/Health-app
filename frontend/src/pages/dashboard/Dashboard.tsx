import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Grid,
  Paper,
  Typography,
  Button,
  Card,
  CardContent,
  List,
  ListItem,
  ListItemText,
  Divider,
  Box,
  CircularProgress
} from '@mui/material';
import {
  Today as TodayIcon,
  AccessTime as AccessTimeIcon,
  Notifications as NotificationsIcon,
  Favorite as FavoriteIcon,
  Message as MessageIcon
} from '@mui/icons-material';
import { useQuery } from 'react-query';
import { useAuth } from '../../context/AuthContext';
import axios from 'axios';
import './Dashboard.css';

// Define interfaces for the data types
interface Appointment {
  id: string;
  title: string;
  startTime: string;
  endTime: string;
  status: string;
  type: string;
  doctor: {
    id: string;
    name: string;
    specialty: string;
  };
}

interface Message {
  id: string;
  sender: string;
  content: string;
  timestamp: string;
  read: boolean;
}

interface Notification {
  id: string;
  title: string;
  message: string;
  timestamp: string;
  read: boolean;
  type: string;
}

interface HealthMetrics {
  HEART_RATE?: number;
  BLOOD_PRESSURE_SYSTOLIC?: number;
  BLOOD_PRESSURE_DIASTOLIC?: number;
  OXYGEN_LEVEL?: number;
  TEMPERATURE?: number;
}

const Dashboard = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const isPatient = user?.roles?.includes('PATIENT');
  const isDoctor = user?.roles?.includes('DOCTOR');
  const isAdmin = user?.roles?.includes('ADMIN');

  // Fetch upcoming appointments
  const { data: appointments, isLoading: appointmentsLoading } = useQuery<Appointment[]>(
    'upcomingAppointments',
    async () => {
      const response = await axios.get('/api/appointments/upcoming');
      return response.data;
    },
    { enabled: !!user }
  );

  // Fetch notifications
  const { data: notifications, isLoading: notificationsLoading } = useQuery<Notification[]>(
    'notifications',
    async () => {
      const response = await axios.get('/api/notifications');
      return response.data;
    },
    { enabled: !!user }
  );

  // Fetch messages
  const { data: messages, isLoading: messagesLoading } = useQuery<Message[]>(
    'recentMessages',
    async () => {
      const response = await axios.get('/api/messages/recent');
      return response.data;
    },
    { enabled: !!user }
  );

  // Fetch health metrics if user is a patient
  const { data: healthMetrics, isLoading: healthLoading } = useQuery<HealthMetrics>(
    'healthMetrics',
    async () => {
      const response = await axios.get('/api/health-metrics/recent');
      return response.data;
    },
    { enabled: !!user && isPatient }
  );

  return (
    <div className="dashboard-container">
      <Typography variant="h4" className="dashboard-title">
        Welcome, {user?.firstName || 'User'}
      </Typography>

      <Grid container spacing={4}>
        {/* Upcoming Appointments */}
        <Grid item xs={12} md={6}>
          <Paper elevation={2} className="dashboard-card">
            <Box className="card-header">
              <TodayIcon />
              <Typography variant="h6">Upcoming Appointments</Typography>
            </Box>
            <Divider />
            {appointmentsLoading ? (
              <Box display="flex" justifyContent="center" p={3}>
                <CircularProgress />
              </Box>
            ) : appointments && appointments.length > 0 ? (
              <List>
                {appointments.slice(0, 3).map((appointment) => (
                  <ListItem key={appointment.id} button onClick={() => navigate(`/appointments/${appointment.id}`)}>
                    <ListItemText
                      primary={appointment.title}
                      secondary={
                        <>
                          <AccessTimeIcon fontSize="small" style={{ verticalAlign: 'middle', marginRight: '5px' }} />
                          {new Date(appointment.startTime).toLocaleString()}
                        </>
                      }
                    />
                  </ListItem>
                ))}
              </List>
            ) : (
              <Box p={3} textAlign="center">
                <Typography>No upcoming appointments</Typography>
              </Box>
            )}
            <Box p={1} textAlign="center">
              <Button 
                variant="outlined" 
                color="primary" 
                onClick={() => navigate('/appointments')}
              >
                View All
              </Button>
              {isPatient && (
                <Button 
                  variant="contained" 
                  color="primary" 
                  onClick={() => navigate('/appointments/new')}
                  style={{ marginLeft: '10px' }}
                >
                  Book New
                </Button>
              )}
            </Box>
          </Paper>
        </Grid>

        {/* Health Metrics for Patients */}
        {isPatient && (
          <Grid item xs={12} md={6}>
            <Paper elevation={2} className="dashboard-card">
              <Box className="card-header">
                <FavoriteIcon />
                <Typography variant="h6">Health Metrics</Typography>
              </Box>
              <Divider />
              {healthLoading ? (
                <Box display="flex" justifyContent="center" p={3}>
                  <CircularProgress />
                </Box>
              ) : healthMetrics ? (
                <Box p={2}>
                  <Grid container spacing={2}>
                    <Grid item xs={6}>
                      <Card>
                        <CardContent>
                          <Typography color="textSecondary" gutterBottom>
                            Heart Rate
                          </Typography>
                          <Typography variant="h5">
                            {healthMetrics.HEART_RATE || '--'} BPM
                          </Typography>
                        </CardContent>
                      </Card>
                    </Grid>
                    <Grid item xs={6}>
                      <Card>
                        <CardContent>
                          <Typography color="textSecondary" gutterBottom>
                            Blood Pressure
                          </Typography>
                          <Typography variant="h5">
                            {healthMetrics.BLOOD_PRESSURE_SYSTOLIC || '--'}/{healthMetrics.BLOOD_PRESSURE_DIASTOLIC || '--'} mmHg
                          </Typography>
                        </CardContent>
                      </Card>
                    </Grid>
                    <Grid item xs={6}>
                      <Card>
                        <CardContent>
                          <Typography color="textSecondary" gutterBottom>
                            Oxygen Level
                          </Typography>
                          <Typography variant="h5">
                            {healthMetrics.OXYGEN_LEVEL || '--'}%
                          </Typography>
                        </CardContent>
                      </Card>
                    </Grid>
                    <Grid item xs={6}>
                      <Card>
                        <CardContent>
                          <Typography color="textSecondary" gutterBottom>
                            Temperature
                          </Typography>
                          <Typography variant="h5">
                            {healthMetrics.TEMPERATURE || '--'} °C
                          </Typography>
                        </CardContent>
                      </Card>
                    </Grid>
                  </Grid>
                </Box>
              ) : (
                <Box p={3} textAlign="center">
                  <Typography>No health data available</Typography>
                </Box>
              )}
              <Box p={1} textAlign="center">
                <Button 
                  variant="outlined" 
                  color="primary" 
                  onClick={() => navigate('/health-metrics')}
                >
                  View Details
                </Button>
                <Button 
                  variant="contained" 
                  color="primary" 
                  onClick={() => navigate('/health-metrics/add')}
                  style={{ marginLeft: '10px' }}
                >
                  Add Data
                </Button>
              </Box>
            </Paper>
          </Grid>
        )}

        {/* Doctor's Patient List - Only for doctors */}
        {isDoctor && (
          <Grid item xs={12} md={6}>
            <Paper elevation={2} className="dashboard-card">
              <Box className="card-header">
                <FavoriteIcon />
                <Typography variant="h6">My Patients</Typography>
              </Box>
              <Divider />
              <List>
                <ListItem button onClick={() => navigate('/patients/1')}>
                  <ListItemText primary="John Doe" secondary="Last visit: 3 days ago" />
                </ListItem>
                <ListItem button onClick={() => navigate('/patients/2')}>
                  <ListItemText primary="Jane Smith" secondary="Last visit: 1 week ago" />
                </ListItem>
                <ListItem button onClick={() => navigate('/patients/3')}>
                  <ListItemText primary="Robert Johnson" secondary="Last visit: Yesterday" />
                </ListItem>
              </List>
              <Box p={1} textAlign="center">
                <Button 
                  variant="outlined" 
                  color="primary" 
                  onClick={() => navigate('/patients')}
                >
                  View All Patients
                </Button>
              </Box>
            </Paper>
          </Grid>
        )}

        {/* Admin Statistics - Only for admins */}
        {isAdmin && (
          <Grid item xs={12} md={6}>
            <Paper elevation={2} className="dashboard-card">
              <Box className="card-header">
                <NotificationsIcon />
                <Typography variant="h6">System Statistics</Typography>
              </Box>
              <Divider />
              <Box p={2}>
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <Card>
                      <CardContent>
                        <Typography color="textSecondary" gutterBottom>
                          Total Users
                        </Typography>
                        <Typography variant="h5">
                          247
                        </Typography>
                      </CardContent>
                    </Card>
                  </Grid>
                  <Grid item xs={6}>
                    <Card>
                      <CardContent>
                        <Typography color="textSecondary" gutterBottom>
                          Total Appointments
                        </Typography>
                        <Typography variant="h5">
                          352
                        </Typography>
                      </CardContent>
                    </Card>
                  </Grid>
                  <Grid item xs={6}>
                    <Card>
                      <CardContent>
                        <Typography color="textSecondary" gutterBottom>
                          Active Doctors
                        </Typography>
                        <Typography variant="h5">
                          18
                        </Typography>
                      </CardContent>
                    </Card>
                  </Grid>
                  <Grid item xs={6}>
                    <Card>
                      <CardContent>
                        <Typography color="textSecondary" gutterBottom>
                          System Status
                        </Typography>
                        <Typography variant="h5" style={{ color: '#4caf50' }}>
                          Healthy
                        </Typography>
                      </CardContent>
                    </Card>
                  </Grid>
                </Grid>
              </Box>
              <Box p={1} textAlign="center">
                <Button 
                  variant="outlined" 
                  color="primary" 
                  onClick={() => navigate('/admin/dashboard')}
                >
                  Admin Dashboard
                </Button>
              </Box>
            </Paper>
          </Grid>
        )}

        {/* Recent Messages */}
        <Grid item xs={12} md={6}>
          <Paper elevation={2} className="dashboard-card">
            <Box className="card-header">
              <MessageIcon />
              <Typography variant="h6">Recent Messages</Typography>
            </Box>
            <Divider />
            {messagesLoading ? (
              <Box display="flex" justifyContent="center" p={3}>
                <CircularProgress />
              </Box>
            ) : messages && messages.length > 0 ? (
              <List>
                {messages.slice(0, 3).map((message) => (
                  <ListItem key={message.id} button onClick={() => navigate('/messages')}>
                    <ListItemText
                      primary={message.sender}
                      secondary={
                        <>
                          {message.content.substring(0, 50)}
                          {message.content.length > 50 ? '...' : ''}
                          <Typography variant="caption" display="block" color="textSecondary">
                            {new Date(message.timestamp).toLocaleString()}
                          </Typography>
                        </>
                      }
                    />
                  </ListItem>
                ))}
              </List>
            ) : (
              <Box p={3} textAlign="center">
                <Typography>No recent messages</Typography>
              </Box>
            )}
            <Box p={1} textAlign="center">
              <Button 
                variant="outlined" 
                color="primary" 
                onClick={() => navigate('/messages')}
              >
                View All Messages
              </Button>
            </Box>
          </Paper>
        </Grid>

        {/* Notifications */}
        <Grid item xs={12} md={6}>
          <Paper elevation={2} className="dashboard-card">
            <Box className="card-header">
              <NotificationsIcon />
              <Typography variant="h6">Notifications</Typography>
            </Box>
            <Divider />
            {notificationsLoading ? (
              <Box display="flex" justifyContent="center" p={3}>
                <CircularProgress />
              </Box>
            ) : notifications && notifications.length > 0 ? (
              <List>
                {notifications.slice(0, 3).map((notification) => (
                  <ListItem key={notification.id} button>
                    <ListItemText
                      primary={notification.title}
                      secondary={
                        <>
                          {notification.message}
                          <Typography variant="caption" display="block" color="textSecondary">
                            {new Date(notification.timestamp).toLocaleString()}
                          </Typography>
                        </>
                      }
                    />
                  </ListItem>
                ))}
              </List>
            ) : (
              <Box p={3} textAlign="center">
                <Typography>No notifications</Typography>
              </Box>
            )}
            <Box p={1} textAlign="center">
              <Button variant="outlined" color="primary">
                View All
              </Button>
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </div>
  );
};

export default Dashboard; 