package mate.academy.springbookapp.service.user;

import mate.academy.springbookapp.dto.user.UserRegistrationRequestDto;
import mate.academy.springbookapp.dto.user.UserResponseDto;
import mate.academy.springbookapp.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;
}
