package cn.edu.sjtu.se.walknshot.apiserver.services;

import cn.edu.sjtu.se.walknshot.apimessages.RegisterResponse;
import cn.edu.sjtu.se.walknshot.apimessages.Util;
import cn.edu.sjtu.se.walknshot.apiserver.daos.TokenDAO;
import cn.edu.sjtu.se.walknshot.apiserver.daos.UserDAO;
import cn.edu.sjtu.se.walknshot.apiserver.entities.Token;
import cn.edu.sjtu.se.walknshot.apiserver.entities.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

@Service
public class AuthenticationService extends ServiceBase {
    private TokenDAO tokenDAO;
    private UserDAO userDAO;

    public AuthenticationService(TokenDAO tokenDAO, UserDAO userDAO) {
        this.tokenDAO = tokenDAO;
        this.userDAO = userDAO;
    }

    @Transactional
    public cn.edu.sjtu.se.walknshot.apimessages.Token login(String username, String password) {
        cn.edu.sjtu.se.walknshot.apimessages.Token mToken = new cn.edu.sjtu.se.walknshot.apimessages.Token(0, null);

        User user = userDAO.getUserByName(username);
        if (user == null || !user.getPassword().equals(password))
            return mToken;

        Token token = new Token();
        token.setUserId(user.getId());
        String passphrase = user.getName() + Math.random();
        try {
            token.setPassphrase(DatatypeConverter.printHexBinary(MessageDigest.getInstance("SHA-256").digest(passphrase.getBytes())));
        } catch (NoSuchAlgorithmException e) {
            // impossible
        }
        tokenDAO.storeToken(token);

        mToken.setUserId(token.getUserId());
        mToken.setPassphrase(token.getPassphrase());
        return mToken;
    }

    @Transactional
    public Boolean validateToken(cn.edu.sjtu.se.walknshot.apimessages.Token mtoken) {
        Token token = tokenDAO.cvtFromMessage(mtoken);
        return token != null;
    }



    @Transactional
    public RegisterResponse registerUser(String username, String password) {
        if (!Util.validUsername(username))
            return new RegisterResponse("Invalid username");
        if (!Util.validPassword(password))
            return new RegisterResponse("Invalid password");
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        try {
            int userId = userDAO.createUser(user).getId();
            return new RegisterResponse(userId);
        } catch (DataIntegrityViolationException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new RegisterResponse("Duplicate username");
        }
    }
}
