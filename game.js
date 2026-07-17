// =========================================================
// Patches — game engine
// Depends on PATCHES_LEVELS from levels.js
// =========================================================

const STORAGE_KEY = "patches-progress-v1";
const PATCH_COLORS = [
  "var(--patch-1)", "var(--patch-2)", "var(--patch-3)", "var(--patch-4)",
  "var(--patch-5)", "var(--patch-6)", "var(--patch-7)", "var(--patch-8)",
];

const state = {
  levelIndex: null,
  level: null,
  rows: 0,
  cols: 0,
  // owner[r][c] = clue index or -1 if empty
  owner: [],
  clueByIndex: [],
  // committed patches: {clueIndex, r1,c1,r2,c2}
  patches: [],
  dragging: false,
  dragAnchor: null, // {r,c,clueIndex}
  dragCurrent: null,
  startTime: null,
  timerHandle: null,
  elapsedAtPause: 0,
};

let progress = loadProgress();

function loadProgress() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return { solved: {}, bestTime: {}, streak: 0, lastPlayedDay: null };
    const parsed = JSON.parse(raw);
    return {
      solved: parsed.solved || {},
      bestTime: parsed.bestTime || {},
      streak: parsed.streak || 0,
      lastPlayedDay: parsed.lastPlayedDay || null,
    };
  } catch (e) {
    return { solved: {}, bestTime: {}, streak: 0, lastPlayedDay: null };
  }
}

function saveProgress() {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(progress));
  } catch (e) { /* storage unavailable — ignore */ }
}

// ---------- DOM refs ----------
const els = {
  levelSelect: document.getElementById("screen-select"),
  gameScreen: document.getElementById("screen-game"),
  levelGrid: document.getElementById("levelGrid"),
  board: document.getElementById("board"),
  levelBadge: document.getElementById("levelBadge"),
  levelMeta: document.getElementById("levelMeta"),
  timer: document.getElementById("timer"),
  winOverlay: document.getElementById("winOverlay"),
  winStats: document.getElementById("winStats"),
  streakCount: document.getElementById("streakCount"),
  howToOverlay: document.getElementById("howToOverlay"),
};

// ---------- Level select rendering ----------
function renderLevelSelect() {
  els.levelGrid.innerHTML = "";
  const dailyId = getDailyLevelId();
  let lastTier = null;

  PATCHES_LEVELS.forEach((lvl, idx) => {
    const tier = `${lvl.rows}×${lvl.cols}`;
    if (tier !== lastTier) {
      const heading = document.createElement("div");
      heading.className = "tier-heading";
      heading.textContent = `${tier} grid`;
      heading.style.gridColumn = "1 / -1";
      els.levelGrid.appendChild(heading);
      lastTier = tier;
    }

    const tile = document.createElement("button");
    tile.className = "level-tile";
    if (progress.solved[lvl.id]) tile.classList.add("solved");
    if (lvl.id === dailyId) tile.classList.add("daily");
    tile.innerHTML = `${lvl.id}<span class="tile-grid-size">${lvl.rows}×${lvl.cols}</span>`;
    tile.addEventListener("click", () => startLevel(idx));
    els.levelGrid.appendChild(tile);
  });

  els.streakCount.textContent = progress.streak;
}

function getDailyLevelId() {
  // deterministic day-of-year rotation through all levels
  const now = new Date();
  const start = new Date(now.getFullYear(), 0, 0);
  const diff = now - start;
  const dayOfYear = Math.floor(diff / 86400000);
  return (dayOfYear % PATCHES_LEVELS.length) + 1;
}

// ---------- Level setup ----------
function startLevel(idx) {
  state.levelIndex = idx;
  state.level = PATCHES_LEVELS[idx];
  state.rows = state.level.rows;
  state.cols = state.level.cols;
  state.clueByIndex = state.level.clues;
  state.owner = Array.from({ length: state.rows }, () => Array(state.cols).fill(-1));
  state.patches = [];
  state.dragging = false;
  state.dragAnchor = null;
  state.dragCurrent = null;

  els.levelBadge.textContent = `Level ${state.level.id}`;
  els.levelMeta.textContent = `${state.rows}×${state.cols} grid · ${state.clueByIndex.length} patches`;

  buildBoardDom();
  renderBoard();
  showScreen("game");
  startTimer();
}

function showScreen(name) {
  els.levelSelect.classList.toggle("active", name === "select");
  els.gameScreen.classList.toggle("active", name === "game");
  if (name === "select") {
    stopTimer();
    renderLevelSelect();
  }
}

