package mate.academy.springbookapp.repository.shoppingcart;

import mate.academy.springbookapp.model.ShoppingCart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @EntityGraph(attributePaths = {"cartItems", "user"})
    ShoppingCart findCartWithItemsByUserId(Long userId);
}
