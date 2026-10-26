package org.ecommerce.shared;

import org.ecommerce.shared.rate_limiter.CustomRateLimitException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "org.ecommerce")
public class GlobalResponseProxyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiResponse) {
            return body;
        }

        if (request.getHeaders().containsHeader("X-Internal-Call")
                && request.getHeaders().getFirst("X-Internal-Call").equalsIgnoreCase("true")) {
            return body; // Returns raw response cleanly without wrapping it!
        }

        return new ApiResponse<>(true, body, "Operation completed successfully");
    }

    @ExceptionHandler(CustomRateLimitException.class)
    public ResponseEntity<ApiResponse<Object>> handleRateLimitException(CustomRateLimitException ex) {
        ApiResponse<Object> errorPayload = new ApiResponse<>(
                false,
                null,
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorPayload);
    }
}
