/*
 * Copyright 2017, MP Objects, http://www.mp-objects.com
 */
package com.mpobjects.labs.unravioli;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 */
public class SourceEntry {

	public Set<String> exports;

	public File file;

	public Set<String> imports;

	public Module module;

	public SourceEntry(File aName) {
		file = aName;
		exports = new HashSet<String>();
		imports = new HashSet<String>();
	}

	public Set<String> getExports() {
		return exports;
	}

	public File getFile() {
		return file;
	}

	public Set<String> getImports() {
		return imports;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module aModule) {
		module = aModule;
	}

	@Override
	public String toString() {
		ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		sb.append("file", file);
		sb.append("exports", exports);
		sb.append("imports", imports);
		return sb.toString();
	}

}
