package mate.academy.springbookapp.repository.order;

import java.util.List;
import java.util.Optional;
import mate.academy.springbookapp.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("from Order o "
            + "left join fetch o.orderItems "
            + "join fetch o.user u "
            + "where u.id = :userId")
    List<Order> findAllOrdersByUserId(Long userId);

    @Query("from Order o "
            + "left join fetch o.orderItems "
            + "join fetch o.user u "
            + "where o.id = :orderId")
    Optional<Order> findOrderByIdWithItems(Long orderId);
}
