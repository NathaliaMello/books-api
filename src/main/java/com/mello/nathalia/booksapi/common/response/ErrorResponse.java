package com.mello.nathalia.booksapi.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {

    private String timestamp;
    private int status;
    private String message;
    private String error;
    private String path;
}
