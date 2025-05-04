import React, { useState } from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../../components/ui/tabs';
import { Button } from '../../components/ui/button';
import { useAuth } from '../../context/AuthContext';
import { AppointmentBooking } from '../../components/appointments/AppointmentBooking';
import { Appointment, AppointmentStatus } from '../../types/appointment';
import { AppointmentList } from '../../components/appointments/AppointmentList';

export const AppointmentsPage = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('upcoming');
  const [showBooking, setShowBooking] = useState(false);
  const [selectedAppointment, setSelectedAppointment] = useState<Appointment | null>(null);

  // Determine if the user is a doctor or patient based on their role
  const userRole = user?.role === 'doctor' ? 'doctor' : 'patient';

  const handleEditAppointment = (appointment: Appointment) => {
    setSelectedAppointment(appointment);
    setShowBooking(true);
  };

  const handleBookingSuccess = () => {
    setShowBooking(false);
    setSelectedAppointment(null);
    setActiveTab('upcoming');
  };

  const getFilteredAppointments = (tab: string) => {
    switch (tab) {
      case 'upcoming':
        return {
          status: ['SCHEDULED', 'CONFIRMED'] as AppointmentStatus[]
        };
      case 'past':
        return {
          status: ['COMPLETED', 'NO_SHOW'] as AppointmentStatus[]
        };
      case 'cancelled':
        return {
          status: ['CANCELLED'] as AppointmentStatus[]
        };
      default:
        return {};
    }
  };

  return (
    <div className="container mx-auto py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">
          {userRole === 'doctor' ? 'My Patient Appointments' : 'My Appointments'}
        </h1>
        {!showBooking && userRole === 'patient' && (
          <Button onClick={() => setShowBooking(true)}>
            Book New Appointment
          </Button>
        )}
      </div>

      {showBooking ? (
        <div>
          <Button
            variant="outline"
            className="mb-4"
            onClick={() => {
              setShowBooking(false);
              setSelectedAppointment(null);
            }}
          >
            Back to Appointments
          </Button>
          <AppointmentBooking
            doctorId={selectedAppointment?.doctor?.id || ''}
            onSuccess={handleBookingSuccess}
          />
        </div>
      ) : (
        <Tabs value={activeTab} onValueChange={setActiveTab}>
          <TabsList>
            <TabsTrigger value="upcoming">Upcoming</TabsTrigger>
            <TabsTrigger value="past">Past</TabsTrigger>
            <TabsTrigger value="cancelled">Cancelled</TabsTrigger>
          </TabsList>

          <TabsContent value="upcoming">
            <AppointmentList
              role={userRole}
              onEdit={handleEditAppointment}
              filters={getFilteredAppointments('upcoming')}
            />
          </TabsContent>

          <TabsContent value="past">
            <AppointmentList
              role={userRole}
              onEdit={handleEditAppointment}
              filters={getFilteredAppointments('past')}
            />
          </TabsContent>

          <TabsContent value="cancelled">
            <AppointmentList
              role={userRole}
              onEdit={handleEditAppointment}
              filters={getFilteredAppointments('cancelled')}
            />
          </TabsContent>
        </Tabs>
      )}
    </div>
  );
}; 