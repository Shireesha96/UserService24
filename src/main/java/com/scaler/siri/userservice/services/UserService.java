package com.scaler.siri.userservice.services;

import com.scaler.siri.userservice.exception.InvalidTokenException;
import com.scaler.siri.userservice.models.Token;
import com.scaler.siri.userservice.models.User;
import com.scaler.siri.userservice.repositories.TokenRepository;
import com.scaler.siri.userservice.repositories.UserRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private TokenRepository tokenRepository;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
    }

    public Token login(String email, String password){
        Optional<User> user = userRepository.findByEmail(email); // why Optional, to avoid null pointer exception, if null is returned, if we use the object without checking null compiler throws exception, so it won't be missed
        if(user.isEmpty()){
            //Signup method or we can throw exception
            throw new RuntimeException("User with email not found in the DB");
        }
        if(!bCryptPasswordEncoder.matches(password, user.get().getHashedPassword())){
            throw new RuntimeException("Invalid password");
        }
        else{
            //Generate token
            Token token = createToken(user.get());
            Token savedToken = tokenRepository.save(token);
            return savedToken;
        }
    }

    public User Signup(String name, String email, String password){
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password)); //instead we should hash using Bcrypt algorithm
        return userRepository.save(user);
    }

    public void logout(String token) throws InvalidTokenException {
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedAndExpiryAtGreaterThan(token, false, new Date());
        if(optionalToken.isPresent()){
            Token existingToken = optionalToken.get();
            existingToken.setDeleted(true);
            tokenRepository.save(existingToken);
        }
        else{
            throw new InvalidTokenException("Invalid token");
        }
    }

    public User validateToken(String token){
        // check if token is present in DB
        // check if the token is expired

        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedAndExpiryAtGreaterThan(token, false, new Date());
        if(optionalToken.isEmpty()){
            return null;
        }

        return optionalToken.get().getUser();
    }

    private Token createToken(User user){
        Token token = new Token();
        token.setUser(user);
        token.setValue(RandomStringUtils.randomAlphanumeric(128));

        LocalDate now = LocalDate.now();
        LocalDate thirtyDaysLater = now.plusDays(30);
        Date date = Date.from(thirtyDaysLater.atStartOfDay(ZoneId.systemDefault()).toInstant());

        token.setExpiryAt(date);
        token.setDeleted(false);

        return token;

    }

}
