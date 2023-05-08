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
package wicketforge.intention;

import com.intellij.java.language.psi.PsiClass;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.intention.IntentionMetaData;
import wicketforge.search.MarkupIndex;
import wicketforge.templates.WicketTemplates;
import wicketforge.util.WicketFilenameUtil;
import wicketforge.util.WicketPsiUtil;

import javax.annotation.Nonnull;

/**
 * AddMarkupBorderIntention
 */
@ExtensionImpl
@IntentionMetaData(ignoreId = "wicket.AddMarkupBorderIntention", categories = {"Java", "Apache Wicket"}, fileExtensions = "java")
public class AddMarkupBorderIntention extends AddMarkupIntention {

    @Override
    @Nonnull
    public String getText() {
        return "Create Markup Border";
    }

    @Override
    protected boolean hasResourceFile(@Nonnull PsiClass psiClass) {
        return MarkupIndex.getBaseFile(psiClass) != null;
    }

    @Nonnull
    @Override
    protected String getResourceFileName(@Nonnull PsiClass psiClass) {
        return WicketFilenameUtil.getMarkupFilename(psiClass);
    }

    @Nonnull
    @Override
    protected String getTemplateName() {
        return WicketTemplates.WICKET_BORDER_HTML;
    }

    @Override
    protected boolean isApplicableForClass(@Nonnull PsiClass psiClass) {
        return WicketPsiUtil.isWicketBorder(psiClass);
    }
}
