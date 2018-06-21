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
package wicketforge.search;

import org.jetbrains.annotations.NotNull;
import consulo.apache.wicket.module.extension.WicketModuleExtension;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopes;
import consulo.roots.ContentFolderScopes;
import consulo.roots.impl.WebResourcesFolderTypeProvider;

public final class WicketSearchScope
{
	private WicketSearchScope()
	{
	}

	@NotNull
	public static GlobalSearchScope resourcesInModuleWithDependenciesAndLibraries(@NotNull Module module)
	{
		GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, true);
		// add all additional resource paths
		WicketModuleExtension facet = ModuleUtilCore.getExtension(module, WicketModuleExtension.class);
		if(facet != null)
		{
			VirtualFile[] contentFolderFiles = ModuleRootManager.getInstance(module).getContentFolderFiles(ContentFolderScopes.of(WebResourcesFolderTypeProvider.getInstance()));
			for(VirtualFile virtualFile : contentFolderFiles)
			{
				scope = scope.uniteWith(GlobalSearchScopes.directoryScope(module.getProject(), virtualFile, true));
			}
		}
		return scope;
	}

	@NotNull
	public static GlobalSearchScope classInModuleWithDependenciesAndLibraries(@NotNull Module module)
	{
		return GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, true);
	}
}