// ---------- Timer ----------
function startTimer() {
  stopTimer();
  state.startTime = Date.now();
  state.timerHandle = setInterval(updateTimerDisplay, 250);
  updateTimerDisplay();
}
function stopTimer() {
  if (state.timerHandle) clearInterval(state.timerHandle);
  state.timerHandle = null;
}
function elapsedSeconds() {
  if (!state.startTime) return 0;
  return Math.floor((Date.now() - state.startTime) / 1000);
}
function updateTimerDisplay() {
  const s = elapsedSeconds();
  const m = Math.floor(s / 60).toString().padStart(2, "0");
  const ss = (s % 60).toString().padStart(2, "0");
  els.timer.textContent = `${m}:${ss}`;
}

// ---------- Board DOM ----------
function buildBoardDom() {
  els.board.innerHTML = "";
  els.board.style.gridTemplateColumns = `repeat(${state.cols}, minmax(28px, 56px))`;
  els.board.style.gridTemplateRows = `repeat(${state.rows}, minmax(28px, 56px))`;

  for (let r = 0; r < state.rows; r++) {
    for (let c = 0; c < state.cols; c++) {
      const cell = document.createElement("div");
      cell.className = "cell";
      cell.dataset.r = r;
      cell.dataset.c = c;
      els.board.appendChild(cell);
    }
  }

  els.board.addEventListener("pointerdown", onPointerDown);
  els.board.addEventListener("pointermove", onPointerMove);
  window.addEventListener("pointerup", onPointerUp);
}

function cellEl(r, c) {
  return els.board.children[r * state.cols + c];
}

function clueAt(r, c) {
  return state.clueByIndex.findIndex((cl) => cl.row === r && cl.col === c);
}

function shapeIconHtml(shape) {
  return `<span class="shape-icon ${shape}"></span>`;
}

function renderBoard() {
  for (let r = 0; r < state.rows; r++) {
    for (let c = 0; c < state.cols; c++) {
      const cell = cellEl(r, c);
      cell.className = "cell";
      cell.style.background = "";
      cell.innerHTML = "";

      const owner = state.owner[r][c];
      const clueIdx = clueAt(r, c);

      if (owner >= 0) {
        cell.classList.add("filled");
        const color = PATCH_COLORS[owner % PATCH_COLORS.length];
        cell.style.background = color;
        applyPatchEdges(cell, r, c, owner);
      }

      if (clueIdx >= 0) {
        const clue = state.clueByIndex[clueIdx];
        const badge = document.createElement("div");
        badge.className = "clue";
        badge.innerHTML = `${clue.value}${shapeIconHtml(clue.shape)}`;
        cell.appendChild(badge);
      }
    }
  }
}

function applyPatchEdges(cell, r, c, owner) {
  const isEdge = (rr, cc) => {
    if (rr < 0 || cc < 0 || rr >= state.rows || cc >= state.cols) return true;
    return state.owner[rr][cc] !== owner;
  };
  if (isEdge(r - 1, c)) cell.classList.add("patch-edge-top");
  if (isEdge(r + 1, c)) cell.classList.add("patch-edge-bottom");
  if (isEdge(r, c - 1)) cell.classList.add("patch-edge-left");
  if (isEdge(r, c + 1)) cell.classList.add("patch-edge-right");
}

// ---------- Interaction ----------
function eventToCell(evt) {
  const target = document.elementFromPoint(evt.clientX, evt.clientY);
  if (!target || !target.classList.contains("cell")) return null;
  return { r: parseInt(target.dataset.r, 10), c: parseInt(target.dataset.c, 10) };
}

function onPointerDown(evt) {
  const pos = eventToCell(evt);
  if (!pos) return;
  evt.preventDefault();

  const owner = state.owner[pos.r][pos.c];
  if (owner >= 0) {
    removePatchByClueIndex(owner);
    renderBoard();
    return;
  }

  const clueIdx = clueAt(pos.r, pos.c);
  if (clueIdx < 0) return; // must start a drag from a clue cell

  state.dragging = true;
  state.dragAnchor = { r: pos.r, c: pos.c, clueIndex: clueIdx };
  state.dragCurrent = { r: pos.r, c: pos.c };
  updateSelectionPreview();
}

function onPointerMove(evt) {
  if (!state.dragging) return;
  const pos = eventToCell(evt);
  if (!pos) return;
  if (state.dragCurrent && state.dragCurrent.r === pos.r && state.dragCurrent.c === pos.c) return;
  state.dragCurrent = pos;
  updateSelectionPreview();
}

