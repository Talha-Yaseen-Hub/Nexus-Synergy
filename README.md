<div align="center">

<img width="100%" src="https://capsule-render.vercel.app/api?type=waving&color=0:0f0c29,50:302b63,100:24243e&height=220&section=header&text=Nexus-Synergy&fontSize=68&fontColor=ffffff&animation=fadeIn&fontAlignY=36&desc=A%20Rectangle-Partition%20Logic%20Puzzle&descAlignY=58&descSize=18&fontList=Inter"/>

<br/>

<img src="https://readme-typing-svg.demolab.com/?font=Inter&weight=400&size=16&duration=4000&pause=1500&color=00D9FF&center=true&vCenter=true&width=820&lines=Fill+the+grid.+Cover+every+clue.+Solve+the+synergy.;32+hand-planned%2C+solver-verified+levels.;Shipped+as+a+web+app+and+a+native+Java+desktop+app.;Zero+dependencies.+Zero+build+step.+100%25+offline." alt="Typing SVG"/>

<br/><br/>

<img src="https://img.shields.io/badge/levels-32-0f0c29?style=for-the-badge&labelColor=000000" />
<img src="https://img.shields.io/badge/platforms-web%20%7C%20java-0f0c29?style=for-the-badge&labelColor=000000" />
<img src="https://img.shields.io/badge/dependencies-zero-0f0c29?style=for-the-badge&labelColor=000000" />
<img src="https://img.shields.io/badge/build%20step-none-0f0c29?style=for-the-badge&labelColor=000000" />
<img src="https://img.shields.io/badge/license-MIT-0f0c29?style=for-the-badge&labelColor=000000" />

<br/>

<img src="https://img.shields.io/github/stars/yourusername/nexus-synergy?style=social" />
<img src="https://img.shields.io/github/forks/yourusername/nexus-synergy?style=social" />
<img src="https://img.shields.io/github/last-commit/yourusername/nexus-synergy?color=302b63&labelColor=000000&style=flat-square" />
<img src="https://img.shields.io/github/repo-size/yourusername/nexus-synergy?color=302b63&labelColor=000000&style=flat-square" />

</div>

<br/>

<p align="center">
<img src="https://skillicons.dev/icons?i=html,css,js,java,python,git,github,vscode&perline=8&theme=dark"/>
</p>

<br/>

> **Note on the badges above:** the star/fork/commit/size badges pull live data from GitHub and only render correctly once you swap `yourusername/nexus-synergy` for your actual GitHub username and repository name.

---

<div align="center">

### Table of Contents

