package br.uesb.cipec.loja_automatica.service;



import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import br.uesb.cipec.loja_automatica.DTO.UserDTO;
import br.uesb.cipec.loja_automatica.DTO.UserLoginDTO;
import br.uesb.cipec.loja_automatica.DTO.UserRegisterDTO;
import br.uesb.cipec.loja_automatica.DTO.UserResponseDTO;
import br.uesb.cipec.loja_automatica.DTO.UserUpdateDTO;
import br.uesb.cipec.loja_automatica.component.AuthenticationFacade;
import br.uesb.cipec.loja_automatica.enums.UserRole;
import br.uesb.cipec.loja_automatica.exception.BadCredentialsException;
import br.uesb.cipec.loja_automatica.exception.EmailAlreadyInUseException;
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
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private JwtUtil jwtUtil;

    public UserResponseDTO create(UserRegisterDTO user){
        if (user == null) {
            throw new RequiredObjectIsNullException("User cannot be null.");
        }
        if (repository.findByEmail(user.getEmail()).isPresent()){
           throw new EmailAlreadyInUseException("Email already in use.");
        }

        var entity = mapper.toEntity(user);
        
        // encrypting the password before saving the entity 
        String encryptedPassword = passwordEncoder.encode(entity.getPassword());
        entity.setPassword(encryptedPassword);
        entity.setRole(UserRole.USER);
        repository.save(entity); //JPA automatically update the entity with the ID generated and other managed fields

        var dto = mapper.toResponseDTO(entity);

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

    public Page<UserResponseDTO> findAll(Pageable pageable){
        Page<User> users = repository.findAll(pageable);


        return users.map(mapper::toResponseDTO);
    }

    public UserResponseDTO update(UserUpdateDTO user){

        if (user == null) {
            throw new RequiredObjectIsNullException("It is not allowed to persist a null object!");
        }

        User entity = authenticationFacade.getCurrentUser();
  
        if(user.getEmail() != null){
            Optional<User> userWithSameEmail = repository.findByEmail(user.getEmail());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(user.getId())) {
                throw new EmailAlreadyInUseException("Email already in use.");
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
  
    public boolean isUserEnabled(Long userId) {
        return repository.isUserEnabled(userId)
                .orElseThrow(() -> new  ResourceNotFoundException("User not found"));
    }
    
    public void deleteMyAccount() {
        // Take the logged user
        User currentUser = authenticationFacade.getCurrentUser();

        // Instead of deleting the tuple we deactivate it
        currentUser.setActive(false); ;
        repository.save(currentUser);
    }


    public String authenticate(UserLoginDTO loginRequest) {
    
        if(!repository.existsByEmailAndEnabledTrue(loginRequest.getEmail())){
            throw new BadCredentialsException("Bad credentials.");
        }

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


    public UserResponseDTO getMyProfile() {
        // Usa a facade para pegar o usuário logado de forma segura
        User currentUser = authenticationFacade.getCurrentUser();
        
        // Mapeia a entidade para um DTO de resposta seguro (sem a senha)
        return mapper.toResponseDTO(currentUser);
    }

    public void enableUser(Long userID){
        var user = repository.findById(userID)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userID));
        user.setEnabled(true);
        repository.save(user);
    }

}
