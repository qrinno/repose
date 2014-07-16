package com.rackspace.papi.filter

import groovy.xml.StreamingMarkupBuilder
import org.xml.sax.SAXParseException
import spock.lang.Specification
import spock.lang.Unroll

import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator

/**
 * This class tests the various asserts within the system model.
 */
class SystemModelConfigTest extends Specification {
    static Schema schema
    def Validator validator
    def StreamingMarkupBuilder xmlBuilder
    def Exception caught

    //@BeforeClass      // JUnit 4
    def setupSpec() {   // Spock (Groovy)
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.1")
        factory.setFeature("http://apache.org/xml/features/validation/cta-full-xpath-checking", true)
        schema = factory.newSchema(new StreamSource(SystemModelConfigTest.class.getResourceAsStream("/META-INF/schema/system-model/system-model.xsd")))
    }

    //@Before                                   // JUnit 4
    //public void setUp() throws Exception {    // JUnit 3
    def setup() {                               // Spock (Groovy)
        validator = schema.newValidator()
        xmlBuilder = new StreamingMarkupBuilder()
        xmlBuilder.encoding = 'UTF-8'
        caught = null
    }

    //@Test                                                             // JUnit 4
    //public void shouldValidateExampleConfigTest() throws Exception {  // JUnit 3
    def "Validate Example Config"() {                                   // Spock (Groovy)
        given:
        final StreamSource sampleSource = new StreamSource(SystemModelConfigTest.class.getResourceAsStream("/META-INF/schema/examples/system-model.cfg.xml"))

        when:
        try {
            validator.validate(sampleSource)
        } catch (Exception e) {
            caught = e
        }

        then:
        caught == null
    }

    private static void appendHeader(StringBuffer xmlBuffer) {
        xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuffer.append("<system-model xmlns=\"http://docs.rackspacecloud.com/repose/system-model/v2.0\">\n");
        xmlBuffer.append("    <repose-cluster id=\"repose\">\n");
        xmlBuffer.append("        <nodes>\n");
        xmlBuffer.append("            <node id=\"node1\" hostname=\"localhost\" http-port=\"8000\"/>\n");
        xmlBuffer.append("        </nodes>\n");
        xmlBuffer.append("        <services>\n");
        xmlBuffer.append("            <service name=\"dist-datastore\"/>\n");
        xmlBuffer.append("        </services>\n");
        xmlBuffer.append("        <destinations>\n");
    }

    private static void appendFooter(StringBuffer xmlBuffer) {
        xmlBuffer.append("        </destinations>\n");
        xmlBuffer.append("    </repose-cluster>\n");
        xmlBuffer.append("</system-model>\n");
    }

    //@Test                                                                                         // JUnit 4
    //public void shouldValidateWhenOnlyOneDestinationWithDefaultTest() throws Exception {          // JUnit 3
    @Unroll("Validate System-Model with only one Endpoint Destination that has default=#default1.")
    // Spock (Groovy)
    def "Validate System-Model with only one Endpoint Destination."() {                             // Spock (Groovy)
        given:
        def xml = xmlBuilder.bind {
            mkp.xmlDeclaration()
            "system-model"(xmlns: 'http://docs.rackspacecloud.com/repose/system-model/v2.0') {
                "repose-cluster"(id: 'repose') {
                    nodes() {
                        node(id: 'node1', hostname: 'localhost', 'http-port': '8000')
                    }
                    services() {
                        service(name: 'dist-datastore')
                    }
                    destinations() {
                        if (default1 == null) {
                            endpoint(id: 'openrepose1', protocol: 'http', hostname: '192.168.1.1', 'root-path': '/', port: '8080')
                        } else {
                            endpoint(id: 'openrepose1', protocol: 'http', hostname: '192.168.1.1', 'root-path': '/', port: '8080', default: default1)
                        }
                    }
                }
            }
        }
        StringBuffer xmlBuffer = new StringBuffer();
        appendHeader(xmlBuffer);
        xmlBuffer.append("            <endpoint id=\"openrepose1\" protocol=\"http\" hostname=\"192.168.1.1\" root-path=\"/\" port=\"8080\"");
        if (default1 != null) {
            xmlBuffer.append(" default=\"");
            xmlBuffer.append(default1);
            xmlBuffer.append("\"");
        }
        xmlBuffer.append("/>\n");
        appendFooter(xmlBuffer);


        when:
        try {
            //validator.validate(new StreamSource(xml.toString()))
            validator.validate(new StreamSource(new StringReader(xmlBuffer.toString())));
        } catch (Exception e) {
            caught = e
        }

        then:
        caught == null

        where:
        default1 | pass
        null     | true
        'true'   | true
        'false'  | true
    }

