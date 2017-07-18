package cn.edu.sjtu.se.walknshot.apiserver.daos;

import cn.edu.sjtu.se.walknshot.apiserver.entities.Picture;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class PictureDAO {
    private SessionFactory sessionFactory;

    public PictureDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Picture addPicture(Long spotId, int storageId) {
        Picture picture = new Picture();
        picture.setSpotId(spotId);
        picture.setStorageId(storageId);
        sessionFactory.getCurrentSession().save(picture);
        return picture;
    }
}
