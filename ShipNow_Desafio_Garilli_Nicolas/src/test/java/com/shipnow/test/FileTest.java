package com.shipnow.test;

import com.shipnow.File;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class FileTest {

    private ByteArrayOutputStream outputStream;

    @Before
    public void setUpStreams() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    public void testGetName() {

        String expectedName = "file.txt";
        File file = new File(expectedName, "");

        String actualName = file.getName();

        assertEquals(expectedName, actualName);
    }

    @Test
    public void testDisplayContent() {
        // setup
        String name = "file.txt";
        String content = "com.shipnow.File content";
        File file = new File(name, content);

        // exec
        file.displayContent();

        // assert
        String expectedOutput = content + System.lineSeparator();
        assertTrue(outputStream.toString().contains(expectedOutput));
    }

    @Test
    public void testDisplayMetadata() {
        // setup
        String name = "file.txt";
        String content = "com.shipnow.File content";
        File file = new File(name, content);

        // exec
        file.displayMetadata();

        // assert
        String expectedOutput = "{fecha_creacion=" + file.getMetadata().get("fecha_creacion") + "}" + System.lineSeparator();
        assertEquals(expectedOutput, outputStream.toString());
    }
}
