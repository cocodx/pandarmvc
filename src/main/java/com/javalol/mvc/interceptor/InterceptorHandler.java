package com.javalol.mvc.interceptor;

import com.javalol.mvc.model.ResultModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class InterceptorHandler implements Interceptor{

    private InterceptorHandler next;

    public InterceptorHandler getNext() {
        return next;
    }

    public void setNext(InterceptorHandler next) {
        this.next = next;
    }

    public abstract ResultModel handleRequest(HttpServletRequest request, HttpServletResponse response);
}
