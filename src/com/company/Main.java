package com.company;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

public class Main {

    private static final String outputXML = "D:/1.xml";         //outputXML

    public static void main(String[] args) {
        //stage with asking N an erase/fulfill table
        System.out.println("___stage with asking N an erase/fulfill table");
        /*Scanner in = new Scanner(System.in);
        int a;
        System.out.println("Enter N:");f
        a = in.nextInt();
        System.out.println("Number is = " + a);*/
        int N = 15;
        DB db = new DB();
        db.openConnection();
        db.fulFill(N);
        //stage with selecting in Filed bean and save to XML
        System.out.println("___stage with selecting in Filed bean and save to XML");
        Field field = db.select();
        System.out.println(field.getField());
        db.closeConnection();
        saveXML(field.getField());
        //stage where 1.xml transform to 2.xml with XSLT
        System.out.println("___stage 2");
        saveWithXSLT();
    }

    /**
     * This method saving input data to HDD in location stored in outputXML
     * @param fields - input List of all values from table TEST
     */
    private static void saveXML(List<Long> fields) {
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            //entries elements
            Document doc = docBuilder.newDocument();
            Element entries = doc.createElement("entries");
            doc.appendChild(entries);
            //entries elements
            for (Long field : fields) {
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
            StreamResult result =  new StreamResult(new File("D:/1.xml"));
            transformer.transform(source, result);
            System.out.println("Done saving to " + outputXML);
        } catch (ParserConfigurationException | TransformerException pce){
            System.out.println("Errors in Configuration/Transforming/Saving");
            pce.printStackTrace();
        }
    }

    private static void saveWithXSLT() {

    }
}
