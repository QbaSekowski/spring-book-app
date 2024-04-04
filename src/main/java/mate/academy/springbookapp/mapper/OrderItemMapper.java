package mate.academy.springbookapp.mapper;

import mate.academy.springbookapp.config.MapperConfig;
import mate.academy.springbookapp.dto.orderitem.OrderItemDto;
import mate.academy.springbookapp.model.CartItem;
import mate.academy.springbookapp.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "book.price", target = "price")
    OrderItem toOrderItemFromCartItem(CartItem cartItem);
}
