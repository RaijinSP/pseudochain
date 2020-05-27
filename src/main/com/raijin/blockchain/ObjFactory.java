package com.raijin.blockchain;

import com.raijin.blockchain.configutils.AfterCreation;
import com.raijin.blockchain.configutils.Inject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObjFactory {

    private static final String PROP_NAME = "blockchain.data";

    public static <T> T createObject(Class<T> type) {
        T t;

        try {

            t = type.getDeclaredConstructor().newInstance();

            String propPath = ClassLoader.getSystemClassLoader().getResource(PROP_NAME).getPath();

            try {

                Stream<String> props = Files.lines(Paths.get(propPath));

                Map<String, String> properties = props.map((l) -> l.split("=")).collect(Collectors.toMap((l) -> l[0], (l) -> l[1]));

                for (Field field : type.getDeclaredFields()) {
                    Inject in = field.getAnnotation(Inject.class);

                    if (in != null) {
                        field.setAccessible(true);
                        field.set(t, in.value().isEmpty() ? properties.get(field.getName()): properties.get(in.value()));
                    }
                }

            } catch (IOException ignored) {
            }

            invokeInitMethods(type, t);

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException x) {
            throw new RuntimeException("Unable to properly create object for class " + type.toString());
        }

        return t;
    }

    private static <T> void invokeInitMethods(Class<T> type, T t) throws InvocationTargetException, IllegalAccessException {
        for (Method m: type.getDeclaredMethods()) {
            AfterCreation ac = m.getAnnotation(AfterCreation.class);
            if (ac != null)
                m.invoke(t);
        }
    }
}
