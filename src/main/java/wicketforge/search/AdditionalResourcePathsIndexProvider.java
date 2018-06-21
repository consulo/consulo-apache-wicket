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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.apache.wicket.module.extension.WicketModuleExtension;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.indexing.IndexableSetContributor;
import consulo.roots.ContentFolderScopes;
import consulo.roots.impl.WebResourcesFolderTypeProvider;

public class AdditionalResourcePathsIndexProvider extends IndexableSetContributor
{
	@Nonnull
	@Override
	public Set<VirtualFile> getAdditionalProjectRootsToIndex(@Nullable Project project)
	{
		if(project != null)
		{
			Set<VirtualFile> files = new HashSet<VirtualFile>();
			for(Module module : ModuleManager.getInstance(project).getModules())
			{
				WicketModuleExtension facet = ModuleUtilCore.getExtension(module, WicketModuleExtension.class);
				if(facet != null)
				{
					VirtualFile[] contentFolderFiles = ModuleRootManager.getInstance(module).getContentFolderFiles(ContentFolderScopes.of(WebResourcesFolderTypeProvider.getInstance()));
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
