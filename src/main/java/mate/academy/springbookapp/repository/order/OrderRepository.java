package mate.academy.springbookapp.repository.order;

import java.util.List;
import java.util.Optional;
import mate.academy.springbookapp.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("FROM Order o LEFT JOIN FETCH o.orderItems JOIN FETCH o.user u WHERE u.id = :userId")
    List<Order> findAllByUserId(Long userId);

    @Query("FROM Order o LEFT JOIN FETCH o.orderItems JOIN FETCH o.user u WHERE o.id = :orderId")
    Optional<Order> findByIdWithItems(Long orderId);
}
