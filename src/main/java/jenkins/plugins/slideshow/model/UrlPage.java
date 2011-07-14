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

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.util.FormValidation;
import jenkins.plugins.slideshow.Messages;
import jenkins.plugins.slideshow.PluginImpl;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A page that shows an arbitrary URL.
 * Created: 7/11/11 3:13 PM
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public class UrlPage extends Page {

    private String url;

    /**
     * Standard constructor.
     *
     * @param overrideTime if this pages should be displayed for a different time than other pages.
     * @param url          the URL itself.
     */
    @DataBoundConstructor
    public UrlPage(Time overrideTime, String url) {
        super(overrideTime);
        this.url = url;
    }

    /**
     * Default constructor.
     * <strong>Do not use unless you are a serializer.</strong>
     */
    public UrlPage() {
    }

    @Override
    public Descriptor<Page> getDescriptor() {
        return Hudson.getInstance().getDescriptorByType(UrlPageDescriptor.class);
    }

    /**
     * The Descriptor for UrlPage.
     *
     * @see PageDescriptor
     */
    @Extension
    public static class UrlPageDescriptor extends PageDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.UrlPage();
        }

        /**
         * Performs a FormValidation on the url value.
         * Validates != null, not empty and that {@link URI#URI(String)} doesn't throw an exception.
         * Called from Jelly.
         *
         * @param value the value to check.
         * @return {@link hudson.util.FormValidation#ok()} if so.
         */
        public FormValidation doCheckUrl(@QueryParameter String value) {
            if (value == null || value.isEmpty()) {
                return FormValidation.error("Please provide a value.");
            } else {
                try {
                    new URI(value);
                    return FormValidation.ok();
                } catch (URISyntaxException e) {
                    return FormValidation.error("Not a valid URI: " + e.getMessage());
                }
            }
        }
    }

    /**
     * The URL to display.
     * If it is relative it will be relative to the slide show itself.
     *
     * @return the URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * The URL to display.
     * If it is relative it will be relative to the slide show itself.
     *
     * @param url the url.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getFullDisplayUrl() {
        return PluginImpl.getFromRootUrl(url);
    }
}
