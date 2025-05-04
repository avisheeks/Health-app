import { useState, useEffect, useCallback } from 'react';
import { useNotification } from '../context/NotificationContext';
import { supabase } from '../config/supabase';

export interface Doctor {
  id: string;
  user_id?: string;
  name: string;
  specialty: string;
  image?: string;
  department?: string;
  hospital?: string;
  schedule?: {
    day: string;
    startTime: string;
    endTime: string;
  }[];
  education?: string[];
  certifications?: string[];
  experience?: string;
  acceptingNewPatients?: boolean;
  bio?: string;
  contactInfo?: {
    email?: string;
    phone?: string;
    officeLocation?: string;
  };
}

export const useDoctors = () => {
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<Error | null>(null);
  const { showNotification } = useNotification();

  useEffect(() => {
    const fetchDoctors = async () => {
      try {
        setLoading(true);

        // Query doctor_profiles joined with users to get complete information
        const { data: doctorProfiles, error: profilesError } = await supabase
          .from('doctor_profiles')
          .select(`
            id,
            user_id,
            license_number,
            bio,
            users (
              id,
              full_name,
              email,
              role
            )
          `);
          
        if (profilesError) {
          console.error('Error fetching doctor profiles:', profilesError);
          throw profilesError;
        }
        
        console.log('DEBUG - Doctor profiles data:', doctorProfiles);
        
        if (doctorProfiles && doctorProfiles.length > 0) {
          // Transform the joined data to match our Doctor interface
          const formattedDoctors = doctorProfiles.map(profile => {
            // Handle the nested users object from Supabase
            const userInfo = profile.users as any;
            
            return {
              id: profile.id, // Use doctor_profile.id as the primary ID for appointments
              user_id: profile.user_id, // Store the user_id for reference
              name: userInfo?.full_name || 'Unknown Doctor',
              specialty: profile.license_number ? `MD ${profile.license_number}` : 'General Medicine',
              bio: profile.bio || '',
              contactInfo: {
                email: userInfo?.email || ''
              }
            };
          });
          
          console.log('DEBUG - Formatted doctors:', formattedDoctors);
          setDoctors(formattedDoctors);
        } else {
          console.warn('No doctor profiles found');
          setDoctors([]);
          showNotification('No doctor profiles found in the system. Please set up doctor profiles.', 'warning');
        }
      } catch (err) {
        console.error('Error fetching doctors:', err);
        setError(err as Error);
        setDoctors([]);
        if (showNotification) {
          showNotification(`Failed to load doctors: ${(err as Error).message}. Please check your database connection.`, 'error');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchDoctors();
  }, [showNotification]);

  // Fetch a doctor by ID - using doctor_profiles
  const fetchDoctorById = useCallback(async (doctorId: string) => {
    setLoading(true);
    try {
      // Fetch from doctor_profiles joined with users
      const { data, error } = await supabase
        .from('doctor_profiles')
        .select(`
          id,
          user_id,
          license_number,
          bio,
          users (
            id,
            full_name,
            email,
            role
          )
        `)
        .eq('id', doctorId)
        .single();
        
      if (error) {
        throw error;
      }
      
      if (!data) {
        throw new Error('Doctor not found');
      }
      
      // Handle the nested users object from Supabase
      const userInfo = data.users as any;
      
      // Transform to match Doctor interface
      const doctor: Doctor = {
        id: data.id,
        user_id: data.user_id,
        name: userInfo?.full_name || 'Unknown Doctor',
        specialty: data.license_number ? `MD ${data.license_number}` : 'General Medicine',
        bio: data.bio || '',
        contactInfo: {
          email: userInfo?.email || ''
        }
      };
      
      return doctor;
    } catch (err) {
      const error = err as Error;
      setError(error);
      showNotification('Error fetching doctor: ' + error.message, 'error');
      return null;
    } finally {
      setLoading(false);
    }
  }, [showNotification]);

  // Filter doctors by specialty
  const filterDoctorsBySpecialty = useCallback(async (specialty: string) => {
    try {
      // For now, this will just return all doctors since specialty filtering isn't implemented
      // You can enhance this later to actually filter by specialty
      const { data, error } = await supabase
        .from('doctor_profiles')
        .select(`
          id,
          user_id,
          license_number,
          bio,
          users (
            id,
            full_name,
            email,
            role
          )
        `);
        
      if (error) {
        throw error;
      }
      
      // Transform the joined data to match our Doctor interface
      const doctors = data.map(profile => {
        const userInfo = profile.users as any;
        
        return {
          id: profile.id,
          user_id: profile.user_id,
          name: userInfo?.full_name || 'Unknown Doctor',
          specialty: profile.license_number ? `MD ${profile.license_number}` : 'General Medicine',
          bio: profile.bio || '',
          contactInfo: {
            email: userInfo?.email || ''
          }
        };
      });
      
      return doctors;
    } catch (err) {
      const error = err as Error;
      setError(error);
      showNotification('Error filtering doctors: ' + error.message, 'error');
      return [];
    }
  }, [showNotification]);

  return {
    doctors,
    loading,
    error,
    fetchDoctorById,
    filterDoctorsBySpecialty
  };
};

export default useDoctors; 