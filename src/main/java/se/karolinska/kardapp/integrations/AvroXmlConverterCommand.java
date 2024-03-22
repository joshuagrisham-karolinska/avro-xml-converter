package se.karolinska.kardapp.integrations;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import javax.xml.transform.OutputKeys;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.ws.commons.schema.XmlSchema;

public class AvroXmlConverterCommand {

    private static String COMMAND_NAME = "avro-xml-converter";
    private static String OPT_HELP = "help";
    private static String OPT_SCHEMA = "schema";
    private static String OPT_DATA = "data";
    private static String OPT_OUTPUT = "output";
    private static String OPT_PRETTY_PRINT = "pretty-print";
    private static String OPT_AVRO_ENCODER_SYNTAX = "avro-encoder-syntax";

    private static Options OPTIONS = new Options();

    public static void main(String[] args) throws IOException {

        CommandLine opts = parseArgs(args);
        if (opts.hasOption(OPT_HELP)) {
            printHelp();
            return;
        }

        if (!opts.hasOption(OPT_SCHEMA)) {
            System.err.println("Error: schema is required");
            printHelp();
            return;
        }

        if (opts.hasOption(OPT_DATA))
            convertData(opts);
        else
            convertSchema(opts);

    }

    private static CommandLine parseArgs(String[] args) {
 
        OPTIONS.addOption(Option.builder("h")
            .longOpt(OPT_HELP)
            .hasArg(false)
            .required(false)
            .desc("print this help message")
            .build());

        OPTIONS.addOption(Option.builder("s")
            .longOpt(OPT_SCHEMA)
            .hasArg(true)
            .required(false)
            .desc("Avro Schema file (.avsc) to be converted, or is needed for use with the data file")
            .type(String.class)
            .build());

        OPTIONS.addOption(Option.builder("d")
            .longOpt(OPT_DATA)
            .hasArg(true)
            .required(false)
            .desc("Avro Data file to be converted")
            .type(String.class)
            .build());

        OPTIONS.addOption(Option.builder("o")
            .longOpt(OPT_OUTPUT)
            .hasArg(true)
            .required(false)
            .desc("write converted XML data (when DATA is set) or schema (when DATA is not set) to file instead of stdout")
            .type(String.class)
            .build());

        OPTIONS.addOption(Option.builder()
            .longOpt(OPT_PRETTY_PRINT)
            .hasArg(false)
            .required(false)
            .desc("pretty-print output with line breaks and indentations")
            .type(Boolean.class)
            .build());

        OPTIONS.addOption(Option.builder()
            .longOpt(OPT_AVRO_ENCODER_SYNTAX)
            .hasArg(false)
            .required(false)
            .desc("Use Avro Encoder syntax instead of simplified syntax (primarily affects how Unions will be represented)")
            .type(Boolean.class)
            .build());

        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(OPTIONS, args);
            return line;
        }
        catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
        return null;
    }

    private static void printHelp() {
        new HelpFormatter()
            .printHelp(COMMAND_NAME + " --schema <arg> [OPTIONS]",
                "when DATA is set, converts Avro data to XML, otherwise converts an Avro schema to an XML Schema",
                OPTIONS,
                "");
        return;
    }

    private static void convertSchema(CommandLine opts) {
        Map<String, String> writerOptions = Map.of(OutputKeys.INDENT, opts.hasOption(OPT_PRETTY_PRINT) ? "yes" : "no");
        try {
            XmlSchema xmlSchema = AvroXmlSchemaConverter.convert(opts.getOptionValue(OPT_SCHEMA),
                opts.hasOption(OPT_AVRO_ENCODER_SYNTAX));
            if (opts.hasOption(OPT_OUTPUT))
                xmlSchema.write(new FileOutputStream(opts.getOptionValue(OPT_OUTPUT)), writerOptions);
            else
                xmlSchema.write(System.out, writerOptions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void convertData(CommandLine opts) {
        try {
            String result = AvroXmlDataConverter.convert(opts.getOptionValue(OPT_SCHEMA),
                opts.getOptionValue(OPT_DATA),
                opts.hasOption(OPT_AVRO_ENCODER_SYNTAX),
                opts.hasOption(OPT_PRETTY_PRINT));
            if (opts.hasOption(OPT_OUTPUT)) {
                PrintStream out = new PrintStream(new FileOutputStream(opts.getOptionValue(OPT_OUTPUT)));
                out.print(result);
                out.close();
            } else {
                System.out.println(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
