package org.eclipse.xpect.ui.util;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.xpect.XpectFile;
import org.eclipse.xpect.XpectTest;
import org.eclipse.xpect.registry.ILanguageInfo;
import org.eclipse.xpect.setup.XtextInjectorSetup;
import org.eclipse.xpect.ui.editor.AssimilatingModule;
import org.eclipse.xpect.ui.internal.XpectActivator;
import org.eclipse.xpect.ui.util.ContentTypeUtil.XpectContentType;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.shared.Access;
import org.eclipse.xtext.ui.util.JdtClasspathUriResolver;
import org.eclipse.xtext.util.Pair;

import com.google.inject.Injector;
import com.google.inject.Module;

public class XtextInjectorSetupUtil {
	private static XpectFile load(IFile file) {
		Injector injector = XpectActivator.getInstance().getInjector(XpectActivator.ORG_ECLIPSE_XPECT_XPECT);
		XtextResourceSet rs = new XtextResourceSet();
		IJavaProject javaProject = JavaCore.create(file.getProject());
		if (javaProject != null && javaProject.exists()) {
			rs.setClasspathURIContext(javaProject);
			rs.setClasspathUriResolver(new JdtClasspathUriResolver());
		}
		URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
		Resource resource = injector.getInstance(IResourceFactory.class).createResource(uri);
		rs.getResources().add(resource);
		try {
			resource.load(Collections.emptyMap());
			for (EObject obj : resource.getContents())
				if (obj instanceof XpectFile)
					return (XpectFile) obj;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	private static Class<? extends Module> getWorkbenchModule(XpectFile xpectFile) {
		if (xpectFile != null) {
			XpectTest xpectTest = xpectFile.getTest();
			if (xpectTest != null && !xpectTest.eIsProxy()) {
				Class<?> testClass = TypeUiUtil.getWorkspaceTypeFromHostPlatform(xpectFile.getTest().getTestClass());
				if (testClass != null) {
					XtextInjectorSetup xtextInjectorSetup = testClass.getAnnotation(XtextInjectorSetup.class);
					if (xtextInjectorSetup != null)
						return xtextInjectorSetup.workbenchModule();
				}
			}
		}
		return null;
	}

	public static Injector getWorkbenchInjector(ILanguageInfo lang, IFile file) {
		Injector defaultInjector = lang.getInjector();
		Module assimilatingModule = defaultInjector.getInstance(AssimilatingModule.class);
		XpectFile xpectFile = XtextInjectorSetupUtil.load(file);
		Class<? extends Module> workbenchModule = XtextInjectorSetupUtil.getWorkbenchModule(xpectFile);
		if (workbenchModule != null)
			return lang.getInjector(assimilatingModule, defaultInjector.getInstance(workbenchModule));
		else
			return lang.getInjector(assimilatingModule);
	}

	public static Injector getWorkspaceInjector(URI uri) {
		for (Pair<IStorage, IProject> storage : Access.getIStorage2UriMapper().get().getStorages(uri))
			if (storage.getFirst() instanceof IFile) {
				IFile file = (IFile) storage.getFirst();
				XpectContentType contentType = new ContentTypeUtil().getContentType(file);
				if (contentType == XpectContentType.XPECT) {
					ILanguageInfo info = ILanguageInfo.Registry.INSTANCE.getLanguageByFileExtension(uri.fileExtension());
					if (info != null)
						return XtextInjectorSetupUtil.getWorkbenchInjector(info, file);
					else
						return null;
				}
			}
		return null;
	}

}