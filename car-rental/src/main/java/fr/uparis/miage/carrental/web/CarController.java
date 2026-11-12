package fr.uparis.miage.carrental.web;

import fr.uparis.miage.carrental.model.Car;
import fr.uparis.miage.carrental.model.Rental;
import fr.uparis.miage.carrental.repository.CarRepository;
import fr.uparis.miage.carrental.repository.RentalRepository;
import fr.uparis.miage.carrental.service.RentalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/**
 * GET    /cars                    liste des voitures
 * GET    /cars/{plate}            une voiture
 * POST   /cars                    ajouter une voiture
 * DELETE /cars/{plate}            supprimer une voiture
 * POST   /cars/{plate}/rent       louer (verifie le client aupres de customer-service)
 * POST   /cars/{plate}/return     rendre la voiture
 * GET    /rentals                 historique des locations
 */
@RestController
public class CarController {

    public record RentRequest(@NotNull Long customerId, @NotNull LocalDate begin, @NotNull LocalDate end) {
    }

    private final CarRepository cars;
    private final RentalRepository rentals;
    private final RentalService rentalService;

    public CarController(CarRepository cars, RentalRepository rentals, RentalService rentalService) {
        this.cars = cars;
        this.rentals = rentals;
        this.rentalService = rentalService;
    }

    @GetMapping("/cars")
    public List<Car> listCars() {
        return cars.findAll();
    }

    @GetMapping("/cars/{plate}")
    public Car getCar(@PathVariable String plate) {
        return rentalService.getCar(plate);
    }

    @PostMapping("/cars")
    @ResponseStatus(HttpStatus.CREATED)
    public Car addCar(@Valid @RequestBody Car car) {
        if (cars.existsByPlateNumber(car.getPlateNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Plaque deja enregistree : " + car.getPlateNumber());
        }
        car.setRented(false);
        return cars.save(car);
    }

    @DeleteMapping("/cars/{plate}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable String plate) {
        cars.delete(rentalService.getCar(plate));
    }

    @PostMapping("/cars/{plate}/rent")
    @ResponseStatus(HttpStatus.CREATED)
    public Rental rent(@PathVariable String plate, @Valid @RequestBody RentRequest request) {
        return rentalService.rent(plate, request.customerId(), request.begin(), request.end());
    }

    @PostMapping("/cars/{plate}/return")
    public Rental giveBack(@PathVariable String plate) {
        return rentalService.giveBack(plate);
    }

    @GetMapping("/rentals")
    public List<Rental> listRentals() {
        return rentals.findAll();
    }
}
