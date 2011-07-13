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

import jenkins.plugins.slideshow.PluginImpl;
import jenkins.plugins.slideshow.SlideShows;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.management.Descriptor;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
            return currentIndex++;
        }
    }

    public Page getNextPage(int currentIndex) {
        int nextIndex = getNextIndex(currentIndex);
        if (nextIndex < 0) {
            return null;
        }
        return pages.get(nextIndex);
    }

    public SlideShows getTheMainPage() {
        return SlideShows.getInstance();
    }

    public Iterator<hudson.model.Descriptor<Page>> getPageDescriptors() {
        return Page.PageDescriptor.getAllPageDescriptors();
    }

    public void doConfigSubmit(StaplerRequest request, StaplerResponse response) throws ServletException, IOException {
        //JSONObject form = request.getSubmittedForm();
        request.bindJSON(this, request.getSubmittedForm());
        PluginImpl.getInstance().save();
        response.sendRedirect2(SlideShows.getInstance().getFullUrl());
    }
}
