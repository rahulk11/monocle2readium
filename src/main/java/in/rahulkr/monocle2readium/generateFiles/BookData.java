package in.rahulkr.monocle2readium.generateFiles;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by rk on 3/21/18.
 */

public class BookData {
    private ArrayList<NavigationItem> contentsList = new ArrayList<>();
    private MetaData metaData = MetaData.getInstance();
    private ArrayList<SpineItem> componentsList = new ArrayList<>();
    private String ff05InstId;
    private static BookData bookData = new BookData();
    final String basepath = "content/";
    static String epubDirPath = "";
    private int totalSpineSize = 0;
    private String _rootUrl = "/";
    private String _rootUrlMO = "http://127.0.0.1:8080/";
    private String rendition_layout;
    private String rendition_flow = "";
    private String rendition_orientation = "auto";
    private String rendition_spread = "auto";
    private String pageProgressionDirection = "";
    private String smilDataJson = "{\n" +
            "\"activeClass\": \"\",\n" +
            "\"duration\": 0,\n" +
            "\"narrator\": \"\",\n" +
            "\"playbackActiveClass\": \"\",\n" +
            "\"escapables\" : [\n" +
            "\"sidebar\",\n" +
            "\"bibliography\",\n" +
            "\"toc\",\n" +
            "\"loi\",\n" +
            "\"appendix\",\n" +
            "\"landmarks\",\n" +
            "\"lot\",\n" +
            "\"index\",\n" +
            "\"colophon\",\n" +
            "\"epigraph\",\n" +
            "\"conclusion\",\n" +
            "\"afterword\",\n" +
            "\"warning\",\n" +
            "\"epilogue\",\n" +
            "\"foreword\",\n" +
            "\"introduction\",\n" +
            "\"prologue\",\n" +
            "\"preface\",\n" +
            "\"preamble\",\n" +
            "\"notice\",\n" +
            "\"errata\",\n" +
            "\"copyright-page\",\n" +
            "\"acknowledgments\",\n" +
            "\"other-credits\",\n" +
            "\"titlepage\",\n" +
            "\"imprimatur\",\n" +
            "\"contributors\",\n" +
            "\"halftitlepage\",\n" +
            "\"dedication\",\n" +
            "\"help\",\n" +
            "\"annotation\",\n" +
            "\"marginalia\",\n" +
            "\"practice\",\n" +
            "\"note\",\n" +
            "\"footnote\",\n" +
            "\"rearnote\",\n" +
            "\"footnotes\",\n" +
            "\"rearnotes\",\n" +
            "\"bridgehead\",\n" +
            "\"page-list\",\n" +
            "\"table\",\n" +
            "\"table-row\",\n" +
            "\"table-cell\",\n" +
            "\"list\",\n" +
            "\"list-item\",\n" +
            "\"glossary\"\n" +
            "],\n" +
            "\"skippables\" : [\n" +
            "\"sidebar\",\n" +
            "\"practice\",\n" +
            "\"marginalia\",\n" +
            "\"annotation\",\n" +
            "\"help\",\n" +
            "\"note\",\n" +
            "\"footnote\",\n" +
            "\"rearnote\",\n" +
            "\"table\",\n" +
            "\"table-row\",\n" +
            "\"table-cell\",\n" +
            "\"list\",\n" +
            "\"list-item\",\n" +
            "\"pagebreak\"\n" +
            "],\n" +
            "\"smil_models\" : [\n" +
            "]}";


    private BookData() {
    }

    public String getBasepath() {
        return basepath;
    }

    public static BookData getInstance(String epubPath) {
        if (!epubPath.equalsIgnoreCase(epubDirPath)) {
            bookData = new BookData();
            epubDirPath = epubPath;
            bookData.clearBookData();
        }
        return bookData;
    }

    public void clearBookData() {
        contentsList.clear();
        componentsList.clear();
        metaData.clearMetaData();
    }

    public void setFF05InstID(String ff05InstId) {
        this.ff05InstId = ff05InstId;
    }

