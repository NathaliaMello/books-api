package com.mello.nathalia.booksapi.common.response;

import java.util.List;

public record CursorPageResponse<T>(
        List<T> content,
        Long nextCursor,     // id do último item, null se for a última página
        boolean hasNext,     // tem mais itens?
        int size
) {
    public static <T> CursorPageResponse<T> of(List<T> content, int size, Long lastId) {
        boolean hasNext = content.size() == size;
        return new CursorPageResponse<>(
                content,
                hasNext ? lastId : null,
                hasNext,
                size
        );
    }
}
