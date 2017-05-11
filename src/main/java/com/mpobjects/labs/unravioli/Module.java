/*
 * Copyright 2017, MP Objects, http://www.mp-objects.com
 */
package com.mpobjects.labs.unravioli;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 */
public class Module {

	protected Set<SourceEntry> entries;

	protected File file;

	protected Map<String, SourceEntry> types;

	public Module(File aName) {
		file = aName;
		entries = new HashSet<>();
		types = new HashMap<>();
	}

	public void addEntry(SourceEntry aEntry) {
		aEntry.setModule(this);
		entries.add(aEntry);
		for (String type : aEntry.getExports()) {
			types.put(type, aEntry);
		}
	}

	public Set<SourceEntry> getEntries() {
		return entries;
	}

	public SourceEntry getEntry(String aType) {
		return types.get(aType);
	}

	public File getFile() {
		return file;
	}

	public Map<String, SourceEntry> getTypes() {
		return types;
	}

	@Override
	public String toString() {
		ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		sb.append("file", file);
		sb.append("entries", entries);
		return sb.toString();
	}
}
