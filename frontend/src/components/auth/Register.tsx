import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './Auth.css';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

// Define props interface for the component
export interface RegisterFormData {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
  role: 'DOCTOR' | 'PATIENT';
  latitude?: number;
  longitude?: number;
}

interface RegisterProps {
  onSubmit: (formData: RegisterFormData) => Promise<void>;
  isSubmitting: boolean;
}

const Register: React.FC<RegisterProps> = ({ onSubmit, isSubmitting }) => {
  const [formData, setFormData] = useState<RegisterFormData>({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'PATIENT',
    latitude: undefined,
    longitude: undefined,
  });
  const [map, setMap] = useState<L.Map | null>(null);
  const [marker, setMarker] = useState<L.Marker | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    onSubmit(formData);
  };

  // Initialize map when role is DOCTOR and map is not already initialized
  React.useEffect(() => {
    if (formData.role === 'DOCTOR' && !map) {
      const leafletMap = L.map('register-map').setView([30.3165, 78.0322], 12);
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
      }).addTo(leafletMap);
      leafletMap.on('click', function (e: L.LeafletMouseEvent) {
        const { lat, lng } = e.latlng;
        setFormData(prev => ({ ...prev, latitude: lat, longitude: lng }));
        if (marker) {
          marker.setLatLng([lat, lng]);
        } else {
          const newMarker = L.marker([lat, lng], { draggable: true }).addTo(leafletMap);
          newMarker.on('dragend', function (event) {
            const m = event.target;
            const position = m.getLatLng();
            setFormData(prev => ({ ...prev, latitude: position.lat, longitude: position.lng }));
          });
          setMarker(newMarker);
        }
      });
      setMap(leafletMap);
    }
    // Clean up map on unmount or when switching role
    return () => {
      if (map) {
        map.remove();
        setMap(null);
        setMarker(null);
      }
    };
    // eslint-disable-next-line
  }, [formData.role]);

  // Update marker position if formData.latitude/longitude changes
  React.useEffect(() => {
    if (marker && formData.latitude && formData.longitude) {
      marker.setLatLng([formData.latitude, formData.longitude]);
    }
  }, [formData.latitude, formData.longitude, marker]);

  return (
    <div className="auth-container">
      <h2 className="auth-title">Create a new account</h2>
      <form onSubmit={handleSubmit} className="auth-form">
        <div className="form-group">
          <label htmlFor="firstName">First Name</label>
          <input
            type="text"
            id="firstName"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            required
            disabled={isSubmitting}
          />
        </div>
        <div className="form-group">
          <label htmlFor="lastName">Last Name</label>
          <input
            type="text"
            id="lastName"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            required
            disabled={isSubmitting}
          />
        </div>
        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
            disabled={isSubmitting}
          />
        </div>
        <div className="form-group">
          <label htmlFor="role">Role</label>
          <select
            id="role"
            name="role"
            value={formData.role}
            onChange={handleChange}
            required
            disabled={isSubmitting}
            className="auth-select"
          >
            <option value="PATIENT">Patient</option>
            <option value="DOCTOR">Doctor</option>
          </select>
        </div>
        {formData.role === 'DOCTOR' && (
          <div className="form-group">
            <label>Select your location on the map</label>
            <div id="register-map" style={{ height: '250px', width: '100%', marginBottom: 10, borderRadius: 8, border: '1px solid #ccc' }} />
            {formData.latitude && formData.longitude && (
              <div style={{ fontSize: '0.9em', color: '#555' }}>
                Selected Location: {formData.latitude.toFixed(5)}, {formData.longitude.toFixed(5)}
              </div>
            )}
          </div>
        )}
        <div className="form-group">
          <label htmlFor="password">Password</label>
          <input
            type="password"
            id="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
            disabled={isSubmitting}
          />
        </div>
        <div className="form-group">
          <label htmlFor="confirmPassword">Confirm Password</label>
          <input
            type="password"
            id="confirmPassword"
            name="confirmPassword"
            value={formData.confirmPassword}
            onChange={handleChange}
            required
            disabled={isSubmitting}
          />
        </div>
        <button 
          type="submit" 
          className="auth-btn" 
          disabled={isSubmitting}
        >
          {isSubmitting ? 'Registering...' : 'Register'}
        </button>
      </form>
      <p>
        <Link to="/login" className="auth-link">Already have an account? Login here</Link>
      </p>
    </div>
  );
};

export default Register; 