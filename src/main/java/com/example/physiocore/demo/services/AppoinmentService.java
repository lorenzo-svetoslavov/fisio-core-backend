package com.example.physiocore.demo.services;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.physiocore.demo.dto.AppointmentDailyDTO;
import com.example.physiocore.demo.dto.AppointmentRequest;
import com.example.physiocore.demo.dto.AppointmentResponse;
import com.example.physiocore.demo.model.Appointment;
import com.example.physiocore.demo.model.ClinicService;
import com.example.physiocore.demo.model.StatusAppointment;
import com.example.physiocore.demo.model.AppUser;
import com.example.physiocore.demo.repository.AppoinmentRepository;
import com.example.physiocore.demo.repository.UserRepository;

@Service
public class AppoinmentService {
        @Autowired
        private AppoinmentRepository appointmentRepository;
        @Autowired
        private UserRepository clientRepository;

        public List<AppointmentResponse> searchAppointments(
                        LocalDate date,
                        Long professionalId,
                        String patientName,
                        StatusAppointment state,
                        ClinicService service) {
                return appointmentRepository.searchAppointments(date, professionalId, patientName, state, service)
                                .stream()
                                .map(app -> new AppointmentResponse(
                                                app.getId(),
                                                app.getPatient().getId(),
                                                app.getPatient().getName(),
                                                app.getPatient().getSurname(),
                                                app.getPatient().getUsername(),
                                                app.getPatient().getPhone(),
                                                app.getDate().toString(),
                                                app.getProfessional().getName() + " " +
                                                                app.getProfessional().getSurname(),
                                                app.getProfessional().getId().toString(),
                                                app.getHourValue(),
                                                app.getService().getLabel(),
                                                app.getState().getLabel(),
                                                app.getAnnotation()))
                                .toList();
        }

        public List<AppointmentDailyDTO> getDailyAgenda(LocalDate date) {
                return appointmentRepository.findByDateCustom(date).stream()
                                .map(app -> new AppointmentDailyDTO(
                                                app.getHourValue(),
                                                app.getProfessional().getName() + " "
                                                                + app.getProfessional().getSurname(),
                                                app.getPatient().getName() + " " + app.getPatient().getSurname(),
                                                app.getService().getLabel(),
                                                app.getState().getLabel()))
                                .collect(Collectors.toList());
        }

        public List<AppointmentDailyDTO> getDailyAgendaByProfessional(LocalDate date, Long professionalId) {
                return appointmentRepository.findDailyAgendaByProfessional(date, professionalId)
                                .stream()
                                .map(app -> new AppointmentDailyDTO(
                                                app.getHourValue(),
                                                app.getProfessional().getName() + " "
                                                                + app.getProfessional().getSurname(),
                                                app.getPatient().getName() + " " + app.getPatient().getSurname(),
                                                app.getService().getLabel(),
                                                app.getState().getLabel()))
                                .toList();
        }

        public List<AppointmentResponse> findByPatient(AppUser user) {
                List<Appointment> appointments = appointmentRepository.findByPatient(user);

                return appointments.stream()
                                .map(appointment -> AppointmentResponse.builder()
                                                .id(appointment.getId())
                                                .patientId(appointment.getPatient().getId())
                                                .username(appointment.getPatient().getUsername())
                                                .name(appointment.getPatient().getName())
                                                .surname(appointment.getPatient().getSurname())
                                                .phone(appointment.getPatient().getPhone())
                                                .date(appointment.getDate())
                                                .hour(appointment.getHourValue())
                                                .service(appointment.getService().getLabel())
                                                .state(appointment.getState().getLabel())
                                                .annotation(appointment.getAnnotation())
                                                .professionalFullName(
                                                                appointment.getProfessional().getName() + " "
                                                                                + appointment.getProfessional()
                                                                                                .getSurname())
                                                .build())
                                .sorted(Comparator
                                                .comparing((AppointmentResponse response) -> LocalDate
                                                                .parse(response.getDate()))
                                                .reversed()
                                                .thenComparing(response -> LocalTime.parse(response.getHour())))
                                .collect(Collectors.toList());
        }

        public List<AppointmentResponse> findByPatientAndState(AppUser user, StatusAppointment state) {
                return appointmentRepository.findByPatientAndState(user, state)
                                .stream()
                                .map(appointment -> AppointmentResponse.builder()
                                                .id(appointment.getId())
                                                .patientId(appointment.getPatient().getId())
                                                .username(appointment.getPatient().getUsername())
                                                .name(appointment.getPatient().getName())
                                                .surname(appointment.getPatient().getSurname())
                                                .phone(appointment.getPatient().getPhone())
                                                .date(appointment.getDate())
                                                .hour(appointment.getHourValue())
                                                .service(appointment.getService().getLabel())
                                                .state(appointment.getState().getLabel())
                                                .annotation(appointment.getAnnotation())
                                                .professionalFullName(
                                                                appointment.getProfessional().getName() + " "
                                                                                + appointment.getProfessional()
                                                                                                .getSurname())
                                                .build())
                                .sorted(Comparator
                                                .comparing((AppointmentResponse response) -> LocalDate
                                                                .parse(response.getDate()))
                                                .reversed()
                                                .thenComparing(response -> LocalTime.parse(response.getHour())))
                                .collect(Collectors.toList());
        }

