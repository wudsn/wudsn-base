/**
 * WUDSN Base as an explicit module, so modular applications can declare
 * <code>requires transitive com.wudsn.tools.base</code> without the compiler
 * warning for automatic modules. The module is open, like the automatic
 * module it replaces: {@link com.wudsn.tools.base.repository.NLS} and the
 * GUI load their properties files and icons through the class loader, which
 * finds resources of a named module only in open packages.
 */
open module com.wudsn.tools.base {
	exports com.wudsn.tools.base;
	exports com.wudsn.tools.base.common;
	exports com.wudsn.tools.base.console;
	exports com.wudsn.tools.base.gui;
	exports com.wudsn.tools.base.io;
	exports com.wudsn.tools.base.repository;

	// Types of these modules appear in the API, e.g. JComponent, Preferences, DefaultHandler.
	requires transitive java.desktop;
	requires transitive java.prefs;
	requires transitive java.xml;
}
