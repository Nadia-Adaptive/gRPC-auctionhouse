package com.weareadaptive.auction.response;


import com.weareadaptive.auction.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class ResponseBuilder {
    public static ResponseEntity badRequest() {
        return ResponseEntity.badRequest().build();
    }

    public static <T> ResponseEntity<Response> created(final int id, final T body) {
        try {
            return ResponseEntity.created(ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}").buildAndExpand(id)
                    .toUri()).body(new Response(body));
        } catch (final Exception e) {
            throw new BusinessException("Something went wrong.");
        }
    }

    public static <T> ResponseEntity<Response> ok(final T body) {
        return ResponseEntity.ok(new Response(body));
    }
}
