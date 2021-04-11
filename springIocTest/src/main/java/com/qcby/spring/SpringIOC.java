package com.qcby.spring;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;

import java.util.HashMap;
import java.util.Map;

public class SpringIOC {
    private Map<String, Object> beanMap = new HashMap<String,Object>();
    private SpringIOC(){}
    SpringIOC(String xmlName) throws Exception {
    loadBean(xmlName);
    }
    private void loadBean(String xmlName) throws Exception {
        //将xml文件转为输入流
        InputStream inputStream = SpringIOC.class.getClassLoader().getResourceAsStream(xmlName);
        //没找到xml文件就抛出异常
        if (inputStream==null){
            throw new FileNotFoundException("没有找到此xml文件：" + xmlName);
        }
        //创建一个DocumentBuilderFactury对象
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //创建DocumentBuilder对象
        DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
        //通过DocumentBuilder对象的parser方法加载Io流文件
        Document parse = documentBuilder.parse(inputStream);
        //用完关闭流
        inputStream.close();
        //获取所有bean节点的集合
        NodeList bean = parse.getElementsByTagName("bean");
        //遍历bean标签
        for (int i = 0; i <bean.getLength() ; i++) {
            Node node = bean.item(i);
            //node 强制类型转换为Element
            if (node instanceof Element){
                Element element = (Element) node;
                //获取bean标签里面的指定元素的值；
                String beanId = element.getAttribute("id");
                String beanClass = element.getAttribute("class");
                //通过反射获取对象
               Class beanClazz = Class.forName(beanClass);
               //创建bean对象
               Object beanObj = beanClazz.newInstance();
                // 获取bean标签的子标签。
                NodeList propertyList = element.getElementsByTagName("property");
                for (int j = 0; j <propertyList.getLength() ; j++) {
                    Node item = propertyList.item(j);
                    if (item instanceof Element){
                        Element element1 = (Element) item;
                        //获得到属性名称
                        String name = element1.getAttribute("name");
                        //获得属性的值
                        String value = element1.getAttribute("value");
                        //通过反射获取指定属性字段
                        Field declaredField = beanObj.getClass().getDeclaredField(name);
                        //将私有属性设置为可以访问的
                        declaredField.setAccessible(true);
                        String fieldTypeName = declaredField.getType().getName();
                        Object o = ParamType(fieldTypeName, value);
                        //为该成员属性赋值
                        declaredField.set(beanObj, o);
                        //将该字段属性设置值
                        beanMap.put(beanId, beanObj);
                    }
                }
            }
        }
    }
    private Object ParamType(String fieldTypeName, String value) {
        Object obj = null;
        //判断该成员属性是否为int或Integer类型
        if ("int".equals(fieldTypeName) || "java.lang.Integer".equals(fieldTypeName)) {
            //转换为int类型并为该成员属性赋值
            int intFieldValue = Integer.parseInt(value);
            obj = intFieldValue;

        }//判断该成员属性是否为String类型
        if ("java.lang.String".equals(fieldTypeName)) {
            //为该成员属性赋值
            obj = value;
        }//判断另外类型同理
        //if(){ }
        return obj;
    }
    public Object getBean(String beanName) {
        Object bean = beanMap.get(beanName);
        if (bean == null) {
            throw new IllegalArgumentException("无法实例化该名称的bean，请确定名称是否正确 " + beanName);
        }
        return bean;
    }
}
