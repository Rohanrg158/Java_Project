package com.library.core;

public class Student extends Person {
    private final String studentId;

    public Student(int id, String name, String studentId) {
        super(id, name);
        this.studentId = studentId;
    }
    public String getStudentId() { return studentId; }
    @Override
    public String getMemberType() {
        return "Student (" + studentId + ")";
    }
}