package com.qcby.spring;

public class Test {
    public static void main(String[] args) throws Exception {
        SpringIOC springIOC = new SpringIOC("FirstSpringConfig.xml");
        Object test = springIOC.getBean("test");
        System.out.println(test);
    }
}
