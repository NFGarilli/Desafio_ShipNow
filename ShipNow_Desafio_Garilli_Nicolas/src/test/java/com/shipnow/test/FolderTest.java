package com.shipnow.test;

import com.shipnow.File;
import com.shipnow.Folder;
import com.shipnow.IResource;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class FolderTest {
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUpStreams() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    public void testAddResource() {
        // setup
        Folder folder = new Folder("com.shipnow.Folder", null);
        File file = new File("com.shipnow.File.txt", "com.shipnow.File content");

        // exec
        folder.addResource(file);

        // assert
        List<IResource> expectedResources = new ArrayList<>();
        expectedResources.add(file);
        assertEquals(expectedResources, folder.getResources());
    }

    @Test
    public void testRemoveResource() {
        // setup
        Folder folder = new Folder("com.shipnow.Folder", null);
        File file1 = new File("File1.txt", "com.shipnow.File 1 content");
        File file2 = new File("File2.txt", "com.shipnow.File 2 content");
        folder.addResource(file1);
        folder.addResource(file2);

        // exec
        folder.removeResource("File1.txt");

        // assert
        List<IResource> expectedResources = new ArrayList<>();
        expectedResources.add(file2);
        assertEquals(expectedResources, folder.getResources());
    }

    @Test
    public void testDisplayContent() {
        // setup
        Folder folder = new Folder("folder1", null);
        File file = new File("file1", "content");
        Folder subfolder = new Folder("subfolder", null);
        folder.addResource(file);
        folder.addResource(subfolder);

        // exec
        folder.displayContent();

        // assert
        String expectedOutput = "Contenido de la carpeta actual:\r\n" +
                "[Archivo] file1\r\n" +
                "[Carpeta] subfolder";

        assertTrue(outputStream.toString().contains(expectedOutput));
    }
}
