package com.javalol.mvc.interceptor;

import com.javalol.mvc.model.ResultModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class IpInterceptorHandler extends InterceptorHandler{

    //ip白名单
    public List<String> ipWhiteList = new ArrayList<>();

    //ip黑名单
    public List<String> ipBlackList = new ArrayList<>();


    @Override
    public ResultModel handleRequest(HttpServletRequest request, HttpServletResponse response) {
        String ip = request.getRemoteHost();
        if (ipWhiteList!=null && ipWhiteList.size()>0 && ipWhiteList.contains(ip)){
            getNext().handleRequest(request,response);
        }else{
            if (ipBlackList!=null && ipBlackList.size()>0 && ipBlackList.contains(ip)){
                return new ResultModel(10002,"INTERNEL_ERROR",null);
            }
        }
        if (getNext()!=null){
            getNext().handleRequest(request,response);
        }
        return new ResultModel(10002,"INTERNEL_ERROR",null);
    }
}
