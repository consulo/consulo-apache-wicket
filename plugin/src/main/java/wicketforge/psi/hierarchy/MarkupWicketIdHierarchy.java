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

import consulo.language.psi.PsiElement;
import consulo.xml.psi.XmlRecursiveElementVisitor;
import consulo.xml.psi.xml.XmlAttribute;
import consulo.xml.psi.xml.XmlAttributeValue;
import consulo.xml.psi.xml.XmlFile;
import wicketforge.Constants;

import jakarta.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class MarkupWicketIdHierarchy {
    private Map<String, MarkupWicketIdItem> wicketIdPathMap;
    private MarkupWicketIdItem root;

    @Nonnull
    public static MarkupWicketIdHierarchy create(@Nonnull XmlFile xmlFile) {
        return new MarkupWicketIdHierarchy(xmlFile);
    }

    private MarkupWicketIdHierarchy(@Nonnull final XmlFile xmlFile) {
        this.wicketIdPathMap = new HashMap<String, MarkupWicketIdItem>();
        this.root = new MarkupWicketIdItem();
        this.wicketIdPathMap.put("", root);

        xmlFile.accept(new XmlRecursiveElementVisitor() {
            private StringBuilder sb = new StringBuilder();
            private MarkupWicketIdItem current = root;

            @Override
            public void visitElement(PsiElement element) {
                // save
                int i = sb.length();
                MarkupWicketIdItem item = current;
                try {
                    // visit
                    super.visitElement(element);
                } finally {
                    // restore
                    if (sb.length() != i) {
                        sb.setLength(i);
                    }
                    current = item;
                }
            }

            @Override
            public void visitXmlAttribute(XmlAttribute attribute) {
                if (Constants.WICKET_ID.equals(attribute.getName())) {
                    XmlAttributeValue attributeValue = attribute.getValueElement();
                    if (attributeValue != null) {
                        String wicketId = attributeValue.getValue();
                        if (wicketId != null) {
                            MarkupWicketIdItem item = new MarkupWicketIdItem(wicketId, attribute, attributeValue);
                            current.addChild(item);
                            sb.append(Constants.HIERARCHYSEPARATOR).append(wicketId);
                            wicketIdPathMap.put(sb.toString(), item);
                            current = item;
                        }
                    }
                }
                super.visitXmlAttribute(attribute);
            }
        });
    }

    @Nonnull
    public Map<String, MarkupWicketIdItem> getWicketIdPathMap() {
        return wicketIdPathMap;
    }

    @Nonnull
    public MarkupWicketIdItem getRoot() {
        return root;
    }
}
