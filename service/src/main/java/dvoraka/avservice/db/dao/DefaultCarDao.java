package dvoraka.avservice.db.dao;

import dvoraka.avservice.db.model.Car;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Default Car DAO implementation.
 */
@Repository
public class DefaultCarDao implements CarDao {

    private final SessionFactory sessionFactory;


    @Autowired
    public DefaultCarDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    @Override
    public long count() {
        Session session = sessionFactory.getCurrentSession();
        String query = "SELECT count(*) from Car";

        return (long) session.createQuery(query).uniqueResult();
    }

    @Transactional
    @Override
    public List<Car> getCars() {
        return new ArrayList<>();
    }
}
