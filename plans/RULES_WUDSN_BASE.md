# Rules: using WUDSN Base's repository/`Action`/`ElementFactory` pattern

These rules apply to every application built on `com.wudsn.tools.base`,
e.g. DIS6502, RASTER Music Tracker (RMT) and The!Cart Studio. They lived
in DIS6502's `plans/` folder until 2026-10-05; DIS6502 and RMT refer to
this file now.

WUDSN Base has an established pattern for menu items, buttons, and other
labeled Swing components: static properties (label text with an embedded
mnemonic marker, tooltip, accelerator) live as one `Action` value in a
repository class, populated reflectively from a `.properties` file at
class load; components are then built by handing that `Action` to
`ElementFactory`, which applies the mnemonic/tooltip/accelerator
consistently and fails fast if a label is missing its mnemonic marker.

DIS6502 uses it for its main menu and all three popup menus
(`SegmentListPanel`, `DisassemblyPanel`, `MemoryInspectorPanel`) - see
`com.wudsn.tools.dis6502.Actions`' javadoc for the concrete result. RMT
uses it for its main menu and toolbars (`RmtMainMenu`, `RmtCommandId`).

## The pieces

- **`com.wudsn.tools.base.repository.Action`** - a plain data holder:
  label, tooltip, accelerator. Nothing else - no icon field, no command
  id, no enablement state, no listener.
- **`com.wudsn.tools.base.repository.NLS`** - a reflection-based
  static-field populator. A class extending it declares `public static`
  (non-final) fields (`String`, `Action`, `DataType`, `Message`, or
  `ValueSet`); calling `initializeClass(MyClass.class, null)` in a static
  initializer loads `MyClass.properties` (plus locale-suffixed variants)
  from the classpath next to the class, and for every `Action`-typed
  field looks up `<fieldName>.label` (mandatory) and `<fieldName>.toolTip`
  (optional), replacing the field with a fully-populated `Action` -
  **preserving the accelerator** if the field was already pre-initialized
  with one in Java source (`new Action(keyCode, modifiers)`). A missing
  mandatory `.label`, or a class that isn't `public`, aborts at load time
  - a fail-fast, load-time-checked mechanism, not a silent fallback. An
  application's repository classes (in DIS6502: `Actions`, `Texts`,
  `DataTypes`, `Messages`) all extend this same `NLS` base and follow this
  same shape.
- **`com.wudsn.tools.base.Actions`** - a ready-made, app-independent
  repository with generic entries any Swing app can reuse as-is
  (`ButtonBar_OK`, `MainMenu_File`, etc.). An application's own `Actions`
  class (e.g. `com.wudsn.tools.dis6502.Actions`) supplies everything
  app-specific, same shape (own package, own `.properties` file), reusing
  the shared class's fields for the few top-level items that need nothing
  app-specific.
