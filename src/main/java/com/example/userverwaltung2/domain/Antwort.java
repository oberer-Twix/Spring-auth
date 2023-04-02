package com.example.userverwaltung2.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Antwort {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.ORDINAL)

    private AntwortMoeglichkeiten clientAntwort;

    @ManyToOne
    @JoinColumn(name = "client")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "frage")
    private Frage frage;

}
