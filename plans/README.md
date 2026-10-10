# Plans

The plan files of WUDSN Base, one per batch of work, numbered in the order
they were created. A new plan takes the next free number and an upper-case
name (`NN_TOPIC_PLAN.md`), and gets a row in the table below. Each plan
carries its status in its first lines; this table is the overview.

The standing document is not numbered: [`RULES_WUDSN_BASE.md`](RULES_WUDSN_BASE.md),
the rules for applications built on WUDSN Base (DIS6502, RASTER Music
Tracker, The!Cart Studio).

Work on WUDSN Base that an application needed is planned in that
application's own `plans/` folder, e.g. DIS6502's plans 15 (settings
sections, MRU list and menu), 16 (`ModalDialog` for OK/Cancel dialogs) and
17 (the OK-only `ModalDialog` for About dialogs).

| No. | Plan | Purpose | Status |
|---|---|---|---|
| 01 | [MESSAGE_AREA_PLAN](01_MESSAGE_AREA_PLAN.md) | A message's number and its repository class's `AREA`, shown together as the identifier (e.g. `DIS-001`); enforced by `NLS` at class load | Done 2026-10-10 |
