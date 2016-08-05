package de.unibi.cebitec.bibiserv.util.streamvalidate;

import de.unibi.cebitec.bibiserv.util.validate.ValidationResult;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
public class StreamValidatorMain {

    /**
     * @param args the command line arguments:
     *        0 : implementing class
     *        1 : Content
     *        2 : Strictness
     *        3 : Cardinality
     *        4 : Alignment (boolean)
     *        5 : name of the output pipe
     *        6 : index for output files
     *        7 : spool directory
     *        8 : name for error message
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
            if (args.length < 9) {

            System.out.println("0 : implementing class");
            System.out.println("1 : Content");
            System.out.println("2 : Strictness");
            System.out.println("3 : Cardinality");
            System.out.println("4 : Alignment (boolean)");
            System.out.println("5 : name of the output pipe");
            System.out.println("6 : index for output files");
            System.out.println("7 : spool directory");
            System.out.println("8 : name for error message");
                
                in.close();
                System.exit(3);
            }
            info = new BufferedWriter(new FileWriter(args[7]+"info"+args[6]+".err"));
            err = new BufferedWriter(new FileWriter(args[7]+"error"+args[6]+".err"));
            out = new BufferedWriter(new FileWriter(args[5]));
            
            errorNameMessage = "Validation failed for "+args[8] +". Message was: \n";
                       
        } catch (IOException ex) {
            try {
                err.write(errorNameMessage);
                err.write("Failed to open pipe.");
                err.close();
                in.close();
            } catch (IOException ex1) {
            }
            System.exit(3);
        }
        
        // handle different formats
        StreamValidator validator = null;
        try {
            Class validatorClass = Class.forName(args[0]);
            validator = (StreamValidator) validatorClass.newInstance();
        } catch (InstantiationException | IllegalAccessException| ClassNotFoundException ex) {
            try {
                err.write(errorNameMessage);
                err.write("Failed to instantiate class.");
                err.close();
                in.close();
            } catch (IOException ex1) {
            }
            System.exit(3);
        }
        
        
        if(validator instanceof SequenceStreamValidator) {
            // set content
            SequenceStreamValidator sValidator = (SequenceStreamValidator) validator;
            boolean found = false;
            for(SequenceStreamValidator.CONTENT c: SequenceStreamValidator.CONTENT.values()) {
                if(c.name().toUpperCase().equals(args[1].toUpperCase())) {
                    sValidator.setContent(c);
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

             // set stricness
            found = false;
            for(SequenceStreamValidator.STRICTNESS s: SequenceStreamValidator.STRICTNESS.values()) {
                if(s.name().toUpperCase().equals(args[2].toUpperCase())) {
                    sValidator.setStrictness(s);
                    found=true;
                    break;
                }
            }
            if(!found && !args[2].toUpperCase().equals("NULL")){
                try {
                    err.write(errorNameMessage);
                    err.write("Invalid Strictness!");
                    err.close();
                    in.close();
                } catch (IOException ex1) {
                }
                System.exit(3);
            }

            // set cardinality
            found = false;
            for(SequenceStreamValidator.CARDINALITY c: SequenceStreamValidator.CARDINALITY.values()) {
                if(c.name().toUpperCase().equals(args[3].toUpperCase())) {
                    sValidator.setCardinality(c);
                    found=true;
                    break;
                }
            }
            if(!found && !args[3].toUpperCase().equals("NULL")){
                try {
                    err.write(errorNameMessage);
                    err.write("Invalid Cardinality!");
                    err.close();
                    in.close();
                } catch (IOException ex1) {
                }
                System.exit(3);
            }
        }
        
        if(validator instanceof AlignmentStreamValidator) {
            AlignmentStreamValidator aValidator = (AlignmentStreamValidator) validator;
            boolean alignment = Boolean.parseBoolean(args[4]);
            aValidator.setAlignment(alignment);
        }
        
        ValidationResult res = validator.validateThis(in, out);

        try {
            // write message
            err.write(errorNameMessage);
            err.write(res.getMessage());
            
            // write infos
            for(String s: res.getWarnings()){
                info.write(s);
                info.newLine();
            }

        } catch (IOException ex) {
            try {
                err.write(errorNameMessage);
                err.write("Failed to write validation response.");
                err.close();
                in.close();
            } catch (IOException ex1) {
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
        
        if (!res.isValid()) {
            if (res.getMessage().toLowerCase().contains("broken pipe")) {
                System.exit(100);
            }

            System.exit(3);
        }
    }
}
