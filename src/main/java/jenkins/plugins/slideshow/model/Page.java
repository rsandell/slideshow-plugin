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
import jenkins.plugins.slideshow.Messages;

/**
 * Created: 7/10/11 5:29 PM
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public abstract class Page implements Describable<Page> {

    private SlideShow parent;

    public SlideShow getParent() {
        return parent;
    }

    public void setParent(SlideShow parent) {
        this.parent = parent;
    }

    /**
     * The descriptor of a page.
     */
    public static class PageDescriptor extends Descriptor<Page> {

        @Override
        public String getDisplayName() {
            return Messages.Page();
        }
    }
}
