package com.shipnow;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class File implements IResource, Serializable {
    private String name;
    private String content;
    private Map<String, String> metadata;

    public File(String name, String content) {
        this.name = name;
        this.content = content;
        this.metadata = new HashMap<>();
        metadata.put("fecha_creacion", new Date().toString());
    }

    public String getName() {
        return name;
    }

    public void displayContent() {
        System.out.println(content);
    }

    public void displayMetadata() {
        System.out.println(metadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return Objects.equals(name, file.name) && Objects.equals(content, file.content);
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}