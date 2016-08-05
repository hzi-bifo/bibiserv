package de.unibi.cebitec.bibiserv.util.streamconvert;

import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 *
 * This is the main class to call all validators from console.
 *
 *
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielfeld.de
 */
public class StreamConverterMain {

    /**
     * @param args the command line arguments:
     *        0 : implementing class
     *        1 : Content
     *        2 : name of the output pipe
     *        3 : index for output files
     *        4 : spool directory
     *        5 : name for display only
     */
    public static void main(String[] args) throws FileNotFoundException {

        Locale.setDefault(Locale.ENGLISH);
        
        // get all Streams
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter err = null;
        BufferedWriter out = null;
        BufferedWriter info = null;
        String errorNameMessage = "";
        try {
            if (args.length < 6) {
                in.close();
                System.exit(3);
            }
            info = new BufferedWriter(new FileWriter(args[4]+"info"+args[3]+".err"));
            err = new BufferedWriter(new FileWriter(args[4]+"error"+args[3]+".err"));
            out = new BufferedWriter(new FileWriter(args[2]));
            
            errorNameMessage = "Conversion failed for "+args[5] +". Message was: \n";
                        
        } catch (IOException ex) {
            try {
                err.write("Failed to open pipe.");
                err.close();
                in.close();
            } catch (IOException ex1) {
            }
            System.exit(3);
        }
        
        // handle different formats
        StreamConverter converter = null;
        try {
            Class converterClass = Class.forName(args[0]);
            converter = (StreamConverter) converterClass.newInstance();
        } catch (InstantiationException | IllegalAccessException| ClassNotFoundException ex) {
            try {
                err.write(errorNameMessage);
                err.write("Failed to create ");
                err.close();
                in.close();
            } catch (IOException ex1) {
            }
            System.exit(3);
        }
        
        // set content
        boolean found = false;
        for(SequenceValidator.CONTENT c: SequenceValidator.CONTENT.values()) {
            if(c.name().equals(args[1])) {
                converter.setContent(c);
                found=true;
                break;
            }
        }
        if(!found && !args[1].toUpperCase().equals("NULL")){
            try {
                err.write(errorNameMessage);
                err.write("Invalid content!");
                err.close();
                in.close();
            } catch (IOException ex1) {
            }
            System.exit(3);
        }
        try {
            converter.convert(in, out);
        } catch (ConversionException ex) {
             try {
                err.write(errorNameMessage);
                err.write(ex.toString());
                err.close();
                in.close();
            } catch (IOException ex1) {
            }
             
            if (ex.toString().toLowerCase().contains("broken pipe")) {
                System.exit(100);
            } 
             
            System.exit(3);
        }
        
        // close verything
        try {
            in.close();
            out.close();
            err.close();
            info.close();
        } catch (IOException ex) {
            System.exit(3);
        }
    }
}
