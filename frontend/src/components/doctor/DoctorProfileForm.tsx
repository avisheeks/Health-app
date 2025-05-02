import React, { useState } from 'react';
import {
  Box,
  TextField,
  Button,
  Grid,
  Typography,
  Autocomplete,
  Chip,
  Paper,
} from '@mui/material';
import { MapContainer, TileLayer, Marker, useMapEvents } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';
import { useAuth } from '../../context/AuthContext';

// Fix Leaflet default marker icon issue
delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
  iconUrl: require('leaflet/dist/images/marker-icon.png'),
  shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
});

const languageOptions = [
  'English', 'Spanish', 'French', 'German', 'Chinese', 'Japanese',
  'Korean', 'Russian', 'Arabic', 'Hindi', 'Portuguese', 'Italian'
];

const specializationOptions = [
  { id: '1', name: 'Cardiology' },
  { id: '2', name: 'Dermatology' },
  { id: '3', name: 'Neurology' },
  { id: '4', name: 'Pediatrics' },
  { id: '5', name: 'Orthopedics' },
  { id: '6', name: 'Psychiatry' },
  { id: '7', name: 'Oncology' },
  { id: '8', name: 'Gynecology' },
  { id: '9', name: 'Ophthalmology' },
  { id: '10', name: 'Dentistry' }
];

interface Location {
  latitude: number;
  longitude: number;
  address: string;
}

interface DoctorProfileFormProps {
  onSubmit: (data: any) => void;
  initialData?: any;
}

// Map click handler component
function LocationMarker({ onLocationSelect }: { onLocationSelect: (lat: number, lng: number) => void }) {
  useMapEvents({
    click(e) {
      onLocationSelect(e.latlng.lat, e.latlng.lng);
    },
  });
  return null;
}

export default function DoctorProfileForm({ onSubmit, initialData }: DoctorProfileFormProps) {
  const { user } = useAuth();
  const [formData, setFormData] = useState({
    firstName: initialData?.firstName || user?.user_metadata?.firstName || '',
    lastName: initialData?.lastName || user?.user_metadata?.lastName || '',
    specializations: initialData?.specializations || [],
    languages: initialData?.languages || [],
    licenseNumber: initialData?.licenseNumber || '',
    experience: initialData?.experience || '',
    education: initialData?.education || '',
    about: initialData?.about || '',
  });

  const [location, setLocation] = useState<Location | null>(
    initialData?.location || null
  );

  const handleInputChange = (field: string) => (event: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [field]: event.target.value });
  };

  const handleLocationSelect = async (lat: number, lng: number) => {
    try {
      // Use OpenStreetMap's Nominatim service for reverse geocoding
      const response = await fetch(
        `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}`
      );
      const data = await response.json();
      
      setLocation({
        latitude: lat,
        longitude: lng,
        address: data.display_name || 'Address not found'
      });
    } catch (error) {
      console.error('Error getting address:', error);
      setLocation({
        latitude: lat,
        longitude: lng,
        address: 'Address not found'
      });
    }
  };

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    onSubmit({
      ...formData,
      location,
    });
  };

  return (
    <Paper elevation={3} sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom>
        Doctor Profile
      </Typography>
      <form onSubmit={handleSubmit}>
        <Grid container spacing={3}>
          <Grid item xs={12} sm={6}>
            <TextField
              fullWidth
              label="First Name"
              value={formData.firstName}
              onChange={handleInputChange('firstName')}
              required
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              fullWidth
              label="Last Name"
              value={formData.lastName}
              onChange={handleInputChange('lastName')}
              required
            />
          </Grid>
          <Grid item xs={12}>
            <Autocomplete
              multiple
              options={specializationOptions}
              getOptionLabel={(option) => option.name}
              value={formData.specializations}
              onChange={(_, newValue) => {
                setFormData({ ...formData, specializations: newValue });
              }}
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Specializations"
                  placeholder="Select specializations"
                />
              )}
              renderTags={(value, getTagProps) =>
                value.map((option, index) => (
                  <Chip
                    label={option.name}
                    {...getTagProps({ index })}
                    key={option.id}
                  />
                ))
              }
            />
          </Grid>
          <Grid item xs={12}>
            <Autocomplete
              multiple
              options={languageOptions}
              value={formData.languages}
              onChange={(_, newValue) => {
                setFormData({ ...formData, languages: newValue });
              }}
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Languages"
                  placeholder="Select languages"
                />
              )}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              fullWidth
              label="License Number"
              value={formData.licenseNumber}
              onChange={handleInputChange('licenseNumber')}
              required
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              fullWidth
              label="Years of Experience"
              type="number"
              value={formData.experience}
              onChange={handleInputChange('experience')}
              required
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Education"
              value={formData.education}
              onChange={handleInputChange('education')}
              multiline
              rows={2}
              required
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              fullWidth
              label="About"
              value={formData.about}
              onChange={handleInputChange('about')}
              multiline
              rows={4}
            />
          </Grid>
          <Grid item xs={12}>
            <Typography variant="subtitle1" gutterBottom>
              Select Location (Click on the map)
            </Typography>
            <Box height={400} width="100%">
              <MapContainer
                center={location ? [location.latitude, location.longitude] : [51.505, -0.09]}
                zoom={13}
                style={{ height: '100%', width: '100%' }}
              >
                <TileLayer
                  url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                  attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                />
                <LocationMarker onLocationSelect={handleLocationSelect} />
                {location && (
                  <Marker position={[location.latitude, location.longitude]} />
                )}
              </MapContainer>
            </Box>
            {location && (
              <Typography variant="body2" color="textSecondary" mt={1}>
                Selected Address: {location.address}
              </Typography>
            )}
          </Grid>
          <Grid item xs={12}>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              size="large"
              fullWidth
            >
              Save Profile
            </Button>
          </Grid>
        </Grid>
      </form>
    </Paper>
  );
} 