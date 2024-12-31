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
package wicketforge.action.ui;

import com.intellij.java.language.psi.JavaPsiFacade;
import com.intellij.java.language.psi.PsiClass;
import consulo.language.psi.PsiDirectory;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.project.Project;
import wicketforge.Constants;

import jakarta.annotation.Nonnull;

/**
 * CreatePageDialog
 */
public class CreatePageDialog extends AbstractCreateDialog {

    public CreatePageDialog(@Nonnull Project project, @Nonnull ActionRunnable actionRunnable, @Nonnull String title, @Nonnull PsiDirectory directory) {
        super(project, actionRunnable, title, directory);
    }

    @Override
    protected PsiClass getDefaultClass(@Nonnull Project project) {
        return JavaPsiFacade.getInstance(project).findClass(Constants.WICKET_PAGE, GlobalSearchScope.allScope(project));
    }
}
