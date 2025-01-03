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

import consulo.annotation.component.ExtensionImpl;
import consulo.apache.wicket.module.extension.WicketModuleExtension;
import consulo.content.base.WebResourcesFolderTypeProvider;
import consulo.language.content.LanguageContentFolderScopes;
import consulo.language.psi.stub.IndexableSetContributor;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.module.content.ModuleRootManager;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ExtensionImpl
public class AdditionalResourcePathsIndexProvider extends IndexableSetContributor
{
	@Nonnull
	@Override
	public Set<VirtualFile> getAdditionalProjectRootsToIndex(@Nullable Project project)
	{
		if(project != null)
		{
			Set<VirtualFile> files = new HashSet<>();
			for(Module module : ModuleManager.getInstance(project).getModules())
			{
				WicketModuleExtension facet = ModuleUtilCore.getExtension(module, WicketModuleExtension.class);
				if(facet != null)
				{
					VirtualFile[] contentFolderFiles = ModuleRootManager.getInstance(module).getContentFolderFiles(LanguageContentFolderScopes.of(WebResourcesFolderTypeProvider.getInstance()));
					for(VirtualFile file : contentFolderFiles)
					{
						files.add(file);
					}
				}
			}
			return files;
		}
		return Collections.emptySet();
	}

	@Override
	public Set<VirtualFile> getAdditionalRootsToIndex()
	{
		return Collections.emptySet();
	}
}
