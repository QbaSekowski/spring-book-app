package mate.academy.springbookapp.mapper;

import mate.academy.springbookapp.config.MapperConfig;
import mate.academy.springbookapp.dto.order.OrderDto;
import mate.academy.springbookapp.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "orderItems", target = "orderItems")
    OrderDto toDto(Order order);
}
