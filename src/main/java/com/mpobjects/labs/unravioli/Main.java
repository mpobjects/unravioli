/*
 * Copyright 2017, MP Objects, http://www.mp-objects.com
 */
package com.mpobjects.labs.unravioli;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Main {
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	private static final String MODULE_PATTERN = "(?:.*)[\\\\/]([^\\\\/]+[\\\\/][^\\\\/]+)[\\\\/]src[\\\\/]java[\\\\/]?(.*)";

	public Main() {
	}

	public static void main(String[] args) throws Exception {

		ModuleScanner scan = new ModuleScanner();
		List<Module> modules = new ArrayList<>();
		for (String arg : args) {
			Module module = scan.scan(new File(arg));
			LOG.info("Module {} with {} entries", module.getFile(), module.getEntries().size());
			modules.add(module);
		}

		Map<String, Module> sources = new HashMap<>();
		for (Module mod : modules) {
			for (SourceEntry src : mod.getEntries()) {
				for (String typ : src.getExports()) {
					sources.put(typ, mod);
				}
			}
		}

		Map<Module, Xref> xrefs = new LinkedHashMap<>();
		for (Module mod : modules) {
			Xref xref = new Xref(mod);
			xrefs.put(mod, xref);
			for (SourceEntry src : mod.getEntries()) {
				for (String typ : src.getImports()) {
					xref.addReference(src, sources.get(typ), typ);
				}
			}
		}

		for (Xref xref : xrefs.values()) {
			LOG.info("");
			LOG.info("{}", xref.getSource().getFile().toString().replaceFirst(MODULE_PATTERN, "$1"));
			for (Module mod : xref.getModuleRefs()) {
				LOG.info("+ {}", mod.getFile().toString().replaceFirst(MODULE_PATTERN, "$1"));
			}
		}

		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = factory.createXMLStreamWriter(new FileWriter("result.xml"));

		writer.writeStartDocument();
		writer.writeStartElement("modules");

		for (Xref xref : xrefs.values()) {
			writeXref(xrefs, writer, xref);
		}

		writer.writeEndElement();
		writer.writeEndDocument();

		writer.flush();
		writer.close();
	}

	private static int countRefs(Collection<XrefEntry> aValues) {
		int res = 0;
		for (XrefEntry entry : aValues) {
			res += entry.getSources().size();
		}
		return res;
	}

	private static String formatModuleName(Module aSource) {
		return aSource.getFile().toString().replaceFirst(MODULE_PATTERN, "$1");
	}

	private static String formatSourceFile(SourceEntry aSrc) {
		return aSrc.getFile().toString().replaceFirst(MODULE_PATTERN, "$2");
	}

	private static void writeXref(Map<Module, Xref> aXrefs, XMLStreamWriter aWriter, Xref aXref) throws Exception {
		aWriter.writeStartElement("module");
		aWriter.writeAttribute("name", formatModuleName(aXref.getSource()));
		aWriter.writeAttribute("file", aXref.getSource().getFile().toString());

		for (Entry<Module, Map<String, XrefEntry>> entry : aXref.getRefs().entrySet()) {
			writeXrefImport(aWriter, aXrefs, aXref, entry);
		}

		aWriter.writeEndElement();
	}

	private static void writeXrefImport(XMLStreamWriter aWriter, Map<Module, Xref> aXrefs, Xref aXref, Entry<Module, Map<String, XrefEntry>> aEntry)
			throws Exception {
		aWriter.writeStartElement("import");
		aWriter.writeAttribute("module", formatModuleName(aEntry.getKey()));
		aWriter.writeAttribute("count", Integer.toString(countRefs(aEntry.getValue().values())));

		int reversedCnt = 0;
		Xref otherXref = aXrefs.get(aEntry.getKey());
		if (otherXref != null && otherXref.getModuleRefs().contains(aXref.getSource())) {
			reversedCnt = countRefs(otherXref.getRefs().get(aXref.getSource()).values());
		}
		aWriter.writeAttribute("incoming", Integer.toString(reversedCnt));

		for (XrefEntry src : aEntry.getValue().values()) {
			writeXrefImportSource(aWriter, src);
		}

		aWriter.writeEndElement();
	}

	private static void writeXrefImportSource(XMLStreamWriter aWriter, XrefEntry aSrc) throws Exception {
		aWriter.writeStartElement("type");
		aWriter.writeAttribute("name", aSrc.getType());
		aWriter.writeAttribute("file", formatSourceFile(aSrc.getTarget()));
		for (SourceEntry src : aSrc.getSources()) {
			aWriter.writeStartElement("from");
			aWriter.writeAttribute("file", formatSourceFile(src));
			aWriter.writeEndElement();
		}
		aWriter.writeEndElement();
	}
}
