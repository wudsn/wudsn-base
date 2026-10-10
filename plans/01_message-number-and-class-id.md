# Plan: Message "number" field and class-level "ID" prefix

Written 2026-10-10. Requested in the context of migrating ASMA
(asma.atari.org) onto WUDSN Base, whose message codes already follow the
pattern this plan introduces (`SAP-110`, `DMO-003`, `COM-001`, ...).

**Status: executed 2026-10-10.** Naming review on the same day: the constant
was renamed from `ID` to the more descriptive `AREA` (with `getArea()` on
`Message`), avoiding the ID/getId() casing debate entirely. Decisions: the full-identifier accessor is
`getFullId()` (`getId()` was removed, `getNumber()` added);
`ValueSets`/`DataTypes` stay out of scope. Implementation notes beyond the
plan: NLS's field loop treats any non-populatable field as a load-time
error, so the `AREA` constant got a narrow exemption (exactly
`public static final String AREA`) to keep the typo-catching strict for
everything else; `MessagesTest` now skips final fields, mirroring the NLS
modifier rule. IDs assigned: `BASE` (com.wudsn.tools.base), `ATARI`
(com.wudsn.tools.base.atari), `DIS` (DIS6502), `TCS` (The!Cart Studio);
RMT declares no own messages repository and needed no change. All four
repositories build green.

## Current state

- `com.wudsn.tools.base.repository.Message` has a private `String id` with
  `getId()`. `NLS` populates it with the repository field name (e.g.
  `E123`), whose first character encodes the severity (`S`/`I`/`E`) and
  whose remainder is a 3-digit number string.
- `Console` and `gui.StatusBar` display `getId()` verbatim, so users see
  `E123` - unique within one application's repository class, but not
  across the tool family, and without a hint which application or domain
  the message belongs to.

## Target state

1. **Rename the field `id` to `number`** (`getNumber()`), holding only the
   3-digit number string (e.g. `"123"`). The severity letter stays part of
   the repository field name only - it is already carried separately in the
   `severity` field.
2. **Add a class-level `ID` to every messages repository class**: a
   user-defined short upper-case identifier, declared in Java source (not
   in the `.properties` file), e.g.

   ```java
   public static final String ID = "DMO"; // Demozoo
   ```

   Examples: `DMO` (Demozoo), `RMT` (RASTER Music Tracker), `SAP` (SAP
   file handling), `DIS` (DIS6502), `TCS` (The!Cart Studio).
3. `NLS.initializeClass` reads the declaring class's `ID` field reflectively
   and passes it into the `Message` constructor; `Message` offers the full
   identifier for display, composed as `<ID>-<number>` (e.g. `DMO-003`).
   `Console` and `StatusBar` switch from `getId()` to the full identifier.

## Fail-fast rules (enforced by NLS at class load, like the existing checks)

- A messages repository class that declares `Message` fields must declare
  `public static final String ID`, non-empty, upper-case letters only
  (suggested 2-4 characters).
- The numeric part of every `Message` field name must be exactly 3 digits,
  and must be unique within the class regardless of the severity letter
  (`E110` and `I110` in one class would collide to `XXX-110` and are
  rejected).

## Impact

- **Base**: `Message` (field rename, constructor, new full-id accessor,
  `toString`), `NLS` (read `ID`, strip the severity letter, uniqueness
  check), `Console`, `StatusBar`; check `MessageQueueRenderer`/table
  columns for further `getId()` consumers.
- **Applications** (compile break by design, fail-fast at load otherwise):
  DIS6502, RMT and The!Cart Studio each add the one-line `ID` constant to
  their messages repository classes; any direct `getId()` callers switch
  to `getNumber()` or the full identifier.
- **`.properties` files are unchanged** - the keys remain the field names
  including the severity letter.
- **ASMA phase 2** (asma.atari.org `plans/06_wudsn-base-migration.md`)
  builds directly on this: its existing codes map 1:1 to repository
  classes with `ID = "SAP"`, `"COM"`, `"DMO"`, `"EXP"` and 3-digit
  numbers, so the visible codes stay identical after the migration.

## Open decisions

- Name of the full-identifier accessor (`getFullId()` vs. `getDisplayId()`
  vs. overriding what `getId()` meant - the latter avoids churn in
  consumers but silently changes displayed output).
- Whether `ValueSets`/`DataTypes` repository classes should carry the same
  `ID` for consistency (out of scope here unless decided otherwise).
