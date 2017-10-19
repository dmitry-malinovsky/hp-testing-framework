package com.dima.hptf.cucumber.ext.logging;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class TestLogHelper {
    public static final String TEST_NAME = "testname";
    private static final Logger log = LoggerFactory.getLogger(TestLogHelper.class);
    public static String CURRENT_LOG_NAME = "";

    public TestLogHelper(){
    }

    public static void startTestLogging(String name) throws Exception {
        MDC.put("testname", name);
        CURRENT_LOG_NAME = name;
    }

    public static String stopTestLogging(){
        String name = MDC.get("testname");
        MDC.remove("testname");
        return name;
    }

    public static String getCurrentLogName() { return MDC.get("testName") == null?"test":MDC.get("testname"); }
}