        public Appointment createAppointment(AppointmentRequest request) {
                AppUser patient = clientRepository.findById(request.getPatient_id())
                                .orElseThrow(() -> new RuntimeException(
                                                "Cliente no encontrado con ID: " + request.getPatient_id()));
                AppUser professional = clientRepository.findById(request.getProfessional_id())
                                .orElseThrow(() -> new RuntimeException(
                                                "Profesional no encontrado con ID: " + request.getProfessional_id()));

                Appointment appointment = new Appointment();
                appointment.setDate(request.getDate());
                appointment.setHourValue(request.getHour());
                appointment.setService(request.getService());
                appointment.setPatient(patient);
                appointment.setProfessional(professional);

                return appointmentRepository.save(appointment);
        }

        public AppointmentResponse updateAppointment(Long id, AppointmentRequest data) {
                Appointment appointment = appointmentRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));

                appointment.setDate(data.getDate());
                appointment.setHourValue(data.getHour());
                appointment.setService(data.getService());
                appointment.setState(data.getState());
                appointment.setAnnotation(data.getAnnotation());

                Appointment updatedAppointment = appointmentRepository.save(appointment);

                return AppointmentResponse.builder()
                                .id(updatedAppointment.getId())
                                .patientId(updatedAppointment.getPatient().getId())
                                .username(updatedAppointment.getPatient().getUsername())
                                .name(updatedAppointment.getPatient().getName())
                                .surname(updatedAppointment.getPatient().getSurname())
                                .phone(updatedAppointment.getPatient().getPhone())
                                .date(updatedAppointment.getDate())
                                .hour(updatedAppointment.getHourValue())
                                .service(updatedAppointment.getService().getLabel())
                                .state(updatedAppointment.getState().getLabel())
                                .annotation(updatedAppointment.getAnnotation())
                                .build();
        }

        public AppointmentResponse getAppointmentById(Long id) {
                Appointment appointment = appointmentRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));

                return AppointmentResponse.builder()
                                .id(appointment.getId())
                                .patientId(appointment.getPatient().getId())
                                .username(appointment.getPatient().getUsername())
                                .name(appointment.getPatient().getName())
                                .surname(appointment.getPatient().getSurname())
                                .phone(appointment.getPatient().getPhone())
                                .professionalId(appointment.getProfessional().getId().toString())
                                .date(appointment.getDate())
                                .hour(appointment.getHourValue())
                                .service(appointment.getService().getLabel())
                                .state(appointment.getState().getLabel())
                                .annotation(appointment.getAnnotation())
                                .build();
        }

        public List<AppointmentResponse> getAllAppointments() {
                List<Appointment> appointments = appointmentRepository.findAll();

                return appointments.stream()
                                .sorted((a, b) -> {
                                        try {
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                                Date dateA = sdf.parse(a.getDate() + " " + a.getHourValue());
                                                Date dateB = sdf.parse(b.getDate() + " " + b.getHourValue());
                                                return dateA.compareTo(dateB);
                                        } catch (Exception e) {
                                                return 0;
                                        }
                                })
                                .map(data -> AppointmentResponse.builder()
                                                .id(data.getId())
                                                .name(data.getPatient().getName())
                                                .surname(data.getPatient().getSurname())
                                                .phone(data.getPatient().getPhone())
                                                .date(data.getDate())
                                                .service(data.getService().getLabel())
                                                .professionalFullName(
                                                                data.getProfessional().getName() + ' '
                                                                                + data.getProfessional().getSurname())
                                                .hour(data.getHourValue())
                                                .state(data.getState().getLabel())
                                                .annotation(data.getAnnotation())
                                                .build())
                                .collect(Collectors.toList());
        }

        public List<AppointmentResponse> getAppointmentsByProfessional(AppUser professional) {
                List<Appointment> appointments = appointmentRepository.findByProfessional(professional);

                return appointments.stream().map(data -> {
                        return AppointmentResponse.builder()
                                        .id(data.getId())
                                        .name(data.getPatient().getName())
                                        .surname(data.getPatient().getSurname())
                                        .phone(data.getPatient().getPhone())
                                        .date(data.getDate())
                                        .hour(data.getHourValue())
                                        .service(data.getService().getLabel())
                                        .state(data.getState().getLabel())
                                        .annotation(data.getAnnotation())
                                        .build();
                }).collect(Collectors.toList());
        }

        public void deleteAppointment(Long id) {
                appointmentRepository.deleteById(id);
        }
}
