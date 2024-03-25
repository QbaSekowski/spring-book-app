package mate.academy.springbookapp.dto.book;

public record BookSearchParametersDto(String[] titles, String[] authors, String[] isbns,
                                      String minPrice, String maxPrice) {
}