- **`com.wudsn.tools.base.gui.ElementFactory`** - builds an actual Swing
  component from an `Action`, and nothing else (no listener attachment,
  no icon):
  ```java
  public static JMenu createMenu(Action action)
  public static JMenuItem createMenuItem(Action action, String actionCommand)
  public static JCheckBoxMenuItem createCheckBoxMenuItem(Action action)
  public static JButton createButton(Action action, boolean withMnemonic)
  public static JToggleButton createToggleButton(Action action, boolean withMnemonic)
  public static void setButtonTextAndMnemonic(AbstractButton button, Action action)
  public static JLabel createLabel(DataType dataType, JComponent field)
  public static JCheckBox createCheckBox(DataType dataType)
  public static JRadioButton createRadioButton(DataType dataType)
  public static void applyLabel(JLabel label, DataType dataType, JComponent field)
  public static void applyLabel(AbstractButton button, DataType dataType)
  ```
  `createLabel`/`createCheckBox`/`createRadioButton`/`applyLabel` are the
  methods here keyed by `DataType` (a paired field's label text, or a
  self-labeled control's own text) rather than `Action` - use these, not a
  hand-rolled `&`-mnemonic parser, for any new self-labeled `JCheckBox`/
  `JRadioButton`, or to re-label an existing `JLabel`/button in place
  (e.g. DIS6502's `LowHighByteDialog` swapping its Low/High Byte labels)
  rather than constructing a new one.
  `createMenu`/`createMenuItem` require the label to contain a mnemonic
  marker and **throw at build time if it doesn't** - this is deliberate
  fail-fast validation, not something to route around. `createMenuItem`
  also pads the label with trailing spaces when the action has an
  accelerator, so Swing's accelerator hint doesn't collide with the text.
- **`com.wudsn.tools.base.gui.ModalDialog`** - the frame of a modal
  OK/Cancel dialog: the button bar (OK as default button, both with their
  mnemonics, Escape and the close box as Cancel), `showModal(focusField)`
  (packs, centers, blocks, then disposes the dialog), the hooks
  `dataToUi()`/`dataFromUi()`/`validateOK()`, the result `okPressed`, and
  for special cases `getOKButton()`, `addButtonBarButton(button)` (at the
  left) and `close()` (ends the dialog without OK). With `cancelButton`
  false, it has OK only.
- **`com.wudsn.tools.base.gui.MRUMenu`** - fills a menu from a
  `com.wudsn.tools.base.common.MRUList` (numbered items, disabled when
  empty).
- **`com.wudsn.tools.base.gui.KeyStroke.M1`/`M2`/`M3`** - the
  platform-independent modifier constants for accelerators (`M1` = Ctrl
  on Windows/Linux, Cmd on macOS; `M2` = Shift; `M3` = Alt/Option). Use
  these instead of `InputEvent` masks directly, so accelerators are
  correct on macOS too.

## Standing rules

- **The wiring is the application's choice; keep it consistent within
  one application.** `ElementFactory` attaches no listener, so each
  application decides how its items reach their code:
  - DIS6502 wires every menu/button item directly: each stays a public
    field, wired by its owning class with its own listener
    (`item.addActionListener(e -> performXxx())`), and the
    `actionCommand` `createMenuItem` requires is inert metadata - the
    field name (e.g. `"newWorkspaceMenuItem"`), useful for
    logging/debugging, not a dispatch key. Do not add a
    shared-`ActionListener`-plus-command-dispatch style there.
  - RMT dispatches: each menu item and toolbar button calls one executor
    with its `RmtCommandId`, whose name is also the `actionCommand`; the
    command id carries the `Action`.
- **No icon support anywhere in `Action`/`ElementFactory`.** A toolbar
  that wants icons needs a separate mechanism.
- **Dynamic, runtime-generated items have no place in this pattern.** A
  submenu whose children are generated at runtime from changing data has
  no fixed label to put in a `.properties` file - build those items
  directly (`new JMenuItem(text, mnemonicChar)`) inside whatever listener
  regenerates them, not through `ElementFactory`; for a recent-files list,
  `MRUMenu.fill` does exactly that. The submenu's own static header label
  still goes through `ElementFactory.createMenu(...)` as usual.
- **An OK/Cancel dialog extends `ModalDialog`** (RMT's dialogs, and
  DIS6502's since its plan 16). The constructor takes the owner window and
  the title, and adds the form as one panel at `BorderLayout.CENTER` of
  the content pane, leaving `fieldsPane` empty - or fills `fieldsPane`
  with `SpringUtilities` (The!Cart Studio); `ModalDialog` owns the bottom.
  A `show(...)`/`showDialog()` method fills the fields, calls
  `showModal(field)` and returns the result from `okPressed`. The checks
  of OK, with their messages, go into `validateOK()`, which returns
  `false` to keep the dialog open; committing to the model goes there or
  after `showModal` when `okPressed`. A dialog that enables OK only for
  complete input uses `getOKButton()`; further buttons go into the button
  bar with `addButtonBarButton`, and a button that ends the dialog with
  its own result sets a flag and calls `close()`. Do not give a dialog a
  fixed size - `showModal` packs it; give its list or text area a
  preferred size instead. An information dialog such as "About" uses
  the OK-only variant, `ModalDialog(owner, title, false)`: no Cancel
  button, Escape and the close box still close it. Only dialogs that are
  neither (a progress dialog, a dialog with repeatable actions) extend
  `JDialog` directly (in DIS6502: `AssembleDialog` and
  `DisassemblyProgressDialog`; the latter wires Escape with DIS6502's
  `ElementUtilities.closeOnEscape`).
