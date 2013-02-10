/*
 * Copyright (c) 2013.
 * @Author Chathuranga Tennakoon
 * @Blog chathurangat.blogspot.com
 * @Web  www.fosshub.org
 * @Email chathuranga.t@gmail.com
 */
package org.fosshub.oauth.exception;

//todo exception handling
public class OAuthException extends Exception{

    private String exceptionMessage;
    private String errorCode;

    public OAuthException(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public OAuthException(String exceptionMessage,Throwable throwable) {
        super(throwable);
        this.exceptionMessage = exceptionMessage;
    }

    public OAuthException(String errorCode,String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
        this.errorCode = errorCode;
    }

    public OAuthException(String errorCode,String exceptionMessage,Throwable throwable) {
        super(throwable);
        this.exceptionMessage = exceptionMessage;
        this.errorCode = errorCode;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
