package mate.academy.springbookapp.repository.book.spec;

import java.math.BigDecimal;
import mate.academy.springbookapp.model.Book;
import mate.academy.springbookapp.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String PRICE_TAG = "price";

    @Override
    public Specification<Book> getSpecification(String[] params) {
        BigDecimal minPrice = params[0] != null ? new BigDecimal(params[0])
                : BigDecimal.ZERO;
        BigDecimal maxPrice = params[1] != null ? new BigDecimal(params[1])
                : BigDecimal.valueOf(Double.MAX_VALUE);
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get(PRICE_TAG), minPrice, maxPrice);
    }

    @Override
    public String getKey() {
        return PRICE_TAG;
    }
}
