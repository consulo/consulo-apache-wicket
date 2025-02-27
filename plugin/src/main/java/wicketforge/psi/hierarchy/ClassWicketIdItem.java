/*
 * Copyright 2010 The WicketForge-Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicketforge.psi.hierarchy;

import consulo.navigation.ItemPresentation;
import consulo.ui.image.Image;
import consulo.util.collection.SmartList;
import wicketforge.Constants;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ClassWicketIdItem implements ItemPresentation
{
    private String wicketId;
    private List<ClassWicketIdNewComponentItem> newComponentItems;
    private List<ClassWicketIdItem> children;
    private ClassWicketIdItem parent;

    ClassWicketIdItem(@Nonnull String wicketId, @Nullable ClassWicketIdItem parent) {
        this.wicketId = wicketId;
        this.newComponentItems = new SmartList<ClassWicketIdNewComponentItem>();
        if (parent != null) {
            this.parent = parent;
            parent.addChild(this);
        }
    }

    @Nullable
    ClassWicketIdItem findChild(@Nonnull String wicketId) {
        if (children != null) {
            for (ClassWicketIdItem child : children) {
                if (wicketId.equals(child.wicketId)) {
                    return child;
                }
            }
        }
        return null;
    }

    boolean contains(@Nonnull ClassWicketIdNewComponentItem newComponentItem) {
        return newComponentItems.contains(newComponentItem) || (parent != null && parent.contains(newComponentItem));
    }

    private void addChild(@Nonnull ClassWicketIdItem child) {
        if (children == null) {
            children = new ArrayList<ClassWicketIdItem>();
        }
        children.add(child);
    }

    @Nonnull
    public String getWicketId() {
        return wicketId;
    }

    @Nonnull
    public List<ClassWicketIdNewComponentItem> getNewComponentItems() {
        return newComponentItems;
    }

    @Nonnull
    public List<ClassWicketIdItem> getChildren() {
        return children == null ? Collections.<ClassWicketIdItem>emptyList() : children;
    }

    /* ItemPresentation */

    @Override
    public String getPresentableText() {
        return wicketId;
    }

    private String location;
    @Override
    public String getLocationString() {
        if (location == null) {
            StringBuilder sb = new StringBuilder();
            for (ClassWicketIdNewComponentItem newComponentItem : newComponentItems) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(newComponentItem.getLocationString());
            }
            location = sb.toString();
        }
        return location;
    }

    @Override
    @Nullable
    public Image getIcon() {
        // we can have multiple ClassWicketIdNewComponentItem -> just show icon from first item...
        if (!newComponentItems.isEmpty()) {
            return newComponentItems.get(0).getIcon();
        }
        return Constants.ICON_CLASS_;
    }
}