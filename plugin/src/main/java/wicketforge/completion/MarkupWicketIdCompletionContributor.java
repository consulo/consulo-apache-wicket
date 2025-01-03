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
package wicketforge.completion;

import com.intellij.java.language.psi.PsiClass;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.ApplicationManager;
import consulo.language.Language;
import consulo.language.editor.completion.CompletionContributor;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.xml.ide.highlighter.HtmlFileType;
import consulo.xml.lang.html.HTMLLanguage;
import consulo.xml.psi.xml.XmlAttribute;
import consulo.xml.psi.xml.XmlAttributeValue;
import consulo.xml.psi.xml.XmlToken;
import wicketforge.Constants;
import wicketforge.psi.hierarchy.ClassWicketIdHierarchy;
import wicketforge.psi.hierarchy.ClassWicketIdItem;
import wicketforge.psi.hierarchy.HierarchyUtil;
import wicketforge.search.ClassIndex;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@ExtensionImpl
public class MarkupWicketIdCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(final CompletionParameters p, final CompletionResultSet rs) {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                // lets do some basic checks...
                PsiFile f = p.getOriginalFile();
                if (f.getFileType() == HtmlFileType.INSTANCE) {
                    PsiElement psiElement = p.getOriginalPosition();
                    if (psiElement instanceof XmlToken) {
                        XmlAttributeValue wicketIdAttribute = getWicketIdAttribute((XmlToken) psiElement);
                        if (wicketIdAttribute != null) {
                            PsiClass clazz = ClassIndex.getAssociatedClass(f);
                            if (clazz != null) {
                                // ... before we search for our parent Item
                                String parentPath = HierarchyUtil.findPathOf(wicketIdAttribute, true);
                                if (parentPath != null) {
                                    ClassWicketIdItem item = ClassWicketIdHierarchy.create(clazz).getWicketIdPathMap().get(parentPath);
                                    if (item != null) {
                                        for (ClassWicketIdItem child : item.getChildren()) {
                                            rs.addElement(
                                                    LookupElementBuilder.create(child.getWicketId())
                                                            //.withIcon(child.getIcon()) // child's icon bother view -> use generic icon
                                                            .withIcon(Constants.WICKET_COMPONENT_ICON)
                                                            .withTypeText(".java")
                                                            .withTailText("  " + child.getLocationString(), true)
                                            );
                                        }
                                    }
                                }
                                rs.stopHere();
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * @param position
     * @return XmlAttributeValue if position is wicket:id attribute else null
     */
    @Nullable
    private XmlAttributeValue getWicketIdAttribute(@Nonnull XmlToken position) {
        if (!(position.getParent() instanceof XmlAttributeValue)) {
            return null;
        }
        XmlAttributeValue attributeValue = (XmlAttributeValue) position.getParent();
        if (!(attributeValue.getParent() instanceof XmlAttribute)) {
            return null;
        }
        XmlAttribute attribute = (XmlAttribute) attributeValue.getParent();
        return Constants.WICKET_ID.equals(attribute.getName()) ? attributeValue : null;
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return HTMLLanguage.INSTANCE;
    }
}
