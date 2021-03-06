/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.xpect.ui.junit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.junit.model.TestElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.swt.graphics.Image;

/**
 * @author Moritz Eysholdt - Initial contribution and API
 */
@SuppressWarnings("restriction")
public class FailureCompareEditorInput extends CompareEditorInput {

	private ITestElement testElement;

	public FailureCompareEditorInput(ITestElement testElement) {
		super(new CompareConfiguration());
		this.testElement = testElement;
	}

	protected Object prepareInput(IProgressMonitor pm) {
		TestElement te = (TestElement) testElement;
		CompareItem ancestor = new CompareItem("Common", "contents");
		CompareItem left = new CompareItem("Left", te.getExpected());
		CompareItem right = new CompareItem("Right", te.getActual());
		return new DiffNode(null, Differencer.CHANGE, ancestor, left, right);
	}

	class CompareItem implements IStreamContentAccessor, ITypedElement, IModificationDate {
		private String contents, name;

		CompareItem(String name, String contents) {
			this.name = name;
			this.contents = contents;
		}

		public InputStream getContents() throws CoreException {
			return new ByteArrayInputStream(contents.getBytes());
		}

		public Image getImage() {
			return null;
		}

		public long getModificationDate() {
			return 0;
		}

		public String getName() {
			return name;
		}

		public String getString() {
			return contents;
		}

		public String getType() {
			return ITypedElement.TEXT_TYPE;
		}
	}

}
