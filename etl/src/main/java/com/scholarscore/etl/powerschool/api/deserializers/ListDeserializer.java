package com.scholarscore.etl.powerschool.api.deserializers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.scholarscore.etl.powerschool.api.model.PsStaffs;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by mattg on 7/15/15.
 */
public abstract class ListDeserializer<T extends List, E> extends JsonDeserializer<T> {
    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ObjectMapper MAPPER = new ObjectMapper().
            setSerializationInclusion(JsonInclude.Include.NON_NULL).
            registerModule(new JavaTimeModule()).
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    abstract String getEntityName();

    @Override
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
    private <V> V readObj(JsonNode node, Class<V> clazz) {
        String name = null;
        try {
            V out = null;
            if(clazz.isAssignableFrom(LocalDate.class)) {
                out = (V) LocalDate.now();
            } else {
                out = clazz.newInstance();
            }
            for (Field field : clazz.getDeclaredFields()) {
                name = field.getName();
                switch (field.getType().getName()) {
                    case "java.lang.String":
                        field.set(out, asText(node, name));
                        break;
                    case "java.lang.Long":
                        field.set(out, asLong(node, name));
                        break;
                    case "java.util.Date":
                        Date date = parseDate(asText(node, name));
                        field.set(out, date);
                        break;
                    case "java.time.LocalDate":
                        if(null == asText(node, name)) {
                            field.set(out, null);
                        } else {
                            LocalDate local = LocalDate.parse(asText(node, name), LOCAL_DATE_FORMATTER);
                            field.set(out, local);
                        }
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
                        field.set(out, innerObj);
                        break;
                }
            }
            return out;
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Date parseDate(String value) {
        if (null != value) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                return sdf.parse(value);
            } catch (Exception e) {
            }
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
        return (Long) Optional.of(node)
                .flatMap(n ->
                        Optional.ofNullable(n.findValue(fieldName))
                                .flatMap(o -> Optional.ofNullable(o.asLong()))).orElse(null);
    }

    private String asText(JsonNode node, String fieldName) {
        return (String) Optional.ofNullable(node)
                .flatMap(n ->
                        Optional.ofNullable(n.findValue(fieldName))
                                .flatMap(o -> Optional.ofNullable(o.asText()))).orElse(null);
    }

    public static void main(String args[]) throws IOException {
        String json = FileUtils.readFileToString(new File("/home/mattg/dev/scholarscore/ETL/src/test/resources/staff.json"));
        List staffs = MAPPER.readValue(json, PsStaffs.class);
        System.out.println(staffs);
    }
}
