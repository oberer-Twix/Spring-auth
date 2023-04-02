package com.example.userverwaltung2.persistance;

import com.example.userverwaltung2.domain.Client;
import com.example.userverwaltung2.domain.Frage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface FragenRepository extends JpaRepository<Frage, Long> {
    @Query("""
            select f
            from Frage f
            where f.ablaufdatum >= ?1 and f.id not in (
                select a.frage.id
                from Antwort a
                where a.client.email = ?2
            )
            """)
    Set<Frage> findAllFragenByUnattendedClient(LocalDate currentDate, String clientID);

    @Query("select f from Frage f where f.question = ?1")
    Set<Frage> findAllByFrage(String frage);


}
