package com.mctoluene.productinformationmanagement.filter;

import com.mctoluene.productinformationmanagement.filter.dto.RequestHeaderContext;

public class RequestHeaderContextHolder {

    private static final ThreadLocal<RequestHeaderContext> contextHolder = new ThreadLocal<>();

    public static void setContext(RequestHeaderContext context) {
        contextHolder.set(context);
    }

    public static RequestHeaderContext getContext() {
        return contextHolder.get();
    }

    public static void clearContext() {
        contextHolder.remove();
    }
}
