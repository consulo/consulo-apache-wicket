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
import consulo.project.Project;
import wicketforge.action.ui.CreatePanelDialog;
import wicketforge.templates.WicketTemplates;

import java.util.function.Consumer;

/**
 * CreateWicketPanelAction
 */
public class CreateWicketPanelAction extends CreateWicketAction {

    public CreateWicketPanelAction() {
        super("Wicket Panel", "Create a new Wicket Panel");
    }

    @Override
    protected void invokeDialog(Project project, PsiDirectory directory, Consumer<PsiElement[]> consumer) {
        ActionRunnableImpl actionRunnable = new ActionRunnableImpl(project, directory, WicketTemplates.WICKET_PANEL_HTML);
        CreatePanelDialog dialog = new CreatePanelDialog(project, actionRunnable, getCommandName(), directory);
        dialog.show();
        consumer.accept(actionRunnable.getCreatedElements());
    }

    @Override
    protected String getErrorTitle() {
        return "Cannot create Wicket Panel";
    }

    @Override
    protected String getCommandName() {
        return "Create Wicket Panel";
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName) {
        return "Creating Wicket Panel " + newName;
    }
}
