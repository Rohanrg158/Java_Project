package com.library.core;

public class Member extends Person {
    public Member(int id, String name) {
        super(id, name);
    }
    @Override
    public String getMemberType() {
        return "Generic Member";
    }
}