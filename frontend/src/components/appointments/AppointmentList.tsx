import React, { useState, useEffect } from 'react';
import { format } from 'date-fns';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '../ui/table';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { useToast } from '../ui/use-toast';
import { useAuth } from '../../context/AuthContext';
import { appointmentService } from '../../services/appointmentService';
import { Appointment, AppointmentStatus } from '../../types/appointment';

interface AppointmentListProps {
  role: 'patient' | 'doctor';
  onEdit?: (appointment: Appointment) => void;
  filters?: {
    status?: AppointmentStatus[];
  };
}

export const AppointmentList: React.FC<AppointmentListProps> = ({
  role,
  onEdit,
  filters = {}
}) => {
  const { user } = useAuth();
  const { toast } = useToast();
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAppointments();
  }, [user?.id, role, filters]);

  const fetchAppointments = async () => {
    try {
      const params: any = {
        [role === 'patient' ? 'patient_id' : 'doctor_id']: user?.id,
      };

      // Add status filter if provided
      if (filters.status?.length) {
        params.status = filters.status;
      }

      const data = await appointmentService.getAppointments(params);
      setAppointments(data);
    } catch (error) {
      toast({
        title: 'Error',
        description: 'Failed to fetch appointments',
        variant: 'destructive'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (id: string, status: AppointmentStatus) => {
    try {
      await appointmentService.update(id, { status });
      toast({
        title: 'Success',
        description: 'Appointment status updated successfully',
        variant: 'success',
      });
      fetchAppointments();
    } catch (error) {
      toast({
        title: 'Error',
        description: 'Failed to update appointment status',
        variant: 'destructive',
      });
    }
  };

  const handleCancel = async (id: string) => {
    try {
      await appointmentService.cancel(id);
      toast({
        title: 'Success',
        description: 'Appointment cancelled successfully',
        variant: 'success',
      });
      fetchAppointments();
    } catch (error) {
      toast({
        title: 'Error',
        description: 'Failed to cancel appointment',
        variant: 'destructive',
      });
    }
  };

  const getStatusBadgeVariant = (status: AppointmentStatus) => {
    switch (status) {
      case 'CONFIRMED':
        return 'success';
      case 'CANCELLED':
        return 'destructive';
      case 'COMPLETED':
        return 'secondary';
      case 'NO_SHOW':
        return 'warning';
      default:
        return 'default';
    }
  };

  const renderActions = (appointment: Appointment) => {
    const isUpcoming = ['SCHEDULED', 'CONFIRMED'].includes(appointment.status as string);
    
    if (role === 'doctor') {
      return (
        <div className="space-x-2">
          {isUpcoming && (
            <>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleStatusUpdate(appointment.id, 'CONFIRMED' as AppointmentStatus)}
              >
                Confirm
              </Button>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleStatusUpdate(appointment.id, 'COMPLETED' as AppointmentStatus)}
              >
                Complete
              </Button>
              <Button
                variant="destructive"
                size="sm"
                onClick={() => handleStatusUpdate(appointment.id, 'NO_SHOW' as AppointmentStatus)}
              >
                No Show
              </Button>
            </>
          )}
        </div>
      );
    }

    return (
      <div className="space-x-2">
        {isUpcoming && (
          <>
            <Button
              variant="outline"
              size="sm"
              onClick={() => onEdit?.(appointment)}
            >
              Edit
            </Button>
            <Button
              variant="destructive"
              size="sm"
              onClick={() => handleCancel(appointment.id)}
            >
              Cancel
            </Button>
          </>
        )}
      </div>
    );
  };

  if (loading) {
    return <div className="text-center py-8">Loading...</div>;
  }

  if (appointments.length === 0) {
    return (
      <div className="text-center py-8 text-gray-500">
        No appointments found
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Date & Time</TableHead>
            <TableHead>{role === 'patient' ? 'Doctor' : 'Patient'}</TableHead>
            <TableHead>Type</TableHead>
            <TableHead>Reason</TableHead>
            <TableHead>Status</TableHead>
            <TableHead>Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {appointments.map((appointment) => (
            <TableRow key={appointment.id}>
              <TableCell>
                {format(new Date(appointment.startTime), 'PPp')}
              </TableCell>
              <TableCell>
                {role === 'patient' ? appointment.doctor.name : appointment.patientId}
              </TableCell>
              <TableCell>
                {appointment.type.toString().replace('_', ' ')}
              </TableCell>
              <TableCell>{appointment.reason}</TableCell>
              <TableCell>
                <Badge variant={getStatusBadgeVariant(appointment.status)}>
                  {appointment.status}
                </Badge>
              </TableCell>
              <TableCell>
                {renderActions(appointment)}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
};

export default AppointmentList; 