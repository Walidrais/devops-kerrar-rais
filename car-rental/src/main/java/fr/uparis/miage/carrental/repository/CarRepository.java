package fr.uparis.miage.carrental.repository;

import fr.uparis.miage.carrental.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByPlateNumber(String plateNumber);

    boolean existsByPlateNumber(String plateNumber);
}
