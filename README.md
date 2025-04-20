# Hospital Management System

A comprehensive hospital management system designed to streamline healthcare operations, enhance patient experience, and improve healthcare delivery.

## Features

- **Patient Portal**: Secure access for patients to manage their healthcare journey
- **Health Metrics Tracking**: Monitor vital signs and health metrics over time
- **Appointment Management**: Schedule, view, and manage appointments
- **Medical Records**: Access to medical history and test results
- **Messaging**: Secure communication with healthcare providers
- **Telemedicine**: Virtual consultations through integrated video platform

## Tech Stack

- **Frontend**: React, TypeScript, Material-UI
- **State Management**: React Query, Context API
- **Visualization**: Chart.js
- **Routing**: React Router
- **Form Handling**: React Hook Form
- **API Communication**: Axios
- **Authentication**: JWT

## Modules

### Health Metrics Module

A comprehensive module for tracking and visualizing health metrics over time:

- Dashboard view of all health metrics
- Detailed history for each metric
- Data entry forms for adding new readings
- Interactive charts and trend analysis
- Date filtering for historical readings

Supports tracking of:
- Heart Rate
- Blood Pressure
- Oxygen Level
- Body Temperature

### Appointments Module

Managing healthcare appointments with features like:

- List and calendar views
- Appointment scheduling
- Reminders and notifications
- Integration with telemedicine platform

### User Profile Module

Managing user information and preferences:

- Personal information
- Medical history
- Insurance details
- Account settings

## Installation and Setup

1. Clone the repository
2. Install dependencies:
   ```
   cd frontend
   npm install
   ```
3. Start the development server:
   ```
   npm start
   ```

## Development

- The application uses TypeScript for type safety
- Material-UI provides consistent design components
- Chart.js is used for data visualization
- React Router handles navigation between pages

## Folder Structure

- `/frontend/src/components`: Reusable UI components
- `/frontend/src/pages`: Page components organized by module
- `/frontend/src/context`: Context providers for state management
- `/frontend/src/services`: API services and data fetching
- `/frontend/src/hooks`: Custom React hooks
- `/frontend/src/utils`: Utility functions
- `/frontend/src/assets`: Static assets like images and icons 