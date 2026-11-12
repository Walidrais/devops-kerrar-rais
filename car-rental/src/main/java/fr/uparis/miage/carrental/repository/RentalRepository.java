package fr.uparis.miage.carrental.repository;

import fr.uparis.miage.carrental.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    Optional<Rental> findFirstByPlateNumberAndReturnedFalse(String plateNumber);
}
