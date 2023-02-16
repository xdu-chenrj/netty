package org.serialize;

public interface Serializer {
    byte[] serialize(Object o);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
