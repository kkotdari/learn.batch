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
    public void run() throws Exception {
        try {
            System.out.println("activate TestService.run() with testType.getValue(): " +  testType.getValue());
            this.testSubService.run();
        } catch (RuntimeException re) {
            throw new RuntimeException("Test runtime exception. location: " + this.getClass());
        }
    }
}
