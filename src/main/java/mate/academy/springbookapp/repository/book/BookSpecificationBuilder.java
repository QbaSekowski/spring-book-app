package mate.academy.springbookapp.repository.book;

import lombok.RequiredArgsConstructor;
import mate.academy.springbookapp.dto.BookSearchParametersDto;
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

    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto bookSearchParametersDto) {
        Specification<Book> spec = Specification.where(null);
        if (bookSearchParametersDto.titles() != null
                && bookSearchParametersDto.titles().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(KEY_TITLE)
                    .getSpecification(bookSearchParametersDto.titles()));
        }
        if (bookSearchParametersDto.authors() != null
                && bookSearchParametersDto.authors().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(KEY_AUTHOR)
                    .getSpecification(bookSearchParametersDto.authors()));
        }
        if (bookSearchParametersDto.isbns() != null
                && bookSearchParametersDto.isbns().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(KEY_ISBN)
                    .getSpecification(bookSearchParametersDto.isbns()));
        }
        return spec;
    }
}
