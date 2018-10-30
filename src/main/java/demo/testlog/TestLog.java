package demo.testlog;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLog {

    private final static Logger log = LoggerFactory.getLogger(TestLog.class);

    public static void main(String[] args) {
        log.info("logback + slf4j starting up ...");
        log.error("测试error");
    }

}
