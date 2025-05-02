import { Appointment, AppointmentCreate, AppointmentUpdate, AppointmentStatus } from '../types/appointment';
import { supabase } from '../config/supabase';

export const appointmentService = {
  // Create a new appointment
  async create(appointment: AppointmentCreate): Promise<Appointment> {
    const response = await fetch('/api/appointments', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(appointment),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.detail || 'Failed to create appointment');
    }

    return response.json();
  },

  // Get appointments with optional filters
  async getAppointments(params: {
    start_date?: string;
    end_date?: string;
    doctor_id?: string;
    patient_id?: string;
    status?: AppointmentStatus[];
  }): Promise<Appointment[]> {
    const queryParams = new URLSearchParams();
    
    // Handle single parameters
    Object.entries(params).forEach(([key, value]) => {
      if (value && !Array.isArray(value)) {
        queryParams.append(key, value);
      }
    });
    
    // Handle status array if present
    if (params.status?.length) {
      params.status.forEach(status => {
        queryParams.append('status', status);
      });
    }

    const response = await fetch(`/api/appointments?${queryParams}`);
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.detail || 'Failed to fetch appointments');
    }

    return response.json();
  },

  // Check slot availability
  async checkAvailability(params: {
    doctor_id: string;
    date: string;
    duration?: number;
  }): Promise<boolean> {
    const queryParams = new URLSearchParams({
      doctor_id: params.doctor_id,
      date: params.date,
      ...(params.duration && { duration: params.duration.toString() }),
    });

    const response = await fetch(`/api/appointments/availability?${queryParams}`);
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.detail || 'Failed to check availability');
    }

    return response.json();
  },

  // Get available time slots
  async getAvailableSlots(params: {
    doctor_id: string;
    date: string;
    duration?: number;
  }): Promise<string[]> {
    const queryParams = new URLSearchParams({
      doctor_id: params.doctor_id,
      date: params.date,
      ...(params.duration && { duration: params.duration.toString() }),
    });

    const response = await fetch(`/api/appointments/slots?${queryParams}`);
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.detail || 'Failed to fetch available slots');
    }

    return response.json();
  },

  // Update an appointment
  async update(id: string, appointment: AppointmentUpdate): Promise<Appointment> {
    const response = await fetch(`/api/appointments/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(appointment),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.detail || 'Failed to update appointment');
    }

    return response.json();
  },

  // Cancel an appointment
  async cancel(id: string): Promise<void> {
    const response = await fetch(`/api/appointments/${id}`, {
      method: 'DELETE',
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.detail || 'Failed to cancel appointment');
    }
  },
}; 