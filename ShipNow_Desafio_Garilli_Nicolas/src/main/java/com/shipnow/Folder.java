package com.shipnow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Folder implements IResource, Serializable {
    private String name;
    private Folder parent;
    private List<IResource> resources;

    public Folder(String name, Folder parent) {
        this.name = name;
        this.parent = parent;
        this.resources = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addResource(IResource resource) {
        if (!resources.contains(resource)) {
            resources.add(resource);
        }
    }

    public void removeResource(String name) {
        for (int i = 0; i < resources.size(); i++) {
            if (resources.get(i).getName().equals(name)) {
                resources.remove(i);
                break;
            }
        }
    }

    public IResource getResource(String name) {
        for (IResource resource : resources) {
            if (resource.getName().equals(name)) {
                return resource;
            }
        }
        return null;
    }

    public void displayContent() {
        System.out.println("Contenido de la carpeta actual:");

        for (IResource resource : resources) {
            if (resource instanceof File) {
                System.out.println("[Archivo] " + resource.getName());
            } else if (resource instanceof Folder) {
                System.out.println("[Carpeta] " + resource.getName());
            }
        }
    }

    public void displayMetadata() {
        System.out.println("Metadata de la carpeta: " + name);
        System.out.println("Fecha de creación: " + new Date().toString());
    }

    public Folder getParent() {
        return parent;
    }

    public List<IResource> getResources() {
        return resources;
    }

    public void setParent(Folder parentFolder) {
        parent = parentFolder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Folder folder = (Folder) o;
        return Objects.equals(name, folder.name) && Objects.equals(resources, folder.resources);
    }
}