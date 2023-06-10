package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserService userService;

    private UserController userController;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenGetRegistrationPage() {
        var view = userController.getRegistrationPage();
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenGetLoginPage() {
        var view = userController.getLoginPage();
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenRegisterThenThenSaveUserAndRedirectToLoginPage() {
        var user = new User(1, "test@mail.ru", "userName", "qwerty");
        when(userService.save(user)).thenReturn(Optional.of(user));
        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        assertThat(view).isEqualTo("redirect:/login");
    }

    @Test
    public void whenRegisterFailedThenReturnErrorPage() {
        String message = "User with this email already exists";
        var user = new User(1, "test@mail.ru", "userName", "qwerty");
        when(userService.save(any(User.class))).thenReturn(Optional.empty());
        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var actualMessage = model.getAttribute("message");
        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo(message);
    }

    @Test
    public void whenUserLogout() {
        HttpSession httpSession = mock(HttpSession.class);
        var view = userController.logout(httpSession);
        assertThat(view).isEqualTo("redirect:/users/login");
    }

    @Test
    public void whenLoginThenRedirectToVacanciesPage() {
        var user = new User(1, "test@mail.ru", "userName", "qwerty");
        when(userService.findByEmailAndPassword(anyString(), anyString())).thenReturn(Optional.of(new User()));
        var model = new ConcurrentModel();
        HttpSession httpSession = mock(HttpSession.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getSession()).thenReturn(httpSession);
        var view = userController.loginUser(user, model, httpServletRequest);
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenLoginFailedThenRedirectToErrorPageAndGetErrorMessage() {
        var user = new User(1, "test@mail.ru", "userName", "qwerty");
        when(userService.findByEmailAndPassword(anyString(), anyString())).thenReturn(Optional.empty());
        var model = new ConcurrentModel();
        HttpSession httpSession = mock(HttpSession.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getSession()).thenReturn(httpSession);
        var view = userController.loginUser(user, model, httpServletRequest);
        var error = "Email or password entered incorrectly";
        var actualError = model.getAttribute("error");
        assertThat(view).isEqualTo("users/login");
        assertThat(error).isEqualTo(actualError);
    }
}