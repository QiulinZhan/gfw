package cn.call110.zw.utils.http.callback;

public interface IGenericsSerializator {
    <T> T transform(String response, Class<T> classOfT);
}
