import React, { useState, useEffect } from 'react';
import {
  Box,
  Button,
  FormControl,
  FormControlLabel,
  FormLabel,
  Grid,
  MenuItem,
  Paper,
  Radio,
  RadioGroup,
  TextField,
  Typography,
  CircularProgress,
  Alert
} from '@mui/material';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { Doctor } from '../../hooks/useDoctors';
import { AppointmentFormData } from '../../hooks/useAppointments';
import { addHours } from 'date-fns';

interface AppointmentFormProps {
  doctors: Doctor[];
  initialValues?: Partial<AppointmentFormData>;
  loading?: boolean;
  onSubmit: (data: AppointmentFormData) => void;
  onCancel: () => void;
  isEditMode?: boolean;
}

const AppointmentForm: React.FC<AppointmentFormProps> = ({
  doctors,
  initialValues,
  loading = false,
  onSubmit,
  onCancel,
  isEditMode = false
}) => {
  const [formData, setFormData] = useState<AppointmentFormData>({
    title: initialValues?.title || '',
    doctorId: initialValues?.doctorId || '',
    startTime: initialValues?.startTime || new Date().toISOString(),
    endTime: initialValues?.endTime || addHours(new Date(), 1).toISOString(),
    type: initialValues?.type || 'IN_PERSON',
    reason: initialValues?.reason || '',
    notes: initialValues?.notes || '',
    location: initialValues?.location || '',
    meetingLink: initialValues?.meetingLink || '',
    insurance: initialValues?.insurance || {
      provider: '',
      policyNumber: '',
      groupNumber: ''
    }
  });
  
  const [errors, setErrors] = useState<Record<string, string>>({});
  
  useEffect(() => {
    if (initialValues) {
      setFormData(prev => {
        // Ensure insurance fields have default values if not provided
        const mergedInsurance = {
          provider: prev.insurance?.provider || '',
          policyNumber: prev.insurance?.policyNumber || '',
          groupNumber: prev.insurance?.groupNumber || undefined,
          ...initialValues.insurance
        };
        
        return {
          ...prev,
          ...initialValues,
          insurance: mergedInsurance
        };
      });
    }
  }, [initialValues]);
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    
    if (name.startsWith('insurance.')) {
      const insuranceField = name.split('.')[1];
      setFormData(prev => ({
        ...prev,
        insurance: {
          ...prev.insurance!,
          [insuranceField]: value
        }
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [name]: value
      }));
    }
    
    // Clear error when field is edited
    if (errors[name]) {
      setErrors(prev => {
        const newErrors = { ...prev };
        delete newErrors[name];
        return newErrors;
      });
    }
  };
  
  const handleStartTimeChange = (date: Date | null) => {
    if (date) {
      const endTime = addHours(date, 1);
      setFormData(prev => ({
        ...prev,
        startTime: date.toISOString(),
        endTime: endTime.toISOString()
      }));
    }
  };
  
  const handleEndTimeChange = (date: Date | null) => {
    if (date) {
      setFormData(prev => ({
        ...prev,
        endTime: date.toISOString()
      }));
    }
  };
  
  const handleAppointmentTypeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const type = e.target.value as 'IN_PERSON' | 'VIRTUAL';
    setFormData(prev => ({
      ...prev,
      type,
      // Clear location or meetingLink based on appointment type
      location: type === 'IN_PERSON' ? prev.location : '',
      meetingLink: type === 'VIRTUAL' ? prev.meetingLink : ''
    }));
  };
  
  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};
    
    if (!formData.title) {
      newErrors.title = 'Appointment title is required';
    }
    
    if (!formData.doctorId) {
      newErrors.doctorId = 'Please select a doctor';
    }
    
    if (formData.type === 'IN_PERSON' && !formData.location) {
      newErrors.location = 'Location is required for in-person appointments';
    }
    
    if (formData.type === 'VIRTUAL' && !formData.meetingLink) {
      newErrors.meetingLink = 'Meeting link is required for virtual appointments';
    }
    
    if (!formData.reason) {
      newErrors.reason = 'Reason for appointment is required';
    }
    
    if (formData.insurance?.provider && !formData.insurance.policyNumber) {
      newErrors['insurance.policyNumber'] = 'Policy number is required';
    }
    
    // Validate start time is before end time
    const startTime = new Date(formData.startTime);
    const endTime = new Date(formData.endTime);
    
    if (startTime >= endTime) {
      newErrors.endTime = 'End time must be after start time';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };
  
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (validateForm()) {
      onSubmit(formData);
    }
  };
  
  return (
    <Paper elevation={3} sx={{ p: 3, mb: 4 }}>
      <Typography variant="h5" mb={3}>
        {isEditMode ? 'Edit Appointment' : 'Schedule New Appointment'}
      </Typography>
      
      <Box component="form" onSubmit={handleSubmit}>
        <Grid container spacing={3}>
          {/* Appointment Title */}
          <Grid item xs={12}>
            <TextField
              name="title"
              label="Appointment Title"
              fullWidth
              required
              value={formData.title}
              onChange={handleChange}
              error={!!errors.title}
              helperText={errors.title}
              disabled={loading}
            />
          </Grid>
          
          {/* Doctor Selection */}
          <Grid item xs={12} sm={6}>
            <TextField
              select
              name="doctorId"
              label="Doctor"
              fullWidth
              required
              value={formData.doctorId}
              onChange={handleChange}
              error={!!errors.doctorId}
              helperText={errors.doctorId}
              disabled={loading}
            >
              <MenuItem value="" disabled>
                Select a doctor
              </MenuItem>
              {doctors.map((doctor) => (
                <MenuItem key={doctor.id} value={doctor.id}>
                  {doctor.name} - {doctor.specialty}
                </MenuItem>
              ))}
            </TextField>
          </Grid>
          
          {/* Appointment Type */}
          <Grid item xs={12} sm={6}>
            <FormControl component="fieldset">
              <FormLabel component="legend">Appointment Type</FormLabel>
              <RadioGroup
                row
                name="type"
                value={formData.type}
                onChange={handleAppointmentTypeChange}
              >
                <FormControlLabel
                  value="IN_PERSON"
                  control={<Radio />}
                  label="In-person"
                  disabled={loading}
                />
                <FormControlLabel
                  value="VIRTUAL"
                  control={<Radio />}
                  label="Telehealth"
                  disabled={loading}
                />
              </RadioGroup>
            </FormControl>
          </Grid>
          
          {/* Date and Time */}
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <Grid item xs={12} sm={6}>
              <DateTimePicker
                label="Start Time"
                value={new Date(formData.startTime)}
                onChange={handleStartTimeChange}
                slotProps={{
                  textField: {
                    fullWidth: true,
                    required: true,
                    disabled: loading
                  }
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <DateTimePicker
                label="End Time"
                value={new Date(formData.endTime)}
                onChange={handleEndTimeChange}
                slotProps={{
                  textField: {
                    fullWidth: true,
                    required: true,
                    error: !!errors.endTime,
                    helperText: errors.endTime,
                    disabled: loading
                  }
                }}
              />
            </Grid>
          </LocalizationProvider>
          
          {/* Location or Meeting Link */}
          <Grid item xs={12}>
            {formData.type === 'IN_PERSON' ? (
              <TextField
                name="location"
                label="Location"
                fullWidth
                required
                value={formData.location}
                onChange={handleChange}
                error={!!errors.location}
                helperText={errors.location}
                disabled={loading}
              />
            ) : (
              <TextField
                name="meetingLink"
                label="Meeting Link"
                fullWidth
                required
                value={formData.meetingLink}
                onChange={handleChange}
                error={!!errors.meetingLink}
                helperText={errors.meetingLink}
                disabled={loading}
              />
            )}
          </Grid>
          
          {/* Reason for Visit */}
          <Grid item xs={12}>
            <TextField
              name="reason"
              label="Reason for Visit"
              fullWidth
              required
              multiline
              rows={3}
              value={formData.reason}
              onChange={handleChange}
              error={!!errors.reason}
              helperText={errors.reason}
              disabled={loading}
            />
          </Grid>
          
          {/* Additional Notes */}
          <Grid item xs={12}>
            <TextField
              name="notes"
              label="Additional Notes"
              fullWidth
              multiline
              rows={2}
              value={formData.notes}
              onChange={handleChange}
              disabled={loading}
            />
          </Grid>
          
          {/* Insurance Information */}
          <Grid item xs={12}>
            <Typography variant="subtitle1" gutterBottom>
              Insurance Information
            </Typography>
          </Grid>
          
          <Grid item xs={12} sm={4}>
            <TextField
              name="insurance.provider"
              label="Insurance Provider"
              fullWidth
              value={formData.insurance?.provider || ''}
              onChange={handleChange}
              disabled={loading}
            />
          </Grid>
          
          <Grid item xs={12} sm={4}>
            <TextField
              name="insurance.policyNumber"
              label="Policy Number"
              fullWidth
              value={formData.insurance?.policyNumber || ''}
              onChange={handleChange}
              error={!!errors['insurance.policyNumber']}
              helperText={errors['insurance.policyNumber']}
              disabled={loading}
            />
          </Grid>
          
          <Grid item xs={12} sm={4}>
            <TextField
              name="insurance.groupNumber"
              label="Group Number"
              fullWidth
              value={formData.insurance?.groupNumber || ''}
              onChange={handleChange}
              disabled={loading}
            />
          </Grid>
          
          {/* Form Actions */}
          <Grid item xs={12} sx={{ mt: 2, display: 'flex', justifyContent: 'flex-end' }}>
            <Button
              variant="outlined"
              onClick={onCancel}
              sx={{ mr: 2 }}
              disabled={loading}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              disabled={loading}
              startIcon={loading ? <CircularProgress size={20} color="inherit" /> : null}
            >
              {loading ? 'Saving...' : isEditMode ? 'Update Appointment' : 'Schedule Appointment'}
            </Button>
          </Grid>
        </Grid>
      </Box>
    </Paper>
  );
};

export default AppointmentForm; 