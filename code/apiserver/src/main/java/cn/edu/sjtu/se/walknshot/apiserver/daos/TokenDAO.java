package cn.edu.sjtu.se.walknshot.apiserver.daos;

import cn.edu.sjtu.se.walknshot.apiserver.entities.Token;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@Repository
public class TokenDAO {
    private SessionFactory sessionFactory;

    public TokenDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void storeToken(Token token) {
        sessionFactory.getCurrentSession().saveOrUpdate(token);
    }

    public Token cvtFromMessage(cn.edu.sjtu.se.walknshot.apimessages.Token mtoken) {
        if (mtoken == null)
            return null;
        Query query = sessionFactory.getCurrentSession().createQuery("FROM Token t WHERE t.userId = :user AND t.passphrase = :pass");
        query.setParameter("user", mtoken.getUserId());
        query.setParameter("pass", mtoken.getPassphrase());
        List l = query.list();
        if (l.isEmpty())
            return null;
        return (Token) l.get(0);
    }
}
