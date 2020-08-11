package com.rpc.test.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class StudentTest {
    public static void main(String[] args) {
        Person zhangsan = new Student("张三");
        InvocationHandler invocationHandler = new StuInvocationHandler<Person>(zhangsan);
        Person person = (Person) Proxy.newProxyInstance(Person.class.getClassLoader(), new Class<?>[]{Person.class},invocationHandler);
        person.giveMoney();
    }
}
