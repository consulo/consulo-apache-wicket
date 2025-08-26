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
package wicketforge.action;

import consulo.language.psi.PsiDirectory;
import consulo.language.psi.PsiElement;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import wicketforge.action.ui.CreatePageDialog;
import wicketforge.templates.WicketTemplates;

import java.util.function.Consumer;

/**
 * CreateWicketPageAction
 */
public class CreateWicketPageAction extends CreateWicketAction {

    protected CreateWicketPageAction() {
        super(LocalizeValue.localizeTODO("Wicket Page"), LocalizeValue.localizeTODO("Create a new Wicket Page"));
    }

    @Override
    protected void invokeDialog(Project project, PsiDirectory directory, Consumer<PsiElement[]> consumer) {
        ActionRunnableImpl actionRunnable = new ActionRunnableImpl(project, directory, WicketTemplates.WICKET_PAGE_HTML);
        CreatePageDialog dialog = new CreatePageDialog(project, actionRunnable, getCommandName().get(), directory);
        dialog.showAsync().doWhenDone(() -> consumer.accept(actionRunnable.getCreatedElements()));
    }

    @Override
    protected LocalizeValue getErrorTitle() {
        return LocalizeValue.localizeTODO("Cannot create Wicket Page");
    }

    @Override
    protected LocalizeValue getCommandName() {
        return LocalizeValue.localizeTODO("Create Wicket Page");
    }

    @Override
    protected LocalizeValue getActionName(PsiDirectory directory, String newName) {
        return LocalizeValue.localizeTODO("Creating Wicket Page " + newName);
    }
}
