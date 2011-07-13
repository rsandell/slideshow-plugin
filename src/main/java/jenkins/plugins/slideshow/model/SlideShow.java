/*
 * The MIT License
 *
 * Copyright 2011 Robert Sandell - sandell.robert@gmail.com. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package jenkins.plugins.slideshow.model;

import hudson.model.Hudson;
import jenkins.plugins.slideshow.PluginImpl;
import jenkins.plugins.slideshow.SlideShows;
import net.sf.json.JSONObject;
import org.jfree.data.time.Millisecond;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import javax.management.Descriptor;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created: 7/10/11 5:29 PM
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public class SlideShow implements Serializable {

    /**
     * The default number of seconds that a page is shown until the next one is requested.
     */
    public static final int DEFAULT_PAGE_TIME = 20;

    private String name;
    private int defaultPageTime;
    private List<Page> pages;

    public SlideShow(String name, int defaultPageTime, List<Page> pages) {
        this.name = name;
        this.defaultPageTime = defaultPageTime;
        this.pages = pages;
    }

    public SlideShow(String name, List<Page> pages) {
        this.name = name;
        this.pages = pages;
        this.defaultPageTime = DEFAULT_PAGE_TIME;
    }

    public SlideShow(String name, int defaultPageTime) {
        this.name = name;
        this.defaultPageTime = defaultPageTime;
        this.pages = new LinkedList<Page>();
    }

    /**
     * Default constructor.
     * <strong>Do not use unless you are a serializer!</strong>
     */
    public SlideShow() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDefaultPageTime() {
        return defaultPageTime;
    }

    public void setDefaultPageTime(int defaultPageTime) {
        this.defaultPageTime = defaultPageTime;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
        for(Page p : pages) {
            p.setParent(this);
        }
    }

    public void addPage(Page page) {
        if(this.pages == null) {
            this.pages = new LinkedList<Page>();
        }
        pages.add(page);
        page.setParent(this);
    }

    public Page getPage(int index) {
        return pages.get(index);
    }

    public int getNextIndex(int currentIndex) {
        if (pages == null || pages.size() <= 0) {
            return -1;
        } else if (currentIndex < 0 || currentIndex >= pages.size() - 1) {
            return 0;
        } else {
            return currentIndex + 1;
        }
    }

    @JavaScriptMethod
    public PagePojo getNextPage(int currentIndex) {
        int nextIndex = getNextIndex(currentIndex);
        if (nextIndex < 0) {
            return null;
        }
        Page page = pages.get(nextIndex);
        return new PagePojo(page.getFullDisplayUrl(), nextIndex, page.getSpecifiedTime());
    }

    @JavaScriptMethod
    public PagePojo getFirstPage() {
        if(pages == null || pages.isEmpty()) {
            return null;
        }
        Page page = pages.get(0);
        return new PagePojo(page.getFullDisplayUrl(), 0, page.getSpecifiedTime());
    }

    public SlideShows getTheMainPage() {
        return SlideShows.getInstance();
    }

    public Iterator<hudson.model.Descriptor<Page>> getPageDescriptors() {
        return Page.PageDescriptor.getAllPageDescriptors();
    }

    public void doConfigSubmit(StaplerRequest request, StaplerResponse response) throws ServletException, IOException {

        Hudson.getInstance().checkPermission(PluginImpl.CONFIGURE);

        request.bindJSON(this, request.getSubmittedForm());
        PluginImpl.getInstance().save();
        response.sendRedirect2(SlideShows.getInstance().getFullUrl());
    }

    public String getConfigUrl() {
        return SlideShows.getInstance().getFullUrl() + "/show/"  + getName() + "/configure";
    }

    public String getViewUrl() {
        return SlideShows.getInstance().getFullUrl() + "/show/"  + getName();
    }

    public static class PagePojo implements Serializable {
        private String url;
        private int index;
        private int timeout;

        public PagePojo(String url, int index, int timeout) {
            this.url = url;
            this.index = index;
            this.timeout = timeout;
        }

        public PagePojo() {
        }

        public String getUrl() {
            return url;
        }

        public int getIndex() {
            return index;
        }

        public int getTimeout() {
            return timeout;
        }

        public int getTimeoutMs() {
            return (int) TimeUnit.SECONDS.toMillis(timeout);
        }
    }
}
