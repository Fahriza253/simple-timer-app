package com.dpzstudio.timer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ResourceReader {

    public ResourceReader() { }

    public static String readResource(String path) throws IOException {
        InputStream stream = ResourceReader.class.getResourceAsStream(path);
        if (stream == null) {
            throw new IOException(
                "Resource not found: " + path
            );
        }

        StringBuilder builder = new  StringBuilder();

        try (
            BufferedReader read = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
        ) {
            String line;
                while ((line = read.readLine()) != null ) {
                    builder
                    .append(line)
                    .append(System.lineSeparator());
            }
        }

        return builder.toString();
    }
}
