package main;

import java.io.Serializable;
import java.util.Objects;


public class FileSystem implements Serializable {
    private Folder root;
    private Folder currentFolder;

    public FileSystem() {
        root = new Folder("root", null);
        currentFolder = root;
    }

    public Folder getCurrentFolder() {
        return currentFolder;
    }

    public void setCurrentFolder(Folder folder) {
        if (folder instanceof Folder) {
            currentFolder = folder;
        } else {
            System.out.println("El recurso proporcionado no es una carpeta.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileSystem that = (FileSystem) o;
        return Objects.equals(root, that.root) && Objects.equals(currentFolder, that.currentFolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root, currentFolder);
    }
}