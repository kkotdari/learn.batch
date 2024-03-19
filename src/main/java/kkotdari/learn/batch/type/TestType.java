package kkotdari.learn.batch.type;

public enum TestType {
    TEST_1("test_1"),
    TEST_2("test_2");
    private final String value;

    public String getValue() {
        return this.value;
    }
    TestType(String value) {
        this.value = value;
    }
}
