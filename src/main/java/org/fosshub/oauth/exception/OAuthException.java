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

    public OAuthException(String message, Throwable throwable){
        super(throwable);
        this.exceptionMessage = message;
    }

    public OAuthException(Throwable throwable) {
        super(throwable);
    }

    public OAuthException(String message) {
        super(message);
        this.exceptionMessage = message;
    }

    public OAuthException() {
        super();
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
