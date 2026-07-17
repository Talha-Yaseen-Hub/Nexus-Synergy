# Patches — a rectangle puzzle game

An original, offline-playable clone of LinkedIn's daily **Patches** puzzle
(the Shikaku-style "fill the grid with numbered rectangles" game).

This package includes **two independent builds** of the same 32 levels:

| Version | Where | Requires |
|---|---|---|
| **Web** (this folder) | `index.html` | just a browser |
| **Java desktop** | `java-desktop-version/patches-game.jar` | a Java 17+ runtime |

Pick whichever fits your VM. The web version is pure HTML/CSS/JavaScript —
no build step, no server required, no dependencies besides two Google
Fonts loaded over the network (falls back to system fonts if you're
offline). The Java version is a native Swing desktop app packaged as a
single double-click-to-run `.jar` with fonts and puzzle data bundled
inside it — see `java-desktop-version/README-JAVA.md` for details.

**32 hand-generated levels** across six grid sizes (5×5 up to 10×10
expert), each verified by a solver script to have a full, gap-free,
overlap-free solution before being shipped. Both versions ship the exact
same puzzles, generated from the same `generate_levels.py` script.

---

## How to play

- Every numbered cell is a **clue**. The number is the exact cell count its
  rectangle must cover, and the small icon under it shows the required
  shape: square, tall, or wide.
- Click a clue and drag to the opposite corner of the rectangle you want to
  place, then release.
- Every rectangle must contain exactly one clue, and rectangles can never
  overlap.
- You win once every cell on the board belongs to a patch, with no gaps.
- Click any placed patch to remove it. Use **Undo**, **Reset**, or **Hint**
  if you get stuck.

Progress, best times, and your daily streak are saved automatically in
your browser's `localStorage` — no account needed.

---

## Running it on your VM

You have two options. Either works — pick whichever is easier.

### Option A — just open the file

1. Unzip this folder anywhere on your VM.
2. Double-click `index.html` (or right-click → Open with → your browser).

That's it. Everything runs client-side.

### Option B — serve it locally (recommended if double-click gives a blank page)

Some browsers restrict certain features when opening files directly via
`file://`. If that happens, serve the folder instead:

```bash
cd patches-game
python3 -m http.server 8000
```

Then open `http://localhost:8000` in your browser.

If you don't have Python, any static file server works, e.g.:

```bash
npx serve .
```

---

## Project structure

```
patches-game/
├── index.html         # page shell: level select, board, modals
├── style.css           # all visual styling (quilted-canvas theme)
├── game.js             # game engine: rendering, drag-to-place, validation, win logic
├── levels.js            # 32 generated puzzles consumed by game.js
├── generate_levels.py  # the generator script that produced levels.js — rerun it
│                          or tweak it to create your own puzzle sets
└── README.md
```

## Adding more levels

`generate_levels.py` procedurally builds valid puzzles by recursively
splitting the grid into rectangles (a "guillotine cut" partition), then
anchoring each clue at one corner of its rectangle so drag-to-place always
works, and finally verifying the whole thing tiles the grid perfectly with
no gaps or overlaps.

To generate a new batch:

```bash
python3 generate_levels.py
```

This overwrites `levels.js`. Edit the `level_plan()` function to change
grid sizes, level count, or difficulty (piece count) per tier.

---

*This is an independent, fan-made project inspired by LinkedIn's Patches
game. It is not affiliated with, endorsed by, or connected to LinkedIn in
any way.*
