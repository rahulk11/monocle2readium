package in.rahulkr.monocle2readium.generateFiles;

import org.json.JSONObject;

/**
 * Created by rk on 3/8/18.
 */

public class MetaData {
    String title, author, version, type, language;
    JSONObject jMetaData;
    static MetaData metaData = new MetaData();

    public static MetaData getInstance() {
        return metaData;
    }

    public void clearMetaData() {
        title = author = version = type = language = "";
        jMetaData = null;
    }

    public MetaData setTitle(String title) {
        metaData.title = title;
        return metaData;
    }

    public MetaData setAuthor(String author) {
        metaData.author = author;
        return metaData;
    }

    public MetaData setVersion(String version) {
        metaData.version = version;
        return metaData;
    }

    public MetaData setType(String type) {
        metaData.type = type;
        return metaData;
    }

    public MetaData setLanguage(String language) {
        metaData.language = language;
        return metaData;
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
        return metaData.type;
    }

    public String getLanguage() {
        return metaData.language;
    }
}
