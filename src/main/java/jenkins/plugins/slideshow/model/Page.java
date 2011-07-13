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

import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.util.Iterator;

/**
 * Base class for Pages in a {@link }SlideShow}.
 * Extend this class and it's Descriptor to create a new type of Page and have it appear in the hetero-list
 * on the configuration page of a SlideShow.
 *
 * <strong style="text-decoration: underline;">Jelly Views</strong><br/>
 * <strong>config-impl.jelly:</strong> Included in the configuration snippet of the Page.<br/>
 * <strong>view-impl.jelly:</strong> Included in the listing of the SlideShows and their Pages.
 *                                   It is included in the middle of a two column table so it is expected of
 *                                   implementations to start with a &lt;tr&gt;...&lt;/tr&gt;
 *
 * Created: 7/10/11 5:29 PM
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public abstract class Page implements Describable<Page> {

    private SlideShow parent;
    private Time overrideTime = null;

    /**
     * Standard constructor.
     *
     * @param overrideTime the time to override, or null to not.
     */
    protected Page(Time overrideTime) {
        this.overrideTime = overrideTime;
    }

    /**
     * Default constructor.
     * <strong>Do not use unless you are a serializer.</strong>
     */
    protected Page() {
    }

    /**
     * If the display time is overridden in this page.
     *
     * @return true if so.
     */
    public boolean isOverrideTime() {
        return overrideTime != null;
    }

    /**
     * The time that this page wants to be displayed, or null if the default in the SlideShow should be used.
     *
     * @return the time.
     */
    public Time getOverrideTime() {
        return overrideTime;
    }

    /**
     * Set to something to override the default time set by the SlideShow or null to use the default.
     *
     * @param overrideTime the time to override
     */
    public void setOverrideTime(Time overrideTime) {
        this.overrideTime = overrideTime;
    }

    /**
     * If the display time is overridden by this page then that value is returned,
     * otherwise {@link jenkins.plugins.slideshow.model.SlideShow#getDefaultPageTime()} is returned.
     *
     * @return the time in seconds to show this page before requesting the next one.
     */
    public int getSpecifiedTime() {
        if (this.overrideTime != null) {
            return overrideTime.getTime();
        } else {
            return getParent().getDefaultPageTime();
        }
    }

    /**
     * The URL to give to the browser when the page is displayed.
     *
     * @return the URL.
     */
    public abstract String getFullDisplayUrl();

    /**
     * The {@link SlideShow} that this page belongs to.
     *
     * @return the parent.
     */
    public SlideShow getParent() {
        return parent;
    }

    /**
     * The {@link SlideShow} that this page belongs to.
     * Called by the SlideShow object when this page is added to it.
     *
     * @param parent the parent.
     */
    public void setParent(SlideShow parent) {
        this.parent = parent;
    }

    /**
     * The descriptor of a page.
     * To create a custom page extend this Descriptor and put <code>@Extension</code> on the class declaration
     * to have it appear in the hetero-list of available page types. Then extend the {@link Page} class,
     * put your data in that and have it return your new descriptor.
     */
    public abstract static class PageDescriptor extends Descriptor<Page> {

        /**
         * Form validation of the override time. Making sure that the user has set a non negative integer.
         * @param value the value to check.
         * @return {@link hudson.util.FormValidation#ok()} if so.
         */
        public FormValidation doCheckTime(@QueryParameter String value) {
            return FormValidation.validateNonNegativeInteger(value);
        }

        /**
         * All the PageDescriptors defined in the system.
         * @return the descriptors.
         */
        public static Iterator<Descriptor<Page>> getAllPageDescriptors() {
            return Hudson.getInstance().getDescriptorList(Page.class).iterator();
        }

        /**
         * The constant {@link SlideShow#DEFAULT_PAGE_TIME} for convenient Jelly access.
         * @return the default page display time.
         */
        public static int getDefaultPageTime() {
            return SlideShow.DEFAULT_PAGE_TIME;
        }
    }

    /**
     * The optional block to override the default display time that is
     * set in the {@link jenkins.plugins.slideshow.model.Page#getParent() parent}.
     */
    public static class Time {
        int time = SlideShow.DEFAULT_PAGE_TIME;

        /**
         * Standard constructor.
         *
         * @param time the time (in seconds) to override with.
         */
        @DataBoundConstructor
        public Time(int time) {
            this.time = time;
        }

        /**
         * Default constructor.
         * <strong>Do not use unless you are a serializer.</strong>
         */
        public Time() {
        }

        /**
         * The display time in seconds.
         *
         * @return the time.
         */
        public int getTime() {
            return time;
        }

        /**
         * The display time in seconds.
         *
         * @param time the time.
         */
        public void setTime(int time) {
            this.time = time;
        }
    }
}
