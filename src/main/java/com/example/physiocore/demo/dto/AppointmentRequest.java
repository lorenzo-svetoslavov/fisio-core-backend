package com.example.physiocore.demo.dto;

import com.example.physiocore.demo.model.ClinicService;
import com.example.physiocore.demo.model.StatusAppointment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class AppointmentRequest {
    private Long patient_id;
    private Long professional_id;
    private String date;
    private String hour;
    private ClinicService service;
    private StatusAppointment state;
    private String annotation;
}
