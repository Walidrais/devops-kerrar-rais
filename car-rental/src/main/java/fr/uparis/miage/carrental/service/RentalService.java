package fr.uparis.miage.carrental.service;

import fr.uparis.miage.carrental.client.Customer;
import fr.uparis.miage.carrental.client.CustomerClient;
import fr.uparis.miage.carrental.model.Car;
import fr.uparis.miage.carrental.model.Rental;
import fr.uparis.miage.carrental.repository.CarRepository;
import fr.uparis.miage.carrental.repository.RentalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class RentalService {

    private final CarRepository cars;
    private final RentalRepository rentals;
    private final CustomerClient customerClient;

    public RentalService(CarRepository cars, RentalRepository rentals, CustomerClient customerClient) {
        this.cars = cars;
        this.rentals = rentals;
        this.customerClient = customerClient;
    }

    public Car getCar(String plateNumber) {
        return cars.findByPlateNumber(plateNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Voiture inconnue : " + plateNumber));
    }

    @Transactional
    public Rental rent(String plateNumber, Long customerId, LocalDate begin, LocalDate end) {
        if (end.isBefore(begin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date de fin est avant la date de debut");
        }
        Car car = getCar(plateNumber);
        if (car.isRented()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Voiture deja louee : " + plateNumber);
        }
        Customer customer = customerClient.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Client inconnu : " + customerId));

        long days = ChronoUnit.DAYS.between(begin, end) + 1;
        car.setRented(true);
        return rentals.save(new Rental(plateNumber, customer.id(), customer.fullName(),
                begin, end, (int) days * car.getPricePerDay()));
    }

    @Transactional
    public Rental giveBack(String plateNumber) {
        Car car = getCar(plateNumber);
        Rental rental = rentals.findFirstByPlateNumberAndReturnedFalse(plateNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                        "Cette voiture n'est pas louee : " + plateNumber));
        car.setRented(false);
        rental.setReturned(true);
        return rental;
    }
}
