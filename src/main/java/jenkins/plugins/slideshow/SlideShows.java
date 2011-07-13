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
import hudson.security.Permission;
import hudson.util.FormValidation;
import jenkins.plugins.slideshow.model.SlideShow;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

/**
 * The main "page" for handling {@link SlideShow}s.
 * Created: 7/10/11 5:57 PM
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
@Extension
public class SlideShows implements RootAction {

    /**
     * The URL-name to this RootAction.
     *
     * @see #getUrlName()
     */
    public static final String URL_NAME = "slideShows";


    @Override
    public String getIconFileName() {
        if (!Hudson.getInstance().hasPermission(getListPermission())) {
            return null;
        } else {
            return "clock.png";
        }

    }

    @Override
    public String getDisplayName() {
        if (!Hudson.getInstance().hasPermission(getListPermission())) {
            return null;
        } else {
            return Messages.SlideShows();
        }
    }

    @Override
    public String getUrlName() {
        return URL_NAME;
    }

    /**
     * All the SlideShows in the system.
     *
     * @return the list of SlideShows
     */
    public List<SlideShow> getShows() {
        return PluginImpl.getInstance().getShows();
    }

    /**
     * Gives the SlideShow with the given name, or null if there is none by that name.
     * Used to navigate the SlideShows with stapler.
     *
     * @param name the name to search, case insensitive.
     * @return the SlideShow.
     */
    public SlideShow getShow(String name) {
        for (SlideShow show : getShows()) {
            if (name.equalsIgnoreCase(show.getName())) {
                return show;
            }
        }
        return null;
    }

    /**
     * The singleton instance of this Action.
     *
     * @return the object.
     */
    public static SlideShows getInstance() {
        List<Action> actions = Hudson.getInstance().getActions();
        for (Action a : actions) {
            if (a instanceof SlideShows) {
                return (SlideShows)a;
            }
        }
        return null;
    }

    /**
     * For validation for the name of a new SlideShow.
     * It checks that it is non empty and unique (no other SlideShow with the same name).
     *
     * @param value the value to check.
     * @return {@link hudson.util.FormValidation#ok()} if so.
     */
    public FormValidation doCheckName(@QueryParameter String value) {
        if (value == null || value.isEmpty()) {
            return FormValidation.error("Please specify a name.");
        } else if (getShow(value) != null) {
            return FormValidation.error("The name {0} is taken.", value);
        }
        return FormValidation.ok();
    }

    /**
     * Form validation for the default page time.
     * It checks that the user set a non negative integer.
     *
     * @param value the value to check.
     * @return {@link hudson.util.FormValidation#ok()} if so.
     */
    public FormValidation doCheckDefaultPageTime(@QueryParameter String value) {
        return FormValidation.validateNonNegativeInteger(value);
    }

    /**
     * Form post method for creating a new SlideShow.
     *
     * @param request  the request
     * @param response the response
     * @throws ServletException if so.
     * @throws IOException      if so.
     */
    public void doCreateNew(StaplerRequest request, StaplerResponse response) throws ServletException, IOException {

        Hudson.getInstance().checkPermission(getCreatePermission());

        JSONObject json = request.getSubmittedForm();
        String name = json.getString("name");
        int time = json.getInt("defaultPageTime");
        SlideShow show = new SlideShow(name, time);
        PluginImpl.getInstance().addShow(show);
        PluginImpl.getInstance().save();
        response.sendRedirect2("show/" + show.getName() + "/configure");
    }

    /**
     * Convenience method for easier Jelly access to the constant {@link SlideShow#DEFAULT_PAGE_TIME}.
     *
     * @return the default page display time.
     */
    public int getDefaultPageTime() {
        return SlideShow.DEFAULT_PAGE_TIME;
    }

    /**
     * The URL to this Action including the context root.
     *
     * @return the URL
     */
    public String getFullUrl() {
        return PluginImpl.getFromRootUrl(getUrlName());
    }

    /**
     * The LIST permission for easier Jelly access to the constant.
     *
     * @return the LIST permission.
     * @see PluginImpl#LIST
     */
    public Permission getListPermission() {
        return PluginImpl.LIST;
    }

    /**
     * The CREATE permission for easier Jelly access to the constant.
     *
     * @return the CREATE permission.
     * @see PluginImpl#CREATE
     */
    public Permission getCreatePermission() {
        return PluginImpl.CREATE;
    }

    /**
     * The CONFIGURE permission for easier Jelly access to the constant.
     *
     * @return the CONFIGURE permission.
     * @see PluginImpl#CONFIGURE
     */
    public Permission getConfigurePermission() {
        return PluginImpl.CONFIGURE;
    }

    /**
     * The DELETE permission for easier Jelly access to the constant.
     *
     * @return the DELETE permission.
     * @see PluginImpl#DELETE
     */
    public Permission getDeletePermission() {
        return PluginImpl.DELETE;
    }
}
