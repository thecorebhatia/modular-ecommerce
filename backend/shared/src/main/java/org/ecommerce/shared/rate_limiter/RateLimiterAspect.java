package org.ecommerce.shared.rate_limiter;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Map;

@Aspect
@Component("customRateLimiterAspect")
public class RateLimiterAspect {

    private final RateLimiterEngine rateLimiterEngine;

    public RateLimiterAspect(RateLimiterEngine rateLimiterEngine) {
        this.rateLimiterEngine = rateLimiterEngine;
    }

    @Around("@annotation(org.ecommerce.shared.rate_limiter.RateLimited)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        // Resolve target execution details
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimited annotation = method.getAnnotation(RateLimited.class);
        LimiterStrategy strategy = annotation.value();

        // Extract native Spring MVC client contexts
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        String cacheKey = getCacheKey(attributes, method, strategy);
        boolean allowed = rateLimiterEngine.isAllowed(cacheKey, strategy.getCapacity(), strategy.getRefillRate());

        if (!allowed) {
            throw new org.ecommerce.shared.rate_limiter.CustomRateLimitException(
                    "Too many requests. Operation rejected by strategy: " + strategy.name()
            );
        }

        return joinPoint.proceed();
    }

    private static String getCacheKey(ServletRequestAttributes attributes, Method method, LimiterStrategy strategy) {
        HttpServletRequest request = attributes.getRequest();

        String clientIp = request.getRemoteAddr();
        String userId = request.getHeader("X-User-Id") != null ? request.getHeader("X-User-Id") : "anonymous";
        String endpointName = method.getName();

        // Resolve context-aware cache routing keys dynamically
        String cacheKey;
        switch (strategy) {
            case GLOBAL -> cacheKey = "ratelimit:global:cluster";
            case ENDPOINT -> cacheKey = "ratelimit:endpoint:" + endpointName + ":" + clientIp;
            case PER_IP -> cacheKey = "ratelimit:ip:order:" + clientIp;
            case PER_ORDER -> cacheKey = "ratelimit:business:order:" + userId;
            default -> throw new IllegalArgumentException("Unknown rate limiting tier");
        }
        return cacheKey;
    }
}
