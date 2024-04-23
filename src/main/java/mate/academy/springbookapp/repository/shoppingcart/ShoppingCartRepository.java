package mate.academy.springbookapp.repository.shoppingcart;

import mate.academy.springbookapp.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("select sc from ShoppingCart sc "
            + "left join fetch sc.cartItems "
            + "join fetch sc.user u "
            + "where u.id = :userId")
    ShoppingCart findCartWithItemsByUserId(Long userId);
}
