package cn.edu.sjtu.se.walknshot.apiserver.controllers;

import cn.edu.sjtu.se.walknshot.apimessages.Token;
import cn.edu.sjtu.se.walknshot.apiserver.services.AuthenticationService;
import cn.edu.sjtu.se.walknshot.apiserver.services.PictureService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PictureController {
    private AuthenticationService auth;
    private PictureService pic;

    public PictureController(AuthenticationService auth, PictureService pic) {
        this.auth = auth;
        this.pic = pic;
    }

    @PostMapping("/picture/upload")
    @ResponseBody
    public Object uploadPicture(
            @RequestParam("token") String sToken,
            @RequestParam("spot") Long spotId,
            @RequestParam("file") MultipartFile file
            ) {
        Token token = Token.fromString(sToken);
        if (!auth.validateToken(token))
            return null;

        InputStream stream;
        try {
            stream = file.getInputStream();
        } catch (IOException e) {
            return null;
        }

        return pic.storePicture(token.getUserId(), spotId, stream);
    }

    @PostMapping("/static/picture")
    @ResponseBody
    public Object uploadPicture(
            @RequestParam("name") String filename
            ) {
        if (filename == null)
            return null;

        InputStreamResource isr = new InputStreamResource(pic.getPicture(filename));
        return new ResponseEntity<>(isr, HttpStatus.OK);
    }

    @PostMapping("/pgroup/upload")
    @ResponseBody
    public Object uploadPGroup(
            @RequestParam("token") String sToken,
            @RequestParam("file[]") List<MultipartFile> files
        ) {
        Token token = Token.fromString(sToken);
        if (!auth.validateToken(token))
            return null;
        List<InputStream> streamList = new ArrayList<>();
        try {
            for (MultipartFile file : files)
                streamList.add(file.getInputStream());
            return pic.storePGroup(token.getUserId(), streamList);
        } catch (IOException e) {
            return null;
        }
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    public Object handleMultipartException(MultipartException e) {
        return null;
    }
}
