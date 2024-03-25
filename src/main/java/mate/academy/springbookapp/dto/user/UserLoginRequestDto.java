package mate.academy.springbookapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @Email
        @NotBlank(message = "email may not be blank")
        String email,
        @NotBlank(message = "password may not be blank")
        @Length(min = 8, max = 25)
        String password) {
}
