package com.company;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

class allBLogic implements java.util.concurrent.Callable, Runnable {


    private static long N;

    allBLogic(long N) {
        allBLogic.N = N;
        if (Thread.interrupted()) System.exit(1);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            // continue processing
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // good practice
                Thread.currentThread().interrupt();
                return;
            }
        }
        //stage with asking N an erase/fulfill table
        System.out.println("Number is = " + N);
        DB db = new DB();
        db.openConnection();
        db.fulFill(N);
        //stage with selecting in Field POJO and save to XML
        System.out.println("___stage with selecting in Filed POJO and save to XML");
        Field field = db.select();
        System.out.println(field.getField());
        db.closeConnection();
        saveXML(field.getField());
        //stage where 1.xml transform to 2.xml with XSLT
        System.out.println("___stage with transform 1.xml with XSLT pattern to 2.xml");
        createXSL();
        saveWithXSLT();
        //parsing 2.xml and sum
        System.out.println("___stage with parse/sum 2.xml");
        long sum = barSoomXML();
        System.out.println("Sum of 2.xml attributes = " + sum);
        System.exit(0);
    }

    @Override
    public Object call() throws Exception {
        return null;
    }

    /**
     * This method saving input data to HDD in location stored in outputXML
     * @param fields - input List of all values from table TEST
     */
    private static void saveXML(List<Long> fields) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            //entries elements
            Document doc = docBuilder.newDocument();
            Element entries = doc.createElement("entries");
            doc.appendChild(entries);
            for (Long field : fields) {
                //entries elements
                Element entry = doc.createElement("entry");
                entries.appendChild(entry);
                //entry elements
                Element f = doc.createElement("field");
                f.setTextContent(String.valueOf(field));
                entry.appendChild(f);
            }
            //write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "4");
            DOMSource source = new DOMSource(doc);
            StreamResult result =  new StreamResult(new File("1.xml"));
            transformer.transform(source, result);
            System.out.println("Done saving to 1.xml");
        } catch (ParserConfigurationException | TransformerException pce){
            System.out.println("Errors in Configuration/Transforming/Saving");
            pce.printStackTrace();
        }
    }

    /**
     Creating XSL file for pattern transforming.
     */
    private static void createXSL() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            //unreadable code, tabs for more visuals like xml.
            Element root = doc.createElement("xsl:stylesheet");
            root.setAttribute("xmlns:xsl", "http://www.w3.org/1999/XSL/Transform");
            root.setAttribute("version", "2.0");
            doc.appendChild(root);
            Element entries = doc.createElement("xsl:template");
            entries.setAttribute("match", "entries");
            root.appendChild(entries);
            Element entriesEl = doc.createElement("xsl:element");
            entriesEl.setAttribute("name", "entries");
            entries.appendChild(entriesEl);
            Element apply = doc.createElement("xsl:apply-templates");
            entriesEl.appendChild(apply);
            Element entriesEntry = doc.createElement("xsl:template");
            entriesEntry.setAttribute("match", "entries/entry");
            root.appendChild(entriesEntry);
            Element copy = doc.createElement("xsl:copy");
            entriesEntry.appendChild(copy);
            Element name = doc.createElement("xsl:attribute");
            name.setAttribute("name", "field");
            copy.appendChild(name);
            Element val = doc.createElement("xsl:value-of");
            val.setAttribute("select", "field");
            name.appendChild(val);
            //write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "4");
            DOMSource source = new DOMSource(doc);
            StreamResult result =  new StreamResult(new File("pattern.xsl"));
            transformer.transform(source, result);
            System.out.println("Done saving XSL to pattern.xsl");
        } catch (ParserConfigurationException | TransformerException pce){
            System.out.println("Errors in Configuration/Transforming/Saving");
            pce.printStackTrace();
        }
    }
    /**
     Transform 1.xml to 2.xml with pattern from pattern.xsl
     */
    private static void saveWithXSLT() {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(new File("pattern.xsl"));
        Transformer transformer;
        Source text = new StreamSource(new File("1.xml"));
        try {
            transformer = factory.newTransformer(xslt);
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "4");
            transformer.transform(text, new StreamResult(new File("2.xml")));
        } catch (TransformerException e) {
            System.out.println("Errors in Configuration/Transforming/Saving from 1.xml to 2.xml");
            e.printStackTrace();
        }
    }

    /**
     * parsing 2.xml and sum all values of field
     * @return sun in Long
     */
    private static long barSoomXML () {         //John Carter doing parse/sum
        long sum = 0;
        try {
            Reader fileReader = new FileReader("2.xml");
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(fileReader);
            while(xmlStreamReader.hasNext())
                if (xmlStreamReader.next() == XMLStreamConstants.START_ELEMENT)
                    for(int i = 0; i < xmlStreamReader.getAttributeCount(); i++)
                        sum += Long.valueOf(xmlStreamReader.getAttributeValue(i)); //won't using Field via unnecessary iterations
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while parsing 2.xml");
        }
        return sum;
    }
}
