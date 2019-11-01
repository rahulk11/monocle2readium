package in.rahulkr.monocle2readium.generateFiles;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateBookData {
    String path;
    String ff05InstId;
    BookData bookData;
    int navPoint = 0;
    boolean shouldGenerateFiles = false;

    public GenerateBookData(String path, boolean isSample) {
        this.path = path;
        bookData = BookData.getInstance(path);
        bookData.clearBookData();
        String appxml;
        if (isSample) {
            appxml = path + File.separator + "sampleapp.xml";
        } else {
            appxml = path + File.separator + "app.xml";
        }
        ff05InstId = getFF05InstanceId(appxml);
        bookData.setFF05InstID(ff05InstId);
        parseBookData();
        if (!new File(path + "/META-INF/container.xml").exists()) {
            shouldGenerateFiles = true;
        }
    }

    private String getFF05InstanceId(String appxml) {
        File appXML = new File(appxml);
        String instId = null;
        if (appXML.exists()) {
            try {
                String fileData = readFileInString(appXML);
                instId = getMatch(fileData, "templateId=\\\"FF05\\\"(\\W|\\w)+ContentFile.*?(instanceId.*?])");
                if (instId != null) {
                    instId = instId.substring(
                            instId.indexOf("<property name=\"ContentFile\""));
                    instId = instId.substring(instId.indexOf("@instanceId"));
                    int first = instId.indexOf("'");
                    int last = instId.lastIndexOf("'");
                    instId = instId.substring(first + 1, last);
                }
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        return instId;
    }

    private void parseBookData() {
        File bookDataFile = new File(path + File.separator + "content/" + ff05InstId + "/bookData.js");
        if (bookDataFile.exists()) {
            String bookDataString = readFileInString(bookDataFile);

            String data = getMatch(bookDataString, "getContents:.*?(\\r\\n|\\r|\\n)return(.*?])(\\r\\n|\\r|\\n)\\}");
            data = data.substring(data.indexOf("return"));
            data = data.substring(data.indexOf("["), data.lastIndexOf("]") + 1);
            bookData.setContentsList(null, data);

            data = getMatch(bookDataString, "getComponents:.*?(\\r\\n|\\r|\\n)return(.*?];)(\\r\\n|\\r|\\n)\\}");
            data = data.substring(data.indexOf("return"));
            data = data.substring(data.indexOf("["), data.lastIndexOf("]") + 1);
            bookData.setComponentsList(data);

            data = getMatch(bookDataString, "getMetaData:(.*?(\\r\\n|\\r|\\n))+?\\}");
            data = data.substring(data.indexOf("return"));
            data = data.substring(data.indexOf("{"), data.lastIndexOf("}") + 1);
            bookData.setMetaData(data);
        }
    }


    public BookData generate() {
        if (shouldGenerateFiles) {
            createContentsXML();
            createTOC();
            createContainerXML();
        }
        return bookData;
    }

    public String getFf05InstId() {
        return ff05InstId;
    }

    private String readFileInString(File file) {
        String fileAsString = null;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader buf = new BufferedReader(new InputStreamReader(inputStream));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }
            buf.close();
            inputStream.close();
            fileAsString = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileAsString;
    }

    private String getMatch(String fileData, String regex) {
        String fullMatch = null;
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(fileData);
        while (matcher.find()) {
            fullMatch = matcher.group(0);
        }
        return fullMatch;
    }

    private void createContentsXML() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        xmlString += "\n<package prefix=\"rendition: http://www.idpf.org/vocab/rendition/#\""
                + " xmlns=\"http://www.idpf.org/2007/opf\" "
                + " unique-identifier=\"BookID\" "
                + " version=\"" + bookData.getVersion() + "\">";
        xmlString += "\n<metadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " +
                "xmlns:opf=\"http://www.idpf.org/2007/opf\">";
        xmlString += "\n<dc:title>" + bookData.getTitle() + "</dc:title>";
        xmlString += "\n<dc:creator opf:role=\"aut\">" + bookData.getAuthor() + "</dc:creator>";
        xmlString += "\n<dc:identifier id=\"BookID\" opf:scheme=\"UUID\"></dc:identifier>";
        xmlString += "\n<dc:language>" + bookData.getLanguage() + "</dc:language>";
        xmlString += "\n<meta property=\"dcterms:modified\" id=\"mod\">" + " " + "</meta>";
        xmlString += "\n<meta property=\"rendition:layout\">" + bookData.getType() + "</meta>";
        xmlString += "\n<meta property=\"rendition:spread\" >" + "auto" + "</meta>";
        xmlString += "\n<meta property=\"rendition:orientation\" >" + "auto" + "</meta>";
        xmlString += "\n</metadata>";
        String manifestItems = "\n<manifest>", spineItems = "\n<spine toc=\"ncx\">", guide = "";

        manifestItems += "\n<item id=\"ncx\" href=\""
                + "toc.ncx\" media-type=\"application/x-dtbncx+xml\"/>";
        int i = 0;
        for (SpineItem spineItem : bookData.getComponentsList()) {
            manifestItems += "\n<item id=\"" + spineItem.getIdRef() + "\" href=\"" + spineItem.getHref() + "\" media-type=\"application/xhtml+xml\"/>";

            spineItems += "\n<itemref idref=\"" + spineItem.getIdRef() + "\"/>";
            if (i == 0) {
                i++;
                guide += "\n<guide>\n" +
                        "<reference type=\"cover\" title=\"Cover\" href=\""
                        + spineItem.getHref() + "\"/>\n" +
                        "</guide>";
            }
        }
        spineItems += "\n</spine>";
        manifestItems += "\n</manifest>";

        xmlString += manifestItems + spineItems + guide + "\n</package>";
        createFile(path + "/content/content.opf", xmlString);
    }

    private void createTOC() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<!DOCTYPE ncx PUBLIC \"-//NISO//DTD ncx 2005-1//EN\""
                + "\n\"http://www.daisy.org/z3986/2005/ncx-2005-1.dtd\">"
                + "\n"
                + "\n<ncx xmlns=\"http://www.daisy.org/z3986/2005/ncx/\" version=\"2005-1\">"
                + "\n<head>"
                + "\n<meta name=\"dtb:uid\" content=\"\"/>"
                + "\n<meta name=\"dtb:depth\" content=\"1\"/>"
                + "\n<meta name=\"dtb:totalPageCount\" content=\"0\"/>"
                + "\n<meta name=\"dtb:maxPageNumber\" content=\"0\"/>"
                + "\n</head>";
        xmlString += "\n<docTitle>" +
                "\n<text>Out of Bounds Out of Control</text>" +
                "\n</docTitle>";
        xmlString += "\n<navMap>";
        for (int i = 0; i < bookData.getContentsList().size(); i++) {
            NavigationItem navigationItem = bookData.getContentsData(i);
            xmlString += genNavPoints(navigationItem);
        }
        xmlString += "\n</navMap>"
                + "\n</ncx>";
        createFile(path + "/content/toc.ncx", xmlString);
    }

    private String genNavPoints(NavigationItem navigationItem) {
        ++navPoint;
        String xmlString = "\n<navPoint id=\"navPoint-" + navPoint + "\" playOrder=\"" + navPoint + "\">"
                + "\n<navLabel>"
                + "\n<text>" + navigationItem.getTitle() + "</text>"
                + "\n</navLabel>"
                + "\n<content src=\"" + navigationItem.getSrc() + "\"/>";

        for (NavigationItem child : navigationItem.getChildren()) {
            xmlString += genNavPoints(child);
        }
        xmlString += "\n</navPoint>";
        return xmlString;
    }

    private void createContainerXML() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        xmlString += "\n<container version=\"1.0\" xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\">" +
                "\n<rootfiles>" +
                "\n<rootfile full-path=\"content/content.opf\" media-type=\"application/oebps-package+xml\"/>" +
                "\n</rootfiles>" +
                "\n</container>";

        createFile(path + "/META-INF/container.xml", xmlString);
    }

    private void createFile(String filePath, String xmlString) {
        final File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        } else {
            file.getParentFile().mkdirs();
        }
        try {
            if (file.createNewFile()) {
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(xmlString);

                myOutWriter.close();

                fOut.flush();
                fOut.close();
                Log.d("BookData", "file created at: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
