package com.yatty.sevennine.client;

import com.yatty.sevennine.api.dto.ErrorResponse;

public class ServerException extends RuntimeException {
    protected ErrorResponse error;
    
    public ServerException(ErrorResponse error) {
        this.error = error;
    }
    
    public ErrorResponse getError() {
        return error;
    }
}
