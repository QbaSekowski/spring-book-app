package mate.academy.springbookapp.dto;

public record BookSearchParametersDto(String[] titles, String[] authors, String[] isbns) {
}
