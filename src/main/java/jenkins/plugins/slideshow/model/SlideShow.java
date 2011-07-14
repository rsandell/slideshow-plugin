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
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A instance of a Slide show.
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

    /**
     * Standard Constructor.
     *
     * @param name            a unique name
     * @param defaultPageTime How long each page should be shown by default
     * @param pages           list of pages.
     */
    public SlideShow(String name, int defaultPageTime, List<Page> pages) {
        this.name = name;
        this.defaultPageTime = defaultPageTime;
        this.pages = pages;
    }

    /**
     * Standard Constructor.
     *
     * @param name  a unique name
     * @param pages list of pages.
     */
    public SlideShow(String name, List<Page> pages) {
        this.name = name;
        this.pages = pages;
        this.defaultPageTime = DEFAULT_PAGE_TIME;
    }

    /**
     * Standard Constructor.
     *
     * @param name            a unique name
     * @param defaultPageTime How long each page should be shown by default
     */
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

    /**
     * The name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * The name of the slide show. Must be unique.
     *
     * @param name the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * How long each page should be shown by default.
     *
     * @return the page display time
     */
    public int getDefaultPageTime() {
        return defaultPageTime;
    }

    /**
     * How long each page should be shown by default.
     *
     * @param defaultPageTime the page display time.
     */
    public void setDefaultPageTime(int defaultPageTime) {
        this.defaultPageTime = defaultPageTime;
    }

    /**
     * A direct reference to the list of pages.
     *
     * @return the pages.
     */
    public List<Page> getPages() {
        return pages;
    }

    /**
     * Sets the list of pages.
     * All Pages will get their {@link jenkins.plugins.slideshow.model.Page#getParent() parent}
     * set to this SlideShow.
     *
     * @param pages the pages.
     */
    public void setPages(List<Page> pages) {
        this.pages = pages;
        for (Page p : pages) {
            p.setParent(this);
        }
    }

    /**
     * Adds a page to the list and sets it's
     * {@link jenkins.plugins.slideshow.model.Page#getParent() parent} to this SlideShow.
     *
     * @param page the page to add.
     */
    public void addPage(Page page) {
        if (this.pages == null) {
            this.pages = new LinkedList<Page>();
        }
        pages.add(page);
        page.setParent(this);
    }

    /**
     * Gets the page at the specified index.
     * Can be used to direct stapler to the correct instance.
     *
     * @param index the index of the page.
     * @return the Page.
     */
    public Page getPage(int index) {
        return pages.get(index);
    }

    /**
     * Gets the index of the specified page, or -1 if the page cannot be found in the list.
     *
     * @param page the page.
     * @return the index
     * @see List#indexOf(Object)
     */
    public int getIndexOf(Page page) {
        if (pages == null) {
            return -1;
        }
        return pages.indexOf(page);
    }

    /**
     * Gets the index of the next page in the list of pages or 0 if the end of the list is reached.
     *
     * @param currentIndex the index of the current page.
     * @return the index of the next page in the chain, or -1 if there are no pages.
     */
    public int getNextIndex(int currentIndex) {
        if (pages == null || pages.size() <= 0) {
            return -1;
        } else if (currentIndex < 0 || currentIndex >= pages.size() - 1) {
            return 0;
        } else {
            return currentIndex + 1;
        }
    }

    /**
     * Gets the next page in the list of pages or the first one if the end is reached.
     * Used by JavaScript to continue the slide show.
     *
     * @param currentIndex the index of the current page.
     * @return the next page.
     */
    @JavaScriptMethod
    public PagePojo getNextPage(int currentIndex) {
        int nextIndex = getNextIndex(currentIndex);
        if (nextIndex < 0) {
            return null;
        }
        Page page = pages.get(nextIndex);
        return new PagePojo(page.getFullDisplayUrl(), nextIndex, page.getSpecifiedTime());
    }

    /**
     * Gets the first page in the slide show.
     * Used by the JavaScript to start the slide show.
     *
     * @return the data for the first page, or null if there is none.
     */
    @JavaScriptMethod
    public PagePojo getFirstPage() {
        if (pages == null || pages.isEmpty()) {
            return null;
        }
        Page page = pages.get(0);
        return new PagePojo(page.getFullDisplayUrl(), 0, page.getSpecifiedTime());
    }

    /**
     * The main RootAction.
     * Used by Jelly to reach common functions.
     *
     * @return the main page.
     */
    public SlideShows getTheMainPage() {
        return SlideShows.getInstance();
    }

    /**
     * All {@link jenkins.plugins.slideshow.model.Page.PageDescriptor}s for the hetero-list.
     *
     * @return a list of the descriptors.
     */
    public Iterator<hudson.model.Descriptor<Page>> getPageDescriptors() {
        return Page.PageDescriptor.getAllPageDescriptors();
    }

    /**
     * For submission handling for the configuration page.
     *
     * @param request  the request
     * @param response the response
     * @throws ServletException if so
     * @throws IOException      if so.
     */
    public void doConfigSubmit(StaplerRequest request, StaplerResponse response) throws ServletException, IOException {

        Hudson.getInstance().checkPermission(PluginImpl.CONFIGURE);

        request.bindJSON(this, request.getSubmittedForm());
        PluginImpl.getInstance().save();
        response.sendRedirect2(SlideShows.getInstance().getFullUrl());
    }

    /**
     * The URL to the slide show's configuration page.
     * Used by Jelly to show a link to it.
     *
     * @return the URL to the configuration page.
     */
    public String getConfigUrl() {
        return SlideShows.getInstance().getFullUrl() + "/show/" + getName() + "/configure";
    }

    /**
     * The URL to the slide show.
     * Used by Jelly to show a link to it.
     *
     * @return the URL to the slide show.
     */
    public String getViewUrl() {
        return SlideShows.getInstance().getFullUrl() + "/show/" + getName();
    }

    /**
     * A POJO that contains the info needed for the JavaScript code to do it's thing.
     */
    public static class PagePojo implements Serializable {
        private String url;
        private int index;
        private int timeout;

        /**
         * Standard constructor.
         *
         * @param url     the URL
         * @param index   the index
         * @param timeout the display time.
         */
        public PagePojo(String url, int index, int timeout) {
            this.url = url;
            this.index = index;
            this.timeout = timeout;
        }

        /**
         * Default constructor.
         * <strong>Do not use unless you are a serializer.</strong>
         */
        public PagePojo() {
        }

        /**
         * The URL to direct the browser to.
         *
         * @return the URL.
         */
        public String getUrl() {
            return url;
        }

        /**
         * The index of this page in the list of pages.
         *
         * @return the index.
         */
        public int getIndex() {
            return index;
        }

        /**
         * The time the page should be displayed (in seconds) before the next page is requested.
         *
         * @return the time in seconds.
         */
        public int getTimeout() {
            return timeout;
        }

        /**
         * The time the page should be displayed (in milliseconds) before the next page is requested.
         *
         * @return the time in milliseconds.
         */
        public int getTimeoutMs() {
            return (int)TimeUnit.SECONDS.toMillis(timeout);
        }
    }
}
