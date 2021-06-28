package Assignment;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileLoader {
    private ArrayList<DrawingCommand> dFile = new ArrayList<>();

    //returns array list
    public ArrayList<DrawingCommand> getdFile() {
        return dFile;
    }

    //loads a selected vec file into drawing commands;
    public ArrayList<DrawingCommand> loadfile() {
        //create file choosed popup and add file filter to only show .vec and folders
        JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));// + "\\Vec");
        FileFilter filter = new FileNameExtensionFilter("vec files", "vec");
        chooser.setFileFilter(filter);

        List<String> commands = new ArrayList<>();
        int returnVal = chooser.showOpenDialog(null);
        //checks if selected option is approved by the jfilechoser
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                //trys to create a buffered reader to read the vec file line by line
                BufferedReader reader = new BufferedReader(new FileReader(chooser.getSelectedFile()));
                String command = reader.readLine();

                //loads command into commands list as a string for parsing later on
                while (command != null) {
                    commands.add(command);
                    command = reader.readLine();
                }
                //close reader to reduce system usage
                reader.close();
            } catch (IOException e) {}
            //ensure dfile is clear
            dFile.clear();

            //itterate through command strings
            for (int i = 0; i < commands.size(); i++) {
                //split command into array based on spaces
                String[] coordstring = commands.get(i).split(" ");
                ArrayList<Double> coordsdouble = new ArrayList<>();
                String properties = null;

                //checks if command is pen or fill type as they store strings in their objects not coordinates
                if(!commands.get(i).contains("PEN")&&!commands.get(i).contains("FILL")){
                    for (int j = 1; j < coordstring.length; j++){coordsdouble.add(Double.parseDouble(coordstring[j]));}
                } else {
                    for (int j = 1; j < coordstring.length; j++){
                        properties=(coordstring[j]);
                    }
                }
                //creates new command based on what command type is currently used
                if (commands.get(i).contains(DrawingCommand.DrawCommands.LINE.cmd())) {
                    dFile.add(new CreationCommand(coordsdouble, DrawingCommand.DrawCommands.LINE));
                } else if (commands.get(i).contains(DrawingCommand.DrawCommands.RECTANGLE.cmd())) {
                    dFile.add(new CreationCommand(coordsdouble, DrawingCommand.DrawCommands.RECTANGLE));
                } else if (commands.get(i).contains(DrawingCommand.DrawCommands.POLYGON.cmd())) {
                    dFile.add(new CreationCommand(coordsdouble, DrawingCommand.DrawCommands.POLYGON));
                } else if (commands.get(i).contains(DrawingCommand.DrawCommands.ELLIPSE.cmd())) {
                    dFile.add(new CreationCommand(coordsdouble, DrawingCommand.DrawCommands.ELLIPSE));
                } else if (commands.get(i).contains(DrawingCommand.DrawCommands.PLOT.cmd())) {
                    dFile.add(new CreationCommand(coordsdouble, DrawingCommand.DrawCommands.PLOT));
                } else if (commands.get(i).contains(DrawingCommand.DrawCommands.PEN.cmd())) {
                    dFile.add(new PropertyCommand(properties, DrawingCommand.DrawCommands.PEN));
                } else if (commands.get(i).contains(DrawingCommand.DrawCommands.FILL.cmd())) {
                    dFile.add(new PropertyCommand(properties, DrawingCommand.DrawCommands.FILL));
                }

            }
            //return the array
            return (dFile);
        }
        return null;
    }

    //saves input arraylist to a vec file
    public void savefile(ArrayList<DrawingCommand> input) {
        //creates jfilechooser to select new file location
        JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));// + "\\Vec");
        //adds filter to file chooser to hide everything but folders and other vec files
        FileFilter filter = new FileNameExtensionFilter("vec files", "vec");
        chooser.setFileFilter(filter);
        //opens popup
        int returnVal = chooser.showSaveDialog(null);
        //ensure the chosen location is approved by jfilechooser
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                //try to create file where the save is to go
                File file = chooser.getSelectedFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file + ".vec"));
                Iterator<DrawingCommand> commandIterator = input.iterator();
                //itterate through commands to write them to the file
                for (int i = 0; i < input.size(); i++) {
                    writer.write(commandIterator.next().tostring());
                    if (commandIterator.hasNext()) {
                        writer.newLine();
                    }
                }
                //close writer to save memory
                writer.close();
            } catch (IOException e) {
            }
        }
    }
}
