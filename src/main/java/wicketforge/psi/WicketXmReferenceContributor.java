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
package wicketforge.psi;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.psi.PsiReferenceContributor;
import consulo.language.psi.PsiReferenceRegistrar;
import consulo.xml.lang.xml.XMLLanguage;
import consulo.xml.patterns.XmlAttributeValuePattern;
import consulo.xml.patterns.XmlPatterns;
import wicketforge.Constants;
import wicketforge.psi.references.MarkupWicketIdReferenceProvider;

import javax.annotation.Nonnull;

@ExtensionImpl
public class WicketXmReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {

        {// html -> wicket:id
            XmlAttributeValuePattern pattern = XmlPatterns.xmlAttributeValue(XmlPatterns.xmlAttribute().withName(Constants.WICKET_ID));
            registrar.registerReferenceProvider(pattern, new MarkupWicketIdReferenceProvider());
        }
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return XMLLanguage.INSTANCE;
    }
}
