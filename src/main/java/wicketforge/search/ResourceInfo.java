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
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.apache.wicket.module.extension.WicketModuleExtension;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.lang.properties.PropertiesFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.SmartList;
import com.intellij.util.indexing.FileContent;
import consulo.roots.ContentFolderScopes;
import consulo.roots.impl.WebResourcesFolderTypeProvider;
import wicketforge.util.FilenameConstants;
import wicketforge.util.WicketFilenameUtil;

final class ResourceInfo
{
	@Nonnull
	public final String qualifiedName;
	@Nullable
	public final String locale;

	private ResourceInfo(@Nullable String packageName, @Nonnull String className, @Nullable String locale)
	{
		this.qualifiedName = packageName == null ? className : packageName + '.' + className; // currently we only need full qualified name
		this.locale = locale;
	}

	/**
	 * get ResourceInfo from PsiFile
	 */
	@Nullable
	public static ResourceInfo from(@Nonnull PsiFile file)
	{
		FileType fileType = file.getFileType();
		if(HtmlFileType.INSTANCE.equals(fileType))
		{
			return fromMarkup(file.getVirtualFile(), file.getProject(), file.getText());
		}
		else if(PropertiesFileType.INSTANCE.equals(fileType) || XmlFileType.INSTANCE.equals(fileType))
		{
			return fromProperties(file.getVirtualFile(), file.getProject());
		}
		return null;
	}

	/**
	 * get ResourceInfo from FileContent
	 */
	@Nullable
	public static ResourceInfo from(@Nonnull FileContent fileContent)
	{
		FileType fileType = fileContent.getFileType();
		if(HtmlFileType.INSTANCE.equals(fileType))
		{
			return fromMarkup(fileContent.getFile(), fileContent.getProject(), fileContent.getContentAsText().toString());
		}
		else if(PropertiesFileType.INSTANCE.equals(fileType) || XmlFileType.INSTANCE.equals(fileType))
		{
			return fromProperties(fileContent.getFile(), fileContent.getProject());
		}
		return null;
	}

	@Nullable
	private static ResourceInfo fromMarkup(@Nullable VirtualFile file, @Nonnull Project project, @Nullable String content)
	{
		if(file == null)
		{
			return null;
		}
		// get classname from wicketforge-bind
		String className = SearchUtils.getBoundClassName(content);
		if(className != null)
		{
			// extract locale
			String locale = WicketFilenameUtil.extractLocale(WicketFilenameUtil.removeExtension(file.getName(),
					FilenameConstants.MARKUP_EXTENSIONS));
			int index = className.lastIndexOf('.');
			return new ResourceInfo(index >= 0 ? className.substring(0, index) : null, index >= 0 ? className.substring(index + 1) : className,
					locale);
		}
		return fromResource(file, project, FilenameConstants.MARKUP_EXTENSIONS);
	}

	@Nullable
	private static ResourceInfo fromProperties(@Nullable VirtualFile file, @Nonnull Project project)
	{
		if(file == null)
		{
			return null;
		}
		return fromResource(file, project, FilenameConstants.PROPERTIES_EXTENSIONS);
	}

	@Nullable
	private static ResourceInfo fromResource(@Nonnull VirtualFile file, @Nonnull Project project, @Nonnull String[] fileExtensions)
	{
		VirtualFile dir = file.getParent();
		if(dir == null || !dir.isDirectory())
		{
			return null;
		}

		String packageName = getPackageNameFromAdditionalResourcePaths(file, dir, project);
		if(packageName == null)
		{
			packageName = ProjectRootManager.getInstance(project).getFileIndex().getPackageNameByDirectory(dir);
		}

		// extract className from filename -> remove extensions
		String className = WicketFilenameUtil.removeExtension(file.getName(), fileExtensions);
		// extract locale
		String locale = WicketFilenameUtil.extractLocale(className);
		className = StringUtil.replace(WicketFilenameUtil.extractBasename(className), "$", ".");
		return new ResourceInfo(packageName, className, locale);
	}

	@Nullable
	private static String getPackageNameFromAdditionalResourcePaths(@Nonnull VirtualFile file, @Nonnull VirtualFile dir, @Nonnull Project project)
	{
		List<Module> modules = new SmartList<Module>();
		Module module = ModuleUtil.findModuleForFile(file, project);
		if(module != null)
		{
			// if we have a module -> only get resourcepaths from this one
			modules.add(module);
		}
		else
		{
			// else scan all modules
			Collections.addAll(modules, ModuleManager.getInstance(project).getModules());
		}
		for(Module module1 : modules)
		{
			WicketModuleExtension facet = ModuleUtilCore.getExtension(module1, WicketModuleExtension.class);
			if(facet != null)
			{
				VirtualFile[] contentFolderFiles = ModuleRootManager.getInstance(module1).getContentFolderFiles(ContentFolderScopes.of
						(WebResourcesFolderTypeProvider.getInstance()));
				for(VirtualFile virtualFile : contentFolderFiles)
				{
					String packageName = VfsUtil.getRelativePath(dir, virtualFile, '.');
					if(packageName != null)
					{
						return packageName;
					}
				}
			}
		}
		return null;
	}
}
