package mate.academy.springbookapp.service.user.impl;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.springbookapp.dto.user.UserRegistrationRequestDto;
import mate.academy.springbookapp.dto.user.UserResponseDto;
import mate.academy.springbookapp.exception.RegistrationException;
import mate.academy.springbookapp.mapper.UserMapper;
import mate.academy.springbookapp.model.Role;
import mate.academy.springbookapp.model.User;
import mate.academy.springbookapp.repository.role.RoleRepository;
import mate.academy.springbookapp.repository.user.UserRepository;
import mate.academy.springbookapp.service.user.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RegistrationException("Can't register user with given email address");
        }
        User savedUser = userMapper.toModel(request);
        savedUser.setPassword(passwordEncoder.encode(savedUser.getPassword()));
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(Role.RoleName.USER);
        roles.add(userRole);
        savedUser.setRoles(roles);
        return userMapper.toDto(userRepository.save(savedUser));
    }
}
