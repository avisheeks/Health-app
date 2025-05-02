export enum AppointmentStatus {
  SCHEDULED = 'SCHEDULED',
  CONFIRMED = 'CONFIRMED',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  NO_SHOW = 'NO_SHOW'
}

export enum AppointmentType {
  REGULAR = 'REGULAR',
  FOLLOW_UP = 'FOLLOW_UP',
  EMERGENCY = 'EMERGENCY'
}

export interface Appointment {
  id: string;
  doctor_id: string;
  patient_id: string;
  appointment_date: string;
  appointment_type: AppointmentType;
  reason: string;
  notes?: string;
  status: AppointmentStatus;
  duration_minutes: number;
}

export interface AppointmentCreate extends Omit<Appointment, 'id' | 'status'> {}

export interface AppointmentUpdate {
  appointment_date?: string;
  appointment_type?: AppointmentType;
  reason?: string;
  duration_minutes?: number;
  notes?: string;
  status?: AppointmentStatus;
} 