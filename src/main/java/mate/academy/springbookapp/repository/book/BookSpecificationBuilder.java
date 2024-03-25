package mate.academy.springbookapp.repository.book;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mate.academy.springbookapp.dto.book.BookSearchParametersDto;
import mate.academy.springbookapp.model.Book;
import mate.academy.springbookapp.repository.SpecificationBuilder;
import mate.academy.springbookapp.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ISBN = "isbn";
    private static final String KEY_PRICE = "price";
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto bookSearchParametersDto) {
        Specification<Book> specification = Specification.where(null);
        Map<String, String[]> parametersMap = new HashMap<>();
        parametersMap.put(KEY_TITLE, bookSearchParametersDto.titles());
        parametersMap.put(KEY_AUTHOR, bookSearchParametersDto.authors());
        parametersMap.put(KEY_ISBN, bookSearchParametersDto.isbns());
        parametersMap.put(KEY_PRICE,
                new String[]{bookSearchParametersDto.minPrice(),
                        bookSearchParametersDto.maxPrice()});

        for (Map.Entry<String, String[]> entry : parametersMap.entrySet()) {
            String[] paramValues = entry.getValue();
            if (paramValues != null && paramValues.length > 0) {
                specification = specification.and(
                        bookSpecificationProviderManager.getSpecificationProvider(entry.getKey())
                                .getSpecification(paramValues));
            }
        }
        return specification;
    }
}
