package br.uesb.cipec.loja_automatica.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.uesb.cipec.loja_automatica.DTO.TokenDTO;
import br.uesb.cipec.loja_automatica.DTO.UserDTO;
import br.uesb.cipec.loja_automatica.DTO.UserLoginDTO;
import br.uesb.cipec.loja_automatica.DTO.UserRegisterDTO;
import br.uesb.cipec.loja_automatica.DTO.UserResponseDTO;
import br.uesb.cipec.loja_automatica.DTO.UserUpdateDTO;
import br.uesb.cipec.loja_automatica.enums.UserRole;
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.mapper.UserMapper;
import br.uesb.cipec.loja_automatica.model.User;
import br.uesb.cipec.loja_automatica.repository.UserRepository;
import br.uesb.cipec.loja_automatica.security.JwtUtil;

@Service
public class UserService {
 
    @Autowired
    UserRepository repository;

    @Autowired
    UserMapper mapper;

    @Autowired 
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired 
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;


    public UserResponseDTO create(UserRegisterDTO user){
        if (user == null) {
            throw new RequiredObjectIsNullException("User cannot be null.");
        }
        if (repository.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email already registered");
        }

        var entity = mapper.toEntity(user);
        
        // encrypting the password before saving the entity 
        String encryptedPassword = passwordEncoder.encode(entity.getPassword());
        entity.setPassword(encryptedPassword);
        entity.setRole(UserRole.USER);
        repository.save(entity); //JPA automatically update the entity with the ID generated and other managed fields

        var dto = mapper.toResponseDTO(entity);

        //create a token for email validation
        TokenDTO token = new TokenDTO(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(30),
            dto.getId());
        
        tokenService.create(token);
        
        //TODO send the email
        emailService.send(dto.getEmail(), token.getToken());

        return dto;
    }

    public UserDTO findById(Long id){
        var entity = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        var dto = mapper.toDTO(entity);

        return dto;
    }

    public UserDTO findByEmail(String email){
        var entity = repository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        var dto = mapper.toDTO(entity);

        return dto;
    }

    public UserResponseDTO findByIdResponseDTO(Long id){
        var entity = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        var dto = mapper.toResponseDTO(entity);

        return dto;
    }

    public UserResponseDTO findByEmailResponseDTO(String email){
        var entity = repository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        var dto = mapper.toResponseDTO(entity);

        return dto;
    }

    public List<UserResponseDTO> findAll(){
        var entities = repository.findAll();

        var users = entities.stream()
        .map(mapper::toResponseDTO)
        .toList();

        return users;
    }

    public UserResponseDTO update(UserUpdateDTO user){
        if(user == null){
         throw new RequiredObjectIsNullException("User cannot be null.");
        }

        User entity = repository.findById(user.getId()).
        orElseThrow(()-> new ResourceNotFoundException("User not found"));
  
        if(user.getEmail() != null){
            Optional<User> userWithSameEmail = repository.findByEmail(user.getEmail());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Email address already in use by another user.");
            }
            entity.setEmail(user.getEmail()); 
        }
    
        if(user.getName()!=null){
            entity.setName(user.getName());
        }
        
        if(user.getDateOfBirth() != null){
            entity.setDateOfBirth(user.getDateOfBirth());
        }
          
        repository.save(entity);
  
        var dto = mapper.toResponseDTO(entity);
        return dto;
    }
  

    public void delete(Long id){
        User entity = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));

        repository.delete(entity);
    }

    public String authenticate(UserLoginDTO loginRequest) {
    
        var usernamePassword = new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(),
            loginRequest.getPassword()
        );

  
      var auth = this.authenticationManager.authenticate(usernamePassword);

    // Pega o principal, que é um UserDetails
    UserDetails userDetails = (UserDetails) auth.getPrincipal();

    // Usa o getUsername() do UserDetails, que já retorna o email
    return jwtUtil.generateToken(userDetails.getUsername());
    }
    
}
