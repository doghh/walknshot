package cn.edu.sjtu.se.walknshot.apiserver.services;

import cn.edu.sjtu.se.walknshot.apimessages.Util;
import cn.edu.sjtu.se.walknshot.apiserver.daos.SpotDAO;
import cn.edu.sjtu.se.walknshot.apiserver.entities.Spot;
import org.springframework.transaction.annotation.Transactional;

public class JourneyService {
    private SpotDAO spotDAO;

    public JourneyService(SpotDAO spotDAO) {
        this.spotDAO = spotDAO;
    }

    @Transactional
    public Long addSpot(int userId, double latitude, double longitude) {
        if (!Util.validLatLng(latitude, longitude))
            return null;
        Spot spot = new Spot();
        spot.setUserId(userId);
        spot.setLatitude(latitude);
        spot.setLongitude(longitude);
        spotDAO.store(spot);
        return spot.getId();
    }
}
