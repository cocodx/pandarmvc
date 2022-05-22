package com.javalol.mvc.context;

import com.javalol.mvc.annotation.Autowired;
import com.javalol.mvc.annotation.Controller;
import com.javalol.mvc.annotation.Service;
import com.javalol.mvc.xml.XmlPaser;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebApplicationContext {

    List<String> classNameList = new ArrayList<>();

    //创建Map集合用于扮演IOC容器
    public Map<String,Object> iocMap = new HashMap<>();

    public WebApplicationContext() {
    }

    String contextConfigLocation;

    public WebApplicationContext(String contextConfigLocation) {
        this.contextConfigLocation = contextConfigLocation;
    }

    //初始化容器
    public void onRefresh() {
        String pack = XmlPaser.getBasePackage(contextConfigLocation).split(":")[1];

        String[] packs = pack.split(",");

        for (String pa:packs){
            executeScanPackage(pa);
        }

        //实例化容器中的bean
        executeInstance();
        //进行spring自动注入操作
        executeAutowired();
    }

    private void executeAutowired() {
        try {

            for (Map.Entry<String,Object> entry : iocMap.entrySet()){
                Object bean = entry.getValue();
                Field[] fields = bean.getClass().getDeclaredFields();
                for (Field field:fields){
                    if (field.isAnnotationPresent(Autowired.class)){
                        //获取注解中的value值 | 该值就是bean的name
                        Autowired autowired = field.getAnnotation(Autowired.class);
                        String beanName = autowired.value();
                        //取消检查机制
                        field.setAccessible(true);
                        //前面controller，后面service

                        field.set(bean,iocMap.get(beanName));
                    }
                }
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private void executeInstance() {
        try{
            for (String s : classNameList) {
                Class<?> clazz = Class.forName(s);

                if (clazz.isAnnotationPresent(Controller.class)){
                    String beanName = clazz.getSimpleName().substring(0,1).toLowerCase().concat(clazz.getSimpleName().substring(1));
                    iocMap.put(beanName,clazz.newInstance());
                }else if (clazz.isAnnotationPresent(Service.class)){
                    Service serviceAnnotation = clazz.getAnnotation(Service.class);
                    String beanName = serviceAnnotation.value();
                    iocMap.put(beanName,clazz.newInstance());
                }
            }
        }catch (Exception e){

        }
    }

    public void executeScanPackage(String pack) {
        URL url = this.getClass().getResource("/"+pack.replaceAll("\\.","/"));
        String listFile = url.getFile();
        File file = new File(listFile);
        for (File f:file.listFiles()) {
            if (f.isDirectory()){
                executeScanPackage(pack+"."+f.getName());
            }else{
                if (f.getName().contains(".class")){
                    String className = pack+"."+f.getName().replaceAll(".class","");
                    classNameList.add(className);
                }
            }
        }
    }
}