    private Object createXml(default1, default2, default3) {
        return xmlBuilder.bind {
            mkp.xmlDeclaration()
            "system-model"(xmlns: 'http://docs.rackspacecloud.com/repose/system-model/v2.0') {
                "repose-cluster"(id: 'repose') {
                    nodes() {
                        node(id: 'node1', hostname: 'localhost', 'http-port': '8000')
                    }
                    services() {
                        service(name: 'dist-datastore')
                    }
                    destinations() {
                        if (default1 == null) {
                            endpoint(id: 'openrepose1', protocol: 'http', hostname: '192.168.1.1', 'root-path': '/', port: '8080')
                        } else {
                            endpoint(id: 'openrepose1', protocol: 'http', hostname: '192.168.1.1', 'root-path': '/', port: '8080', default: default1)
                        }
                        if (default2 == null) {
                            endpoint(id: 'openrepose2', protocol: 'http', hostname: '192.168.1.2', 'root-path': '/', port: '8080')
                        } else {
                            endpoint(id: 'openrepose2', protocol: 'http', hostname: '192.168.1.2', 'root-path': '/', port: '8080', default: default2)
                        }
                        if (default3 == null) {
                            endpoint(id: 'openrepose3', protocol: 'http', hostname: '192.168.1.3', 'root-path': '/', port: '8080')
                        } else {
                            endpoint(id: 'openrepose3', protocol: 'http', hostname: '192.168.1.3', 'root-path': '/', port: '8080', default: default3)
                        }
                    }
                }
            }
        }
    }

    private static StringBuffer appendXml(default1, default2, default3) {
        StringBuffer xmlBuffer = new StringBuffer();
        appendHeader(xmlBuffer);
        xmlBuffer.append("            <endpoint id=\"openrepose1\" protocol=\"http\" hostname=\"192.168.1.1\" root-path=\"/\" port=\"8080\"");
        if (default1 != null) {
            xmlBuffer.append(" default=\"");
            xmlBuffer.append(default1);
            xmlBuffer.append("\"");
        }
        xmlBuffer.append("/>\n");

        xmlBuffer.append("            <endpoint id=\"openrepose2\" protocol=\"http\" hostname=\"192.168.1.2\" root-path=\"/\" port=\"8080\"");
        if (default2 != null) {
            xmlBuffer.append(" default=\"");
            xmlBuffer.append(default2);
            xmlBuffer.append("\"");
        }
        xmlBuffer.append("/>\n");

        xmlBuffer.append("            <endpoint id=\"openrepose3\" protocol=\"http\" hostname=\"192.168.1.3\" root-path=\"/\" port=\"8080\"");
        if (default3 != null) {
            xmlBuffer.append(" default=\"");
            xmlBuffer.append(default3);
            xmlBuffer.append("\"");
        }
        xmlBuffer.append("/>\n");
        appendFooter(xmlBuffer);
        return xmlBuffer;
    }

    //@Test                                                                                                                         // JUnit 4
    //public void shouldNotValidateWhenNotOneAndOnlyOneDefaultDestinationTest() throws Exception {                                  // JUnit 3
    @Unroll("InValidate System-Model with Endpoint Destinations default1=#default1, default2=#default2, and default3=#default3.")   // Spock (Groovy)
    def "InValidate System-Model with three Endpoint Destinations."() {                                                             // Spock (Groovy)
        given:
        def xml = createXml(default1, default2, default3)
        StringBuffer xmlBuffer = appendXml(default1, default2, default3);

        when:
        try {
            //validator.validate(new StreamSource(xml.toString()))
            validator.validate(new StreamSource(new StringReader(xmlBuffer.toString())));
        } catch (Exception e) {
            caught = e
        }

        then:
        //if (pass) {
        //    caught == null
        //} else {
            caught.getClass() == SAXParseException.class
            //caught.getLocalizedMessage().contains(errorMessage)
            caught.getLocalizedMessage().contains('There should only be one default destination')
        //}

        where:
        default1 | default2 | default3 //| pass  | errorMessage
        null     | null     | null     //| false | 'There should only be one default destination'
        //null     | null     | 'true'   //| true  | null
        null     | null     | 'false'  //| false | 'There should only be one default destination'
        //null     | 'true'   | null     //| true  | null
        null     | 'true'   | 'true'   //| false | 'There should only be one default destination'
        //null     | 'true'   | 'false'  //| true  | null
        null     | 'false'  | null     //| false | 'There should only be one default destination'
        //null     | 'false'  | 'true'   //| true  | null
        null     | 'false'  | 'false'  //| false | 'There should only be one default destination'
        //'true'   | null     | null     //| true  | null
        'true'   | null     | 'true'   //| false | 'There should only be one default destination'
        //'true'   | null     | 'false'  //| true  | null
        'true'   | 'true'   | null     //| false | 'There should only be one default destination'
        'true'   | 'true'   | 'true'   //| false | 'There should only be one default destination'
        'true'   | 'true'   | 'false'  //| false | 'There should only be one default destination'
        //'true'   | 'false'  | null     //| true  | null
        'true'   | 'false'  | 'true'   //| false | 'There should only be one default destination'
        //'true'   | 'false'  | 'false'  //| true  | null
        'false'  | null     | null     //| false | 'There should only be one default destination'
        //'false'  | null     | 'true'   //| true  | null
        'false'  | null     | 'false'  //| false | 'There should only be one default destination'
        //'false'  | 'true'   | null     //| true  | null
        'false'  | 'true'   | 'true'   //| false | 'There should only be one default destination'
        //'false'  | 'true'   | 'false'  //| true  | null
        'false'  | 'false'  | null     //| false | 'There should only be one default destination'
        //'false'  | 'false'  | 'true'   //| true  | null
    }

