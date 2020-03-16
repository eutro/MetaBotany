package eutros.botaniapp.api.internal.config;

import java.lang.reflect.Type;

public interface ITomlSerializer<FieldType, SerializableType> {
    SerializableType serialize(FieldType toSerialize);

    FieldType deserialize(SerializableType toDeserialize, Type type);

    ITomlSerializer<?, ?> IDENTITY = new IdentitySerializer();

    class IdentitySerializer implements ITomlSerializer<Object, Object> {

        @Override
        public Object serialize(Object toSerialize) {
            return toSerialize;
        }

        @Override
        public Object deserialize(Object toDeserialize, Type type) {
            return toDeserialize;
        }
    }
}
