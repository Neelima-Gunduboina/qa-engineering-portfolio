package com.neelima.qa.dataproviders;

import com.neelima.qa.utils.JsonUtils;
import org.testng.annotations.DataProvider;

import java.util.List;
import java.util.Map;

public class JsonDataProvider {
    @DataProvider(name = "registerData")
    public static Object[][] registerData() {
        List<Map<String, Object>> rows = JsonUtils.readListFromResource("testdata/loginData.json");
        Object[][] out = new Object[rows.size()][1];
        for (int i = 0; i < rows.size(); i++) out[i][0] = rows.get(i);
        return out;
    }

    @DataProvider(name = "transferData")
    public static Object[][] transferData() {
        List<Map<String, Object>> rows = JsonUtils.readListFromResource("testdata/transferData.json");
        Object[][] out = new Object[rows.size()][1];
        for (int i = 0; i < rows.size(); i++) out[i][0] = rows.get(i);
        return out;
    }
}
