package com.loaders;

import java.io.InputStream;

public class ResourceLoader {
    public static InputStream load(String path) {
        InputStream input = ResourceLoader.class.getResourceAsStream(path);
            assert input != null;

        return input;
    }
}
