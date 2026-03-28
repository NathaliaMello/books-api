package com.mello.nathalia.booksapi.infrastructure.client.googlebooks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@Configuration
public class GoogleBooksClient {

    private final RestClient restClient;

    @Value("${google.books.api-key}")
    private String apiKey;

    @Value("${google.books.base-url}")
    private String baseUrl;

    public GoogleBooksClient(RestClient restClient) {  // injeta RestClient, não Builder
        this.restClient = restClient;
    }

    public Optional<GoogleBooksData> filterByTitleAndAuthor(String title, String author) {
        try {
            String query = String.format("intitle:%s+inauthor:%s", title, author);

            GoogleBooksResponse response = restClient.get()
                    .uri(baseUrl + "/volumes?q={query}&key={key}", query, apiKey)
                    .retrieve()
                    .body(GoogleBooksResponse.class);

            return Optional.ofNullable(response)
                    .map(GoogleBooksResponse::getItems)
                    .filter(items -> !items.isEmpty())          // verifica se tem resultados
                    .map(items -> items.getFirst().getVolumeInfo()) // pega o VolumeInfo do primeiro
                    .map(info -> new GoogleBooksData(
                            info.getDescription(),
                            info.getIsbn()
                    ));

        } catch (Exception ex) {
            log.warn("Erro ao buscar livro na API do Google Books: título='{}', autor='{}'", title, author);
            return Optional.empty();
        }
    }
}
