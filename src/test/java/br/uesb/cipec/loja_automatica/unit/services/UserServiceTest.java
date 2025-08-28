package br.uesb.cipec.loja_automatica.unit.services;

import br.uesb.cipec.loja_automatica.DTO.UserRegisterDTO;
import br.uesb.cipec.loja_automatica.DTO.UserResponseDTO;
import br.uesb.cipec.loja_automatica.DTO.UserUpdateDTO;
import br.uesb.cipec.loja_automatica.component.AuthenticationFacade;
import br.uesb.cipec.loja_automatica.exception.EmailAlreadyInUseException;
import br.uesb.cipec.loja_automatica.exception.InvalidUserDataException;
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.mapper.UserMapper;
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
import java.util.Optional;

import org.hibernate.mapping.Any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    @DisplayName("Given valid update data, when updateMyProfile is called, then should return updated user")
    void givenValidUpdateData_whenUpdateMyProfile_thenReturnsUpdatedUser() {

      // Given ; Arrange
        when(authenticationFacade.getCurrentUser()).thenReturn(userEntity);

        UserUpdateDTO updateRequestDTO = new UserUpdateDTO();
        updateRequestDTO.setName("Franco Atualizado");
        updateRequestDTO.setEmail("franco.atualizado@example.com");

        when(repository.save(any(User.class))).thenReturn(userEntity);

        when(mapper.toResponseDTO(any(User.class))).thenReturn(userResponseDTO);

      // When ; Act
        UserResponseDTO result = service.update(updateRequestDTO);

      // Then ; Assert
        verify(repository, times(1)).save(userEntity);

        assertEquals("Franco Atualizado", userEntity.getName());
        assertEquals("franco.atualizado@example.com", userEntity.getEmail());
    }

    @Test
    @DisplayName("Given an email that belongs to another user, when updateMyProfile is called, then should throw exception")
    void givenExistingEmailForOtherUser_whenUpdateMyProfile_thenThrowsException() {
        // TODO: Implementar a lógica do teste
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
}