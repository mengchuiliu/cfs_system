package entity;

import java.util.List;

/**
 * Created by Administrator on 2017/5/23.
 */

public class PageModel {
    private String name;
    private List<PageContent> list;

    public static class PageContent {
        private String type;
        private String content;
        private int icon;
        private String contentName;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getContentName() {
            return contentName;
        }

        public void setContentName(String contentName) {
            this.contentName = contentName;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        @Override
        public String toString() {
            return "PageContent{" +
                    "type='" + type + '\'' +
                    ", content='" + content + '\'' +
                    ", contentName='" + contentName + '\'' +
                    ", icon=" + icon +
                    '}';
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PageContent> getList() {
        return list;
    }

    public void setList(List<PageContent> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageModel{" +
                "name='" + name + '\'' +
                ", list=" + list +
                '}';
    }
}
