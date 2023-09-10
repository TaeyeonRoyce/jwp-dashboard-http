package org.apache.coyote.http11.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class StaticResourceResolver {

    private static final String RESOURCE_DIRECTORY = "static/";

    private StaticResourceResolver() {
    }

    public static String resolveResource(String viewName) {
        final URL viewPath = findResourceByViewName(viewName);
        return readResource(viewPath);
    }

    private static URL findResourceByViewName(final String viewName) {
        final ClassLoader classLoader = StaticResourceResolver.class.getClassLoader();
        final URL resource = classLoader.getResource(RESOURCE_DIRECTORY + viewName);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found: " + viewName);
        }
        return resource;
    }

    private static String readResource(final URL viewPath) {
        try {
            return new String(Files.readAllBytes(new File(viewPath.getFile()).toPath()));
        } catch (final IOException e) {
            throw new IllegalArgumentException("Resource read fail: " + viewPath.getFile(), e);
        }
    }
}
