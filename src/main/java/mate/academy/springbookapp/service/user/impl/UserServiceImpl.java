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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Role.RoleName DEFAULT_ROLE_NAME = Role.RoleName.USER;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Transactional
    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.email()).isPresent()) {
            throw new RegistrationException(
                    "Can't register user - user with this email already exists!");
        }
        User modelUser = userMapper.toModel(requestDto);
        modelUser.setPassword(passwordEncoder.encode(requestDto.password()));
        Role defaultRole = roleRepository.findByName(DEFAULT_ROLE_NAME)
                .orElseThrow(() -> new RegistrationException("Can't find default role"));
        modelUser.setRoles(Set.of(defaultRole));
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(modelUser);
        User savedUser = userRepository.save(modelUser);
        shoppingCartRepository.save(shoppingCart);
        return userMapper.toDto(savedUser);
    }

    @Override
    public User getUserFromAuthentication(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
