package com.javalol.mvc.interceptor;

import com.javalol.mvc.model.ResultModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Interceptor {

    public ResultModel handleRequest(HttpServletRequest request, HttpServletResponse response);
}
