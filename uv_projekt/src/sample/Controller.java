package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.TextField;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Controller {



    @FXML
    Stage primaryStage;
    ToolBar toolbar;
    public CheckBox read,write,execute;
    public VBox information,favourites;
    public String selected_path;
    public Label created_at,modified_at,size;
    public TabPane tabPane;
    public Button button1,btn_return,add_tab,delete,restore,copy,move;
    public ListView ListView,ListView1;
    public javafx.scene.control.TextField search,name1,mkdir,search_map;
    public  ObservableList<String> list,list_files, list_folders,deleted_items,restore_fav,copies,moves;
    public String path = "C:\\Users\\kevin";
    //public String image = "C:\\Users\\User\\Downloads\\uv_projekt\\src\\folder.png";
    //public final Image folder = new Image("file:C:\\Users\\User\\Downloads\\uv_projekt\\src\\folder.png");




    public  void save_pref(){
        URL tmp_path = Controller.class.getResource("preferences.txt");
        File f = new File(tmp_path.getFile());
        //File f = new File("C:\\Users\\User\\Downloads\\uv_projekt\\src\\sample\\preferences.txt");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.print("");
        writer.close();

        for(int i=0;i<list.size();i++){
            add_string(list.get(i));
        }

    }

    public void maps(String path){

        //int selectedIndex = ListView.getSelectionModel().getSelectedIndex();
        //path = path + "\\" + list.get(selectedIndex).substring(10);
        System.out.println(path);
        //list = FXCollections.observableArrayList();
        list_files = FXCollections.observableArrayList();

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                // list_files.add(listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                list_files.add(listOfFiles[i].getName());
            }
        }
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                list_files.add(listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                //list_files.add(listOfFiles[i].getName());
            }
        }

        //ListView.setItems(list);
        ListView1.setItems(list_files);
        //listView.setText("fuk yeah");
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static long size_dir(Path path) {

        final AtomicLong size = new AtomicLong(0);

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                    size.addAndGet(attrs.size());

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {

                    System.out.println("skipped: " + file + " (" + exc + ")");
                    // Skip folders that can't be traversed
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

                    if (exc != null)
                        System.out.println("had trouble traversing: " + dir + " (" + exc + ")");
                    // Ignore errors traversing a folder
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            throw new AssertionError("walkFileTree will not throw IOException if the FileVisitor does not");
        }

        return size.get();
    }

    public String date(String d){
        DateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        String inputText = d;
        Date date = null;
        try {
            date = ISO8601DATEFORMAT.parse(inputText);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  outputFormat.format(date);
    }

    public void file_info(String path){
        Path file = Paths.get(path);
        File file1 = new File(path);
        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(file, BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(attr.isDirectory()){
            //size.setText(humanReadableByteCount(size_dir(file),true));
        }else{
        Long si =  attr.size();
            size.setText(humanReadableByteCount(si,true));
        }

        System.out.println(file1.canExecute());
        if(file1.canRead()) read.setSelected(true);
        else read.setSelected(false);
        if(file1.canWrite())write.setSelected(true);
        else write.setSelected(false);
        if(file1.canExecute()) execute.setSelected(true);
        else execute.setSelected(false);




       // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");


        created_at.setText(date(String.valueOf(attr.creationTime())));
        modified_at.setText(date(String.valueOf(attr.lastModifiedTime())));
        name1.setText(ret_name(selected_path));



    }

   public void display_image(ListView list){

        list.setCellFactory(param -> new ListCell<String>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {

                    File tmp = new File(name);

                    ImageIcon icon =(ImageIcon) FileSystemView.getFileSystemView().getSystemIcon( tmp );


                    java.awt.Image temp = icon.getImage();
                    BufferedImage image = new BufferedImage(icon.getIconWidth(),icon.getIconHeight(),BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = image.createGraphics();
                    g2d.drawImage(temp,0,0,null);
                    g2d.dispose();
                    Image fxIcon = SwingFXUtils.toFXImage(image, null);

                    imageView.setImage(fxIcon);

                    {
                        int endIndex = name.lastIndexOf(File.separator);
                        if (endIndex != -1)
                        {
                            name =name.substring(endIndex+1); // not forgot to put check if(endIndex != -1)
                        }

                    }



                    imageView.setImage(fxIcon);
                    setText(name);
                    setGraphic(imageView);
                }
            }
        });
    }

    public void display_image_files(ListView list){
        list.setEditable(true);
        list.setCellFactory(param -> new ListCell<String>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    File tmp = new File(path+File.separator+name);

                    ImageIcon icon =(ImageIcon) FileSystemView.getFileSystemView().getSystemIcon( tmp );
                    java.awt.Image temp = icon.getImage();
                    BufferedImage image = new BufferedImage(icon.getIconWidth(),icon.getIconHeight(),BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = image.createGraphics();
                    g2d.drawImage(temp,0,0,null);
                    g2d.dispose();
                    Image fxIcon = SwingFXUtils.toFXImage(image, null);

                    imageView.setImage(fxIcon);



                    setText(name);
                    setGraphic(imageView);
                }
            }
        });
    }


    @FXML


    public void initialize(){
//        toolbar.prefWidthProperty().bind(primaryStage.widthProperty());
        //favourites.maxHeightProperty().bind(favourites.heightProperty());

        try {
            File file = File.createTempFile("temp", "dir");
        } catch (IOException e) {
            e.printStackTrace();
        }

        URL tmp_path = Controller.class.getResource("preferences.txt");
        File preference = new File(tmp_path.getFile());

        search.setText(path+File.separator);
        deleted_items = FXCollections.observableArrayList();
        restore_fav = FXCollections.observableArrayList();
        list = FXCollections.observableArrayList();
        copies = FXCollections.observableArrayList();
        moves = FXCollections.observableArrayList();

        try {
            try (BufferedReader br = new BufferedReader(new FileReader(preference))) {
                String line;
                try {
                    while ((line = br.readLine()) != null) {
                        // process the line.
                        list.add(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        ListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ListView1.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ListView.setEditable(true);
        ListView1.setEditable(true);
        list_files = FXCollections.observableArrayList();



        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                // list_files.add(listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                list_files.add(listOfFiles[i].getName());
            }
        }
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                list_files.add(listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                //list_files.add(listOfFiles[i].getName());
            }
        }

        //ListView.setItems(list);
        ListView1.setItems(list_files);
        ListView.setItems(list);
        display_image(ListView);
        display_image_files(ListView1);


    }

    @FXML

    public void search_in_file(KeyEvent keycode){




    }
    public void start_search(KeyEvent keycode){
        System.out.println("test");

        if(keycode.getCode() == KeyCode.ENTER){

            String tmp_path = search.getText();

            list_files = FXCollections.observableArrayList();
            File folder = new File(tmp_path);
            if(folder.exists()){
                path = tmp_path;
            }
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    // list_files.add(listOfFiles[i].getName());
                } else if (listOfFiles[i].isDirectory()) {
                    list_files.add(listOfFiles[i].getName());
                }
            }
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    list_files.add(listOfFiles[i].getName());
                } else if (listOfFiles[i].isDirectory()) {
                    //list_files.add(listOfFiles[i].getName());
                }
            }

            ListView1.setItems(list_files);
            information.setOpacity(0.3);
        }

    }

    public void onclick(final ActionEvent event){

        Button b = (Button) event.getSource();
         if(b.getId().equals(btn_return.getId())){

            if (null != path && path.length() > 0 && !path.equals("C:\\"))
            {
                int endIndex = path.lastIndexOf(File.separator);
                if (endIndex != -1)
                {
                    path = path.substring(0, endIndex); // not forgot to put check if(endIndex != -1)
                }
                maps(path);
            }


        }
        search.setText(path);
    }

    public void onDrag(MouseEvent event){
        Dragboard db = ListView1.startDragAndDrop(TransferMode.ANY);

        ObservableList temp =  ListView1.getSelectionModel().getSelectedIndices();
        ClipboardContent cb = new ClipboardContent();
        String s = "";

        for(int i = temp.size()-1;i>=0;i--){

            s = s+" "+path+File.separator+list_files.get((int)temp.get(i)).replace(" ","*");
            //list.remove((int)temp.get(i));


        }

        cb.putString(s);
        db.setContent(cb);

        event.consume();



    }

    public void onDragDelete(MouseEvent event){
        Dragboard db = ListView.startDragAndDrop(TransferMode.ANY);

        ObservableList temp =  ListView.getSelectionModel().getSelectedIndices();
        ClipboardContent cb = new ClipboardContent();
        String s = "";
        for(int i = temp.size()-1;i>=0;i--){

            s = s+" "+list.get((int)temp.get(i)).replace(" ","*");
            restore_fav.add(list.get((int)temp.get(i)));
            list.remove((int)temp.get(i));


        }
        restore.setOpacity(1);
        cb.putString(list.get(ListView.getSelectionModel().getSelectedIndex()));
        cb.getString();
        db.setContent(cb);
        save_pref();
        event.consume();

    }

    public String ret_name(String str)
    {

        if (null != str && str.length() > 0 && !str.equals("C:\\")) {
            int endIndex = str.lastIndexOf(File.separator);
            if (endIndex != -1) {
                str = str.substring(endIndex + 1); // not forgot to put check if(endIndex != -1)
            }
        }
            return str;
    }
    public String ret_parent(String str)
    {

        if (null != str && str.length() > 0 && !str.equals("C:\\")) {
            int endIndex = str.lastIndexOf(File.separator);
            if (endIndex != -1) {
                str = str.substring(0,endIndex); // not forgot to put check if(endIndex != -1)
            }
        }
        return str;
    }

    public void add_string(String str){

        try {
            URL tmp_path = Controller.class.getResource("preferences.txt");
            File preference = new File(tmp_path.getFile());

            String filename = preference.getAbsolutePath();
            FileWriter fw = new FileWriter(filename, true); //the true will append the new data
            fw.write(str + "\n");//appends the string to the file
            fw.close();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    public void onDragDrop(DragEvent event){
        if (event.getDragboard().hasString()) {
            String str = event.getDragboard().getString();
            //System.out.print(str);

            String[] st = str.split(" ");
            for(int i = 1 ;i<st.length;i++){

                list.add(st[i].replace("*"," "));
            }

            ListView.setItems(list);

           save_pref();
            //listView.setText("Fucking hell");
        }else if(event.getDragboard().hasFiles()){

            List <File> tmp = event.getDragboard().getFiles();
            for(int i = 0;i<tmp.size();i++) {
                String str = tmp.get(i).getAbsolutePath();
                list.add(str);
            }
            save_pref();
            ListView.setItems(list);
        }
        information.setOpacity(0.4);
    }

    public void onDragOver(DragEvent event){
        if(event.getDragboard().hasString()) event.acceptTransferModes(TransferMode.ANY);
        if(event.getDragboard().hasFiles()) event.acceptTransferModes(TransferMode.ANY);
        //System.out.print("soemthing");
        //ListView.setItems(list);
    }



    public void onListClick(MouseEvent click){
        int selectedIndex = ListView.getSelectionModel().getSelectedIndex();

        if(selectedIndex != -1){
        selected_path = list.get(selectedIndex);
        //String type = list.get(selectedIndex).split(" ")[0];
        if(click.getClickCount() == 2) {
            path = list.get(selectedIndex);
            System.out.println(path);
            //list = FXCollections.observableArrayList();
            list_files = FXCollections.observableArrayList();

            File folder = new File(path);

            if (folder.isDirectory()) {
                File[] listOfFiles = folder.listFiles();
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        // list_files.add(listOfFiles[i].getName());
                    } else if (listOfFiles[i].isDirectory()) {
                        list_files.add(listOfFiles[i].getName());
                    }
                }
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        list_files.add(listOfFiles[i].getName());
                    } else if (listOfFiles[i].isDirectory()) {
                        //list_files.add(listOfFiles[i].getName());
                    }
                }
                //ListView.setItems(list);
                ListView1.setItems(list_files);

                search.setText(path);

            } else if (folder.isFile()) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(folder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            information.setOpacity(0.4);
        }else{
            String tmp_path = list.get(selectedIndex);
            file_info(tmp_path);
            information.setOpacity(1);

        }
        }else{
            information.setOpacity(0.4);

        }
    }


    public void add_tab(){
        System.out.println("Somethiog");
        Tab new_tab = new Tab();
        //new_tab = tabPane.getTabs().get(0);
        tabPane.getTabs().add(new_tab);


    }



    public void set_list(String path){
        list_files = FXCollections.observableArrayList();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                // list_files.add(listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                list_files.add(listOfFiles[i].getName());
            }
        }
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                list_files.add(listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                //list_files.add(listOfFiles[i].getName());
            }
        }
        ListView1.setItems(list_files);
        search.setText(path);
        information.setOpacity(30);
    }

    public void onListClick1(MouseEvent click){

        int selectedIndex = ListView1.getSelectionModel().getSelectedIndex();

        if(selectedIndex != -1){
        //String type = list.get(selectedIndex).split(" ")[0];
        String tmp =path+ File.separator + list_files.get(selectedIndex);
        selected_path = tmp;
        System.out.println(path);
        File file = new File(tmp);

        if(click.getClickCount() == 2 ){
            if(file.isDirectory()) {
                path=tmp;
                set_list(path);
            }else if(file.isFile()){


                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            };
            information.setOpacity(0.4);
        }else{
            file_info(tmp);
            information.setOpacity(1);
        }
        }else{
            information.setOpacity(0.4);

        }
    }

    public void name_update(KeyEvent keycode){
        if(keycode.getCode() ==  KeyCode.ENTER){

            File file = new File(selected_path);

// File (or directory) with new name

            File file2 = new File(ret_parent(selected_path)+File.separator+name1.getText());

            if (file2.exists())
                try {
                    throw new IOException("file exists");
                } catch (IOException e) {
                    e.printStackTrace();
                }

// Rename file (or directory)
            boolean success = file.renameTo(file2);

            if (!success) {
                // File was not successfully renamed
            }

            set_list(ret_parent(selected_path));
        }
    }

    public void edit_start(){

    }

    public void edit_commit(){

    }

    public void onDelete(){
        ObservableList selectedIndex = ListView1.getSelectionModel().getSelectedIndices();

        for(int i = selectedIndex.size()-1 ; i>=0;i--){
            int x =(int) selectedIndex.get(i);
            String tmp = list_files.get(x);

            list_files.remove(x);
            deleted_items.add(tmp);
            //Path p = path+File.separator+tmp;
            try {
                Files.deleteIfExists(Paths.get(path+File.separator+tmp));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        information.setOpacity(0.4);


    }


    public void onDragDelete(){

    }

    public void can_read(){


        if(read.isSelected()){
            File file = new File(selected_path);
            if(file.isFile()){

            }
            file.setReadable(true);
        }else {
            //System.out.println(selected_path);
            File file = new File(selected_path);
            System.out.println(file.setReadable(false,false));
            // System.out.println("can execute"+file.canExecute());
        }

    }

    public void can_write(){

        if(write.isSelected()){
            File file = new File(selected_path);
            if(file.isFile()){

            }
            file.setWritable(true);
        }else {
            //System.out.println(selected_path);
            File file = new File(selected_path);
            System.out.println(file.setWritable(false,false));
            //System.out.println("can execute"+file.canExecute());
        }


    }

    public void can_execute(){
        if(execute.isSelected()){
            File file = new File(selected_path);
            if(file.isFile()){

            }
            file.setExecutable(true);
        }else {
            //System.out.println(selected_path);
            File file = new File(selected_path);
            System.out.println(file.setExecutable(false,false));
           // System.out.println("can execute"+file.canExecute());
        }

    }


    public void restore(){

        for(int i = 0;i< restore_fav.size();i++){
            list.add(restore_fav.get(i));
            //System.out.println(restore_fav.get(i));
        }
        save_pref();
        ListView.setItems(list);
        restore_fav = FXCollections.observableArrayList();;
        restore.setOpacity(0.4);

    }

    public void mkdir(KeyEvent event){

        if(event.getCode() ==KeyCode.ENTER){
            mkdir.setOpacity(0.4);

            try {
                Files.createDirectories(Paths.get(mkdir.getText()));
                set_list(path);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mkdir.setText("Create directory");
        }


    }

    public void mkdir_path(MouseEvent click){

        if(click.getClickCount() > 0) {
            mkdir.setText(path+File.separator);
            mkdir.positionCaret(path.length()+1);
            mkdir.setOpacity(1);
        }
    }

    public void create_copy(){

        for(int i =0;i<copies.size();i++){
            //File tm = new File(moves.get(i));
            //String par = ret_parent(moves.get(i));
            String src = copies.get(i);
            String dest = path+File.separator+ret_name(copies.get(i));
            try {
                Files.copy(Paths.get(src),Paths.get(dest),StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        copy.setText("Copy");
        copies = FXCollections.observableArrayList();
        set_list(path);
        //Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);

    }

    public void save_copies(DragEvent event){
         event.acceptTransferModes(TransferMode.ANY);


        if (event.getDragboard().hasString()) {
            String str = event.getDragboard().getString();
            //System.out.print(str);

            String[] st = str.split(" ");
            for(int i = 1 ;i<st.length;i++){
               copies.add(st[i].replace("*"," "));
            }

            copy.setText("Copy("+copies.size()+")");
            set_list(path);
            //ListView.setItems(list);

            //save_pref();
            //listView.setText("Fucking hell");
        }else if(event.getDragboard().hasFiles()){

            List <File> tmp = event.getDragboard().getFiles();
            for(int i = 0;i<tmp.size();i++) {
                String str = tmp.get(i).getAbsolutePath();
                copies.add(str);
            }
            // save_pref();
            //ListView.setItems(list);
        }

    }

    public void move(){

        for(int i =0;i< moves.size();i++){
            File tm = new File(moves.get(i));
            String nam = ret_name(moves.get(i));
            if(tm.exists()){
            tm.renameTo(new File(path+File.separator+nam));
            }
        }
        move.setText("Move");
        moves = FXCollections.observableArrayList();
        set_list(path);

    }

    public void save_moves(DragEvent event){
        event.acceptTransferModes(TransferMode.ANY);

        if (event.getDragboard().hasString()) {
            String str = event.getDragboard().getString();
            //System.out.print(str);

            String[] st = str.split(" ");
            for(int i = 1 ;i<st.length;i++){
                moves.add(st[i].replace("*"," "));
            }

            move.setText("Move("+moves.size()+")");
            set_list(path);
            //ListView.setItems(list);

            //save_pref();
            //listView.setText("Fucking hell");
        }else if(event.getDragboard().hasFiles()){

            List <File> tmp = event.getDragboard().getFiles();
            for(int i = 0;i<tmp.size();i++) {
                String str = tmp.get(i).getAbsolutePath();
                copies.add(str);
            }
           // save_pref();
            //ListView.setItems(list);
        }

    }


}
