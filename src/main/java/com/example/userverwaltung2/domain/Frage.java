package com.example.userverwaltung2.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Frage {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Size(min = 1, max = 20)
    private String titel;

    @Size(min = 1, max = 200)
    private String question;

    @FutureOrPresent
    private LocalDate ablaufdatum;
}
