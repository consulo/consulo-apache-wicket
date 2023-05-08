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

import com.intellij.java.language.psi.JavaDirectoryService;
import consulo.ide.IdeView;
import consulo.language.editor.LangDataKeys;
import consulo.language.psi.PsiDirectory;
import consulo.module.Module;
import consulo.module.content.ProjectFileIndex;
import consulo.module.content.ProjectRootManager;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DefaultActionGroup;
import wicketforge.Constants;
import wicketforge.facet.WicketForgeFacet;

/**
 * WicketActionGroup
 */
public class WicketActionGroup extends DefaultActionGroup
{
    public WicketActionGroup() {
        super("WicketForge", true);
        getTemplatePresentation().setDescription("Wicket");
        getTemplatePresentation().setIcon(Constants.WICKET_ICON);
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setVisible(isUnderSourceRoots(e));
    }

    private static boolean isUnderSourceRoots(final AnActionEvent e) {
        final IdeView view = e.getData(IdeView.KEY);
        if (view != null) {
            final Module module = e.getData(LangDataKeys.MODULE);
            // let user create page/panel when we have a wicket-lib (so we can detect new facet)
            if (module != null && WicketForgeFacet.isLibraryPresent(module)) {
                ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(module.getProject()).getFileIndex();
                for (PsiDirectory dir : view.getDirectories()) {
                    if (projectFileIndex.isInSourceContent(dir.getVirtualFile()) && JavaDirectoryService.getInstance().getPackage(dir) != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
