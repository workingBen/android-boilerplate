package net;

import java.io.IOException;
import java.io.Reader;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ApiException extends RuntimeException {
	
	public static final int API_TOKEN_INVALID = 10000;
	
	/* 
	 * registration/login
	 */
	public static final int API_LOGIN_FAILED = 10001;
	
	/**
	 * CLIENT EXCEPTIONS using the ApiException wrapper
	 */
    public static final int CLIENT_SOCKET_TIMEOUT = 20001;
    public static final int CLIENT_CONNECTION_TIMEOUT = 20002;
    public static final int CLIENT_GENERIC_IO_EXCEPTION = 20003;
	
    public int code;
    public String name;
    public String message;
    public String debug;
    
    public static ApiException fromJSON(Reader stream) {
        return new Gson().fromJson(stream, ApiException.class);
    }

    public static ApiException fromJSON(JsonObject json) {
        return new Gson().fromJson(json, ApiException.class);
    }
    
    /**
     * Creates exception with the specified message. If you are wrapping another exception, consider
     * using {@link #ApiException(String, Throwable)} instead.
     *
     * @param msg error message describing a possible cause of this exception.
     */
    public ApiException(String msg) {
        super(msg);
    }

    /**
     * Creates exception with the specified code, desccription and message. If you are wrapping another exception, consider
     * using {@link #ApiException(String, Throwable)} instead.
     *
     * @param code error code describing the exception.
     * @param description string describing the error code.
     * @param msg error message describing a possible cause of this exception.
     */
    public ApiException(int code, String name, String message, String debug) {
        super(message);
        this.code = code;
        this.name = name;
        this.message = message;
        this.debug = debug;
    }

    /**
     * Creates exception with the specified message and cause.
     * 
     * @param msg error message describing what happened.
     * @param cause root exception that caused this exception to be thrown.
     */
    public ApiException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Creates exception with the specified cause. Consider using
     * {@link #ApiException(String, Throwable)} instead if you can describe what
     * happened.
     * 
     * @param cause root exception that caused this exception to be thrown.
     */
    public ApiException(Throwable cause) {
        super(cause);
        if (cause instanceof SocketTimeoutException) {
        	this.code = CLIENT_SOCKET_TIMEOUT;
        } else if (cause instanceof ConnectTimeoutException) {
        	this.code = CLIENT_CONNECTION_TIMEOUT;
        } else if (cause instanceof IOException) {
        	this.code = CLIENT_GENERIC_IO_EXCEPTION;
        }
    }

    public String toString() {
        return String.format("<BoilerplateApiException:%s:%s:%s>", this.code, this.message, getMessage());
    } 
}