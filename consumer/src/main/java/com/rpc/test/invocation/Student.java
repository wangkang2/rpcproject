package com.rpc.test.invocation;

public class Student implements Person {

    private String name;
    public Student(String name){
        this.name = name;
    }

    @Override
    public void giveMoney() {
        System.out.println(name+"给钱了");
    }
}
