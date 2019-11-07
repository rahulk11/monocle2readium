package in.rahulkr.monocle2readium;

import in.rahulkr.monocle2readium.generateFiles.BookData;
import in.rahulkr.monocle2readium.generateFiles.GenerateBookData;

public class Container {
    //    private final String content = "/content/";
//    private final String containerPath = "META-INF/container.xml";
    private String epubPath;
    private static final Container container = new Container();
    private BookData bookData;

    private Container() {

    }

    public static Container getInstance() {
        return container;
    }

    public void openBook(String path) {
        container.epubPath = path;
//        parseContainer();
        GenerateBookData generateBookData = new GenerateBookData(path, false);
        bookData = generateBookData.generate();
    }

    public String getEpubPath() {
        return epubPath;
    }

    public BookData getBookData() {
        return bookData;
    }

//    private void parseContainer() {
//        String path = epubPath + File.separator + containerPath;
//        if (epubPath.endsWith("/")) {
//            path = epubPath + containerPath;
//        }
//        XMLParser parser = new XMLParser(path);
//        String contentPath = (String) parser.parse("/ocf:container/ocf:rootfiles/ocf:rootfile/@full-path", XPathConstants.STRING);
//        Log.d("Container", "contentPath: " + contentPath);
//    }
}
