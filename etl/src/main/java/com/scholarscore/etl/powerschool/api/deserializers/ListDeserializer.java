package com.scholarscore.etl.powerschool.api.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.scholarscore.etl.powerschool.api.model.PsStaffs;
import com.scholarscore.etl.powerschool.api.model.student.PsExtensionField;
import com.scholarscore.etl.powerschool.api.model.student.PsExtensionFields;
import com.scholarscore.util.EdPanelObjectMapper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by mattg on 7/15/15.
 */
public abstract class ListDeserializer<T extends List, E> extends JsonDeserializer<T> {
    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final Logger LOGGER = LoggerFactory.getLogger(ListDeserializer.class);
    private static final Pattern INVALID_CHARACTERS = Pattern.compile("[^\\x00-\\x7F]");
    
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
     * @param <V>
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
                        field.set(out, normalize(asText(node, name)));
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
                        if (field.isSynthetic()) {
                            System.out.println("Ignoring synthetic field " + field.getName() + " of type " + field.getType());
                            continue;
                        }
                        JsonNode innerNode = node.get(field.getName().toLowerCase());
                        if (null == innerNode) {
                            LOGGER.trace("Can't parse inner node " + field.getName() + " from class " + out.getClass().getSimpleName() + " -- not found!");
                            continue;
                        }
                        Object innerObj = readObj(innerNode,
                                field.getType());
                        field.set(out, innerObj);
                        break;
                }
            }
            if(out instanceof PsExtensionFields) {
                if(node instanceof ArrayNode) {
                    for(JsonNode n: (ArrayNode)node) {
                        PsExtensionField ef = new PsExtensionField();
                        for(Field f: ef.getClass().getDeclaredFields()) {
                            f.set(ef, asText(n, f.getName()));
                        }
                        ((PsExtensionFields)out).put(ef.name, ef);
                    }
                }
            }
            return out;
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String normalize(String value) { 
        if (value == null) { return null; } 
        // first attempt to convert characters 
        String normalizedString = Normalizer.normalize(value, Normalizer.Form.NFD);
        return INVALID_CHARACTERS.matcher(normalizedString).replaceAll("");
    }

    private Date parseDate(String value) {
        if (null != value) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                return sdf.parse(value);
            } catch (Exception e) {
                LOGGER.warn("Unable to parse date value from string " + value + ", expecting format " + sdf.toPattern() + ".");
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
        List staffs = EdPanelObjectMapper.MAPPER.readValue(json, PsStaffs.class);
        System.out.println(staffs);
    }
}
