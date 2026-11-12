package fr.uparis.miage.carrental;

import fr.uparis.miage.carrental.model.Car;
import fr.uparis.miage.carrental.repository.CarRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CarRentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalApplication.class, args);
    }

    /** Quelques voitures au premier demarrage, si la base est vide. */
    @Bean
    CommandLineRunner seedCars(CarRepository cars) {
        return args -> {
            if (cars.count() == 0) {
                cars.save(new Car("AA-111-AA", "Renault", "Clio", 35));
                cars.save(new Car("BB-222-BB", "Peugeot", "208", 40));
                cars.save(new Car("CC-333-CC", "Tesla", "Model 3", 90));
            }
        };
    }
}
