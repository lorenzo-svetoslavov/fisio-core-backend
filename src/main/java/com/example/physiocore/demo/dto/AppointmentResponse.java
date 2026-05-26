package com.example.physiocore.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;

    // IMPORTANTE: Angular necesita esto para this.reservationForm.patchValue({ patient_id: ... })
    private Long patientId;

    private String name;
    private String surname;
    private String username;
    private String phone;
    private String date;
    private String professionalFullName;
    private String professionalId;
    private String hour;
    private String service;
    private String state;
    private String annotation;
}
