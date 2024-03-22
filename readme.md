# Avro XML Converter (Schema and Data)

Java library and command line utility to convert Avro to XML; supports conversion of both schema (.avsc to .xsd) and data files (JSON-formatted Avro to .xml).

## Command Line Usage

Package the JAR:

```sh
mvn clean package
```

Run the converted using the `java` CLI to convert a schema and print the result to standard out:

```sh
java -jar target/avro-xml-converter-1.0-SNAPSHOT-jar-with-dependencies.jar --schema src/main/resources/AdministrationRCC.avsc --pretty-print
```

### Options

Output from `java -jar target/avro-xml-converter-1.0-SNAPSHOT-jar-with-dependencies.jar --help`:

```sh
usage: avro-xml-converter --schema <arg> [OPTIONS]
when DATA is set, converts Avro data to XML, otherwise converts an Avro
schema to an XML Schema
    --avro-encoder-syntax   Use Avro Encoder syntax instead of simplified
                            syntax (primarily affects how Unions will be
                            represented)
 -d,--data <arg>            Avro Data file to be converted
 -h,--help                  print this help message
 -o,--output <arg>          write converted XML data (when DATA is set) or
                            schema (when DATA is not set) to file instead
                            of stdout
    --pretty-print          pretty-print output with line breaks and
                            indentations
 -s,--schema <arg>          Avro Schema file (.avsc) to be converted, or
                            is needed for use with the data file
```

## Simplified Syntax vs Avro Encoder Syntax

This utility defaults to the "simplified" structure, but can be controlled by the Java property `useAvroEncoderSyntax` (represented by the command line option `--avro-encoder-syntax`).

Background:

The Avro format is in fact represented by JSON, and as such it is relatively common to just convert from the structure represented by JSON to the target format. When using the official `org.apache.avro.*` Java objects, there are typically two possibilities when converting data (marshalling) from a Avro object to this JSON format:

1. Convert the Avro data object to a String (for example by using the `.toString()` method) where you will receive a JSON-formatted string, or
2. Use Avro's DataWriter with one of the available Avro decoders (`JsonDecoder`, `BinaryDecoder`, etc)

What is interesting in the simple #1 case is that the `toString` method for Avro data objects has been overwritten and gives a slightly different and more "simplified" result structure than what is given in the Avro specification. Basically, that you get more of a typical/"friendly" JSON Structure representing the data. See [GenericData.java#L684](https://github.com/apache/avro/blob/08d0d14855382d9e388cf51d4ae3c6241d897fe0/lang/java/avro/src/main/java/org/apache/avro/generic/GenericData.java#L684) as an example if you really want to dive into the details on this.

The primary differences that the author of this utility has found when using the Avro decoders as compared to the simplified JSON structure are as follows:

- Avro Union type values are wrapped in an additional element with their type name (e.g. `"aUnionedStringValue": {"string": "value"}` instead of `"aUnionedStringValue": "value"`),
- naming of Record types seem to have their fully qualified name (`"namespace.name"` instead of `"name"`),
- some additional intermediary levels are included with Arrays
