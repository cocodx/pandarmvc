package com.javalol;

import com.javalol.mvc.xml.XmlPaser;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;

public class TestReadXml {

    @Test
    public void testReadXml(){
        InputStream inputStream = XmlPaser.class.getClassLoader().getResourceAsStream("springmvc.xml");
        System.out.println(inputStream);

        URL url1 = TestReadXml.class.getResource("");
        System.out.println(url1);
        URL url2 = XmlPaser.class.getResource("");
        System.out.println(url2);
    }

    @Test
    public void testControllerName() throws ClassNotFoundException {
        Class clazz = Class.forName("com.javalol.service.controller.UserController");
        String beanName = clazz.getSimpleName().substring(0,1).toLowerCase().concat(clazz.getSimpleName().substring(1));
        System.out.println(beanName);

    }
}
