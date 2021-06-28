package Assignment;

import java.util.ArrayList;


//Crate an interface so that both types of commands can be put in lists  together
public interface DrawingCommand {
    DrawCommands type();

    ArrayList<Double> coordinates();

    String property();

    String tostring();

    //create an enum to hold command types
    enum DrawCommands {
        LINE("LINE"),
        RECTANGLE("RECTANGLE"),
        ELLIPSE("ELLIPSE"),
        POLYGON("POLYGON"),
        PEN("PEN"),
        FILL("FILL"),
        PLOT("PLOT");

        private String string;

        DrawCommands(String string) {
            this.string = string;
        }

        public String cmd() {
            return string;
        }
    }
}

//create creation command class for commands that create shapes
class CreationCommand implements DrawingCommand {
    //store private variables that can be referenced later
    private ArrayList<Double> values;
    private DrawCommands cmdtype;
    private String cmdstring;

    //constructor used in file loading
    public CreationCommand(ArrayList<Double> coordinates, DrawCommands type) {
        this.values = coordinates;
        this.cmdtype = type;
        //turn the coordinates list into a saveable string
        this.cmdstring = type.toString() + " " + coordinates.toString().replace("[", "").replace(",", "").replace("]", "");

    }

    //return methods
    public DrawCommands type() {
        return (cmdtype);
    }

    public ArrayList<Double> coordinates() {
        return values;
    }

    public String property() {
        return null;
    }

    public String tostring() {
        return cmdstring;
    }
}

//property class for fill and pen colour commands
class PropertyCommand implements DrawingCommand {
    //store private variables
    private String property;
    private DrawCommands cmdtype;
    private String cmdstring;

    //constructor used in file loading and saving
    public PropertyCommand(String propertyin, DrawCommands type) {
        this.property = propertyin;
        this.cmdtype = type;
        //turn the property and type into a saveable string
        this.cmdstring = type.toString() + " " + propertyin;
    }

    //return methods
    public DrawCommands type() {
        return (cmdtype);
    }

    public ArrayList<Double> coordinates() {
        return null;
    }

    public String property() {
        return property;
    }

    public String tostring() {
        return cmdstring;
    }
}
