/*
 * Copyright 2017, MP Objects, http://www.mp-objects.com
 */
package com.mpobjects.labs.unravioli;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class Xref {

	protected Map<Module, Map<String, XrefEntry>> refs;

	protected Module source;

	public Xref(Module aSource) {
		source = aSource;
		refs = new LinkedHashMap<>();
	}

	public void addReference(SourceEntry aSource, Module aTarget, String aType) {
		if (aTarget == null || aSource == null || aTarget == source) {
			return;
		}
		SourceEntry target = aTarget.getEntry(aType);
		if (target == null) {
			return;
		}

		Map<String, XrefEntry> entries = refs.get(aTarget);
		if (entries == null) {
			entries = new LinkedHashMap<>();
			refs.put(aTarget, entries);
		}
		XrefEntry entry = entries.get(aType);
		if (entry == null) {
			entry = new XrefEntry(target, aType);
			entries.put(aType, entry);
		}
		entry.getSources().add(aSource);
	}

	public Set<Module> getModuleRefs() {
		return refs.keySet();
	}

	public Map<Module, Map<String, XrefEntry>> getRefs() {
		return refs;
	}

	public Module getSource() {
		return source;
	}

}
