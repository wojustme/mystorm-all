package com.wojustme.mystorm.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author wojustme
 * @date 2017/7/18
 * @package com.wojustme.mystorm.util
 */
public final class JsonUtil {
  private static final Gson gson = new GsonBuilder().create();

  public static <T> String toJsonStr(T obj) {
    String json;
    try {
      json = gson.toJson(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return json;
  }

  public static <T> T toBeanObj(String json, Class<T> type) {
    T pojo = null;
    try {
      pojo = gson.fromJson(json, type);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return pojo;
  }

}
