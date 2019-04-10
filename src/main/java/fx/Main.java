package fx;

import dialogs.*;
import explorer.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javassist.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class Main extends Application {

    private static String jarPath;
    private static FileChooser fileChooser;
    private static FileChooser fileSaver;
    private static ListView<MyClass> classListView;
    private static ListView<MyField> fieldListView;
    private static ListView<MyConstructor> constructorListView;
    private static ListView<MyMethod> methodListView;

    public static void main(String[] args) {
        launch(args);
    }

    private static Pane getRoot() {
        VBox root = new VBox();
        HBox hBox = new HBox();
        MenuBar menuBar = createMenuBar();

        classListView = createClassListView();
        HBox.setHgrow(classListView, Priority.ALWAYS);
        hBox.getChildren().add(classListView);

        fieldListView = createFieldListView();
        HBox.setHgrow(fieldListView, Priority.ALWAYS);
        hBox.getChildren().add(fieldListView);

        constructorListView = createConstructorListView();
        HBox.setHgrow(constructorListView, Priority.ALWAYS);
        hBox.getChildren().add(constructorListView);

        methodListView = createMethodListView();
        HBox.setHgrow(methodListView, Priority.ALWAYS);
        hBox.getChildren().add(methodListView);

        VBox.setVgrow(hBox, Priority.ALWAYS);
        root.getChildren().add(menuBar);
        root.getChildren().add(hBox);

        createFileChooser();
        createFileSaver();

        return root;
    }

    private static MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem fileOpen = new MenuItem("Open");
        MenuItem fileSaveAs = new MenuItem("Save as");

        fileOpen.setOnAction(e -> addClasses());
        fileSaveAs.setOnAction(e -> saveJar());

        fileMenu.getItems().add(fileOpen);
        fileMenu.getItems().add(fileSaveAs);

        menuBar.getMenus().add(fileMenu);

        return menuBar;
    }

    private static void addClasses() {

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            jarPath = selectedFile.getAbsolutePath();

            ArrayList<MyClass> classes = Explorer.getClasses(jarPath);
            classListView.getItems().clear();
            fieldListView.getItems().clear();
            constructorListView.getItems().clear();
            methodListView.getItems().clear();
            classListView.getItems().addAll(classes);
        }
    }

    private static void saveJar() {
        File selectedFile = fileSaver.showSaveDialog(null);

        if (Explorer.jarrFile != null) {
            if (selectedFile != null) {
                String destinationPath = selectedFile.getAbsolutePath();
                if (Objects.equals(jarPath, destinationPath)) {
                    showAlert("Can't save to opened jar file.");
                } else {
                    Manifest manifest = Explorer.manifest;
                    JarOutputStream jarOutputStream = null;
                    try {
                        File file = new File(destinationPath);
                        OutputStream outputStream = new FileOutputStream(file);
                        jarOutputStream = new JarOutputStream(outputStream, manifest);
                    } catch (IOException e) {
                        showAlert("Can't create this file.");
                    }

                    List<String> fileList = new ArrayList<>();
                    for (int i = 0; i < classListView.getItems().size(); i++)
                        fileList.add(((classListView.getItems().get(i).getCtClass().getName()).replaceAll("\\.", "/")) + ".class");

                    int len;
                    byte[] buffer = new byte[1024];
                    int i = 0;
                    for (String file : fileList) {
                        try {
                            JarEntry je = new JarEntry(file);
                            Objects.requireNonNull(jarOutputStream).putNextEntry(je);

                            InputStream is = new BufferedInputStream(new ByteArrayInputStream(classListView.getItems().get(i).getCtClass().toBytecode()));
                            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                                jarOutputStream.write(buffer, 0, len);
                            }
                            i++;
                            is.close();
                            jarOutputStream.closeEntry();
                        } catch (IOException e) {
                            showAlert("Error with saving class files.");
                        } catch (CannotCompileException e) {
                            showAlert("Error with comipiling class file.");
                        }
                    }
                    for (int f = 0; f < Explorer.files.size(); f++) {
                        try {
                            JarEntry jf = Explorer.files.get(f);
                            InputStream is = Explorer.jarrFile.getInputStream(jf);
                            Objects.requireNonNull(jarOutputStream).putNextEntry(Explorer.files.get(f));
                            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                                jarOutputStream.write(buffer, 0, len);
                            }
                            jarOutputStream.closeEntry();
                            is.close();
                        } catch (IOException e) {
                            showAlert("Error while saving non-class files.");
                        }
                    }
                    try {
                        Objects.requireNonNull(jarOutputStream).close();
                    } catch (IOException e) {
                        showAlert("Error closing OutputStream");
                    }
                    showAlert("Saved successfully!");

                }
            }
        } else {
            showAlert("Nothing to save.");
        }
    }

    private static ListView<MyClass> createClassListView() {
        classListView = new ListView<>();

        classListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        getFields(newValue.getCtClass());
                        getConstructors(newValue.getCtClass());
                        getMethods(newValue.getCtClass());
                    }
                });


        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItem1 = new MenuItem("Add class");
        menuItem1.setOnAction(event -> addClass());
        MenuItem menuItem2 = new MenuItem("Delete selected class");
        menuItem2.setOnAction(event -> {
            if (classListView.getSelectionModel().getSelectedItem() != null) {
                MyClass clazz = classListView.getSelectionModel().getSelectedItem();
                if (clazz.getAdded()) {
                    clazz.getCtClass().defrost();
                    classListView.getItems().remove(clazz);
                } else
                    showAlert("You can only remove added classes.");
            } else {
                showAlert("Select class you want to delete.");
            }
        });

        contextMenu.getItems().addAll(menuItem1, menuItem2);

        classListView.setOnContextMenuRequested(event -> contextMenu.show(classListView, event.getScreenX(), event.getScreenY()));


        return classListView;
    }

    private static ListView<MyField> createFieldListView() {
        fieldListView = new ListView<>();


        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItem1 = new MenuItem("Add field");
        menuItem1.setOnAction(event -> {

            if (classListView.getSelectionModel().getSelectedItem() != null) {
                CtClass clazz = classListView.getSelectionModel().getSelectedItem().getCtClass();
                clazz.defrost();
                CtField field;
                try {
                    String results[] = AddField.addField();
                    if (results != null) {
                        String ctF = results[0] + " " + results[1] + " " + results[2] + " " + results[3] + results[4] + ";";
                        field = CtField.make(ctF, clazz);
                        boolean duplicate = false;
                        for (int i = 0; i < fieldListView.getItems().size(); i++) {
                            if (Objects.equals(fieldListView.getItems().get(i).getCtField().getName(), field.getName())) {
                                duplicate = true;
                                showAlert("Don't add duplicates.");
                                break;
                            }
                        }
                        if (!duplicate) {
                            clazz.addField(field);
                        }
                    }
                } catch (CannotCompileException e) {
                    showAlert("Couldn't add this field.");
                }
                getFields(clazz);
            } else {
                showAlert("Select class before adding a field.");
            }

        });

        MenuItem menuItem2 = new MenuItem("Delete selected field");
        menuItem2.setOnAction(event -> {
            if (classListView.getSelectionModel().getSelectedItem() != null && fieldListView.getSelectionModel().getSelectedItem() != null) {
                CtClass clazz = classListView.getSelectionModel().getSelectedItem().getCtClass();
                clazz.defrost();
                CtField field = fieldListView.getSelectionModel().getSelectedItem().getCtField();
                try {
                    clazz.removeField(field);
                    getFields(clazz);
                } catch (NotFoundException e) {
                    showAlert("Field not found.");
                }
            } else {
                showAlert("Select field you want to delete.");
            }
        });

        contextMenu.getItems().addAll(menuItem1, menuItem2);
        fieldListView.setOnContextMenuRequested(event -> contextMenu.show(fieldListView, event.getScreenX(), event.getScreenY()));

        return fieldListView;
    }

    private static ListView<MyConstructor> createConstructorListView() {
        constructorListView = new ListView<>();

        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItem1 = new MenuItem("Add constructor");

        menuItem1.setOnAction(event -> {
            if (classListView.getSelectionModel().getSelectedItem() != null) {
                CtClass clazz = classListView.getSelectionModel().getSelectedItem().getCtClass();
                clazz.defrost();
                CtConstructor constructor;
                try {
                    String results[] = AddConstructor.addConstructor();
                    if (results != null) {
                        String ctC = results[0] + " " + clazz.getSimpleName() + "(" + results[1] + ") {" + results[2] + "}";
                        constructor = CtNewConstructor.make(ctC, clazz);
                        boolean duplicate = false;
                        for (int i = 0; i < constructorListView.getItems().size(); i++) {
                            if (Objects.equals(constructorListView.getItems().get(i).getCtConstructor().getLongName(), constructor.getLongName())) {
                                duplicate = true;
                                showAlert("Don't add duplicates.");
                                break;
                            }
                        }
                        if (!duplicate) {
                            clazz.addConstructor(constructor);
                        }
                    }
                } catch (CannotCompileException e) {
                    showAlert("Couldn't add this constructor.");
                }
                getConstructors(clazz);
            } else {
                showAlert("Select class before adding a constructor.");
            }
        });

        MenuItem menuItem2 = new MenuItem("Delete selected constructor");

        menuItem2.setOnAction(event -> {
            if (classListView.getSelectionModel().getSelectedItem() != null && constructorListView.getSelectionModel().getSelectedItem() != null) {
                try {
                    CtClass clazz = classListView.getSelectionModel().getSelectedItem().getCtClass();
                    clazz.defrost();
                    CtConstructor constructor = constructorListView.getSelectionModel().getSelectedItem().getCtConstructor();
                    clazz.removeConstructor(constructor);
                    getConstructors(clazz);

                } catch (NotFoundException e) {
                    showAlert("Constructor not found.");
                }
            } else {
                showAlert("Select constructor you want to delete.");
            }
        });


        MenuItem menuItem3 = new MenuItem("Overwrite selected constructor body.");

        menuItem3.setOnAction(event -> {
            if (classListView.getSelectionModel().getSelectedItem() != null && constructorListView.getSelectionModel().getSelectedItem() != null) {
                try {
                    CtClass clazz = classListView.getSelectionModel().getSelectedItem().getCtClass();
                    clazz.defrost();
                    CtConstructor constructor = constructorListView.getSelectionModel().getSelectedItem().getCtConstructor();
                    String ctC = OverwriteConstructor.overwriteConstructor();
                    if (ctC != null) {
                        constructor.setBody("{" + ctC + "}");
                        getConstructors(clazz);
                    }

                } catch (CannotCompileException e) {
                    showAlert("Invalid constructor body.");
                }
            } else {
                showAlert("Select constructor you want to overwrite.");
            }
        });

        contextMenu.getItems().addAll(menuItem1, menuItem2, menuItem3);

        constructorListView.setOnContextMenuRequested(event -> contextMenu.show(constructorListView, event.getScreenX(), event.getScreenY()));

        return constructorListView;
    }

    private static ListView<MyMethod> createMethodListView() {
        methodListView = new ListView<>();


        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItem1 = new MenuItem("Add method");

        menuItem1.setOnAction(event -> {

            if (classListView.getSelectionModel().getSelectedItem() != null) {
                CtClass clazz = classListView.getSelectionModel().getSelectedItem().getCtClass();
                clazz.defrost();
                CtMethod method;
                try {
                    String results[] = AddMethod.addMethod();
                    if (results != null) {
                        String ctM = results[0] + " " + results[1] + " " + results[2] + " " + results[3] + " (" + results[4] + ") {" + results[5] + "}";
                        method = CtNewMethod.make(ctM, clazz);
                        boolean duplicate = false;
                        for (int i = 0; i < methodListView.getItems().size(); i++) {
                            if (Objects.equals(methodListView.getItems().get(i).getCtMethod().getLongName(), method.getLongName())) {
                                duplicate = true;
                                showAlert("Don't add duplicates.");
                                break;
                            }
                        }
                        if (!duplicate) {
                            clazz.addMethod(method);
                        }
                    }
                } catch (CannotCompileException e) {
                    showAlert("Couldn't add this method.");
                }
                getMethods(clazz);
            } else {
                showAlert("Select class before adding a method.");
            }

        });
        MenuItem menuItem2 = new MenuItem("Delete selected method");

        menuItem2.setOnAction(event -> {
            if (classListView.getSelectionModel().getSelectedItem() != null && methodListView.getSelectionModel().getSelectedItem() != null) {
                try {
                    CtClass clazz = classListView.getSelectionModel().getSelectedItem().getCtClass();
                    clazz.defrost();
                    CtMethod method = methodListView.getSelectionModel().getSelectedItem().getCtMethod();
                    clazz.removeMethod(method);
                    getMethods(clazz);
                } catch (NotFoundException e) {
                    showAlert("Method not found.");
                }
            } else {
                showAlert("Select method you want to delete.");
            }
        });

        MenuItem menuItem3 = new MenuItem("Overwrite selected method body");

        menuItem3.setOnAction(event -> {
            if (classListView.getSelectionModel().getSelectedItem() != null && methodListView.getSelectionModel().getSelectedItem() != null) {
                try {
                    CtClass clazz = classListView.getSelectionModel().getSelectedItem().getCtClass();
                    clazz.defrost();
                    CtMethod method = methodListView.getSelectionModel().getSelectedItem().getCtMethod();
                    String ctM = OverwriteMethod.overwriteMethod();
                    if (ctM != null) {
                        method.setBody("{" + ctM + "}");
                        getMethods(clazz);
                    }

                } catch (CannotCompileException e) {
                    showAlert("Invalid method body.");
                }
            } else {
                showAlert("Select method you want to overwrite.");
            }
        });

        MenuItem menuItem4 = new MenuItem("Inject at selected method beginning.");

        menuItem4.setOnAction(event -> {
            if (classListView.getSelectionModel().getSelectedItem() != null && methodListView.getSelectionModel().getSelectedItem() != null) {
                try {
                    CtClass clazz = classListView.getSelectionModel().getSelectedItem().getCtClass();
                    clazz.defrost();
                    CtMethod method = methodListView.getSelectionModel().getSelectedItem().getCtMethod();
                    String ctM = InjectMethodBeginning.injectMethodBeginning();
                    if (ctM != null) {
                        method.insertBefore(ctM);
                        getMethods(clazz);
                    }

                } catch (CannotCompileException e) {
                    e.printStackTrace();
                    showAlert("Invalid injection.");
                }
            } else {
                showAlert("Select method you want to inject into.");
            }
        });

        MenuItem menuItem5 = new MenuItem("Inject at select method ending");

        menuItem5.setOnAction(event -> {
            if (classListView.getSelectionModel().getSelectedItem() != null && methodListView.getSelectionModel().getSelectedItem() != null) {
                try {
                    CtClass clazz = classListView.getSelectionModel().getSelectedItem().getCtClass();
                    clazz.defrost();
                    CtMethod method = methodListView.getSelectionModel().getSelectedItem().getCtMethod();
                    String ctM = InjectMethodEnding.injectMethodEnding();
                    if (ctM != null) {
                        method.insertAfter(ctM);
                        getMethods(clazz);
                    }

                } catch (CannotCompileException e) {
                    showAlert("Invalid injection.");
                }
            } else {
                showAlert("Select method you want to inject into.");
            }
        });
        contextMenu.getItems().addAll(menuItem1, menuItem2, menuItem3, menuItem4, menuItem5);

        methodListView.setOnContextMenuRequested(event -> contextMenu.show(methodListView, event.getScreenX(), event.getScreenY()));

        return methodListView;

    }

    private static void getFields(CtClass ctClass) {
        CtField fields[] = ctClass.getDeclaredFields();
        fieldListView.getItems().clear();
        for (CtField field : fields) {
            fieldListView.getItems().add(new MyField(field));
        }
    }

    private static void getMethods(CtClass ctClass) {
        CtMethod methods[] = ctClass.getDeclaredMethods();
        methodListView.getItems().clear();
        for (CtMethod method : methods) {
            methodListView.getItems().add(new MyMethod(method));
        }

    }

    private static void getConstructors(CtClass ctClass) {
        CtConstructor constructors[] = ctClass.getDeclaredConstructors();
        constructorListView.getItems().clear();
        for (CtConstructor constructor : constructors) {
            constructorListView.getItems().add(new MyConstructor(constructor));
        }
    }

    private static void createFileChooser() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Jar file", "*.jar")
        );
    }

    private static void createFileSaver() {
        fileSaver = new FileChooser();
        fileSaver.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Jar file", "*.jar")
        );
    }

    private static void addClass() {
        if (classListView.getItems().size() > 1) {


            String results[] = AddClass.addClass();
            if (results[0] != null && results[1] != null && results[2] != null) {
                int mod = Explorer.getMod(results);
                boolean duplicate = false;
                for (int i = 0; i < classListView.getItems().size(); i++) {
                    if (Objects.equals(classListView.getItems().get(i).getCtClass().getName(), results[3])) {
                        duplicate = true;
                        showAlert("Don't add duplicates.");
                        break;
                    }
                }
                if (!duplicate && results[3].length() > 0) {
                    CtClass clazz = Explorer.classPool.makeClass(results[3]);
                    try {
                        clazz.setModifiers(mod);
                        if(Objects.equals("run", results[4])){
                            clazz.addInterface(Explorer.classPool.get("java.lang.Runnable"));
                            CtMethod method = CtNewMethod.make("public void run() {}", clazz);
                            clazz.addMethod(method);
                        }

                        classListView.getItems().add(new MyClass(clazz, true));
                    } catch (RuntimeException e) {
                        showAlert("Invalid modifiers.");
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    } catch (CannotCompileException e) {
                        e.printStackTrace();
                    }

                }
            }


        } else {
            showAlert("Can't add class, load JAR first.");
        }

    }


    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning.");
        alert.setHeaderText(null);
        alert.setContentText(message);


        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("badger.png"));

        alert.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = getRoot();
        primaryStage.setTitle("theBadger");
        primaryStage.getIcons().add(new Image("badger.png"));
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
    }
}