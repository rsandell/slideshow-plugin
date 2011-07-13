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

package jenkins.plugins.slideshow;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.Hudson;
import jenkins.plugins.slideshow.model.Page;
import jenkins.plugins.slideshow.model.SlideShow;
import org.kohsuke.stapler.Stapler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 * Main Plugin Singleton
 * Created: 7/10/11 5:13 PM
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
@Extension
public class PluginImpl extends Plugin {

    private List<SlideShow> shows;

    @Override
    public void start() throws Exception {
        super.start();
        load();
    }

    public static PluginImpl getInstance() {
        return Hudson.getInstance().getPlugin(PluginImpl.class);
    }

    @Override
    public synchronized void save() throws IOException {
        super.save();
    }

    public List<SlideShow> getShows() {
        if(shows == null) {
            shows = new LinkedList<SlideShow>();
        }
        return shows;
    }

    public void setShows(List<SlideShow> shows) {
        this.shows = shows;
    }

    public void addShow(SlideShow show) {
        getShows().add(show);
    }

    public static String getRootUrl() {
        String rootUrl = Hudson.getInstance().getRootUrl();
        if (rootUrl == null) {
            rootUrl = Stapler.getCurrentRequest().getRootPath();
        }
        if (rootUrl != null && !rootUrl.endsWith("/")) {
            rootUrl = rootUrl + "/";
        }
        return rootUrl;
    }

    public static String getFromRootUrl(String url) {
        try {
            URI u = new URI(url);

            if (u.isAbsolute()) {
                return url;
            } else {
                String rootUrl = getRootUrl();
                if (rootUrl == null) {
                    rootUrl = "";
                }
                return rootUrl + url;
            }

        } catch (URISyntaxException e) {
            return url;
        }
    }
}
