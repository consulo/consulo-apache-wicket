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
package wicketforge.facet;

import org.jetbrains.annotations.Nullable;
import consulo.apache.wicket.module.extension.WicketModuleExtension;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtil;
import wicketforge.Constants;
import wicketforge.search.WicketSearchScope;
import wicketforge.util.WicketFileUtil;

/**
 * WicketForgeFacet
 */
public class WicketForgeFacet
{

	/**
	 * Returns true if the passed Module contains the Wicket's Component class.
	 * This method works with Wicket version 1.3 and higher.
	 *
	 * @param module Module
	 * @return boolean
	 */
	public static boolean isLibraryPresent(@Nullable Module module)
	{
		if(module == null)
		{
			return false;
		}
		JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(module.getProject());
		return psiFacade.findClass(Constants.WICKET_COMPONENT, WicketSearchScope.classInModuleWithDependenciesAndLibraries(module)) != null;
	}

	/**
	 * @param element
	 * @return true if element has WicketForgeFacet or is from library
	 */
	public static boolean hasFacetOrIsFromLibrary(@Nullable PsiElement element)
	{
		if(element != null)
		{
			VirtualFile vf = PsiUtil.getVirtualFile(element);
			if(vf != null)
			{
				Project project = element.getProject();
				Module module = ModuleUtil.findModuleForFile(vf, project);
				// if we got a module -> check if WicketForgeFacet available
				if(module != null)
				{
					return ModuleUtilCore.getExtension(module, WicketModuleExtension.class) != null;
				}
				// else check if file from lib
				return WicketFileUtil.isInLibrary(vf, project);
			}
		}
		return false;
	}
}

