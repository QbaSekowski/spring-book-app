package mate.academy.springbookapp.repository.shoppingcart;

import mate.academy.springbookapp.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("SELECT sc FROM ShoppingCart sc "
            + "LEFT JOIN FETCH sc.cartItems "
            + "JOIN FETCH sc.user u")
    ShoppingCart findCartWithItemsByUserId(Long userId);
}
