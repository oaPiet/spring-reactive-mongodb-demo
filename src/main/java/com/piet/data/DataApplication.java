package com.piet.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class DataApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataApplication.class, args);
    }

}

@Component
@RequiredArgsConstructor
@Log4j2
class SampleDataInitializer {
    private final  ReservationRepository reservationRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void ready() {

        Flux<Reservation> reservations = Flux.just("Leonardo", "Donatello", "Rafael")
                .map(name -> new Reservation(null, name))
                .flatMap(this.reservationRepository::save);

        this.reservationRepository
                .deleteAll()
                .thenMany(reservations)
                .thenMany(this.reservationRepository.findAll())
                .subscribe(log::info);

    }
}

@Repository
interface ReservationRepository extends ReactiveMongoRepository<Reservation, String> {
    Flux<Reservation> findByName(String name);
}

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
class Reservation {

    @Id
    private String id;
    private String name;
}
