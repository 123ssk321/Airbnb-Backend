package scc.storage;

import scc.data.dao.RentalDAO;

import java.util.List;

public interface RentalsStorage {

    RentalDAO putRental(RentalDAO rental);

    RentalDAO getRental(String rentalId);

    boolean hasRental(String rentalId);

    void deleteRentalById(String rentalId);

    void deleteRental(RentalDAO rental);

    List<RentalDAO> getRentalsByUser(String userId, int start, int length);

    List<RentalDAO> getRentalsByHouse(String houseId, int start, int length);

}
