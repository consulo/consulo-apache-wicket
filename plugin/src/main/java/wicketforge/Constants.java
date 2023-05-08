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
package wicketforge;

import consulo.apache.wicket.icon.ApacheWicketIconGroup;
import consulo.ui.image.Image;
import wicketforge.templates.WicketTemplates;

import javax.annotation.Nonnull;

/**
 * A collection of constants used by the plugin.
 */
public interface Constants {
    //
    public static final String WICKET_ID = "wicket:id";

    //
    public static final char HIERARCHYSEPARATOR = '#';

    // wicket classes
    public static final String WICKET_APPLICATION = "org.apache.wicket.Application";
    public static final String WICKET_COMPONENT = "org.apache.wicket.Component";
    public static final String WICKET_PAGE = "org.apache.wicket.Page";
    public static final String WICKET_PANEL = "org.apache.wicket.markup.html.panel.Panel";
    public static final String WICKET_FORMCOMPONENTPANEL = "org.apache.wicket.markup.html.form.FormComponentPanel";
    public static final String WICKET_BORDER = "org.apache.wicket.markup.html.border.Border";
    public static final String WICKET_RESOURCEMODEL = "org.apache.wicket.model.ResourceModel";
    public static final String WICKET_STRINGRESOURCEMODEL = "org.apache.wicket.model.StringResourceModel";

    // intension/inspection
    public static final String INTENSION_INSPECTION_GROUPNAME = "Wicket";

    // fileTemplate properties
    public static final String PROP_WICKET_NS = "WICKET_NS";

    // icons
    public static final Image WICKET_ICON = ApacheWicketIconGroup.wicket();
    public static final Image WICKET_COMPONENT_ICON = ApacheWicketIconGroup.wicket_component();
    public static final Image TOJAVAREF = ApacheWicketIconGroup.wicket_form();
    public static final Image TOMARKUPREF = ApacheWicketIconGroup.wicket_form();

    // icon markup references
    public static final Image ICON_MARKUP_ = ApacheWicketIconGroup.wicket_component();
    public static final Image ICON_MARKUP_DIV = ApacheWicketIconGroup.componentsDiv();
    public static final Image ICON_MARKUP_SPAN = ApacheWicketIconGroup.componentsDiv();
    public static final Image ICON_MARKUP_LINK = ApacheWicketIconGroup.componentsLink();
    public static final Image ICON_MARKUP_TABLE = ApacheWicketIconGroup.componentsTable();
    public static final Image ICON_MARKUP_TR = ApacheWicketIconGroup.componentsTable_tr();
    public static final Image ICON_MARKUP_TD = ApacheWicketIconGroup.componentsTable_td();
    public static final Image ICON_MARKUP_UL = ApacheWicketIconGroup.componentsUl();
    public static final Image ICON_MARKUP_LI = ApacheWicketIconGroup.componentsBullet();
    public static final Image ICON_MARKUP_LABEL = ApacheWicketIconGroup.componentsLabel();
    public static final Image ICON_MARKUP_INPUT = ApacheWicketIconGroup.componentsTextfield();
    public static final Image ICON_MARKUP_INPUT_CHECKBOX = ApacheWicketIconGroup.componentsCheckbox();
    public static final Image ICON_MARKUP_INPUT_RADIO = ApacheWicketIconGroup.componentsRadiobutton();
    public static final Image ICON_MARKUP_TEXTAREA = ApacheWicketIconGroup.componentsTextarea();
    public static final Image ICON_MARKUP_SELECT = ApacheWicketIconGroup.componentsSelect();
    public static final Image ICON_MARKUP_OPTION = ApacheWicketIconGroup.componentsBullet();
    public static final Image ICON_MARKUP_FORM = ApacheWicketIconGroup.componentsForm();
    public static final Image ICON_MARKUP_IMG = ApacheWicketIconGroup.componentsImage();
    public static final Image ICON_MARKUP_BUTTON = ApacheWicketIconGroup.componentsButton();

    // icon class references
    public static final Image ICON_CLASS_ = ApacheWicketIconGroup.wicket_component();
    public static final Image ICON_CLASS_FORM = ApacheWicketIconGroup.componentsForm();
    public static final Image ICON_CLASS_SELECT = ApacheWicketIconGroup.componentsSelect();
    public static final Image ICON_CLASS_CHECKBOX = ApacheWicketIconGroup.componentsCheckbox();
    public static final Image ICON_CLASS_RADIO = ApacheWicketIconGroup.componentsRadiobutton();
    public static final Image ICON_CLASS_LABEL = ApacheWicketIconGroup.componentsLabel();
    public static final Image ICON_CLASS_LINK = ApacheWicketIconGroup.componentsLink();
    public static final Image ICON_CLASS_BUTTON = ApacheWicketIconGroup.componentsButton();
    public static final Image ICON_CLASS_TEXTFIELD = ApacheWicketIconGroup.componentsTextfield();
    public static final Image ICON_CLASS_TEXTAREA = ApacheWicketIconGroup.componentsTextarea();
    public static final Image ICON_CLASS_REPEATER = ApacheWicketIconGroup.componentsRepeater();
    public static final Image ICON_CLASS_PANEL = ApacheWicketIconGroup.componentsPanel();
    public static final Image ICON_CLASS_BORDER = ApacheWicketIconGroup.componentsBorder();
    public static final Image ICON_CLASS_FORMCOMPONENTPANEL = ApacheWicketIconGroup.componentsFormcomponent();
    public static final Image ICON_CLASS_FORMCOMPONENT = ApacheWicketIconGroup.componentsFormcomponent();
    public static final Image ICON_CLASS_WEBMARKUPCONTAINER = ApacheWicketIconGroup.componentsDiv();

    enum PropertiesType {
        PROPERTIES(WicketTemplates.WICKET_PROPERTIES),
        XML(WicketTemplates.WICKET_PROPERTIES_XML);

        private String templateName;

        private PropertiesType(String templateName) {
            this.templateName = templateName;
        }

        @Nonnull
        public String getTemplateName() {
            return templateName;
        }
    }
}
