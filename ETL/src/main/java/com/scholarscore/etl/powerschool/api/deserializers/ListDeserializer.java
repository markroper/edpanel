package com.scholarscore.etl.powerschool.api.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.scholarscore.etl.powerschool.api.model.Staffs;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

/**
 * Created by mattg on 7/15/15.
 */
public abstract class ListDeserializer<T extends List, E> extends JsonDeserializer<T> {

    abstract String getEntityName();

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        List staffs = (List) getTemplateElementInstance();
        JsonNode element = node.findValue(getEntityName());
        if (!element.isArray()) {
            staffs.add(readObj(element, getCollectionClassType()));
        }
        else {
            element.elements().forEachRemaining(value -> {
                Object result = readObj(value, getCollectionClassType());
                staffs.add(result);
            });
        }
        return (T)staffs;
    }

    public Class getCollectionClassType() {
        try {
            Object instance = ((Class) ((ParameterizedType) this.getClass().
                    getGenericSuperclass()).getActualTypeArguments()[1]).newInstance();
            return instance.getClass();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getTemplateElementInstance() {
        try {
            Object instance = ((Class) ((ParameterizedType) this.getClass().
                    getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();
            return instance;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Read the object from the node object as a stream of fields and append the attributes as available
     *
     * @param node
     * @param clazz
     * @param <T>
     * @return
     */
    private <V> V readObj(JsonNode node, Class<V> clazz) {
        try {
            V out = clazz.newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                String name = field.getName();
                //System.out.println(name + " type: " + field.getType().toString());
                switch (field.getType().getName()) {
                    case "java.lang.String":
                        field.set(out, asText(node, name));
                        break;
                    case "java.lang.Long":
                        field.set(out, asLong(node, name));
                        break;
                    case "java.lang.List":
                        if (field.getClass().isAnnotationPresent(JsonDeserialize.class)) {
                            JsonDeserialize annotation = field.getClass().getAnnotation(JsonDeserialize.class);
                            Class impl = annotation.using();
                        }
                    default:
                        Object innerObj = readObj(
                                node.findValue(field.getName().toLowerCase()),
                                field.getType());
                        field.set(out,
                                innerObj);
                        break;
                }
            }
            return out;
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Using Java 8 monads pull out the field value for Longs
     *
     * @param node
     * @param fieldName
     * @return
     */
    private Long asLong(JsonNode node, String fieldName) {
        return Optional.of(node)
                .flatMap(n ->
                        Optional.ofNullable(n.findValue(fieldName))
                                .flatMap(o -> Optional.ofNullable(o.asLong()))).orElse(null);
    }

    private String asText(JsonNode node, String fieldName) {
        return Optional.ofNullable(node)
                .flatMap(n ->
                        Optional.ofNullable(n.findValue(fieldName))
                                .flatMap(o -> Optional.ofNullable(o.asText()))).orElse(null);
    }

    public static void main(String args[]) throws IOException {
        String json = FileUtils.readFileToString(new File("/home/mattg/dev/scholarscore/ETL/src/test/resources/staff.json"));
        ObjectMapper mapper = new ObjectMapper();
        List staffs = mapper.readValue(json, Staffs.class);
        System.out.println(staffs);
    }
}
