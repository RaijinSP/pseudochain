package com.raijin.blockchain;

import com.raijin.blockchain.configutils.AfterCreation;
import com.raijin.blockchain.configutils.Inject;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObjFactory {

    private static final String PROP_NAME = "blockchain.data";

    public static <T> T createObject(Class<T> type) {
        T t;

        try {

            Constructor<T> c = type.getDeclaredConstructor();
            c.setAccessible(true);
            t = c.newInstance();

            URL propPath = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(PROP_NAME));

            try {

                Stream<String> props = Files.lines(Paths.get(propPath.toURI()));

                Map<String, String> properties = props.map((l) -> l.split("=")).collect(Collectors.toMap((l) -> l[0], (l) -> l[1]));


                for (Field field : type.getDeclaredFields()) {
                    Inject in = field.getAnnotation(Inject.class);

                    if (in != null) {
                        field.setAccessible(true);
                        field.set(t, in.value().isEmpty() ? properties.get(field.getName()): properties.get(in.value()));
                    }
                }

            } catch (IOException | URISyntaxException ignored) {
            }

            invokeInitMethods(type, t);

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException x) {
            throw new RuntimeException("Unable to properly create object for class " + type.toString(), x);
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