    public void setContentsList(NavigationItem parent, String contents) {
        try {
            JSONArray contentsArray = new JSONArray(contents);
            for (int i = 0; i < contentsArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) contentsArray.get(i);
                Log.d("MainActivity", "JSONObject: " + jsonObject);
                String children = jsonObject.getString("children");
                String title = jsonObject.getString("title");
                String src = jsonObject.getString("src");
                src = ff05InstId + "/" + src;
                NavigationItem contentItem = new NavigationItem(title, src);
                contentItem.setJSONObj(jsonObject);
                if (children != null && !children.equalsIgnoreCase("null")) {
                    setContentsList(contentItem, children);
                }
                if (parent == null)
                    addContentsData(contentItem);
                else
                    parent.addChild(contentItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<NavigationItem> getContentsList() {
        return this.contentsList;
    }

    public void setComponentsList(String components) {
        totalSpineSize = 0;
        this.componentsList.clear();
        try {
            JSONArray componentsArray = new JSONArray(components);
            for (int i = 0; i < componentsArray.length(); i++) {
                String component = componentsArray.getString(i);
                String[] split = component.split("/");
                String component_id = split[(split.length - 1)];
                component = ff05InstId + "/" + component;
                SpineItem spineItem = new SpineItem(component, component_id);
                long size = getFileSize(component);
                spineItem.setChapterSize(size);
                spineItem.setSizeOfPrevChapters(totalSpineSize);
                totalSpineSize += size;
                componentsList.add(spineItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getComponentsCount() {
        return this.componentsList.size();
    }

    /**
     * @param index index at the componentList
     * @return ComponentData - component at index
     */
    public SpineItem getComponentAt(int index) {
        if (componentsList.size() > index)
            return componentsList.get(index);
        else return null;
    }

    public SpineItem getComponentByHref(String uri) {
        for (SpineItem spineItem : componentsList) {
            if (spineItem.getHref().equalsIgnoreCase(uri))
                return spineItem;
        }
        return null;
    }

    public SpineItem getComponentByIdref(String idref) {
        for (SpineItem spineItem : componentsList) {
            if (spineItem.getIdRef().equalsIgnoreCase(idref))
                return spineItem;
        }
        return null;
    }

    public ArrayList<SpineItem> getComponentsList() {
        return componentsList;
    }

    public void setRootUrls(String rootUrl, String rootUrlMO) {
        _rootUrl = rootUrl;
        _rootUrlMO = rootUrlMO;
    }

    public void setMetaData(String metad) {
        try {
            JSONObject metaObject = new JSONObject(metad);
            metaData.setTitle(metaObject.getString("title"))
                    .setAuthor(metaObject.getString("creator"))
                    .setVersion(metaObject.getString("version"))
                    .setType(metaObject.getString("type"))
                    .setLanguage(metaObject.getString("language"));
            metaData.jMetaData = metaObject;
            if (metaData.type.equalsIgnoreCase("FIXED"))
                rendition_layout = "pre-paginated";
            else
                rendition_layout = "reflowable";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public String getTitle() {
        return metaData.title;
    }

    public String getAuthor() {
        return metaData.author;
    }

    public String getVersion() {
        return metaData.version;
    }

    public String getType() {
        return rendition_layout;
    }

    public String getLanguage() {
        return metaData.language;
    }

    public NavigationItem getContentsData(int index) {
        return contentsList.get(index);
    }

    public void addContentsData(NavigationItem navigationItem) {
        contentsList.add(navigationItem);
    }

    public void calculateChapterSize() {

    }

    public int getTotalSpineSize() {
        return totalSpineSize;
    }

    public long getFileSize(String relPath) {
        return new File(epubDirPath + File.separator + basepath + relPath).length();
    }

    public ByteArrayInputStream getInputStream(String relPath) {
        return new ByteArrayInputStream(readDataAll(relPath));
    }

    public byte[] readDataAll(String relPath) {
        File file = new File(epubDirPath + File.separator + basepath + relPath);
        if (file.exists()) {
            int size = (int) file.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return bytes;
        }
        return null;
    }

    public JSONObject toJSON() {
        JSONObject o = new JSONObject();
        try {
            o.put("rootUrl", _rootUrl);
            if (_rootUrlMO != null) {
                o.put("rootUrlMO", _rootUrlMO);
            }

            o.put("rendition_layout", rendition_layout);
            o.put("rendition_flow", rendition_flow);
            o.put("rendition_orientation", rendition_orientation);
            o.put("rendition_spread", rendition_spread);
            JSONArray spineArray = new JSONArray();
            for (SpineItem item : componentsList) {
                spineArray.put(item.toJSON());
            }
            JSONObject spine = new JSONObject();
            spine.put("items", spineArray);
            spine.put("direction", pageProgressionDirection);
            o.put("spine", spine);

            JSONObject mo = new JSONObject(smilDataJson);
            o.put("media_overlay", mo);

//			Log.i(TAG, "JSON: " + o.toString(2));
        } catch (JSONException e) {
            Log.e("BookData", "" + e.getMessage(), e);
        }
        return o;
    }
}

