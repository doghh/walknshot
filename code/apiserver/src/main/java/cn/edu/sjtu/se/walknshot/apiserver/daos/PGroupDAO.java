package cn.edu.sjtu.se.walknshot.apiserver.daos;

import cn.edu.sjtu.se.walknshot.apiserver.entities.Comment;
import cn.edu.sjtu.se.walknshot.apiserver.entities.PGroup;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class PGroupDAO {
    private SessionFactory sessionFactory;

    public PGroupDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addPGroup(PGroup pgroup) {
        sessionFactory.getCurrentSession().save(pgroup);
    }

    public PGroup getPGroup(int id) {
        return sessionFactory.getCurrentSession().get(PGroup.class, id);
    }

    public Comment addComment(int pgroupId, int userId, String content) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPgroupId(pgroupId);
        comment.setContent(content);
        sessionFactory.getCurrentSession().save(comment);
        return sessionFactory.getCurrentSession().get(Comment.class, comment.getId());
    }

    @SuppressWarnings("unchecked")
    public List<Comment> getComments(int pgroupId) {
        Query query = sessionFactory.getCurrentSession().createQuery("FROM Comment c WHERE c.pgroupId = :pgroupId");
        query.setParameter("pgroupId", pgroupId);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getAll() {
        List<PGroup> pg = sessionFactory.getCurrentSession().createQuery("FROM PGroup").list();
        List<Integer> rv = new ArrayList<Integer>();
        for (PGroup pgroup : pg)
            rv.add(pgroup.getId());
        return rv;
    }

    public List<Integer> getByUser(int uid) {
        return getAll();
    }
}
