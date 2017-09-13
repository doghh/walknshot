package cn.edu.sjtu.se.walknshot.apiserver.services;

import cn.edu.sjtu.se.walknshot.apimessages.CommentEntry;
import cn.edu.sjtu.se.walknshot.apimessages.PGroupDetails;
import cn.edu.sjtu.se.walknshot.apimessages.PictureEntry;
import cn.edu.sjtu.se.walknshot.apiserver.daos.PGroupDAO;
import cn.edu.sjtu.se.walknshot.apiserver.daos.PictureDAO;
import cn.edu.sjtu.se.walknshot.apiserver.daos.SpotDAO;
import cn.edu.sjtu.se.walknshot.apiserver.daos.StorageDAO;
import cn.edu.sjtu.se.walknshot.apiserver.entities.Comment;
import cn.edu.sjtu.se.walknshot.apiserver.entities.PGroup;
import cn.edu.sjtu.se.walknshot.apiserver.entities.Picture;
import cn.edu.sjtu.se.walknshot.apiserver.entities.Storage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Service
public class PictureService {
    private PGroupDAO pgroupDAO;
    private PictureDAO pictureDAO;
    private SpotDAO spotDAO;
    private StorageDAO storageDAO;

    private final String storageCollection = "picture";
    public PictureService(PGroupDAO pgroupDAO, PictureDAO pictureDAO, SpotDAO spotDAO, StorageDAO storageDAO) {
        this.pgroupDAO = pgroupDAO;
        this.pictureDAO = pictureDAO;
        this.spotDAO = spotDAO;
        this.storageDAO = storageDAO;
    }

    @Transactional
    public PictureEntry storePicture(int userId, Long spotId, InputStream stream) {
        if (spotId != null && spotDAO.getSpot(spotId).getUserId() != userId)
            return null;
        Storage storage = storageDAO.storeFile(storageCollection, stream);
        Picture picture = pictureDAO.addPicture(spotId, storage.getId());

        PictureEntry entry = new PictureEntry();
        entry.setId(picture.getId());
        entry.setSpotId(spotId);
        entry.setStorageName(storage.getFilename());

        return entry;
    }

    public InputStream getPicture(String filename) {
        /*
        try {
            return IOUtils.toByteArray(storageDAO.getFile(storageCollection, filename));
        } catch (IOException e) {
            return null;
        }
        */
        return storageDAO.getFile(storageCollection, filename);
    }

    @Transactional
    public PGroupDetails storePGroup(int userId, List<InputStream> streams) {
        PGroupDetails details = new PGroupDetails();
        PGroup pgroup = new PGroup();
        for (InputStream stream : streams) {
            Storage storage = storageDAO.storeFile(storageCollection, stream);
            Picture picture = pictureDAO.addPicture(null, storage.getId());
            PictureEntry entry = new PictureEntry();
            entry.setId(picture.getId());
            entry.setStorageName(storage.getFilename());
            pgroup.getPictures().add(picture);
            details.getPictures().add(entry);
        }
        pgroupDAO.addPGroup(pgroup);
        return details;
    }

    @Transactional
    public PGroupDetails getPGroup(int pgroupId) {
        PGroupDetails details = new PGroupDetails();
        List pictures = details.getPictures();
        PGroup pgroup = pgroupDAO.getPGroup(pgroupId);
        for (Picture picture : pgroup.getPictures()) {
            PictureEntry entry = new PictureEntry();
            entry.setId(picture.getId());
            entry.setStorageName(storageDAO.getFilename(picture.getStorageId()));
            details.getPictures().add(entry);
        }
        List<Comment> comments = pgroupDAO.getComments(pgroupId);
        for (Comment comment : comments) {
            CommentEntry entry = new CommentEntry();
            entry.setUserId(comment.getUserId());
            entry.setTime(comment.getTime());
            entry.setContent(comment.getContent());
            details.getComments().add(entry);
        }
        return details;
    }

    @Transactional
    public CommentEntry addComment(int userId, int pgroupId, String content) {
        Comment comment = pgroupDAO.addComment(pgroupId, userId, content);
        CommentEntry entry = new CommentEntry();
        entry.setUserId(userId);
        entry.setContent(comment.getContent());
        entry.setTime(comment.getTime());
        return entry;
    }


    @Transactional
    public List<Integer> getPGroupList(Integer uid) {
        if (uid == null)
            return pgroupDAO.getAll();
        else
            return pgroupDAO.getByUser(uid);
    }
}
