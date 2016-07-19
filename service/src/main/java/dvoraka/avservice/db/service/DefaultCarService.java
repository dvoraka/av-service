package dvoraka.avservice.db.service;

import dvoraka.avservice.db.dao.CarDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default Car service implementation.
 */
@Service
@Transactional
public class DefaultCarService implements CarService {

    @Autowired
    private CarDao carDao;


    @Override
    public long count() {
        return carDao.count();
    }
}
