package kkotdari.learn.batch.service;

public class TestSubService {
    public void run() throws Exception {
        System.out.println("activate TestSubService.run()");
        throw new RuntimeException("Test runtime exception. location: " + this.getClass());
    }
}
