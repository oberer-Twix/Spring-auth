package com.example.userverwaltung2.persistance;

import com.example.userverwaltung2.domain.Antwort;
import com.example.userverwaltung2.domain.Client;
import com.example.userverwaltung2.domain.Frage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface AntwortRepository extends JpaRepository<Antwort, Long> {

    Optional<Antwort> findByClientAndFrage(Client client, Frage frage);

    Set<Antwort> findAllByFrage(Frage frage);
}