function onPointerUp() {
  if (!state.dragging) return;
  state.dragging = false;
  const anchor = state.dragAnchor;
  const current = state.dragCurrent || anchor;
  clearSelectionPreview();

  const rect = boundingRect(anchor, current);
  const result = validatePlacement(anchor.clueIndex, rect);

  if (result.ok) {
    commitPatch(anchor.clueIndex, rect);
    renderBoard();
    checkWin();
  } else {
    flashInvalid(rect);
  }

  state.dragAnchor = null;
  state.dragCurrent = null;
}

function boundingRect(a, b) {
  return {
    r1: Math.min(a.r, b.r),
    c1: Math.min(a.c, b.c),
    r2: Math.max(a.r, b.r),
    c2: Math.max(a.c, b.c),
  };
}

function updateSelectionPreview() {
  clearSelectionPreview();
  if (!state.dragAnchor) return;
  const rect = boundingRect(state.dragAnchor, state.dragCurrent);
  const result = validatePlacement(state.dragAnchor.clueIndex, rect, { partial: true });

  for (let r = rect.r1; r <= rect.r2; r++) {
    for (let c = rect.c1; c <= rect.c2; c++) {
      const cell = cellEl(r, c);
      cell.classList.add("selecting");
      if (!result.ok) cell.classList.add("invalid");
    }
  }
}

function clearSelectionPreview() {
  els.board.querySelectorAll(".selecting").forEach((el) => {
    el.classList.remove("selecting", "invalid");
  });
}

function flashInvalid(rect) {
  for (let r = rect.r1; r <= rect.r2; r++) {
    for (let c = rect.c1; c <= rect.c2; c++) {
      const cell = cellEl(r, c);
      cell.classList.add("shake");
      setTimeout(() => cell.classList.remove("shake"), 340);
    }
  }
}

// ---------- Validation ----------
function validatePlacement(clueIndex, rect, opts = {}) {
  const clue = state.clueByIndex[clueIndex];
  const w = rect.c2 - rect.c1 + 1;
  const h = rect.r2 - rect.r1 + 1;
  const area = w * h;

  // must not extend outside grid (guaranteed by clamp, kept for safety)
  if (rect.r1 < 0 || rect.c1 < 0 || rect.r2 >= state.rows || rect.c2 >= state.cols) {
    return { ok: false, reason: "out-of-bounds" };
  }

  // shape check
  if (clue.shape === "square" && w !== h) return { ok: false, reason: "shape" };
  if (clue.shape === "tall" && h <= w) return { ok: false, reason: "shape" };
  if (clue.shape === "wide" && w <= h) return { ok: false, reason: "shape" };

  // area check — only enforce once the drag is "big enough" during partial preview
  if (!opts.partial && area !== clue.value) return { ok: false, reason: "area" };
  if (opts.partial && area > clue.value) return { ok: false, reason: "area" };

  // must not overlap already committed patches
  for (let r = rect.r1; r <= rect.r2; r++) {
    for (let c = rect.c1; c <= rect.c2; c++) {
      if (state.owner[r][c] >= 0) return { ok: false, reason: "overlap" };
    }
  }

  // must contain exactly one clue cell (the anchor's own clue)
  for (let idx = 0; idx < state.clueByIndex.length; idx++) {
    if (idx === clueIndex) continue;
    const cl = state.clueByIndex[idx];
    if (cl.row >= rect.r1 && cl.row <= rect.r2 && cl.col >= rect.c1 && cl.col <= rect.c2) {
      return { ok: false, reason: "double-clue" };
    }
  }

  if (!opts.partial && area !== clue.value) return { ok: false, reason: "area" };

  return { ok: true };
}

function commitPatch(clueIndex, rect) {
  state.patches.push({ clueIndex, ...rect });
  for (let r = rect.r1; r <= rect.r2; r++) {
    for (let c = rect.c1; c <= rect.c2; c++) {
      state.owner[r][c] = clueIndex;
    }
  }
}

function removePatchByClueIndex(clueIndex) {
  const idx = state.patches.findIndex((p) => p.clueIndex === clueIndex);
  if (idx === -1) return;
  const p = state.patches[idx];
  for (let r = p.r1; r <= p.r2; r++) {
    for (let c = p.c1; c <= p.c2; c++) {
      state.owner[r][c] = -1;
    }
  }
  state.patches.splice(idx, 1);
}

