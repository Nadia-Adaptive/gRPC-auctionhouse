package com.weareadaptive.auction.configuration;

import com.weareadaptive.auction.exception.BusinessException;
import com.weareadaptive.auction.exception.NotFoundException;
import com.weareadaptive.auction.response.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
public class AuctionExceptionHandler extends ResponseEntityExceptionHandler {
    private final HttpHeaders headers;
    private final Logger logger = LoggerFactory.getLogger(AuctionExceptionHandler.class);

    public AuctionExceptionHandler() {
        headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    }

    @ExceptionHandler(value = {BusinessException.class, IllegalArgumentException.class, NullPointerException.class,
            ClassCastException.class, DataAccessException.class})
    protected ResponseEntity<Object> handleBadRequestException(final RuntimeException ex, final WebRequest request) {
        logException(ex);
        return handleExceptionInternal(ex, ResponseBuilder.badRequest(), headers, BAD_REQUEST, request);
    }


    @ExceptionHandler(value = {NotFoundException.class})
    protected ResponseEntity<Object> handleNotFoundException(final RuntimeException ex, final WebRequest request) {
        logException(ex);
        return handleExceptionInternal(ex, null, headers, NOT_FOUND, request);
    }

    @ExceptionHandler(value = {UsernameNotFoundException.class, BadCredentialsException.class})
    protected ResponseEntity<Object> handleBadCredentialsException(final RuntimeException ex,
                                                                   final WebRequest request) {
        logException(ex);
        return handleExceptionInternal(ex, null, headers, UNAUTHORIZED, request);
    }

    private void logException(final Exception ex) {
        logger.error("Exception thrown: " + ex.getClass().getName() + " - " + ex.getMessage());
    }
}
