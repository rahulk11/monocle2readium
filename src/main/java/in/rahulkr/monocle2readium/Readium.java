package in.rahulkr.monocle2readium;

public class Readium {
    private String epubPath;

    public Readium(String path) {
        this.epubPath = path;
    }

    public Container openBook() {
        Container container = Container.getInstance();
        container.openBook(epubPath);
        return container;
    }
}
