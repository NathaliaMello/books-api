package com.mello.nathalia.booksapi.infrastructure.client.googlebooks;

import lombok.Getter;

import java.util.List;

@Getter
public class GoogleBooksResponse {

    private List<VolumeItem> items;

    @Getter
    public static class VolumeItem {
        private VolumeInfo volumeInfo;
    }

    @Getter
    public static class VolumeInfo {
        private String description;
        private List<IndustryIdentifier> industryIdentifiers;

        public String getIsbn() {
            if (industryIdentifiers == null) return null;
            return industryIdentifiers.stream()
                    .filter(i -> "ISBN_13".equals(i.getType()))
                    .map(IndustryIdentifier::getIdentifier)
                    .findFirst()
                    .orElse(null);
        }
    }

    @Getter
    public static class IndustryIdentifier {
        private String type;
        private String identifier;
    }
}
