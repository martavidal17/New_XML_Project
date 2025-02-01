package org.example.zoo.utils;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class XMLParser {
    private static final String XML_FILE = "src/main/resources/zoo.xml";

    public Document loadXML() {
        try {
            File file = new File(XML_FILE);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setIgnoringElementContentWhitespace(true); // Evitar espacios en blanco
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            removeEmptyTextNodes(doc); // 🔥 Limpia nodos vacíos antes de procesar
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean saveData(Document doc) {
        try {
            removeEmptyTextNodes(doc); // 🔥 Asegura que no hay espacios en blanco extra
            doc.normalizeDocument(); // 🔥 Normaliza el XML antes de guardarlo

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            // 🔥 Ajuste para una indentación limpia sin saltos de línea extra
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(XML_FILE));

            transformer.transform(source, result);
            System.out.println("📄 XML guardado correctamente sin espacios extra.");
            return true;
        } catch (TransformerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 🔥 Método para eliminar nodos de texto vacíos y evitar saltos de línea innecesarios
     */
    private void removeEmptyTextNodes(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = nodeList.getLength() - 1; i >= 0; i--) {
            Node child = nodeList.item(i);
            if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().trim().isEmpty()) {
                node.removeChild(child);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeEmptyTextNodes(child);
            }
        }
    }
}
