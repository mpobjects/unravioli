/*
 * Copyright 2017, MP Objects, http://www.mp-objects.com
 */
package com.mpobjects.labs.unravioli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

/**
 *
 */
public class ModuleScanner extends DirectoryWalker<SourceEntry> {

	public static final IOFileFilter DIR_FILTER;

	public static final IOFileFilter FILE_FILTER;

	private static final Logger LOG = LoggerFactory.getLogger(ModuleScanner.class);

	static {
		DIR_FILTER = FileFilterUtils.makeSVNAware(null);
		FILE_FILTER = FileFilterUtils.suffixFileFilter(".java");
	}

	public ModuleScanner() {
		super(DIR_FILTER, FILE_FILTER, -1);
	}

	public Module scan(File aBaseDir) throws IOException {
		LOG.info("Scanning directory {}", aBaseDir);
		Module module = new Module(aBaseDir);
		List<SourceEntry> result = new ArrayList<>();
		walk(module.getFile(), result);
		for (SourceEntry entry : result) {
			module.addEntry(entry);
		}
		return module;
	}

	@Override
	protected void handleFile(File aFile, int aDepth, Collection<SourceEntry> aResults) throws IOException {
		LOG.debug("Processing file {}", aFile);
		SourceEntry entry = new SourceEntry(aFile);
		parse(entry);
		aResults.add(entry);
		LOG.trace("{}", entry);
	}

	protected void parse(SourceEntry aEntry) throws IOException {
		CompilationUnit cu = JavaParser.parse(aEntry.getFile());
		for (ImportDeclaration id : cu.getImports()) {
			processImport(aEntry, id);
		}
		for (TypeDeclaration<?> tp : cu.getTypes()) {
			processType(aEntry, tp, cu.getPackageDeclaration().get());
		}
	}

	protected void processImport(SourceEntry aEntry, ImportDeclaration aImport) {
		if (aImport.isStatic()) {
			if (aImport.isAsterisk()) {
				// full name is the class
				aEntry.getImports().add(aImport.getName().asString());
			} else {
				// qualifier is the class
				aEntry.getImports().add(aImport.getName().getQualifier().get().asString());
			}
		} else if (aImport.isAsterisk()) {
			// TODO: how to handle wildcards?
			LOG.warn("FIXME: wildcard import in {}: {}", aEntry, aImport);
		} else {
			aEntry.getImports().add(aImport.getName().asString());
		}
	}

	protected void processType(SourceEntry aEntry, TypeDeclaration<?> aType, PackageDeclaration aPackageDecl) {
		if (aPackageDecl != null) {
			aEntry.getExports().add(String.format("%s.%s", aPackageDecl.getName().toString(), aType.getName().toString()));
		} else {
			aEntry.getExports().add(aType.getName().toString());
		}
	}
}
