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

import consulo.apache.wicket.module.extension.WicketModuleExtension;
import consulo.content.base.WebResourcesFolderTypeProvider;
import consulo.language.content.LanguageContentFolderScopes;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.scope.GlobalSearchScopesCore;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.content.ModuleRootManager;
import consulo.virtualFileSystem.VirtualFile;

import javax.annotation.Nonnull;

public final class WicketSearchScope
{
	private WicketSearchScope()
	{
	}

	@Nonnull
	public static GlobalSearchScope resourcesInModuleWithDependenciesAndLibraries(@Nonnull Module module)
	{
		GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, true);
		// add all additional resource paths
		WicketModuleExtension facet = ModuleUtilCore.getExtension(module, WicketModuleExtension.class);
		if(facet != null)
		{
			VirtualFile[] contentFolderFiles = ModuleRootManager.getInstance(module).getContentFolderFiles(LanguageContentFolderScopes.of(WebResourcesFolderTypeProvider.getInstance()));
			for(VirtualFile virtualFile : contentFolderFiles)
			{
				scope = scope.uniteWith(GlobalSearchScopesCore.directoryScope(module.getProject(), virtualFile, true));
			}
		}
		return scope;
	}

	@Nonnull
	public static GlobalSearchScope classInModuleWithDependenciesAndLibraries(@Nonnull Module module)
	{
		return GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, true);
	}
}
