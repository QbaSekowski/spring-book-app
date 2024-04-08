package mate.academy.springbookapp.repository.order;

import java.util.List;
import java.util.Optional;
import mate.academy.springbookapp.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"orderItems", "user"})
    List<Order> findAllByUserId(Long userId);

    @EntityGraph(attributePaths = {"orderItems", "user"})
    Optional<Order> findById(Long orderId);
}
