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
package wicketforge.codeInsight;

import com.intellij.java.language.psi.PsiClass;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.gutter.LineMarkerInfo;
import consulo.language.editor.gutter.LineMarkerProvider;
import consulo.language.psi.NavigatablePsiElement;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.xml.lang.html.HTMLLanguage;
import consulo.xml.psi.xml.XmlDocument;
import consulo.xml.psi.xml.XmlTag;
import consulo.xml.psi.xml.XmlToken;
import consulo.xml.psi.xml.XmlTokenType;
import wicketforge.Constants;
import wicketforge.facet.WicketForgeFacet;
import wicketforge.search.ClassIndex;
import wicketforge.util.WicketPsiUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ExtensionImpl
public class WicketMarkupLineMarkerProvider implements LineMarkerProvider {
    @RequiredReadAction
    @Override
    @Nullable
    public LineMarkerInfo getLineMarkerInfo(@Nonnull PsiElement element) {
        // look for root tag
        if (element instanceof XmlToken && ((XmlToken) element).getTokenType() == XmlTokenType.XML_START_TAG_START &&
                element.getParent() instanceof XmlTag && element.getParent().getParent() instanceof XmlDocument) {
            PsiFile file = element.getContainingFile();
            if (file != null) {
                if (WicketForgeFacet.hasFacetOrIsFromLibrary(file)) {
                    final PsiClass psiClass = ClassIndex.getAssociatedClass(file);
                    if (psiClass != null && WicketPsiUtil.isWicketComponentWithAssociatedMarkup(psiClass)) {
                        return NavigableLineMarkerInfo.create(element, new NavigatablePsiElement[]{psiClass}, Constants.TOJAVAREF);
                    }
                }
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return HTMLLanguage.INSTANCE;
    }
}
