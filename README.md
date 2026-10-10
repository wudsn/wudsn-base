# wudsn-base
This repository contains Java projects which provide resuable function for implementing localizable command line and UI tools.
The individual projects are intended to be linked into other projects as required.

## Plans
`plans/README.md` indexes the plan files of WUDSN Base: numbered plans for batches of work, and the standing rules.

## Rules
`plans/RULES_WUDSN_BASE.md` holds the standing rules for applications built on WUDSN Base (DIS6502, RASTER Music Tracker, The!Cart Studio): the repository/`Action`/`ElementFactory` pattern, `ModalDialog` and `MRUMenu`.

## Line endings
`.gitattributes` stores text files with LF in the repository and checks them out with the platform's own line endings (CRLF on Windows, as Eclipse writes them). Shell scripts keep LF everywhere. Keep a file's existing line endings when editing it.