    //@Test                                                                                                                     // JUnit 4
    //public void shouldValidateWhenOneAndOnlyOneDefaultDestinationTest() throws Exception {                                    // JUnit 3
    @Unroll("Validate System-Model with Endpoint Destinations default1=#default1, default2=#default2, and default3=#default3.") // Spock (Groovy)
    def "Validate System-Model with three Endpoint Destinations."() {                                                           // Spock (Groovy)
        given:
        def xml = createXml(default1, default2, default3)
        StringBuffer xmlBuffer = appendXml(default1, default2, default3);

        when:
        try {
            //validator.validate(new StreamSource(xml.toString()))
            validator.validate(new StreamSource(new StringReader(xmlBuffer.toString())));
        } catch (Exception e) {
            caught = e
        }

        then:
        //if (pass) {
            caught == null
        //} else {
        //    caught.getClass() == SAXParseException.class
        //    caught.getLocalizedMessage().contains(errorMessage)
        //}

        where:
        default1 | default2 | default3 //| pass  | errorMessage
        //null     | null     | null     //| false | 'There should only be one default destination'
        null     | null     | 'true'   //| true  | null
        //null     | null     | 'false'  //| false | 'There should only be one default destination'
        null     | 'true'   | null     //| true  | null
        //null     | 'true'   | 'true'   //| false | 'There should only be one default destination'
        null     | 'true'   | 'false'  //| true  | null
        //null     | 'false'  | null     //| false | 'There should only be one default destination'
        null     | 'false'  | 'true'   //| true  | null
        //null     | 'false'  | 'false'  //| false | 'There should only be one default destination'
        'true'   | null     | null     //| true  | null
        //'true'   | null     | 'true'   //| false | 'There should only be one default destination'
        'true'   | null     | 'false'  //| true  | null
        //'true'   | 'true'   | null     //| false | 'There should only be one default destination'
        //'true'   | 'true'   | 'true'   //| false | 'There should only be one default destination'
        //'true'   | 'true'   | 'false'  //| false | 'There should only be one default destination'
        'true'   | 'false'  | null     //| true  | null
        //'true'   | 'false'  | 'true'   //| false | 'There should only be one default destination'
        'true'   | 'false'  | 'false'  //| true  | null
        //'false'  | null     | null     //| false | 'There should only be one default destination'
        'false'  | null     | 'true'   //| true  | null
        //'false'  | null     | 'false'  //| false | 'There should only be one default destination'
        'false'  | 'true'   | null     //| true  | null
        //'false'  | 'true'   | 'true'   //| false | 'There should only be one default destination'
        'false'  | 'true'   | 'false'  //| true  | null
        //'false'  | 'false'  | null     //| false | 'There should only be one default destination'
        'false'  | 'false'  | 'true'   //| true  | null
        //'false'  | 'false'  | 'true'   //| false | 'There should only be one default destination'
    }

    ////@After                  // JUnit 4
    ////public void tearDown() {// JUnit 3
    //def cleanup() {           // Spock (Groovy)
    //}

    ////@AfterClass             // JUnit 4
    //def cleanupSpec() {       // Spock (Groovy)
    //}
}
