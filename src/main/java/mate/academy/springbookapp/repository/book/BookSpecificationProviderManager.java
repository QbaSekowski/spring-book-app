package mate.academy.springbookapp.repository.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.springbookapp.model.Book;
import mate.academy.springbookapp.repository.SpecificationProvider;
import mate.academy.springbookapp.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {

    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(b -> b.getKey().equals(key))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Can't find correct specification provider for key: "
                                + key));
    }
}
