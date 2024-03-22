package se.karolinska.kardapp.integrations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class AvroXmlDataConverterTests {

    String schema_simple = """
        {
            "type": "record",
            "name": "SimpleRecord",
            "namespace": "ns.test",
            "fields": [
              {
                "name": "value",
                "type": "string"
              }
            ]
          }
    """;

    String schema_simple_optional = """
        {
            "type": "record",
            "name": "SimpleOptionalRecord",
            "namespace": "ns.test",
            "fields": [
                {
                    "name": "optionalValue",
                    "type": [
                      "null",
                      "string"
                    ],
                    "default": null
                }
            ]
          }
    """;

    String schema_array_union_records = """
        {
            "type": "record",
            "name": "ArrayUnionRecordsRecord",
            "namespace": "ns.test",
            "fields": [
                {
                    "name": "optionalValue",
                    "type": [
                      "null",
                      "string"
                    ],
                    "default": null
                },
                {
                    "name": "recordField",
                    "type": [
                        "null",
                        {
                          "type": "record",
                          "name": "nestedRecordField",
                          "namespace": "ns.test.nested",
                          "fields": [
                            {
                              "name": "unionArray",
                              "type": [
                                "null",
                                {
                                  "type": "array",
                                  "items": [
                                    "null",
                                    {
                                      "type": "record",
                                      "name": "NestedRecord1",
                                      "namespace": "ns.test.nested.level2",
                                      "fields": [
                                        {
                                          "name": "aString",
                                          "type": [
                                            "null",
                                            "string"
                                          ],
                                          "default": null
                                        },
                                        {
                                            "name": "anInt",
                                            "type": "int"
                                        }
                                      ]
                                    },
                                    {
                                        "type": "record",
                                        "name": "NestedRecord2",
                                        "namespace": "ns.test.nested.level2",
                                        "fields": [
                                          {
                                            "name": "aLong",
                                            "type": [
                                              "null",
                                              "long"
                                            ],
                                            "default": null
                                          }
                                        ]
                                    }
                                  ]
              ]
          }
    """;


    String string1 = "a sample value";
    String string2 = "another value";

    Schema schema_record2Strings = SchemaBuilder.record("Record2Strings").namespace("ns.test").fields()
        .requiredString("value1")
        .optionalString("value2")
        .endRecord();

    @Test
    void simpleRecord() throws JsonMappingException, JsonProcessingException {
        GenericRecord record = new GenericData.Record(new Schema.Parser().parse(schema_simple));
        record.put("value", string1);

        String xml = AvroXmlDataConverter.convert(record, false);

        assertEquals("<SimpleRecord><value>" + string1 + "</value></SimpleRecord>", xml);
    }

    @Test
    void simpleRecordAsAvro() throws JsonMappingException, JsonProcessingException {
        GenericRecord record = new GenericData.Record(new Schema.Parser().parse(schema_simple));
        record.put("value", string1);

        String xml = AvroXmlDataConverter.convert(record, true);

        assertEquals("<SimpleRecord><value>" + string1 + "</value></SimpleRecord>", xml);
    }

    @Test
    void simpleWithOptional() throws JsonMappingException, JsonProcessingException {
        GenericRecord record = new GenericData.Record(new Schema.Parser().parse(schema_simple_optional));
        record.put("optionalValue", string2);

        String xml = AvroXmlDataConverter.convert(record, false);

        assertEquals("<SimpleOptionalRecord><optionalValue>" + string2 + "</optionalValue></SimpleOptionalRecord>", xml);
    }

    @Test
    void simpleWithOptionalAsAvro() throws JsonMappingException, JsonProcessingException {
        GenericRecord record = new GenericData.Record(new Schema.Parser().parse(schema_simple_optional));
        record.put("optionalValue", string2);

        String xml = AvroXmlDataConverter.convert(record, true);

        assertEquals("<SimpleOptionalRecord><optionalValue><string>" + string2 + "</string></optionalValue></SimpleOptionalRecord>", xml);
    }

    @Test
    void test() throws JsonMappingException, JsonProcessingException {
/*
        Schema unionRecordsSchema = SchemaBuilder.record("UnionRecords").namespace("ns.test.value").fields()
            .optionalString("stringValue").endRecord();

        Field unionRecordsField = new Field("records", Schema.create(Schema.Type.UNION));
        unionRecordsField.schema().getTypes().add(Schema.create(Schema.Type.NULL));
        unionRecordsField.schema().getTypes().add(schema_recordString);
        unionRecordsField.schema().getTypes().add(schema_record2Strings);
        unionRecordsSchema.getFields().add(unionRecordsField);

        GenericRecord recordString = new GenericData.Record(schema_recordString);
        recordString.put("value", string1);

        GenericRecord record2Strings = new GenericData.Record(schema_record2Strings);
        record2Strings.put("value1", string1);
        record2Strings.put("value2", string2);

        GenericRecord a1Record = new GenericData.Record(unionRecordsSchema);
        a1Record.put("stringValue", string2);
        a1Record.put("records", recordString);

        GenericRecord a2Record = new GenericData.Record(unionRecordsSchema);
        a2Record.put("records", record2Strings);

        GenericArray<GenericRecord> array = new GenericData.Array<>(2, unionRecordsSchema);
        array.add(a1Record);
        array.add(a2Record);


        Schema schema = SchemaBuilder.record("TestRecord").namespace("ns.test").fields()
            .name("arrayField").type().array().items(unionRecordsSchema).noDefault()
            .requiredString("value")
            .endRecord();

        GenericRecord record = new GenericData.Record(schema);
        record.put("arrayField", array);
        record.put("value", string1);

        String xml = AvroXmlDataConverter.convert(record, false);
        assertEquals("<test><value>a sample value</value></test>", xml);
        */
    }

}
