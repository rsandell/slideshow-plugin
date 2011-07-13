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
import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.RootAction;
import hudson.util.FormValidation;
import jenkins.plugins.slideshow.model.SlideShow;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created: 7/10/11 5:57 PM
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
@Extension
public class SlideShows implements RootAction {

    public static final String URL_NAME = "slideShows";


    @Override
    public String getIconFileName() {
        return "clock.gf";
    }

    @Override
    public String getDisplayName() {
        return Messages.SlideShows();
    }

    @Override
    public String getUrlName() {
        return URL_NAME;
    }

    public List<SlideShow> getShows() {
        return PluginImpl.getInstance().getShows();
    }

    public SlideShow getShow(String name) {
        for (SlideShow show : getShows()) {
            if (name.equalsIgnoreCase(show.getName())) {
                return show;
            }
        }
        return null;
    }

    public static SlideShows getInstance() {
        List<Action> actions = Hudson.getInstance().getActions();
        for (Action a : actions) {
            if (a instanceof SlideShows) {
                return (SlideShows) a;
            }
        }
        return null;
    }

    public FormValidation doCheckName(@QueryParameter String value) {
        if (value == null || value.isEmpty()) {
            return FormValidation.error("Please specify a name.");
        } else if (getShow(value) != null) {
            return FormValidation.error("The name {0} is taken.", value);
        }
        return FormValidation.ok();
    }

    public FormValidation doCheckDefaultPageTime(@QueryParameter String value) {
        return FormValidation.validateNonNegativeInteger(value);
    }

    public void doCreateNew(StaplerRequest request, StaplerResponse response) throws ServletException, IOException {
        JSONObject json = request.getSubmittedForm();
        String name = json.getString("name");
        int time = json.getInt("defaultPageTime");
        SlideShow show = new SlideShow(name, time);
        PluginImpl.getInstance().addShow(show);
        PluginImpl.getInstance().save();
        response.sendRedirect2("show/" + show.getName() + "/configure");
    }

    public int getDefaultPageTime() {
        return SlideShow.DEFAULT_PAGE_TIME;
    }

    public String getFullUrl() {
        return PluginImpl.getFromRootUrl(getUrlName());
    }
}
