package kkotdari.learn.batch.service;

import kkotdari.learn.batch.type.TestType;

public class TestService {
    private final TestSubService testSubService;
    private TestType testType;
    public TestService(TestSubService testSubService) {
        this.testSubService = testSubService;
    }

    public void setTestType(TestType testType) {
        this.testType = testType;
    }
    public void run() {
        System.out.println("activate TestService.run() with testType.getValue(): " +  testType.getValue());
        this.testSubService.run();
    }
}
