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
package wicketforge.util;

import com.intellij.java.language.psi.JavaPsiFacade;
import com.intellij.java.language.psi.PsiClass;
import com.intellij.java.language.psi.PsiMethod;
import consulo.language.psi.PsiElement;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import wicketforge.search.WicketSearchScope;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
// TODO think about to remove WicketVersion and implement getNS and getXmlPropertiesFileExtension in other way...
enum WicketVersion {
    WICKET_1_3("http://wicket.apache.org/dtds.data/wicket-xhtml1.3-strict.dtd", FilenameConstants.EXT_XML),
    WICKET_1_4("http://wicket.apache.org/dtds.data/wicket-xhtml1.4-strict.dtd", FilenameConstants.EXT_XML),
    WICKET_1_5("http://wicket.apache.org", FilenameConstants.EXT_PROPERTIES_XML); // schema included in wickets jars (see issue 143)

    private String ns;
    private String xmlPropertiesFileExtension;

    private WicketVersion(@Nonnull String ns, @Nonnull String xmlPropertiesFileExtension) {
        this.ns = ns;
        this.xmlPropertiesFileExtension = xmlPropertiesFileExtension;
    }

    @Nonnull
    public String getXmlPropertiesFileExtension() {
        return xmlPropertiesFileExtension;
    }

    @Nonnull
    public String getNS() {
        return ns;
    }

    @Nonnull
    public static WicketVersion getVersion(@Nonnull PsiElement element) {
        Module module = ModuleUtilCore.findModuleForPsiElement(element);
        if (module == null) {
            return WICKET_1_5;
        }
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(module.getProject());

        PsiClass c = psiFacade.findClass("org.apache.wicket.Component", WicketSearchScope.classInModuleWithDependenciesAndLibraries(module));
        if (c == null) {
            return WICKET_1_5;
        }

        List<String> methods = new ArrayList<String>();
        for (PsiMethod m : c.getMethods()) {
            methods.add(m.getName());
        }
        if (methods.contains("getMarkup")) {
            return WICKET_1_5;
        } else if (methods.contains("getDefaultModel")) {
            return WICKET_1_4;
        }
        return WICKET_1_3;

    }
}
