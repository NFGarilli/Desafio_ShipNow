package test;

import main.File;
import main.Folder;
import main.User;
import main.FileSystem;
import org.junit.Before;
import org.junit.Test;
import main.Console;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class ConsoleTest {
    private Console console = new Console();
    private Map<String, User> users;
    private FileSystem fileSystem = new FileSystem();
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() {
        users = new HashMap<>();
        console.setFileSystem(fileSystem);
        console.setUsers(users);
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Asegurarse de que los datos se carguen correctamente desde el archivo
        try {
            console.loadDataFromFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateFile_SuccessBecauseImLogged() {
        // setup
        String[] arguments = {"file1", "content"};
        String[] user = {"Nico", "123"};

        // exec
        console.createUser(user);
        console.login(user);
        console.createFile(arguments);

        // assert
        assertTrue(console.getCurrentFolder().getResource("file1") instanceof File);
    }

    @Test
    public void testCreateFile_FailsBecauseImNotLogged() {
        // setup
        String[] arguments = {"file1", "content"};

        // exec
        console.createFile(arguments);

        // assert
        assertTrue(outputStream.toString().contains("Debe iniciar sesión para realizar esta operación."));
    }

    @Test
    public void testCreateUser_Success() {
        // setup
        String[] arguments = {"usernameNueva", "passwordNueva"};

        // exec
        console.createUser(arguments);

        // assert
        assertTrue(console.getUsers().containsKey("username"));
        assertTrue(outputStream.toString().contains("Usuario creado: username"));
    }

    @Test
    public void testCreateUser_FailsDueToExistingUser() {
        // setup
        String[] arguments = {"existingUser", "password"};
        User existingUser = new User("existingUser", "password");
        console.getUsers().put("existingUser", existingUser);

        // exec
        console.createUser(arguments);

        // assert
        assertTrue(outputStream.toString().contains("El usuario 'existingUser' ya existe."));
    }

    @Test
    public void testCreateUser_FailsDueToInvalidArguments() {
        // setup
        String[] arguments = {"username"};

        // exec
        console.createUser(arguments);

        // assert
        assertTrue(outputStream.toString().contains("Comando incorrecto. Uso: create_user <username> <password>"));
    }

    @Test
    public void testLogin_Success() {
        // setup
        String[] arguments = {"username", "password"};
        User user = new User("username", "password");
        console.getUsers().put("username", user);

        // exec
        console.login(arguments);

        // assert
        assertSame(user, console.getCurrentUser());
        assertTrue(outputStream.toString().contains("Usuario logueado: username"));
    }

    @Test
    public void testLogin_FailsDueToIncorrectUsername() {
        // setup
        String[] arguments = {"nonexistentUser", "password"};

        // exec
        console.login(arguments);

        // assert
        assertNull(console.getCurrentUser());
        assertTrue(outputStream.toString().contains("Nombre de usuario o contraseña incorrectos."));
    }

    @Test
    public void testLogin_FailsDueToIncorrectPassword() {
        // setup
        String[] arguments = {"username", "wrongPassword"};
        User user = new User("username", "password");
        console.getUsers().put("username", user);

        // exec
        console.login(arguments);

        // assert
        assertNull(console.getCurrentUser());
        assertTrue(outputStream.toString().contains("Nombre de usuario o contraseña incorrectos."));
    }

    @Test
    public void testDeleteResource_Success_File() {
        // setup
        String[] arguments = {"file1"};
        String[] user = {"Nico", "123"};
        Folder currentFolder = console.getCurrentFolder();
        File file = new File("file1", "content");
        currentFolder.addResource(file);

        // exec
        console.createUser(user);
        console.login(user);
        console.deleteResource(arguments);

        // assert
        assertNull(currentFolder.getResource("file1"));
        assertTrue(outputStream.toString().contains("Archivo eliminado: file1"));
    }

    @Test
    public void testDeleteResource_Success_Folder() {
        // setup
        String[] arguments = {"folder1"};
        String[] user = {"Nico", "123"};
        Folder currentFolder = console.getCurrentFolder();
        Folder folder = new Folder("folder1", null);
        currentFolder.addResource(folder);

        // exect
        console.createUser(user);
        console.login(user);
        console.deleteResource(arguments);

        // assert
        assertNull(currentFolder.getResource("folder1"));
        assertTrue(outputStream.toString().contains("Carpeta eliminada: folder1"));
    }

    @Test
    public void testDeleteResource_NotFound() {
        // setup
        String[] arguments = {"nonexistentResource"};
        String[] user = {"Nico", "123"};

        // exec
        console.createUser(user);
        console.login(user);
        console.deleteResource(arguments);

        // assert
        assertTrue(outputStream.toString().contains("Recurso no encontrado: nonexistentResource"));
    }

    @Test
    public void testDeleteResource_NotLoggedIn() {
        // setup
        String[] arguments = {"resource"};

        // exec
        console.deleteResource(arguments);

        // assert
        assertTrue(outputStream.toString().contains("Debe iniciar sesión para realizar esta operación"));
    }

    @Test
    public void testChangeDirectory_Success_Folder() {
        // setup
        String[] arguments = {"folder1"};
        Folder currentFolder = console.getCurrentFolder();
        Folder folder1 = new Folder("folder1", null);
        currentFolder.addResource(folder1);

        // exec
        console.changeDirectory(arguments);

        // assert
        assertSame(folder1, console.getCurrentFolder());
        assertTrue(outputStream.toString().contains("Directorio actual: folder1"));
    }

    @Test
    public void testChangeDirectory_Success_ParentFolder() {
        // setup
        String[] arguments = {".."};
        Folder currentFolder = console.getCurrentFolder();
        Folder parentFolder = new Folder("parentFolder", null);
        currentFolder.setParent(parentFolder);

        // exec
        console.changeDirectory(arguments);

        // assert
        assertSame(parentFolder, console.getCurrentFolder());
        assertTrue(outputStream.toString().contains("Directorio actual: parentFolder"));
    }

    @Test
    public void testChangeDirectory_RootFolder() {
        // setup
        String[] arguments = {".."};

        // exec
        console.changeDirectory(arguments);

        // assert
        assertNull(console.getCurrentFolder().getParent());
        assertTrue(outputStream.toString().contains("Ya se encuentra en la carpeta raíz."));
    }

    @Test
    public void testSaveDataToFile() {
        // setup
        User user = new User("username", "password");
        users.put("username", user);

        // exec
        console.saveDataToFile();

        // assert
        try (FileInputStream fileIn = new FileInputStream("filesystem.ser");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            FileSystem loadedFileSystem = (FileSystem) in.readObject();
            Map<String, User> loadedUsers = (Map<String, User>) in.readObject();

           assertTrue(fileSystem.equals(loadedFileSystem));
           assertEquals(users, loadedUsers);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoadDataFromFile() {
        // Arrange
        User user = new User("username", "password");
        users.put("username", user);

        try (FileOutputStream fileOut = new FileOutputStream("filesystem.ser");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(fileSystem);
            out.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Act
        console.loadDataFromFile();

        // Assert
        assertEquals(fileSystem, console.getFileSystem());
        assertEquals(users, console.getUsers());
    }
}
