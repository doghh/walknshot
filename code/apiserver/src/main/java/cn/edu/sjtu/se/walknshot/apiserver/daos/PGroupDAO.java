package cn.edu.sjtu.se.walknshot.apiserver.daos;

import cn.edu.sjtu.se.walknshot.apiserver.entities.PGroup;
import org.hibernate.SessionFactory;

public class PGroupDAO {
    private SessionFactory sessionFactory;

    public PGroupDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addPGroup(PGroup pgroup) {
        sessionFactory.getCurrentSession().save(pgroup);
    }
}
