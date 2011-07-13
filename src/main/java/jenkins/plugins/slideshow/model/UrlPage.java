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
 * Created: 7/11/11 3:13 PM
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public class UrlPage extends Page {

    private String url;

    @DataBoundConstructor
    public UrlPage(Time overrideTime, String url) {
        super(overrideTime);
        this.url = url;
    }

    public UrlPage() {
    }

    @Override
    public Descriptor<Page> getDescriptor() {
        return Hudson.getInstance().getDescriptorByType(UrlPageDescriptor.class);
    }

    @Extension
    public static class UrlPageDescriptor extends PageDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.UrlPage();
        }

        public FormValidation doCheckUrl(@QueryParameter String value) {
            if(value == null || value.isEmpty()) {
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

    public String getUrl() {
        return url;
    }

    @Override
    public String getFullDisplayUrl() {
        return PluginImpl.getFromRootUrl(url);
    }
}