function undoLast() {
  const last = state.patches[state.patches.length - 1];
  if (!last) return;
  removePatchByClueIndex(last.clueIndex);
  renderBoard();
}

function resetLevel() {
  state.owner = Array.from({ length: state.rows }, () => Array(state.cols).fill(-1));
  state.patches = [];
  renderBoard();
  startTimer();
}

function giveHint() {
  const unsolved = state.clueByIndex
    .map((cl, idx) => idx)
    .filter((idx) => !state.patches.some((p) => p.clueIndex === idx));
  if (unsolved.length === 0) return;
  const idx = unsolved[Math.floor(Math.random() * unsolved.length)];
  const sol = state.clueByIndex[idx].solution;
  const rect = { r1: sol.row, c1: sol.col, r2: sol.row + sol.h - 1, c2: sol.col + sol.w - 1 };

  // only commit if it doesn't conflict with whatever the player has already placed
  const check = validatePlacement(idx, rect);
  if (!check.ok) {
    // player has boxed themselves in around this clue — just glow the clue cell
    const cell = cellEl(state.clueByIndex[idx].row, state.clueByIndex[idx].col);
    cell.classList.add("hint-glow");
    setTimeout(() => cell.classList.remove("hint-glow"), 2400);
    return;
  }
  commitPatch(idx, rect);
  renderBoard();
  for (let r = rect.r1; r <= rect.r2; r++) {
    for (let c = rect.c1; c <= rect.c2; c++) {
      cellEl(r, c).classList.add("hint-glow");
      setTimeout(() => cellEl(r, c) && cellEl(r, c).classList.remove("hint-glow"), 2400);
    }
  }
  checkWin();
}

// ---------- Win detection ----------
function checkWin() {
  for (let r = 0; r < state.rows; r++) {
    for (let c = 0; c < state.cols; c++) {
      if (state.owner[r][c] < 0) return false;
    }
  }
  onLevelSolved();
  return true;
}

function onLevelSolved() {
  stopTimer();
  const seconds = elapsedSeconds();
  const lvlId = state.level.id;

  const isNewSolve = !progress.solved[lvlId];
  progress.solved[lvlId] = true;
  if (!progress.bestTime[lvlId] || seconds < progress.bestTime[lvlId]) {
    progress.bestTime[lvlId] = seconds;
  }

  const today = new Date().toDateString();
  if (isNewSolve || progress.lastPlayedDay !== today) {
    if (progress.lastPlayedDay !== today) {
      progress.streak = (progress.streak || 0) + 1;
      progress.lastPlayedDay = today;
    }
  }
  saveProgress();

  const m = Math.floor(seconds / 60).toString().padStart(2, "0");
  const s = (seconds % 60).toString().padStart(2, "0");
  els.winStats.textContent = `Solved in ${m}:${s} · Best: ${formatBest(lvlId)}`;
  els.winOverlay.classList.add("active");
}

function formatBest(lvlId) {
  const best = progress.bestTime[lvlId];
  if (best === undefined) return "--:--";
  const m = Math.floor(best / 60).toString().padStart(2, "0");
  const s = (best % 60).toString().padStart(2, "0");
  return `${m}:${s}`;
}

function goToNextLevel() {
  els.winOverlay.classList.remove("active");
  const nextIdx = state.levelIndex + 1;
  if (nextIdx < PATCHES_LEVELS.length) {
    startLevel(nextIdx);
  } else {
    showScreen("select");
  }
}

// ---------- Wire up static UI ----------
document.getElementById("btnBack").addEventListener("click", () => showScreen("select"));
document.getElementById("btnUndo").addEventListener("click", undoLast);
document.getElementById("btnReset").addEventListener("click", resetLevel);
document.getElementById("btnHint").addEventListener("click", giveHint);
document.getElementById("btnNextLevel").addEventListener("click", goToNextLevel);
document.getElementById("btnBackToLevels").addEventListener("click", () => {
  els.winOverlay.classList.remove("active");
  showScreen("select");
});
document.getElementById("btnHowTo").addEventListener("click", () => {
  els.howToOverlay.classList.add("active");
});
document.getElementById("btnCloseHowTo").addEventListener("click", () => {
  els.howToOverlay.classList.remove("active");
});
document.getElementById("btnPlayDaily").addEventListener("click", () => {
  const dailyId = getDailyLevelId();
  const idx = PATCHES_LEVELS.findIndex((l) => l.id === dailyId);
  startLevel(idx === -1 ? 0 : idx);
});

renderLevelSelect();
showScreen("select");
