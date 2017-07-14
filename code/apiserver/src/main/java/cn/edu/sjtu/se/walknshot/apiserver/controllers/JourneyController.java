package cn.edu.sjtu.se.walknshot.apiserver.controllers;

import cn.edu.sjtu.se.walknshot.apimessages.Token;
import cn.edu.sjtu.se.walknshot.apiserver.services.AuthenticationService;
import cn.edu.sjtu.se.walknshot.apiserver.services.JourneyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JourneyController {
    private AuthenticationService auth;
    private JourneyService jour;

    public JourneyController(AuthenticationService auth, JourneyService jour) {
        this.auth = auth;
        this.jour = jour;
    }

    @RequestMapping("/spot/add")
    @ResponseBody
    public Object addSpot(
            @RequestParam("token") String sToken,
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude
            ) {
        Token token = Token.fromString(sToken);
        if (!auth.validateToken(token))
            return null;
        return jour.addSpot(token.getUserId(), latitude, longitude);
    }
}
