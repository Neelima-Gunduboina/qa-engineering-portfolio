package com.neelima.qa.dataproviders;

import com.neelima.qa.utils.JsonUtils;
import org.testng.annotations.DataProvider;

import java.util.List;
import java.util.Map;

/**
 * TestNG data providers backed by JSON files in src/test/resources/testdata.
 */
public final class JsonDataProvider {

    private JsonDataProvider() {
    }

    @DataProvider(name = "registerData")
    public static Object[][] registerData() {
        return toRows("testdata/registerData.json");
    }

    @DataProvider(name = "transferData")
    public static Object[][] transferData() {
        return toRows("testdata/transferData.json");
    }

    private static Object[][] toRows(String resource) {
        List<Map<String, Object>> rows = JsonUtils.readListFromResource(resource);
        Object[][] out = new Object[rows.size()][1];
        for (int i = 0; i < rows.size(); i++) {
            out[i][0] = rows.get(i);
        }
        return out;
    }
}
