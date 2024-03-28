package mate.academy.springbookapp.service.user.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.springbookapp.dto.user.UserRegistrationRequestDto;
import mate.academy.springbookapp.dto.user.UserResponseDto;
import mate.academy.springbookapp.exception.RegistrationException;
import mate.academy.springbookapp.mapper.UserMapper;
import mate.academy.springbookapp.model.Role;
import mate.academy.springbookapp.model.ShoppingCart;
import mate.academy.springbookapp.model.User;
import mate.academy.springbookapp.repository.role.RoleRepository;
import mate.academy.springbookapp.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.springbookapp.repository.user.UserRepository;
import mate.academy.springbookapp.service.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RegistrationException("Can't register user with given email address");
        }
        User createdUser = userMapper.toModel(request);
        createdUser.setPassword(passwordEncoder.encode(createdUser.getPassword()));
        createdUser.setRoles(Set.of(roleRepository.findByName(Role.RoleName.USER)));
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(createdUser);
        shoppingCartRepository.save(shoppingCart);
        return userMapper.toDto(userRepository.save(createdUser));
    }

    @Override
    public User getUserFromAuthentication(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
