package com.shipnow;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Console {
    private FileSystem fileSystem;
    private Scanner scanner;
    private User currentUser;
    private Map<String, User> users;

    public Console() {
        fileSystem = new FileSystem();
        scanner = new Scanner(System.in);
        users = new HashMap<>();
    }

    public void run() {
        while (true) {
            System.out.print("Ingrese un comando: ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                saveDataToFile(); // Guardar los datos al salir
                break;
            }
            executeCommand(input);
        }
    }

    public void executeCommand(String input) {
        String[] parts = input.trim().split(" ");
        String command = parts[0];
        String[] arguments = Arrays.copyOfRange(parts, 1, parts.length);


        switch (command) {
            case "create_file":
                createFile(arguments);
                break;
            case "show":
                showFile(arguments);
                break;
            case "metadata":
                showMetadata(arguments);
                break;
            case "create_folder":
                createFolder(arguments);
                break;
            case "cd":
                changeDirectory(arguments);
                break;
            case "destroy":
                deleteResource(arguments);
                break;
            case "ls":
                listContents();
                break;
            case "whereami":
                showCurrentFolder();
                break;
            case "create_user":
                createUser(arguments);
                break;
            case "login":
                login(arguments);
                break;
            case "logout":
                logout();
                break;
            case "whoami":
                showCurrentUser();
                break;
            default:
                System.out.println("Comando no reconocido.");
        }
    }

    private boolean isLoggedIn() {
        if (currentUser == null) {
            System.out.println("Debe iniciar sesión para realizar esta operación.");
            return false;
        }
        return true;
    }


    public void createUser(String[] arguments) {
        if (!validateArguments(arguments, 2, "create_user <username> <password>")) {
            return;
        }

        String username = arguments[0];
        String password = arguments[1];

        if (users.containsKey(username)) {
            System.out.println("El usuario '" + username + "' ya existe.");
            return;
        }

        User newUser = new User(username, password);
        users.put(username, newUser);
        System.out.println("Usuario creado: " + username);
    }

    public void login(String[] arguments) {
        if (!validateArguments(arguments, 2, "login <username> <password>")) {
            return;
        }

        String username = arguments[0];
        String password = arguments[1];

        User user = users.get(username);
        if (user == null || !user.getPassword().equals(password)) {
            System.out.println("Nombre de usuario o contraseña incorrectos.");
            return;
        }

        currentUser = user;
        System.out.println("Usuario logueado: " + username);
    }

    private void logout() {
        if (!isLoggedIn()) {
            return;
        }
            currentUser = null;
            System.out.println("Usuario deslogueado exitosamente.");
    }

    private void showCurrentUser() {
        if (currentUser == null) {
            System.out.println("Ningún usuario logueado.");
        } else {
            System.out.println("Usuario actual: " + currentUser.getUsername());
        }
    }

    public void createFile(String[] arguments) {
        if (!isLoggedIn()) {
            return;
        }

        if (arguments.length < 2) {
            System.out.println("Comando incorrecto. Uso: create_file <nombre> <contenido>");
            return;
        }
        String fileName = arguments[0];
        //De esta manera, puedo escribir mas de una palabra como segundo argumento para que el contenido del archivo sea un texto.
        String content = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));

        Folder currentFolder = fileSystem.getCurrentFolder();

        // Verificar si ya existe un archivo con el mismo nombre
        if (currentFolder.getResource(fileName) != null) {
            System.out.println("Ya existe un archivo con el nombre '" + fileName + "'.");
            return;
        }

        File file = new File(fileName, content);
        currentFolder.addResource(file);
        System.out.println("Archivo creado: " + fileName);
    }

    private void showFile(String[] arguments) {
        showResource(arguments, "show");
    }

    private void showMetadata(String[] arguments) {
        showResource(arguments, "metadata");
    }

    private void showResource(String[] arguments, String action) {
        if (!validateArguments(arguments, 1, action + " <nombre>")) {
            return;
        }

        String fileName = arguments[0];
        IResource resource = fileSystem.getCurrentFolder().getResource(fileName);

        if (resource instanceof File) {
            if (action.equals("show")) {
                resource.displayContent();
            } else {
                resource.displayMetadata();
            }
        } else {
            System.out.println("Archivo no encontrado: " + fileName);
        }
    }

    private void createFolder(String[] arguments) {
        if (!isLoggedIn()) {
            return;
        }

        if (!validateArguments(arguments, 1, "create_folder <nombre>")) {
            return;
        }

        String folderName = arguments[0];
        Folder folder = new Folder(folderName, fileSystem.getCurrentFolder());
        fileSystem.getCurrentFolder().addResource(folder);
        System.out.println("Carpeta creada: " + folderName);
    }

    public void changeDirectory(String[] arguments) {
        if (!validateArguments(arguments, 1, "cd <nombre>")) {
            return;
        }

        String folderName = arguments[0];

        if (folderName.equals("..")) {
            if (fileSystem.getCurrentFolder().getParent() == null) {
                System.out.println("Ya se encuentra en la carpeta raíz.");
                return;
            }
            fileSystem.setCurrentFolder(fileSystem.getCurrentFolder().getParent());
        } else {
            IResource resource = fileSystem.getCurrentFolder().getResource(folderName);

            if (resource instanceof Folder) {
                fileSystem.setCurrentFolder((Folder) resource);
            } else {
                System.out.println("Carpeta no encontrada: " + folderName);
                return;
            }
        }

        System.out.println("Directorio actual: " + fileSystem.getCurrentFolder().getName());
    }

    public void deleteResource(String[] arguments) {
        if (!isLoggedIn()) {
            return;
        }

        if (!validateArguments(arguments, 1, "destroy <nombre>")) {
            return;
        }

        String resourceName = arguments[0];
        IResource resource = fileSystem.getCurrentFolder().getResource(resourceName);

        if (resource != null) {
            fileSystem.getCurrentFolder().removeResource(resourceName);

            if (resource instanceof File) {
                System.out.println("Archivo eliminado: " + resourceName);
            } else if (resource instanceof Folder) {
                System.out.println("Carpeta eliminada: " + resourceName);
            }
        } else {
            System.out.println("Recurso no encontrado: " + resourceName);
        }
    }

    public void listContents() {
        Folder currentFolder = fileSystem.getCurrentFolder();
        if (currentFolder.getResources().isEmpty()) {
            System.out.println("La carpeta está vacía.");
        } else {
            currentFolder.displayContent();
        }
    }

    private void showCurrentFolder() {
        System.out.println("Directorio actual: " + fileSystem.getCurrentFolder().getName());
    }

    private boolean validateArguments(String[] arguments, int expectedLength, String usage) {
        if (arguments.length != expectedLength) {
            System.out.println("Comando incorrecto. Uso: " + usage);
            return false;
        }
        return true;
    }

    public void saveDataToFile() {
        try (FileOutputStream fileOut = new FileOutputStream("filesystem.ser");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(fileSystem);
            out.writeObject(users);
        } catch (IOException e) {
            System.out.println("Error al guardar los datos: " + e.getMessage());
        }
    }

    public void loadDataFromFile() {
        try (FileInputStream fileIn = new FileInputStream("filesystem.ser");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            fileSystem = (FileSystem) in.readObject();
            users = (Map<String, User>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al cargar los datos: " + e.getMessage());
        }
    }

    public Folder getCurrentFolder() {
        return fileSystem.getCurrentFolder();
    }

    public static void main(String[] args) {
        Console console = new Console();
        console.loadDataFromFile(); // Cargar los datos al iniciar
        console.run();
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public void setUsers(Map<String, User> users) {
        this.users = users;
    }
}