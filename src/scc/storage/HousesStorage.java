package scc.storage;

import scc.data.dao.HouseDAO;
import scc.data.dto.DiscountedRental;
import scc.data.dto.HouseList;
import scc.data.dto.HouseOwner;

import java.util.List;

public interface HousesStorage {

    HouseDAO putHouse(HouseDAO house);

    HouseDAO getHouse(String houseId);

    void deleteHouseById(String houseId);

    void deleteHouse(HouseDAO house);

    List<HouseList> searchHouses(String location, String startDate, String endDate, int start, int length);

    List<DiscountedRental> getDiscountedHouses(int start, int length);

    List<HouseOwner> getHousesByOwner(String ownerId, int start, int length);

}
