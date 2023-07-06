package test;

import org.junit.Before;
import org.junit.Test;
import main.IResource;
import main.Folder;
import main.File;
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
        Folder folder = new Folder("Folder", null);
        File file = new File("File.txt", "File content");

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
        Folder folder = new Folder("Folder", null);
        File file1 = new File("File1.txt", "File 1 content");
        File file2 = new File("File2.txt", "File 2 content");
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
