package fr.uparis.miage.carrental.client;

/** Client tel que renvoye par le service customer-service. */
public record Customer(Long id, String firstName, String lastName, String email) {

    public String fullName() {
        return firstName + " " + lastName;
    }
}
