/*
 * Copyright 2013 The WicketForge-Team
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
package wicketforge.refactoring;

import com.intellij.java.language.psi.PsiClass;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.psi.PsiFile;
import wicketforge.search.PropertiesIndex;
import wicketforge.util.FilenameConstants;

import jakarta.annotation.Nonnull;

@ExtensionImpl
public class PropertiesRenamerFactory extends ResourceRenamerFactory {
    public PropertiesRenamerFactory() {
        super("Properties", FilenameConstants.PROPERTIES_EXTENSIONS);
    }

    @Nonnull
    @Override
    protected PsiFile[] getAllFiles(@Nonnull PsiClass psiClass) {
        return PropertiesIndex.getAllFiles(psiClass);
    }
}
