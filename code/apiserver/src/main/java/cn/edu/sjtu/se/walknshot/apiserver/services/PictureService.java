package cn.edu.sjtu.se.walknshot.apiserver.services;

import cn.edu.sjtu.se.walknshot.apimessages.PictureEntry;
import cn.edu.sjtu.se.walknshot.apiserver.daos.PictureDAO;
import cn.edu.sjtu.se.walknshot.apiserver.daos.SpotDAO;
import cn.edu.sjtu.se.walknshot.apiserver.daos.StorageDAO;
import cn.edu.sjtu.se.walknshot.apiserver.entities.Picture;
import cn.edu.sjtu.se.walknshot.apiserver.entities.Storage;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;

@Service
public class PictureService {
    private PictureDAO pictureDAO;
    private SpotDAO spotDAO;
    private StorageDAO storageDAO;

    private final String storageCollection = "picture";

    public PictureService(PictureDAO pictureDAO, SpotDAO spotDAO, StorageDAO storageDAO) {
        this.pictureDAO = pictureDAO;
        this.spotDAO = spotDAO;
        this.storageDAO = storageDAO;
    }

    @Transactional
    public PictureEntry storePicture(int userId, long spotId, InputStream stream) {
        if (spotDAO.getSpot(spotId).getUserId() != userId)
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
}
