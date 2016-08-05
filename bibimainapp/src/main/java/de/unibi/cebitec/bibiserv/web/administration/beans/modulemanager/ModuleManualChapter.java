package de.unibi.cebitec.bibiserv.web.administration.beans.modulemanager;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "chapter")
public class ModuleManualChapter {

    @XmlElement(required = true)
    private String title;
    @XmlElement(required = true)
    private String content;

    public ModuleManualChapter() {
    }

    public ModuleManualChapter(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }
}