**[About](#about)** · **[Why "Nexus-Synergy"?](#why-nexus-synergy)** · **[Preview](#preview)** · **[Features](#features)** · **[How to Play](#how-to-play)** · **[Architecture](#architecture)** · **[Project Structure](#project-structure)** · **[File Reference](#file-reference)** · **[Level Generator](#level-generator)** · **[Getting Started](#getting-started)** · **[Validation](#validation--testing)** · **[Roadmap](#roadmap)** · **[Contributing](#contributing)**

</div>

---

<br/>

## About

**Nexus-Synergy** is an original, independently built puzzle inspired by the mechanics of a well-known daily rectangle-partition game. The underlying logic draws from the classic **Shikaku** family of puzzles — a genre with roots in Japanese pencil-and-paper logic puzzles, where the challenge is purely combinatorial: no words, no luck, just geometry and deduction.

The rule set is simple to state and satisfyingly hard to master: every numbered cell on the board is a **clue**. Your job is to draw a rectangle around that clue whose total cell count matches the number exactly — and to keep doing this, clue by clue, until every single cell on the board belongs to exactly one rectangle. No gaps. No overlaps. No cell left unclaimed. The moment the last rectangle locks into place, the board is solved.

This repository ships **two complete, independent implementations of the same 32 puzzles**:

<table>
<tr>
<th align="left" width="16%">Version</th>
<th align="left" width="44%">Stack</th>
<th align="left" width="40%">Best For</th>
</tr>
<tr>
<td><strong>Web</strong></td>
<td>Vanilla HTML5, CSS3, JavaScript (ES6) — no frameworks, no build tools</td>
<td>Opening instantly in any browser, hosting on GitHub Pages, or embedding anywhere</td>
</tr>
<tr>
<td><strong>Java Desktop</strong></td>
<td>Java 17+, Swing, custom-painted UI components</td>
<td>Running as a native, fully offline desktop app with no browser dependency</td>
</tr>
</table>

Both versions consume **identical puzzle data**, generated once by a single Python script (`generate_levels.py`) and exported into two formats — a JavaScript array for the web build, and a compact text encoding for the Java build. A fix or a new level batch only ever needs to be generated once, and both apps stay perfectly in sync automatically — there's no risk of the web version and the desktop version silently drifting apart over time.

> **Disclaimer** — This is a fan-made, independently engineered project built for educational and portfolio purposes. It is not affiliated with, endorsed by, or connected to any commercial product. No proprietary code, assets, or puzzle data were used; only the publicly observable rules of the genre were reimplemented from scratch.

<div align="right"><a href="#nexus-synergy">↑ back to top</a></div>

<br/>

---

## Why "Nexus-Synergy"?

The name is a deliberate nod to what's actually happening on the board. Every clue cell is a **nexus** — a fixed point the whole puzzle radiates outward from — and the puzzle is only solved once every one of those individual rectangles works together, edge to edge, in perfect **synergy**, to cover the entire grid with no seams left showing. The name describes the win condition, not just the theme.

<div align="right"><a href="#nexus-synergy">↑ back to top</a></div>

<br/>

---

## Preview

<div align="center">
<table>
<tr>
<td align="center" width="33%">
<img src="https://placehold.co/500x360/0f0c29/00d9ff?text=Level+Select&font=raleway" width="100%"/>
<br/><sub><strong>Level Select</strong> — 32 levels grouped by grid-size tier</sub>
</td>
<td align="center" width="33%">
<img src="https://placehold.co/500x360/302b63/00d9ff?text=Fresh+Puzzle&font=raleway" width="100%"/>
<br/><sub><strong>Fresh Puzzle</strong> — clues showing target area and shape</sub>
</td>
<td align="center" width="33%">
<img src="https://placehold.co/500x360/24243e/00d9ff?text=Solved+Board&font=raleway" width="100%"/>
<br/><sub><strong>Solved Board</strong> — every cell claimed by exactly one patch</sub>
</td>
</tr>
</table>

<sub>Screenshots shown are placeholders — see the note below.</sub>

</div>

> ⚠️ **About the images not rendering:** the three images above are temporary placeholders generated on the fly by `placehold.co` — they'll always load, since they don't depend on anything in your repo. Your **real** screenshots weren't showing up because GitHub can only render an image tag if the file actually exists at that exact path *inside the repository itself*. The most common reasons that fails:
> 1. The PNG files were never actually added and committed to `assets/screenshots/` — referencing a path in the README doesn't create the file.
> 2. A filename case mismatch (`Level-Select.png` vs `level-select.png`) — GitHub's servers are case-sensitive even if your local OS isn't.
> 3. The images were added on a different branch than the one GitHub is rendering the README from.
>
> **To fix it:** drop your real `.png` files into `assets/screenshots/`, `git add` + commit + push them, then swap the three `placehold.co` URLs above back to the original relative paths, e.g. `assets/screenshots/board-solved.png`.

<div align="right"><a href="#nexus-synergy">↑ back to top</a></div>

<br/>

---

## Features

<table>
<tr>
<td width="50%" valign="top">

**Puzzle Engine**
- 32 hand-planned, solver-verified levels
- 6 difficulty tiers, 5×5 through 10×10 expert
- Guillotine-cut generator with corner-anchored clues
- Full tiling guaranteed — no gaps, no overlaps, ever
- Identical validation logic shared byte-for-byte in intent across both builds

**Visual Design**
- Custom "quilted-canvas" theme with stitched borders and a fabric-swatch palette
- Fredoka + Inter typography bundled in both builds
- Smooth drag-preview feedback — green for valid, red for invalid
- Consistent color language between the web CSS variables and the Java `Theme` class

</td>
<td width="50%" valign="top">

**Gameplay**
- Click-and-drag rectangle placement, corner to corner
- Undo, Reset, and Hint (reveals one unsolved patch)
- Live timer with best-time tracking per level
- Daily featured puzzle, deterministic by date
- Persistent progress — solved levels, streaks, best times

**Delivery**
- Web: open `index.html` — done
- Desktop: double-click one `.jar` — done
- No installers, no accounts, no telemetry
- Fully offline after the initial download — nothing ever phones home

</td>
</tr>
</table>

<br/>

### A closer look at what makes it tick

- **Deterministic daily puzzle** — the "puzzle of the day" isn't random each time you open the app; it's derived from the calendar date, so everyone playing on the same day gets the same board, and refreshing the page won't let you reroll it.
- **Hint, not a solver** — the Hint button reveals exactly one still-unsolved patch at a time, pulled from the generator's own reference solution. It nudges you forward without ever solving the whole board for you.
- **Local, private progress** — solved levels, streaks, and best times are stored entirely on your own machine (`localStorage` on web, a properties file on desktop). Nothing is ever uploaded anywhere.

<div align="right"><a href="#nexus-synergy">↑ back to top</a></div>

<br/>

---

## How to Play

<table>
<tr>
<td width="58%" valign="top">

| Step | Action |
|:---:|---|
| 1 | Every numbered cell is a **clue** — the number is the exact cell count its rectangle must cover |
| 2 | The icon under the number shows the required **shape**: square, tall, or wide |
| 3 | **Click** a clue and **drag** to the opposite corner of the intended rectangle |
| 4 | **Release** to commit — the patch turns green if valid, flashes red if not |
| 5 | Every rectangle must contain **exactly one clue**, and rectangles can never overlap |
| 6 | The puzzle is solved the instant **every cell** on the board belongs to a patch |
| — | Click any placed patch to remove it, or use **Undo**, **Reset**, or **Hint** |

</td>
<td width="42%" valign="top">

```mermaid
flowchart TD
    A[Click a clue cell] --> B[Drag to opposite corner]
    B --> C{Release mouse}
    C --> D{Shape matches?}
    D -- No --> H[Reject placement]
    D -- Yes --> E{Area equals clue?}
    E -- No --> H
    E -- Yes --> F{Overlaps another patch<br/>or contains another clue?}
    F -- Yes --> H
    F -- No --> G[Commit patch]
    G --> I{Board fully covered?}
    I -- No --> A
    I -- Yes --> J[Puzzle Solved]
```

</td>
</tr>
</table>

<br/>

**Worked example** — say a clue reads `6` with a "wide" icon underneath. That single number is doing two jobs at once: it tells you the rectangle must cover exactly 6 cells, *and* it tells you the rectangle must be wider than it is tall (so a 1×6, 2×3, or 3×2-oriented-wide shape, never a 6×1). You'd drag from that clue cell out to a corner that produces a 2-row-by-3-column block. If the drag produces the right area but the wrong proportions — say a 1×6 strip when a 2×3 block was needed — the game rejects it, because shape is checked independently from area, not inferred from it.

<div align="right"><a href="#nexus-synergy">↑ back to top</a></div>

<br/>

---

## Architecture

Both builds share the exact same three-layer structure — only the rendering layer differs.

```mermaid
flowchart LR
    subgraph Shared["Shared Puzzle Data"]
        GEN[generate_levels.py] --> JSON[levels_data.json]
        JSON --> JS[levels.js]
        JSON --> DAT[levels.dat]
    end

    subgraph Web["Web Build"]
        JS --> GAMEJS[game.js<br/>engine + validation]
        GAMEJS --> DOM[index.html + style.css<br/>DOM rendering]
    end

    subgraph Desktop["Java Desktop Build"]
        DAT --> LOADER[LevelLoader.java]
        LOADER --> ENGINE[BoardPanel.java<br/>engine + validation]
        ENGINE --> SWING[Swing components<br/>custom-painted UI]
    end

    style Shared fill:#0f0c29,stroke:#4a4a6a,color:#eaeaea
    style Web fill:#16213e,stroke:#3a5a78,color:#eaeaea
    style Desktop fill:#162e1e,stroke:#4c7a4c,color:#eaeaea
```

`game.js` and `BoardPanel.java` implement an **identical validation ruleset** — shape check → area check → overlap check → single-clue check — line-for-line equivalent logic in two languages, so any puzzle solvable in one build is guaranteed solvable in the other.

### Why a shared data pipeline instead of two separate generators?

Maintaining puzzle logic in two languages is the single easiest way for a project like this to quietly rot: a fix applied to the JavaScript validator that never makes it into the Java validator means the two builds can silently disagree about whether a given placement is legal. Generating the levels **once**, in one place, and exporting to two formats sidesteps that entire class of bug — there is only ever one source of truth for what a "valid" puzzle looks like, even though there are two separate rendering engines consuming it.

<div align="right"><a href="#nexus-synergy">↑ back to top</a></div>

<br/>

---

## Project Structure

```
nexus-synergy/
│
├── index.html                    Web app entry point — screens and modals
├── style.css                     Full visual theme (quilted-canvas design)
├── game.js                       Web game engine — render, drag, validate, win
├── levels.js                     32 generated puzzles (web format)
├── generate_levels.py            Puzzle generator — source of truth for both builds
├── README.md                     You are here
│
├── assets/
│   └── screenshots/               Gameplay screenshots used in this README
│
└── java-desktop-version/
    │
    ├── nexus-synergy.jar          Double-click to run — everything bundled inside
    ├── README-JAVA.md             Java-specific setup and rebuild instructions
    ├── MANIFEST.MF                Jar manifest — declares the Main-Class
    │
    ├── src/nexussynergy/           All Java source (14 files, see reference below)
    │
    ├── resources/
    │   ├── levels.dat               32 generated puzzles (Java format)
    │   └── fonts/                   Bundled Fredoka + Inter TTFs
    │
    └── licenses/                  OFL license text for the bundled fonts
```

<div align="right"><a href="#nexus-synergy">↑ back to top</a></div>

<br/>

---

## File Reference

<details open>
<summary><strong>Web version</strong></summary>
<br/>

<table>
<tr><th align="left" width="22%">File</th><th align="left">Role</th></tr>
<tr>
<td><code>index.html</code></td>
<td>The page shell. Defines the level-select screen, the game screen (toolbar and board container), the win modal, and the how-to-play modal. Loads fonts, <code>style.css</code>, <code>levels.js</code>, then <code>game.js</code>.</td>
</tr>
<tr>
<td><code>style.css</code></td>
<td>All visual styling — the color palette and typography as CSS variables, the fabric-and-stitching aesthetic (dashed borders, patch-edge borders), button and modal styles, and responsive rules for mobile.</td>
</tr>
<tr>
<td><code>game.js</code></td>
<td>The entire client-side engine in one file: board state, DOM rendering, pointer-event drag handling, the <code>validatePlacement()</code> rule-checker, win detection, the timer, hint logic, and <code>localStorage</code>-backed progress persistence.</td>
</tr>
<tr>
<td><code>levels.js</code></td>
<td>Auto-generated data file — a single <code>NEXUS_SYNERGY_LEVELS</code> array of 32 level objects (rows, columns, and each clue's position, value, shape, and solution). Never hand-edited; regenerate it via the Python script instead.</td>
</tr>
<tr>
<td><code>generate_levels.py</code></td>
<td>The puzzle generator. Recursively splits each grid into rectangles via a guillotine-cut partition, classifies each piece's shape, anchors its clue at a corner, and verifies the result tiles perfectly before writing <code>levels.js</code>.</td>
</tr>
</table>

</details>

<details>
<summary><strong>Java desktop version — <code>src/nexussynergy/</code></strong></summary>
<br/>

<table>
<tr><th align="left" width="22%">File</th><th align="left">Role</th></tr>
<tr>
<td><code>NexusSynergyApp.java</code></td>
<td><strong>Main entry point.</strong> Builds the <code>JFrame</code>, header, footer, and the <code>CardLayout</code> that swaps between the level-select and game screens. Owns the <code>Progress</code> instance and wires every screen's callbacks together.</td>
</tr>
<tr>
<td><code>LevelLoader.java</code></td>
<td>Parses the bundled <code>levels.dat</code> resource — a compact <code>id|rows|cols|clue;clue;...</code> text format — into a list of <code>Level</code> objects at startup.</td>
</tr>
<tr>
<td><code>Level.java</code></td>
<td>Data model for one puzzle: id, dimensions, and its list of <code>Clue</code> objects.</td>
</tr>
<tr>
<td><code>Clue.java</code></td>
<td>Data model for a single clue: cell position, required area, required <code>Shape</code>, and the generator's reference solution rectangle, used only for hints.</td>
</tr>
<tr>
<td><code>Shape.java</code></td>
<td>Enum of the three shape categories — <code>SQUARE</code>, <code>TALL</code>, <code>WIDE</code>.</td>
</tr>
<tr>
<td><code>BoardPanel.java</code></td>
<td><strong>The core game engine.</strong> A custom <code>JPanel</code> that paints the grid, clue badges, and placed patches; handles all mouse-drag interaction; and implements the same shape → area → overlap → single-clue validation ruleset as <code>game.js</code>. Also owns undo, reset, hint, and win detection.</td>
</tr>
<tr>
<td><code>GamePanel.java</code></td>
<td>The game screen's chrome — back button, level badge, live timer, and the Hint / Undo / Reset toolbar — wrapped around a <code>BoardPanel</code>.</td>
</tr>
<tr>
<td><code>LevelSelectPanel.java</code></td>
<td>Renders the scrollable, tier-grouped grid of level tiles, the daily-puzzle marker, solved checkmarks, and the "Play today's puzzle" / "How to play" actions.</td>
</tr>
<tr>
<td><code>WrapLayout.java</code></td>
<td>A small utility layout manager — a <code>FlowLayout</code> that correctly wraps rows inside a scroll pane — used by the level tile grid.</td>
</tr>
<tr>
<td><code>RoundedButton.java</code></td>
<td>Custom pill-shaped, hover-responsive button component matching the web app's <code>.btn</code> style, used throughout in place of default Swing button chrome.</td>
</tr>
<tr>
<td><code>WinDialog.java</code></td>
<td>The puzzle-complete dialog — shows solve time, best time, and Next Level / All Levels actions.</td>
</tr>
<tr>
<td><code>HowToDialog.java</code></td>
<td>The rules and instructions dialog, styled to match the rest of the app.</td>
</tr>
<tr>
<td><code>Progress.java</code></td>
<td>Reads and writes <code>~/.nexus-synergy/progress.properties</code> — solved levels, best times per level, and the daily play streak.</td>
</tr>
<tr>
<td><code>Theme.java</code></td>
<td>Centralized color palette (mirrors the CSS variables exactly) and font loading — registers the bundled Fredoka / Inter TTFs at startup, falling back to system fonts if that fails.</td>
</tr>
</table>

</details>

<details>
<summary><strong>Java desktop version — supporting files</strong></summary>
<br/>

<table>
<tr><th align="left" width="22%">File</th><th align="left">Role</th></tr>
<tr>
<td><code>nexus-synergy.jar</code></td>
<td>The final, runnable artifact. Contains all compiled classes, <code>levels.dat</code>, and both font files — fully self-sufficient.</td>
</tr>
<tr>
<td><code>MANIFEST.MF</code></td>
<td>One line — <code>Main-Class: nexussynergy.NexusSynergyApp</code> — tells the JVM what to launch when the jar is run directly.</td>
</tr>
<tr>
<td><code>resources/levels.dat</code></td>
<td>The same 32 puzzles as <code>levels.js</code>, re-encoded into a compact pipe/comma-delimited text format for fast, dependency-free parsing in Java.</td>
</tr>
<tr>
<td><code>resources/fonts/*.ttf</code></td>
<td>Fredoka (display) and Inter (body), bundled so the desktop app looks identical everywhere regardless of installed system fonts.</td>
</tr>
<tr>
<td><code>licenses/OFL-*.txt</code></td>
<td>SIL Open Font License text for the two bundled fonts, included per license requirements.</td>
</tr>
<tr>
<td><code>README-JAVA.md</code></td>
<td>A focused setup and rebuild guide for just the Java build.</td>
</tr>
</table>

</details>

<div align="right"><a href="#nexus-synergy">↑ back to top</a></div>

<br/>

---

## Level Generator

Every one of the 32 levels is produced algorithmically, then rigorously verified before shipping — nothing is placed by hand, and nothing ships unverified.

```mermaid
flowchart TD
    A[Start: full R×C grid<br/>as one rectangle] --> B[Pick the largest<br/>splittable rectangle]
    B --> C[Cut it — vertical or<br/>horizontal, randomized]
    C --> D{Reached target<br/>piece count?}
    D -- No --> B
    D -- Yes --> E[For each rectangle:<br/>compute area and shape]
    E --> F[Anchor its clue at<br/>one of its four corners]
    F --> G[Verify: full tiling,<br/>zero overlap, zero gaps]
    G -- Fails --> B
    G -- Passes --> H[Export to<br/>levels.js + levels.dat]

    style A fill:#0f0c29,stroke:#4a4a6a,color:#eaeaea
    style H fill:#162e1e,stroke:#4c7a4c,color:#eaeaea
```

**Difficulty scaling** — six tiers, five puzzles each, plus two expert bonus puzzles:

<div align="center">

| Levels | Grid | Pieces | Difficulty |
|:---:|:---:|:---:|:---|
| 1 – 6 | 5×5 | 5 – 8 | Easy |
| 7 – 12 | 6×6 | 7 – 10 | Easy–Medium |
| 13 – 18 | 7×7 | 9 – 12 | Medium |
| 19 – 24 | 8×8 | 11 – 14 | Medium–Hard |
| 25 – 30 | 9×9 | 13 – 16 | Hard |
| 31 – 32 | 10×10 | 17 – 18 | Expert |

</div>

Clues are corner-anchored — never placed mid-rectangle — specifically so a corner-to-corner mouse drag can always reconstruct the intended shape, matching the real feel of drag-based rectangle puzzles.

### Why "guillotine-cut" instead of random rectangle placement?

A naive approach — scattering rectangles randomly and hoping they tile — almost never produces a valid full tiling, because leftover irregular gaps are nearly guaranteed. A **guillotine cut** sidesteps that entirely: at every step, the generator takes one whole rectangle and slices it, edge to edge, into two smaller rectangles — either a single vertical or a single horizontal cut all the way across. Because every cut divides an already-perfect rectangle into two smaller perfect rectangles, the property "the pieces perfectly tile the original grid" is mathematically guaranteed at every step, not something that needs to be checked and retried afterward. The verification pass in step G is a final safety net, not the primary correctness mechanism.

<div align="right"><a href="#nexus-synergy">↑ back to top</a></div>

<br/>

---

## Getting Started

### Option A — Web version

```bash
# Just open it — no install, no server needed
cd nexus-synergy
open index.html        # macOS
xdg-open index.html    # Linux
start index.html       # Windows
```

If your browser blocks local file access for any reason, serve it instead:

```bash
python3 -m http.server 8000
# then visit http://localhost:8000
```

### Option B — Java desktop version

Requires a Java 17+ runtime (`java -version` to check; install via `sudo apt install default-jre` or [adoptium.net](https://adoptium.net) if missing).

```bash
cd nexus-synergy/java-desktop-version
java -jar nexus-synergy.jar
```

Or double-click `nexus-synergy.jar` if your OS associates `.jar` files with Java.

### Rebuilding the Java app from source

```bash
cd nexus-synergy/java-desktop-version
mkdir -p out
javac -encoding UTF-8 -d out src/nexussynergy/*.java
cp -r resources/fonts out/fonts
cp resources/levels.dat out/levels.dat
jar cfm nexus-synergy.jar MANIFEST.MF -C out .
```

### Regenerating levels

```bash
cd nexus-synergy
python3 generate_levels.py
```

Edit `level_plan()` inside the script to change grid sizes, level count, or difficulty per tier. This overwrites `levels.js`, which can then be re-encoded into `levels.dat` for the Java build.

<div align="right"><a href="#nexus-synergy">↑ back to top</a></div>

<br/>

---

## Validation & Testing

Every level shipped in this repo passed through three independent verification passes before being included:

<table>
<tr><th align="left" width="30%">Check</th><th align="left">What It Confirms</th></tr>
<tr>
<td>Structural validation</td>
<td>No duplicate clue coordinates; every clue's area sums exactly to the grid's total cell count</td>
</tr>
<tr>
<td>Geometric validation</td>
<td>The generator's own solution rectangles tile the grid with zero overlap and zero gaps, and every clue sits on a corner of its own rectangle</td>
</tr>
<tr>
<td>Engine simulation</td>
<td>Each level was "played" programmatically using the exact same validation function shipped in <code>game.js</code>, confirming every puzzle is solvable through the real game rules — not just by construction</td>
</tr>
</table>

The Java build was additionally verified by driving the real UI: a `Robot`-based test harness performed genuine mouse press/drag/release sequences through every clue of a live, rendered window and confirmed the board reached a fully-solved state — exercising the actual production code path, not a shortcut.

<div align="right"><a href="#nexus-synergy">↑ back to top</a></div>

<br/>

---

## Roadmap

<table>
<tr>
<td width="33%" valign="top">

**Near-term**
- [ ] Daily-puzzle cloud sync
- [ ] Difficulty-rated hint costs
- [ ] Sound effects toggle

</td>
<td width="33%" valign="top">

**Mid-term**
- [ ] Level editor / custom puzzle sharing
- [ ] Mobile-native wrapper (Android/iOS)
- [ ] Colorblind-friendly palette mode

</td>
<td width="33%" valign="top">

**Long-term**
- [ ] Multiplayer race mode
- [ ] Procedural infinite mode
- [ ] Leaderboards

</td>
</tr>
</table>

<div align="right"><a href="#nexus-synergy">↑ back to top</a></div>

<br/>

---

## Contributing

Found a bug? Open an issue. Have an idea for a level pack? Open a pull request.

<div align="center">

<img src="https://img.shields.io/badge/contributions-welcome-0f0c29?style=for-the-badge&labelColor=000000" />
<img src="https://img.shields.io/badge/issues-open-0f0c29?style=for-the-badge&labelColor=000000" />
<img src="https://img.shields.io/badge/PRs-accepted-0f0c29?style=for-the-badge&labelColor=000000" />

<br/><br/>

<img src="https://contrib.rocks/image?repo=yourusername/nexus-synergy" />

<br/><br/>

<sub>Star history — updates automatically once this repo has real traffic:</sub>

<br/>

<img src="https://api.star-history.com/svg?repos=yourusername/nexus-synergy&type=Date" width="500"/>

<br/><br/>

<sub>Built with care, verified with code, and inspired by a genuinely fun little puzzle format.</sub>

</div>

<img width="100%" src="https://capsule-render.vercel.app/api?type=waving&color=0:0f0c29,50:302b63,100:24243e&height=120&section=footer"/>
