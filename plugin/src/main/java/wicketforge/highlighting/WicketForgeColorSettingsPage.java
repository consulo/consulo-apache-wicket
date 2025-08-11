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
package wicketforge.highlighting;

import consulo.annotation.component.ExtensionImpl;
import consulo.colorScheme.TextAttributes;
import consulo.colorScheme.TextAttributesKey;
import consulo.colorScheme.setting.AttributesDescriptor;
import consulo.language.editor.annotation.HighlightSeverity;
import consulo.language.editor.colorScheme.setting.ColorSettingsPage;
import consulo.language.editor.highlight.DefaultSyntaxHighlighter;
import consulo.language.editor.highlight.SyntaxHighlighter;
import consulo.language.editor.rawHighlight.HighlightInfoType;
import consulo.language.psi.PsiElement;
import consulo.localize.LocalizeValue;
import consulo.ui.color.RGBColor;
import consulo.ui.util.LightDarkColorValue;
import jakarta.annotation.Nonnull;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 */
@ExtensionImpl
public class WicketForgeColorSettingsPage implements ColorSettingsPage {
    private static final TextAttributes DEFAULTWICKETID = new TextAttributes(new LightDarkColorValue(new RGBColor(232, 89, 10), new RGBColor(200, 83, 10)), null, null, null, Font.BOLD);
    private static final TextAttributes DEFAULTWICKETID_NOTRESOLVABLE = new TextAttributes(null, null, null, null, Font.PLAIN);

    private static final TextAttributesKey JAVAWICKETID = TextAttributesKey.createTextAttributesKey("JAVAWICKETID", DEFAULTWICKETID);
    private static final TextAttributesKey JAVAWICKETID_NOTRESOLVABLE = TextAttributesKey.createTextAttributesKey("JAVAWICKETID_NOTRESOLVABLE", DEFAULTWICKETID_NOTRESOLVABLE);
    private static final TextAttributesKey MARKUPWICKETID = TextAttributesKey.createTextAttributesKey("MARKUPWICKETID", DEFAULTWICKETID);

    public static final HighlightInfoType HIGHLIGHT_JAVAWICKETID = new WicketHighlightInfoType(JAVAWICKETID);
    public static final HighlightInfoType HIGHLIGHT_JAVAWICKETID_NOTRESOLVABLE = new WicketHighlightInfoType(JAVAWICKETID_NOTRESOLVABLE);
    public static final HighlightInfoType HIGHLIGHT_MARKUPWICKETID = new WicketHighlightInfoType(MARKUPWICKETID);

    private static final AttributesDescriptor[] ATTRIBUTESDESC = {
            new AttributesDescriptor("java wicketId", JAVAWICKETID),
            new AttributesDescriptor("java wicketId (not resolvable)", JAVAWICKETID_NOTRESOLVABLE),
            new AttributesDescriptor("markup wicketId", MARKUPWICKETID)
    };

    @Override
    @Nonnull
    public LocalizeValue getDisplayName() {
        return LocalizeValue.localizeTODO("Wicket");
    }

    @Override
    @Nonnull
    public AttributesDescriptor[] getAttributeDescriptors() {
        return ATTRIBUTESDESC;
    }

    @Override
    @Nonnull
    public SyntaxHighlighter getHighlighter() {
        return new DefaultSyntaxHighlighter();
    }

    @Override
    @Nonnull
    public String getDemoText() {
        return "-- java\n" +
                "new Label(<javaWicketId>\"someWicketId\"</javaWicketId>, \"Hello World!\")\n" +
                "new Label(<javaWicketIdNotResolvable>id</javaWicketIdNotResolvable>, \"Hello World!\")\n" +
                "\n" +
                "-- markup\n" +
                "span wicket:id=<markupWicketId>\"someWicketId\"</markupWicketId>\n";
    }

    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        Map<String, TextAttributesKey> map = new HashMap<String, TextAttributesKey>();
        map.put("javaWicketId", JAVAWICKETID);
        map.put("javaWicketIdNotResolvable", JAVAWICKETID_NOTRESOLVABLE);
        map.put("markupWicketId", MARKUPWICKETID);
        return map;
    }

    private static class WicketHighlightInfoType implements HighlightInfoType {
        private TextAttributesKey textAttributesKey;

        private WicketHighlightInfoType(TextAttributesKey textAttributesKey) {
            this.textAttributesKey = textAttributesKey;
        }

        @Override
        @Nonnull
        public HighlightSeverity getSeverity(PsiElement psiElement) {
            return HighlightSeverity.INFORMATION;
        }

        @Override
        public TextAttributesKey getAttributesKey() {
            return textAttributesKey;
        }
	}
}
