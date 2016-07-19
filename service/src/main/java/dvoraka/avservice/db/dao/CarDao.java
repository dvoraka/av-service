package dvoraka.avservice.db.dao;

import dvoraka.avservice.db.model.Car;

import java.util.List;

/**
 * Car DAO.
 */
public interface CarDao {

    long count();

    List<Car> getCars();
}
