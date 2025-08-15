package br.uesb.cipec.loja_automatica.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.uesb.cipec.loja_automatica.DTO.TokenDTO;
import br.uesb.cipec.loja_automatica.DTO.UserDTO;
import br.uesb.cipec.loja_automatica.exception.UserAlreadyVerifiedException;
import br.uesb.cipec.loja_automatica.mapper.UserMapper;
import br.uesb.cipec.loja_automatica.repository.UserRepository;

@Service
public class UserConfirmationService {
    
    @Autowired
    UserMapper mapper;

    @Autowired
    UserRepository repository;

    @Autowired 
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Autowired 
    private UserService userService;

    public void sendConfirmationToken(String email){
        UserDTO userDTO = userService.findByEmail(email);

        if (userService.isUserEnabled(userDTO.getId())) {
            throw new UserAlreadyVerifiedException("This user account is already verified and active.");
        }

         //create a token 
         TokenDTO token = new TokenDTO(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(15),
            userDTO.getId());
        
        tokenService.create(token);
        
        //send an email with the validation link
        emailService.send(userDTO.getName(), userDTO.getEmail(), token.getToken());
        
    }

    public void confirmUserEmail(String token){
        TokenDTO tokenDTO = tokenService.findByToken(token);
        UserDTO userDTO = userService.findById(tokenDTO.getUserID());

        //check if the user is already enable
        if(userService.isUserEnabled(userDTO.getId())){
            throw new UserAlreadyVerifiedException("This user account is already verified and active.");
        }

        //confirm the token
        tokenService.confirmToken(token);

        //enable the user
        userService.enableUser(userDTO.getId());
    }
}

