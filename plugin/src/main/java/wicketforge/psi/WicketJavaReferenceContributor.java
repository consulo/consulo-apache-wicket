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

import com.intellij.java.language.JavaLanguage;
import com.intellij.java.language.patterns.PsiJavaPatterns;
import com.intellij.java.language.psi.PsiAnonymousClass;
import com.intellij.java.language.psi.PsiExpressionList;
import com.intellij.java.language.psi.PsiLiteralExpression;
import com.intellij.java.language.psi.PsiNewExpression;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.pattern.ElementPattern;
import consulo.language.pattern.StandardPatterns;
import consulo.language.psi.PsiReferenceContributor;
import consulo.language.psi.PsiReferenceRegistrar;
import wicketforge.psi.references.ClassWicketIdReferenceProvider;

import javax.annotation.Nonnull;

@ExtensionImpl
public class WicketJavaReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {

        {// java -> new Component("..." ...)
            ElementPattern<PsiLiteralExpression> pattern = StandardPatterns.or(
                    //
                    PsiJavaPatterns.psiElement(PsiLiteralExpression.class).withParent(PsiExpressionList.class).withSuperParent(2, PsiNewExpression.class),
                    // for Anonymous create like Link's...
                    PsiJavaPatterns.psiElement(PsiLiteralExpression.class).withParent(PsiExpressionList.class).withSuperParent(2, PsiAnonymousClass.class).withSuperParent(3, PsiNewExpression.class)
            );
            registrar.registerReferenceProvider(pattern, new ClassWicketIdReferenceProvider());
        }
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return JavaLanguage.INSTANCE;
    }
}
