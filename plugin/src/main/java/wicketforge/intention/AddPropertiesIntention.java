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
import wicketforge.Constants;
import wicketforge.search.PropertiesIndex;
import wicketforge.templates.WicketTemplates;
import wicketforge.util.WicketFilenameUtil;
import wicketforge.util.WicketPsiUtil;

import jakarta.annotation.Nonnull;

/**
 * Class level intention used to create properties files for Wicket page and panel components.
 */
@ExtensionImpl
@IntentionMetaData(ignoreId = "wicket.AddPropertiesIntention", categories = {"Java", "Apache Wicket"}, fileExtensions = "java")
public class AddPropertiesIntention extends AddMarkupIntention {

    @Override
    @Nonnull
    public String getText() {
        return "Create Properties File";
    }

    @Override
    protected boolean hasResourceFile(@Nonnull PsiClass psiClass) {
        return PropertiesIndex.getBaseFile(psiClass) != null;
    }

    @Nonnull
    @Override
    protected String getResourceFileName(@Nonnull PsiClass psiClass) {
        return WicketFilenameUtil.getPropertiesFilename(psiClass, Constants.PropertiesType.PROPERTIES);
    }

    @Nonnull
    @Override
    protected String getTemplateName() {
        return WicketTemplates.WICKET_PROPERTIES;
    }

    @Override
    protected boolean isApplicableForClass(@Nonnull PsiClass psiClass) {
        return WicketPsiUtil.isWicketComponentWithAssociatedMarkup(psiClass);
    }
}