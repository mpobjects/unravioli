/*
 * Copyright 2017, MP Objects, http://www.mp-objects.com
 */
package com.mpobjects.labs.unravioli;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class XrefEntry {

	protected Set<SourceEntry> sources;

	protected SourceEntry target;

	protected String type;

	public XrefEntry(SourceEntry aTarget, String aType) {
		type = aType;
		target = aTarget;
		sources = new HashSet<>();
	}

	public Set<SourceEntry> getSources() {
		return sources;
	}

	public SourceEntry getTarget() {
		return target;
	}

	public String getType() {
		return type;
	}

}
