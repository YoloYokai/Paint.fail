package Assignment;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

//class to hold functions that are used to connect drawing command objects to drawnshape objects
public class ObjectExchanger {
    //returns a list of drawing commands that is created from the drawn shape object
    public static ArrayList<DrawingCommand> updatecommands(Graphics2D canvas, ArrayList<DrawnShape> shapes) {
        //initialize output array
        ArrayList<DrawingCommand> output = new ArrayList<>();

        //load variables for initial referencing
        double width = canvas.getClip().getBounds().getWidth();
        double height = canvas.getClip().getBounds().getHeight();
        boolean fillstate = shapes.get(0).getfillstate();
        Color pen = shapes.get(0).getShapeStroke();
        Color fill = shapes.get(0).getShapeFill();


        //adds fill command if the first shape in the list has a true fill state
        //decodes fill colour to hex 
        if (fillstate) {
            output.add(new PropertyCommand("#" + Integer.toHexString(shapes.get(0).getShapeFill().getRGB()).substring(2), DrawingCommand.DrawCommands.FILL));
        }
        //adds pen colour command 
        output.add(new PropertyCommand("#" + Integer.toHexString(shapes.get(0).getShapeFill().getRGB()).substring(2), DrawingCommand.DrawCommands.PEN));

        //for loop to interate through the shapes
        for (DrawnShape current_shape : shapes) {
            //if the shape toggles fill off adds fill off command and changes current fillstate
            if (fillstate && !current_shape.getfillstate()) {
                output.add(new PropertyCommand("OFF", DrawingCommand.DrawCommands.FILL));
                fillstate = false;
            }

            //checks if fill colour is the same as the previous shapes 
            //if it isnt add a fill colour change
            if (fill != current_shape.getShapeFill()) {
                output.add(new PropertyCommand("#" + Integer.toHexString(current_shape.getShapeFill().getRGB()).substring(2), DrawingCommand.DrawCommands.FILL));
                fillstate = true;
                fill = current_shape.getShapeFill();
            }

            //checks pen colour against previous pen colour
            //if it isnt add a pen colour command
            if (pen != current_shape.getShapeStroke()) {
                output.add(new PropertyCommand("#" + Integer.toHexString(current_shape.getShapeStroke().getRGB()).substring(2), DrawingCommand.DrawCommands.PEN));
                pen = current_shape.getShapeStroke();
            }

            //load an array to hold coordinates temporarily 
            ArrayList<Double> tmpcoords = new ArrayList<>();
            //iterate through the shapes coordinates and convert them to percentages with 6 decimals
            for (int i = 0; i < current_shape.getPos().size(); i += 2) {
                tmpcoords.add(Math.round((current_shape.getPos().get(i) / width) * 1000000.0) / 1000000.0);
                tmpcoords.add(Math.round((current_shape.getPos().get(i + 1) / height) * 1000000.0) / 1000000.0);
            }
            //create a new creation command based on the input shape uses enumerator to compress if statements
            for (DrawingCommand.DrawCommands type : DrawingCommand.DrawCommands.values()) {
                if (current_shape.getType() == type) {
                    output.add(new CreationCommand(tmpcoords, type));
                }
            }
        }
        //return all commands
        return output;
    }


    //command to convert drawing commands to drawn shapes
    public static ArrayList<DrawnShape> updateCanvas(Graphics2D canvas, ArrayList<DrawingCommand> commands) {
        //create temporary variables
        boolean fill = false;
        Color pencolor = new Color(1);
        Color fillcolor = new Color(1);
        ArrayList<DrawnShape> output = new ArrayList<>();
        double width = canvas.getClip().getBounds().getWidth();
        double height = canvas.getClip().getBounds().getHeight();


        //itterate through the array list
        for (DrawingCommand current_command : commands) {
            //checks if current command is either fill or pen
            if (current_command.type() != DrawingCommand.DrawCommands.FILL && current_command.type() != DrawingCommand.DrawCommands.PEN) {
                //if no colour change is made uses default colour of black
                canvas.setPaint(pencolor);
                //run command through function to get shape
                canvas.draw(CmdtoShape(current_command, width, height));
                //fills shape if fill is true
                if (fill) {
                    canvas.setPaint(fillcolor);
                    canvas.fill(CmdtoShape(current_command, width, height));
                }
                //creates drawn shape object and saves it to the output variable
                output.add(new DrawnShape(CmdtoShape(current_command, width, height), fillcolor, fill, pencolor, current_command.type(), current_command.coordinates()));

            }
            //If current commmand is a fill command decode the colour or change state
            if (current_command.type() == DrawingCommand.DrawCommands.FILL) {
                if (current_command.property().contains("OFF")) {
                    fill = false;
                } else {
                    fillcolor = Color.decode(current_command.property());
                    fill = true;
                }
            }
            //decode pen colour if current command is pen
            if (current_command.type() == DrawingCommand.DrawCommands.PEN) {
                pencolor = Color.decode(current_command.property());
            }

        }
        //return output
        return output;
    }

    //creates shape object from commands and canvas size
    private static Shape CmdtoShape(DrawingCommand input, double width, double height) {
        //initialise variables
        Shape output;
        ArrayList<Double> positions = input.coordinates();

        //creates a shape based on the current command type
        if (input.type() == DrawingCommand.DrawCommands.PLOT) {
            output = new Line2D.Double(positions.get(0) * width, positions.get(1) * height, positions.get(0) * width, positions.get(1) * height);
        } else if (input.type() == DrawingCommand.DrawCommands.LINE) {
            output = new Line2D.Double(positions.get(0) * width, positions.get(1) * height, positions.get(2) * width, positions.get(3) * height);
        } else if (input.type() == DrawingCommand.DrawCommands.ELLIPSE) {
            output = new Ellipse2D.Double(positions.get(0) * width, positions.get(1) * height, Math.abs((positions.get(0) - positions.get(2)) * width), Math.abs((positions.get(3) - positions.get(1)) * height));
        } else if (input.type() == DrawingCommand.DrawCommands.RECTANGLE) {
            output = new Rectangle2D.Double(positions.get(0) * width, positions.get(1) * height, Math.abs((positions.get(0) - positions.get(2)) * width), Math.abs((positions.get(3) - positions.get(1)) * height));
        } else if (input.type() == DrawingCommand.DrawCommands.POLYGON) {
            //split coordinates into x and y to parese into polygon shape constructor
            ArrayList<Double> x = new ArrayList<>();
            ArrayList<Double> y = new ArrayList<>();
            int count = 0;

            for (int i = 0; i < input.coordinates().size(); i += 2) {
                x.add(input.coordinates().get(i) * width);
                y.add(input.coordinates().get(i + 1) * height);
                count++;
            }

            //create int[] for input into polygon function
            int[] xout = new int[x.size()];
            for (int i = 0; i < xout.length; i++) {
                xout[i] = x.get(i).intValue();
            }
            int[] yout = new int[y.size()];
            for (int i = 0; i < yout.length; i++) {
                yout[i] = y.get(i).intValue();
            }
            output = new Polygon(xout, yout, count);
        } else {
            output = null;
        }
        //return created shape
        return output;
    }
}
