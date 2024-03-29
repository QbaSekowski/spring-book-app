package mate.academy.springbookapp.mapper;

import mate.academy.springbookapp.config.MapperConfig;
import mate.academy.springbookapp.dto.shoppingcart.ShoppingCartDto;
import mate.academy.springbookapp.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {CartItemMapper.class})
public interface ShoppingCartMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "cartItems", target = "cartItems")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
