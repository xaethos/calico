package net.xaethos.lib.activeprovider.models;

import java.util.HashMap;
import java.util.Map;

public class HashModelHandler extends BaseModelHandler {

    private final HashMap<String,Object> mContent;

    public HashModelHandler() {
        mContent = new HashMap<String, Object>();
    }

    public HashModelHandler(Map<String, Object> content) {
        mContent = new HashMap<String, Object>(content);
    }

    @Override
    public Object get(String field, Class<?> cls) {
        return mContent.get(field);
    }

    @Override
    public void set(String field, Object value) {
        mContent.put(field, value);
    }

}
