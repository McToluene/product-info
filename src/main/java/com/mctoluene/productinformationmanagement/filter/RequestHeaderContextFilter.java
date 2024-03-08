package com.mctoluene.productinformationmanagement.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mctoluene.productinformationmanagement.filter.dto.RequestHeaderContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RequestHeaderContextFilter extends OncePerRequestFilter {

    private static final String API_PATH = "/api/";
    private static final List<String> REQUIRED_HEADERS = Arrays.asList("x-trace-id", "x-country-code");

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        RequestHeaderContext context;

        Map<String, String> headersMap = Collections.list(req.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        name -> name,
                        req::getHeader));

        String requestPathValue = req.getRequestURI();

        if (isPathAPI(requestPathValue)) {
            context = extractHeaders(headersMap);
            log.info("request header context {} ", context);

            validateHeaders(context);

            RequestHeaderContextHolder.setContext(context);
            chain.doFilter(req, response);
        }

        if (requestPathValue.contains("/swagger-ui") || requestPathValue.contains("/api-docs")
                || requestPathValue.contains("/actuator") || requestPathValue.contains("/favicon.ico")) {
            context = new RequestHeaderContext(
                    UUID.randomUUID().toString(),
                    "NGN");
            RequestHeaderContextHolder.setContext(context);
            chain.doFilter(req, response);
        }

    }

    private void validateHeaders(RequestHeaderContext context) {
        if (Objects.isNull(context.countryCode()) && Objects.isNull(context.traceId()))
            log.error("Missing required headers or invalid path");
    }

    private boolean isPathAPI(String path) {
        return StringUtils.contains(path, API_PATH);
    }

    private RequestHeaderContext extractHeaders(Map<String, String> headers) {
        return new RequestHeaderContext(
                headers.get(REQUIRED_HEADERS.get(0)),
                headers.get(REQUIRED_HEADERS.get(1)));
    }

}
