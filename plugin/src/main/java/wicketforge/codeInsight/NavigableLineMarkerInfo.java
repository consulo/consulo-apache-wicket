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
package wicketforge.codeInsight;

import com.intellij.java.impl.codeInsight.daemon.impl.GutterIconTooltipHelper;
import consulo.codeEditor.markup.GutterIconRenderer;
import consulo.language.editor.Pass;
import consulo.language.editor.gutter.LineMarkerInfo;
import consulo.language.editor.ui.DefaultPsiElementCellRenderer;
import consulo.language.editor.ui.PsiElementListNavigator;
import consulo.language.psi.NavigatablePsiElement;
import consulo.language.psi.PsiElement;
import consulo.ui.image.Image;

import jakarta.annotation.Nonnull;

/**
 *
 */
class NavigableLineMarkerInfo
{
	private NavigableLineMarkerInfo()
	{
	}

	public static LineMarkerInfo create(@Nonnull PsiElement element, @Nonnull final NavigatablePsiElement[] targets, @Nonnull Image icon)
	{
		return new LineMarkerInfo(element, element.getTextRange(), icon, Pass.UPDATE_ALL,
				psiElement -> GutterIconTooltipHelper.composeText(targets, "", "{0}"),
				(e, elt) -> PsiElementListNavigator.openTargets(e, targets, "Select Target", null, new DefaultPsiElementCellRenderer()),
				GutterIconRenderer.Alignment.LEFT);
	}
}
