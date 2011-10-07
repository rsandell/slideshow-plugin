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
import hudson.model.PageDecorator;
import jenkins.plugins.slideshow.model.SlideShow;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jenkins.plugins.slideshow.SlideShows.URL_NAME;

/**
 * Assists in manipulating the DOM for pages inside a slide show
 * where the Jenkins top and bottom decorators should be hidden.
 * Created: 10/5/11 6:13 PM
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
@Extension
public class HideHeadersPageDecorator extends PageDecorator {

    private static final Logger logger = Logger.getLogger(HideHeadersPageDecorator.class.getName());

    /**
     * Default constructor.
     */
    public HideHeadersPageDecorator() {
        super(HideHeadersPageDecorator.class);
    }

    /**
     * Tries to retrieve the SlideShow from the referer field of the request.
     *
     * @return the SlideShow that referred to this page or null if none was found.
     */
    public SlideShow getSlideShow() {
        //TODO find out a way to map the referer via stapler
        StaplerRequest request = Stapler.getCurrentRequest();
        try {
            if (request != null && request.getReferer() != null && !request.getPathInfo().contains(URL_NAME)) {
                URI referer = new URI(request.getReferer());
                if (request.getServerName().equals(referer.getHost())) {
                    logger.fine("Have a server name");
                    int index = referer.getPath().indexOf(URL_NAME);
                    if (index > 0) {
                        logger.fine("Is a slide show");
                        String slideShowsPath = referer.getPath().substring(index + URL_NAME.length());
                        logger.fine("SlideShow is: " + slideShowsPath);
                        if (slideShowsPath != null && slideShowsPath.length() > 0) {
                            String[] paths = slideShowsPath.split("/");
                            for (int i = 0; i < paths.length; i++) {
                                if (paths[i].equals("show") && i < (paths.length - 1)) {
                                    return SlideShows.getInstance().getShow(paths[i + 1]);
                                }
                            }
                            return null;
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (URISyntaxException e) {
            logger.log(Level.WARNING, "Bad referrer syntax ", e);
            return null;
        }
    }
}
