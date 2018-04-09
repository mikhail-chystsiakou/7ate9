package com.yatty.sevennine.backend.exceptions;

public class SevenNineException extends RuntimeException {
    protected String shortDescription = "Internal server error";
    protected String additionalInfo;
    
    public SevenNineException() {
        super();
    }

    public SevenNineException(String message) {
        super(message);
    }

    public SevenNineException(String message, Throwable cause) {
        super(message, cause);
    }

    public SevenNineException(Throwable cause) {
        super(cause);
    }

    public String getShortDescription() {
        return shortDescription;
    }
    
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    
    public String getAdditionalInfo() {
        return additionalInfo;
    }
    
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
