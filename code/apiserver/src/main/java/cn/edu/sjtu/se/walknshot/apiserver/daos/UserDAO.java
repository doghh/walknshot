package cn.edu.sjtu.se.walknshot.apiserver.daos;

import cn.edu.sjtu.se.walknshot.apiserver.entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDAO {
    private SessionFactory sessionFactory;

    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User getUserByName(String name) {
        Query query = sessionFactory.getCurrentSession().createQuery("FROM User u WHERE u.name = :name");
        query.setParameter("name", name);
        List entries = query.list();
        if (entries.isEmpty())
            return null;
        return (User) entries.get(0);
    }

    public User createUser(User user) {
        sessionFactory.getCurrentSession().save(user);
        return user;
    }
}
