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
import jenkins.plugins.slideshow.Messages;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * A Page type where the user can input arbitrary html code.
 * Created: 10/5/11 4:01 PM
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public class HtmlPage extends Page {

    /**
     * Starting fragment of the html decoration.
     * @see HtmlPageDescriptor#decorateIfNeeded(String).
     */
    public static final String HTML_DECORATION_START = "<html><head><title></title></head><body>";
    /**
     * Ending fragment of the html decoration.
     * @see HtmlPageDescriptor#decorateIfNeeded(String).
     */
    public static final String HTML_DECORATION_END = "</body></html>";
    /**
     * What to check for if a string should be decorated or not.
     */
    public static final String HTML_TAG_START = "<html";

    private String html;

    /**
     * Standard constructor.
     *
     * @param overrideTime the display time if it is overridden.
     * @param html         the html code, if the code is not stasrting with &lt;html&gt;
     *                     it will be decorated with an empty html header and body tag..
     */
    @DataBoundConstructor
    public HtmlPage(Time overrideTime, String html) {
        super(overrideTime);
        this.html = HtmlPageDescriptor.decorateIfNeeded(html);
    }

    /**
     * Default constructor.
     * <strong>Do not use unless you are a serializer.</strong>
     */
    public HtmlPage() {
    }

    /**
     * The html code.
     *
     * @return the user's html.
     */
    public String getHtml() {
        return html;
    }

    @Override
    public String getFullDisplayUrl() {
        return null;
    }

    @Override
    public Descriptor<Page> getDescriptor() {
        return Hudson.getInstance().getDescriptorByType(HtmlPageDescriptor.class);
    }

    /**
     * The Descriptor for {@link HtmlPage}.
     */
    @Extension
    public static class HtmlPageDescriptor extends PageDescriptor {

        /**
         * If the text doesn't contain a &lt;html&gt; fragment it will be decorated with it.
         * @param html the html to decorate.
         * @return the decorated string.
         */
        public static String decorateIfNeeded(String html) {
            html = html.trim();
            int safeLength = HTML_TAG_START.length();
            if (html.length() > safeLength) {
                if (!html.substring(0, safeLength).toLowerCase().startsWith(HTML_TAG_START)) {
                    html = HTML_DECORATION_START + html + HTML_DECORATION_END;
                }

            } else {
                html = HTML_DECORATION_START + html + HTML_DECORATION_END;
            }
            return html;
        }

        @Override
        public String getDisplayName() {
            return Messages.HtmlPage();
        }
    }
}
