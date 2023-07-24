package org.nervousync.test.utils;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.MultilingualUtils;

public final class MultilingualTest extends BaseTest {
    @Test
    @Order(0)
    public void register() {
        this.logger.info("Register zh-CN result: {}",
                MultilingualUtils.registerResource("Utils", "zh-CN",
                        "src/main/resources/META-INF/i18n/zh-CN.xml"));
        this.logger.info("Register en-US result: {}",
                MultilingualUtils.registerResource("Utils", "en-US",
                        "src/main/resources/META-INF/i18n/en-US.xml"));
    }

    @Test
    @Order(10)
    public void message() {
        this.logger.info(MultilingualUtils.findMessage("Utils", "Not_Support_Type_Location_Error"));
        this.logger.info(MultilingualUtils.findMessage("Utils", "zh-CN", "Out_Of_Index_Raw_Error", 10, 8, 3));
    }

    @Test
    @Order(20)
    public void destroy() {
        MultilingualUtils.removeResource("Utils", "zh-CN");
        this.logger.info(MultilingualUtils.findMessage("Utils", "zh-CN", "Out_Of_Index_Raw_Error", 10, 8, 3));
        MultilingualUtils.removeBundle("Utils");
        this.logger.info(MultilingualUtils.findMessage("Utils", "Not_Support_Type_Location_Error"));
    }
}
