package mate.academy.springbookapp.repository.cartitem;

import mate.academy.springbookapp.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Modifying
    @Query("UPDATE CartItem ci SET ci.isDeleted = TRUE WHERE ci.shoppingCart.id = :cartId")
    void deleteAllByCartId(Long cartId);
}
