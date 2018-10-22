package com.mrll.javelin.common.test.filter;

public class TestSuppressionContext {

    private static final ThreadLocal<TestSuppressionContext> RUN_SUPPRESSIBLE = new ThreadLocal<>();
    private final boolean shouldSuppress;

    public TestSuppressionContext(boolean shouldSuppress) {
        this.shouldSuppress = shouldSuppress;
    }

    public static void turnSuppressionOn() {
        setTestSuppressible(true);
    }

    public static void turnSuppressionOff() {
        setTestSuppressible(false);
    }

    public static void setTestSuppressible(boolean shouldDisableOutgoingMessages) {
        RUN_SUPPRESSIBLE.set(new TestSuppressionContext(shouldDisableOutgoingMessages));
    }

    public static TestSuppressionContext getContext() {
        return RUN_SUPPRESSIBLE.get();
    }

    public static boolean isSupressionOn() {
        TestSuppressionContext context = getContext();
        return context != null && context.shouldSuppress;
    }

    public static void clear() {
        RUN_SUPPRESSIBLE.remove();
    }

}
