package cn.edu.sjtu.se.walknshot.apiserver.daos;

import cn.edu.sjtu.se.walknshot.apiserver.entities.Spot;
import org.hibernate.SessionFactory;

public class SpotDAO {
    private SessionFactory sessionFactory;

    public SpotDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void store(Spot spot) {
        sessionFactory.getCurrentSession().save(spot);
    }

    public Spot getSpot(long id) {
        return sessionFactory.getCurrentSession().load(Spot.class, id);
    }
}
