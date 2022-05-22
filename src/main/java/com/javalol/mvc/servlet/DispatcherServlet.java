package com.javalol.mvc.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javalol.mvc.annotation.Controller;
import com.javalol.mvc.annotation.RequestMapping;
import com.javalol.mvc.annotation.RequestParam;
import com.javalol.mvc.annotation.ResponseBody;
import com.javalol.mvc.context.WebApplicationContext;
import com.javalol.mvc.handler.MyHandler;
import com.javalol.mvc.interceptor.DefaultInterceptorHandler;
import com.javalol.mvc.interceptor.Interceptor;
import com.javalol.mvc.interceptor.IpInterceptorHandler;
import com.javalol.mvc.model.ResultModel;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DispatcherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //进行请求分发处理
        doDispatcher(req,resp);
    }

    public void doDispatcher(HttpServletRequest req, HttpServletResponse resp) {

        try{
            if (interceptor!=null){
                ResultModel resultModel = interceptor.handleRequest(req,resp);
                if (resultModel.getCode()!=10000){
                    ObjectMapper objectMapper = new ObjectMapper();
                    String json = objectMapper.writeValueAsString(resultModel);
                    resp.setContentType("text/html;charset=utf-8");
                    PrintWriter writer = resp.getWriter();
                    writer.write(json);
                    writer.flush();
                    writer.close();
                }
            }
            //根据用户地址 /findUser 查找Handler | Controller
            MyHandler myHandler = getHandler(req);

            if (myHandler==null){
                resp.getWriter().print("<h1>404 NOT FOUND!</h1>");
            }else{
                //调用处理方法之前 进行参数的注入
                String[] strings = new String[0];
                if (myHandler.getParameters()!=null){
                    for (String parameter : myHandler.getParameters()) {
                        strings = new String[strings.length+1];
                        String value = req.getParameter(parameter);
                        strings[strings.length-1]=value;
                    }
                }

                //调用目标方法
                Object result = myHandler.getMethod().invoke(myHandler.getController(),strings);

                //假设进行请求转发
                if (result instanceof String){
                    String viewName = (String) result;
                    if (viewName.contains(":")){
                        String viewType = viewName.split(":")[0];
                        String viewPage = viewName.split(":")[1];
                        if (viewType.equals("forward")){
                            req.getRequestDispatcher(viewPage).forward(req,resp);
                        }else{
                            resp.sendRedirect(viewPage);
                        }
                    }
                }else{
                    //返回json格式数据
                    Method method = myHandler.getMethod();
                    if (method.isAnnotationPresent(ResponseBody.class)){
                        //将返回值转成json格式数据
                        ObjectMapper mapper = new ObjectMapper();
                        String json = mapper.writeValueAsString(result);
                        resp.setContentType("text/html;charset=utf-8");
                        PrintWriter writer = resp.getWriter();
                        writer.write(json);
                        writer.flush();
                        writer.close();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public MyHandler getHandler(HttpServletRequest request){
        String requestURI = request.getRequestURI();
        for (MyHandler myHandler:handlerList){
            if (myHandler.getUrl().equals(requestURI)) {
                return myHandler;
            }
        }
        return null;
    }

    //指定springmvc容器
    private WebApplicationContext webApplicationContext;

    //创建集合，用于存放映射关系，用户发送请求直接从该集合进行匹配
    List<MyHandler> handlerList = new ArrayList<>();

    Interceptor interceptor;

    //1、加载初始化参数
    @Override
    public void init() throws ServletException {
        String contextConfigLocation = this.getServletConfig().getInitParameter("contextConfigLocation");
        webApplicationContext = new WebApplicationContext(contextConfigLocation);

        //3、进行初始化操作
        webApplicationContext.onRefresh();

        //4、初始化请求映射关系  /findUser
        initHandlerMapping();

        //5、初始化过滤器
        initInteceptor();
    }

    private void initInteceptor(){
        IpInterceptorHandler ith1 = new IpInterceptorHandler();
        DefaultInterceptorHandler ith2 = new DefaultInterceptorHandler();
        ith1.setNext(ith2);
        interceptor = ith1;
    }

    private void initHandlerMapping() {
        for (Map.Entry<String, Object> entry : webApplicationContext.iocMap.entrySet()) {
            //获取bean的class类型
            Class<?> clazz = entry.getValue().getClass();
            if (clazz.isAnnotationPresent(Controller.class)){
                //获取bean中所有的方法，为这些方法建立映射关系
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method:methods){
                    if (method.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        //获取注解中的值 /findUser
                        String url = requestMapping.value();
                        //建立 映射地址 与 控制器的关系
                        MyHandler myHandler = new MyHandler(url,entry.getValue(),method);

                        List<String> parametersStr = null;
                        Parameter[] parameters = method.getParameters();
                        if (parameters!=null && parameters.length>0){
                            for (Parameter parameter : parameters) {
                                //TODO: 记录参数位置
                                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                                parametersStr = new ArrayList<>();
                                parametersStr.add(requestParam.value());
                            }
                        }
                        if (parametersStr!=null){
                            myHandler.setParameters(parametersStr);
                        }

                        handlerList.add(myHandler);
                    }
                }

            }

        }
    }


}
