package cn.call110.utils.http.callback;

public interface IGenericsSerializator {
    <T> T transform(String response, Class<T> classOfT);
}
