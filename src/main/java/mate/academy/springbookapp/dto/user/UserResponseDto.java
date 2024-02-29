package mate.academy.springbookapp.dto.user;

public record UserResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String shippingAddress) {
}
