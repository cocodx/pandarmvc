package com.javalol.mvc.interceptor;

import com.javalol.mvc.model.ResultModel;
import com.javalol.mvc.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultInterceptorHandler extends InterceptorHandler{

    private DispatcherServlet dispatcherServlet;

    public DispatcherServlet getDispatcherServlet() {
        return dispatcherServlet;
    }

    public void setDispatcherServlet(DispatcherServlet dispatcherServlet) {
        this.dispatcherServlet = dispatcherServlet;
    }

    @Override
    public ResultModel handleRequest(HttpServletRequest request, HttpServletResponse response) {
        //TODO 调用dispatcherServlet
        dispatcherServlet.doDispatcher(request,response);
        return null;
    }
}
