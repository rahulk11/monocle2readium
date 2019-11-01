//package in.rahulk11.readium4j;
//
//import android.util.Log;
//
//import java.io.File;
//import java.io.InputStream;
//import java.util.HashMap;
//
//import org.dom4j.Document;
//import org.dom4j.DocumentException;
//import org.dom4j.Node;
//import org.dom4j.XPath;
//import org.dom4j.io.SAXReader;
//import org.dom4j.tree.DefaultAttribute;
//
//import javax.xml.namespace.NamespaceContext;
//import javax.xml.namespace.QName;
//import javax.xml.xpath.XPathConstants;
//
//public class XMLParser {
//
//    private Document xmlDocument = null;
//
//    /**
//     * @param filePath accepts the sdcard path of the file.
//     */
//    public XMLParser(String filePath) {
//        File file = new File(filePath);
//        SAXReader xmlReader = new SAXReader();
//        try {
//            xmlDocument = xmlReader.read(file);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * @param in accepts the inputstream from the webservice response.
//     */
//    public XMLParser(InputStream in) {
//        SAXReader xmlReader = new SAXReader();
//        try {
//            xmlDocument = xmlReader.read(in);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void clear() {
//        xmlDocument.clearContent();
//        xmlDocument = null;
//    }
//
//    /**
//     * @param expression xpath of the node u want value from
//     * @param returnType can be 1.XPathConstants.NODE to get a particular node
//     *                   2.XPathConstants.NODESET to get the set of nodes.
//     * @return
//     */
//    public Object parse(String expression, QName returnType) {
//        try {
//            XPath xpath = xmlDocument.createXPath(expression);
//            HashMap<String, String> hashMap = new HashMap<String, String>();
//            hashMap.put("ocf", "urn:oasis:names:tc:opendocument:xmlns:container");
//            xpath.setNamespaceURIs(hashMap);
//            if (returnType == XPathConstants.STRING) {
//                return (String) ((DefaultAttribute) xpath.selectObject(xmlDocument)).getData();
//            } else if (returnType == XPathConstants.NODE) {
//                return ((Node) xpath.selectNodes(xmlDocument).get(0));
//            } else if (returnType == XPathConstants.NODESET) {
//                return xpath.selectNodes(xmlDocument);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return null;
//        }
//        return null;
//    }
//
//    /**
//     * @param node       accepts node
//     * @param expression accepts xpath of the particular node
//     * @return
//     */
//    public String getValue(Node node, String expression) {
//        String value = "";
//        try {
//            return (String) node.selectObject(expression);
//
//        } catch (Exception e) {
//            Log.e("XMLParser", e.toString() + "Error");
//        }
//        return value;
//    }
//
//    /**
//     * @param node accepts node
//     * @return will convert a node into an XML.
//     */
//    public static String nodeToString(Node node) {
//        if (null == node) {
//            return null;
//        }
//        return node.asXML();
//    }
//}
