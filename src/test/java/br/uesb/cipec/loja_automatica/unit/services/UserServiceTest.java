package br.uesb.cipec.loja_automatica.unit.services;

import br.uesb.cipec.loja_automatica.DTO.UserDTO;
import br.uesb.cipec.loja_automatica.DTO.UserRegisterDTO;
import br.uesb.cipec.loja_automatica.DTO.UserResponseDTO;
import br.uesb.cipec.loja_automatica.DTO.UserUpdateDTO;
import br.uesb.cipec.loja_automatica.component.AuthenticationFacade;
import br.uesb.cipec.loja_automatica.exception.EmailAlreadyInUseException;
import br.uesb.cipec.loja_automatica.exception.InvalidUserDataException;
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.mapper.UserMapper;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.model.User;
import br.uesb.cipec.loja_automatica.repository.UserRepository;
import br.uesb.cipec.loja_automatica.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.mapping.Any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    //Mocks for UserService dependencies
    @Mock
    private UserRepository repository;
    @Mock
    private UserMapper mapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationFacade authenticationFacade;

    

    // Real service instance with injected mocks
    @InjectMocks
    private UserService service;

    // Test data
    private User userEntity;
    private UserRegisterDTO userRegisterDTO;
    private UserResponseDTO userResponseDTO;
    private LocalDate birthDate;

    @BeforeEach
    void setUp() {
        birthDate = LocalDate.of(2003, 11, 14);

        userEntity = new User();
        userEntity.setEmail("teste@gmail.com");
        userEntity.setName("Franco");
        userEntity.setPassword("12345"); 
        userEntity.setDateOfBirth(birthDate);

        userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("teste@gmail.com");
        userRegisterDTO.setName("Franco");
        userRegisterDTO.setDateOfBirth(birthDate);
        userRegisterDTO.setPassword("12345"); // Original passowrd

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setEmail("teste@gmail.com");
        userResponseDTO.setName("Franco");
        userResponseDTO.setDateOfBirth(birthDate);
        userResponseDTO.setDateOfRegistration(LocalDate.now());
        userResponseDTO.setId(1L);

        
    }

    // Research test

    @Test
    @DisplayName("Given an existing ID, when findById is called, then should return UserDTO")
    void givenExistingId_whenFindById_thenReturnsUserDTO() {
        // ARRANGE
      
        when(repository.findById(1L)).thenReturn(Optional.of(userEntity));
     
        when(mapper.toDTO(userEntity)).thenReturn(new UserDTO()); 

        // ACT
        UserDTO result = service.findById(1L);

        // ASSERT
        assertNotNull(result);
        verify(repository, times(1)).findById(1L);
        verify(mapper, times(1)).toDTO(userEntity);
    }

    @Test
    @DisplayName("Given a non-existent ID, when findById is called, then should throw ResourceNotFoundException")
    void givenNonExistentId_whenFindById_thenThrowsResourceNotFoundException() {
        // ARRANGE
 
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(99L);
        });
    }



    @Test
    @DisplayName("Given an existing email, when findByEmail is called, then should return UserDTO")
    void givenExistingEmail_whenFindByEmail_thenReturnsUserDTO() {
        // ARRANGE
        when(repository.findByEmail("teste@gmail.com")).thenReturn(Optional.of(userEntity));
        when(mapper.toDTO(userEntity)).thenReturn(new UserDTO());

        // ACT
        UserDTO result = service.findByEmail("teste@gmail.com");

        // ASSERT
        assertNotNull(result);
        verify(repository, times(1)).findByEmail("teste@gmail.com");
    }

    @Test
    @DisplayName("Given a non-existent email, when findByEmail is called, then should throw ResourceNotFoundException")
    void givenNonExistentEmail_whenFindByEmail_thenThrowsResourceNotFoundException() {
        // ARRANGE
        when(repository.findByEmail("wrong@email.com")).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, () -> {
            service.findByEmail("wrong@email.com");
        });
    }

   

    @Test
    @DisplayName("Given an existing ID, when findByIdResponseDTO is called, then should return UserResponseDTO")
    void givenExistingId_whenFindByIdResponseDTO_thenReturnsUserResponseDTO() {
        // ARRANGE
        when(repository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(mapper.toResponseDTO(userEntity)).thenReturn(userResponseDTO);

        // ACT
        UserResponseDTO result = service.findByIdResponseDTO(1L);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Franco", result.getName());
    }

    @Test
    @DisplayName("Given a non-existent ID, when findByIdResponseDTO is called, then should throw ResourceNotFoundException")
    void givenNonExistentId_whenFindByIdResponseDTO_thenThrowsResourceNotFoundException() {
        // ARRANGE
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, () -> {
            service.findByIdResponseDTO(99L);
        });
    }


    @Test
    @DisplayName("Given an existing email, when findByEmailResponseDTO is called, then should return UserResponseDTO")
    void givenExistingEmail_whenFindByEmailResponseDTO_thenReturnsUserResponseDTO() {
        // ARRANGE
        when(repository.findByEmail("teste@gmail.com")).thenReturn(Optional.of(userEntity));
        when(mapper.toResponseDTO(userEntity)).thenReturn(userResponseDTO);

        // ACT
        UserResponseDTO result = service.findByEmailResponseDTO("teste@gmail.com");

        // ASSERT
        assertNotNull(result);
        assertEquals("teste@gmail.com", result.getEmail());
    }

    @Test
    @DisplayName("Given a non-existent email, when findByEmailResponseDTO is called, then should throw ResourceNotFoundException")
    void givenNonExistentEmail_whenFindByEmailResponseDTO_thenThrowsResourceNotFoundException() {
        // ARRANGE
        when(repository.findByEmail("wrong@email.com")).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, () -> {
            service.findByEmailResponseDTO("wrong@email.com");
        });
    }

    @Test
    void givenAPageOfUsers_WhenFindAll_ThenReturnPageOfPeople(){
        // Arrange

         List<User>  users = new ArrayList();
      for(int i = 0 ; i < 10 ; i++){
        users.add(userEntity);
      }

    Pageable pageableWithUsers = PageRequest.of(0, 10);

    Page<User> pageImpl = new PageImpl<>(users , pageableWithUsers , 10L);

    when(mapper.toResponseDTO(userEntity)).thenReturn(userResponseDTO);

    when(repository.findAll(any(Pageable.class))).thenReturn(pageImpl);

    // Act 
    Page<UserResponseDTO> resultPage = service.findAll(pageableWithUsers);

     assertNotNull(resultPage);
    assertEquals(10, resultPage.getContent().size());
    assertEquals("Franco", resultPage.getContent().get(0).getName());
    assertEquals(1, resultPage.getTotalPages());
    assertEquals(10L, resultPage.getTotalElements());


    }

    // --- Tests for create method---

    @Test
    @DisplayName("Given valid new user data, when create is called, then should return the saved user")
    void givenValidUser_whenCreate_thenReturnsSavedUser() {
        // Given ; Arrange
        
        //Ensure the email did not exist
    when(repository.findByEmail(userRegisterDTO.getEmail())).thenReturn(Optional.empty());

    when(mapper.toEntity(userRegisterDTO)).thenReturn(userEntity);

    // Encode de passowrd
    when(passwordEncoder.encode("12345")).thenReturn("fadsnj@!3fds@4");

    when(repository.save(userEntity)).thenReturn(userEntity);

    when(mapper.toResponseDTO(userEntity)).thenReturn(userResponseDTO);

    // When ; Act

    UserResponseDTO savedUser = service.create(userRegisterDTO);

    // Then ; Assert

    assertNotNull(savedUser);
    assertEquals("Franco", savedUser.getName());
    assertEquals("teste@gmail.com", savedUser.getEmail());
    assertEquals(1L, savedUser.getId());
    assertEquals(LocalDate.now(), savedUser.getDateOfRegistration());

    verify(passwordEncoder, times(1)).encode("12345");
    verify(repository, times(1)).save(userEntity);
    

    }



    @Test
    @DisplayName("Should throw InvalidUserDataException when creating with a blank name")
    void givenUserWithBlankName_whenCreate_thenThrowsInvalidUserDataException() {
        // ARRANGE
        userRegisterDTO.setName(""); // Prepara o dado inválido

        // ACT & ASSERT
        Exception exception = assertThrows(InvalidUserDataException.class, () -> {
            service.create(userRegisterDTO);
        });

        String expectedMessage = "User name cannot be null or empty.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

     @Test
    @DisplayName("Should throw InvalidUserDataException when creating with a null name")
    void givenUserWithNullkName_whenCreate_thenThrowsInvalidUserDataException() {
        // ARRANGE
        userRegisterDTO.setName(null); // Prepara o dado inválido

        // ACT & ASSERT
        Exception exception = assertThrows(InvalidUserDataException.class, () -> {
            service.create(userRegisterDTO);
        });

        String expectedMessage = "User name cannot be null or empty.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Should throw InvalidUserDataException when creating with a blank email")
    void givenUserWithBlankEmail_whenCreate_thenThrowsInvalidUserDataException() {
        // ARRANGE
        userRegisterDTO.setEmail(""); // Prepara o dado inválido

        // ACT & ASSERT
        Exception exception = assertThrows(InvalidUserDataException.class, () -> {
            service.create(userRegisterDTO);
        });

        String expectedMessage = "User email cannot be null or empty.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }


    @Test
    @DisplayName("Should throw InvalidUserDataException when creating with a null email")
    void givenUserWithNullEmail_whenCreate_thenThrowsInvalidUserDataException() {
        // ARRANGE
        userRegisterDTO.setEmail(null); // Prepara o dado inválido

        // ACT & ASSERT
        Exception exception = assertThrows(InvalidUserDataException.class, () -> {
            service.create(userRegisterDTO);
        });

        String expectedMessage = "User email cannot be null or empty.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Should throw InvalidUserDataException when creating with a blank password")
    void givenUserWithBlankPassword_whenCreate_thenThrowsInvalidUserDataException() {
        // ARRANGE
        userRegisterDTO.setPassword(""); // Prepara o dado inválido

        // ACT & ASSERT
        Exception exception = assertThrows(InvalidUserDataException.class, () -> {
            service.create(userRegisterDTO);
        });

        String expectedMessage = "User password cannot be null or empty.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Given an already registered email, when create is called, then should throw IllegalArgumentException")
    void givenExistingEmail_whenCreate_thenThrowsException() {

        //Given ; Arrange
       when(repository.findByEmail(userRegisterDTO.getEmail())).thenReturn(Optional.of(userEntity));

       
       // Act| Assert
        Exception exception = assertThrows(EmailAlreadyInUseException.class, 
        () -> {
            service.create(userRegisterDTO);
        });

      String expectedMessage = "Email already in use.";
      String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals(expectedMessage,actualMessage);


    }

    @Test
    @DisplayName("Given a null user DTO, when create is called, then should throw RequiredObjectIsNullException")
    void givenNullUser_whenCreate_thenThrowsException() {

        // Act| Assert
        Exception exception = assertThrows(RequiredObjectIsNullException.class, 
        () -> {
            service.create(null);
        });

      String expectedMessage = "User cannot be null.";
      String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals(expectedMessage,actualMessage);
    }

    // --- Testes para o método getMyProfile ---

    @Test
    @DisplayName("Given an authenticated user, when getMyProfile is called, then should return their profile")
    void givenAuthenticatedUser_whenGetMyProfile_thenReturnsUserProfile() {

      // Given ; Arrange
      when(authenticationFacade.getCurrentUser()).thenReturn(userEntity);

      when(mapper.toResponseDTO(userEntity)).thenReturn(userResponseDTO);

      // when ; act

        UserResponseDTO responseAuthentication = service.getMyProfile();

      // Then , Assert 

      assertNotNull(responseAuthentication);
      assertEquals(userResponseDTO, responseAuthentication);
      assertEquals(userResponseDTO.getName(), responseAuthentication.getName());
      assertEquals(userResponseDTO.getEmail(), responseAuthentication.getEmail());

    }
    
    // --- Testes para o método updateMyProfile ---

    @Test
    void givenNullUser_whenUpdateMyProfile_ThenThowsExecpion(){

        Exception exception = assertThrows(RequiredObjectIsNullException.class,
         ()->{
            service.update(null);
         });

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Given valid update data, when updateMyProfile is called, then should return updated user")
    void givenValidUpdateData_whenUpdateMyProfile_thenReturnsUpdatedUser() {

      // Given ; Arrange
        when(authenticationFacade.getCurrentUser()).thenReturn(userEntity);

        LocalDate newBirthDate = LocalDate.of(2000, 01, 01);
        UserUpdateDTO updateRequestDTO = new UserUpdateDTO();
        updateRequestDTO.setName("Franco Atualizado");
        updateRequestDTO.setEmail("franco.atualizado@example.com");
        updateRequestDTO.setDateOfBirth(newBirthDate);

      // When ; Act
         service.update(updateRequestDTO);

      // Then ; Assert
        verify(repository, times(1)).save(userEntity);

        assertEquals("Franco Atualizado", userEntity.getName());
        assertEquals("franco.atualizado@example.com", userEntity.getEmail());
        assertEquals(newBirthDate, userEntity.getDateOfBirth());

        

    }

    @Test
    @DisplayName("Given an update DTO with a null name, when updateMyProfile is called, then the original name should be kept")
    void givenUpdateDtoWithNullName_whenUpdateMyProfile_thenOriginalNameShouldBeKept() {
        // --- ARRANGE ---

        when(authenticationFacade.getCurrentUser()).thenReturn(userEntity);

   
        UserUpdateDTO updateRequestDTO = new UserUpdateDTO();
        updateRequestDTO.setName(null); 
        updateRequestDTO.setEmail("franco.novoemail@example.com");

     
        when(repository.save(any(User.class))).thenReturn(userEntity);
        
        // --- ACT ---

        service.update(updateRequestDTO);

        // --- ASSERT ---

        verify(repository, times(1)).save(userEntity);
        
        assertEquals("Franco", userEntity.getName());
       
        assertEquals("franco.novoemail@example.com", userEntity.getEmail());
    }
    @Test
    @DisplayName("Given an update DTO with a null name, when updateMyProfile is called, then the original name should be kept")
    void givenUpdateDtoWithNullBirthday_whenUpdateMyProfile_thenOriginalBirthdayShouldBeKept() {
        // --- ARRANGE ---

        when(authenticationFacade.getCurrentUser()).thenReturn(userEntity);

   
        UserUpdateDTO updateRequestDTO = new UserUpdateDTO();
        updateRequestDTO.setDateOfBirth(null); 
        updateRequestDTO.setEmail("franco.novoemail@example.com");

     
        when(repository.save(any(User.class))).thenReturn(userEntity);
        
        // --- ACT ---

        service.update(updateRequestDTO);

        // --- ASSERT ---

        verify(repository, times(1)).save(userEntity);
        
        assertNotNull(updateRequestDTO);
        assertEquals(birthDate , userEntity.getDateOfBirth());
        assertEquals("franco.novoemail@example.com", userEntity.getEmail());
    }

    @Test
    @DisplayName("Given an email that belongs to another user, when updateMyProfile is called, then should throw exception")
    void givenExistingEmailForOtherUser_whenUpdateMyProfile_thenThrowsException() {
        // Given; Arrange

        User otherUserWithSameEmail = new User();
            otherUserWithSameEmail.setId(2L);
            otherUserWithSameEmail.setEmail("email.repetido@example.com");

        UserUpdateDTO updateRequestDTO = new UserUpdateDTO();
        updateRequestDTO.setEmail("email.repetido@example.com");

        when(authenticationFacade.getCurrentUser()).thenReturn(userEntity);

         when(repository.findByEmail("email.repetido@example.com"))
         .thenReturn(Optional.of(otherUserWithSameEmail));

        // When ; Act 

          Exception exception = assertThrows(EmailAlreadyInUseException.class, () -> {
            service.update(updateRequestDTO);
        });

        String expectedMessage = "Email already in use";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
   
    }

    // --- Delete Tests ---

    @Test
    @DisplayName("Given an authenticated user, when deleteMyAccount is called, then should perform a soft delete")
    void givenAuthenticatedUser_whenDeleteMyAccount_thenPerformsSoftDelete() {
       // Given , Arrange

       userEntity.setActive(true);

       when(authenticationFacade.getCurrentUser()).thenReturn(userEntity);


      // When , Act

      service.deleteMyAccount();

      // Then , Assert
     assertFalse(userEntity.isActive());

     verify(repository, times(1)).save(userEntity);
    }

    // Tests for enabled user

     @Test
    @DisplayName("Given an existing and enabled user, when isUserEnabled is called, then should return true")
    void givenEnabledUser_whenIsUserEnabled_thenReturnsTrue() {
        
  
        // Given ; Arrange
       when(repository.isUserEnabled(1L)).thenReturn(Optional.of(true));

        // When ; Act
        Boolean enabled = service.isUserEnabled(1L);

        // Then ; Assert
        assertEquals(true, enabled);

    }

    @Test
    @DisplayName("Given an existing but disabled user, when isUserEnabled is called, then should return false")
    void givenDisabledUser_whenIsUserEnabled_thenReturnsFalse() {
        

       when(repository.isUserEnabled(1L)).thenReturn(Optional.of(false));

        Boolean enabled = service.isUserEnabled(1L);

        assertEquals(false, enabled);

    }

    @Test
    @DisplayName("Given a non-existent user ID, when isUserEnabled is called, then should throw ResourceNotFoundException")
    void givenNonExistentUser_whenIsUserEnabled_thenThrowsResourceNotFoundException() {

        when(repository.isUserEnabled(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
         () -> {
            service.isUserEnabled(1L);
         });

        String expectedMessage = "User not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

       
    }

    @Test
    @DisplayName("Given an existing user, when enableUser is called, then should set user to enabled and save")
    void givenExistingUser_whenEnableUser_thenSetsEnabledToTrueAndSaves() {
       
       
        when(repository.findById(1L)).thenReturn(Optional.of(userEntity));
        userEntity.setEnabled(false);

        service.enableUser(1L);

        assertEquals(true, userEntity.isEnabled());
       
        verify(repository , times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Given a non-existent user ID, when enableUser is called, then should throw ResourceNotFoundException")
    void givenNonExistentUser_whenEnableUser_thenThrowsResourceNotFoundException() {

          when(repository.findById(1L)).thenReturn(Optional.empty());

          Exception exception = assertThrows(ResourceNotFoundException.class, 
          ()->{
            service.enableUser(1L);
          });


        String expectedMessage = "User not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}