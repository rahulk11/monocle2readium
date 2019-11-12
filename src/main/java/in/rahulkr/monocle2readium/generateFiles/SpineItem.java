package in.rahulkr.monocle2readium.generateFiles;

import org.json.JSONException;
import org.json.JSONObject;

public class SpineItem {
    private String idRef;
    private String title;
    private String href;
    private String mediaType = "application/xhtml+xml";
    private String pageSpread;
    private String renditionLayout;
    private String renditionFlow;
    private String renditionOrientation;
    private String renditionSpread;
    private boolean linear = true;
    private String mediaOverlayId;
    private long chapterSize;
    private long sizeOfPrevChapters;

    public SpineItem(String path, String id, String title) {
        this.href = path;
        this.idRef = id;
        this.title = title;
    }

    public SpineItem(String path, String id) {
        this.href = path;
        this.idRef = id;
        if (NavigationItem.srcTitleMap != null)
            this.title = NavigationItem.srcTitleMap.get(href);
        else this.title = "";
    }

    public String getIdRef() {
        return idRef;
    }

    public String getHref() {
        return href;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setChapterSize(long size) {
        this.chapterSize = size;
    }

    public void setSizeOfPrevChapters(int size) {
        this.sizeOfPrevChapters = size;
    }

    public long getChapterSize() {
        return chapterSize;
    }

    public long getSizeOfPrevChapters() {
        return sizeOfPrevChapters;
    }

    public boolean isFixedLayout(BookData bookData) {
        return "pre-paginated".equals(bookData.getType());
    }

    public JSONObject toJSON() throws JSONException {
        return new JSONObject()
                .put("href", href)
                .put("media_type", mediaType)
                .put("page_spread", pageSpread)
                .put("idref", idRef)
                .put("rendition_layout", renditionLayout)
                .put("rendition_flow", renditionFlow)
                .put("rendition_orientation", renditionOrientation)
                .put("rendition_spread", renditionSpread)
                .put("linear", linear ? "yes" : "no")
                .put("media_overlay_id", mediaOverlayId);
    }
}
