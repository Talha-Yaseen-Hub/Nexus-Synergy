"""
Generates 'Patches' puzzle levels (Shikaku-style rectangle partition puzzles)
and writes them out as a JS file (levels.js) consumed by the game.

Algorithm:
1. Start with the full grid as one rectangle.
2. Repeatedly pick the largest rectangle in a working list and split it in
   half (horizontally or vertically, with a bit of randomness) until we
   have `target_pieces` rectangles, subject to a minimum edge length of 1.
3. For every resulting rectangle, record its area (the clue number) and
   shape category (square / tall / wide), then pick a random cell inside
   the rectangle to host the clue.
4. Shuffle clue positions are deterministic per level via a seeded RNG so
   the output is reproducible.
"""
import json
import random

def split_rect(rects, rng):
    # pick the rectangle with the largest area that is splittable
    splittable = [r for r in rects if r[2] >= 2 or r[3] >= 2]
    if not splittable:
        return False
    # bias towards splitting bigger rectangles so pieces stay reasonably balanced
    splittable.sort(key=lambda r: r[2] * r[3], reverse=True)
    # take one of the top few at random for variety
    pool = splittable[: max(1, len(splittable) // 2)]
    x, y, w, h = rng.choice(pool)
    rects.remove((x, y, w, h))

    can_v = w >= 2
    can_h = h >= 2
    if not can_v and not can_h:
        rects.append((x, y, w, h))
        return False

    if can_v and can_h:
        direction = rng.choice(["v", "h"])
    elif can_v:
        direction = "v"
    else:
        direction = "h"

    if direction == "v":
        cut = rng.randint(1, w - 1)
        rects.append((x, y, cut, h))
        rects.append((x + cut, y, w - cut, h))
    else:
        cut = rng.randint(1, h - 1)
        rects.append((x, y, w, cut))
        rects.append((x, y + cut, w, h - cut))
    return True


def generate_level(rows, cols, target_pieces, seed):
    rng = random.Random(seed)
    rects = [(0, 0, cols, rows)]
    attempts = 0
    while len(rects) < target_pieces and attempts < target_pieces * 20:
        ok = split_rect(rects, rng)
        attempts += 1
        if not ok and all((r[2] < 2 and r[3] < 2) for r in rects):
            break

    clues = []
    for (x, y, w, h) in rects:
        area = w * h
        if w == h:
            shape = "square"
        elif h > w:
            shape = "tall"
        else:
            shape = "wide"
        # Anchor the clue at one of the rectangle's four corners so a
        # corner-to-corner drag in the UI can always reconstruct the shape.
        corners = [(x, y), (x + w - 1, y), (x, y + h - 1), (x + w - 1, y + h - 1)]
        cx, cy = rng.choice(corners)
        clues.append({
            "row": cy, "col": cx, "value": area, "shape": shape,
            "solution": {"row": y, "col": x, "w": w, "h": h},
        })

    clues.sort(key=lambda c: (c["row"], c["col"]))
    return {"rows": rows, "cols": cols, "clues": clues, "pieces": len(rects)}


def level_plan():
    plan = []
    # (rows, cols, target_pieces) tiers, 32 levels total, difficulty ramps up
    tiers = [
        (5, 5, range(1, 7)),    # levels 1-6   easy 5x5
        (6, 6, range(7, 13)),   # levels 7-12  6x6
        (7, 7, range(13, 19)),  # levels 13-18 7x7
        (8, 8, range(19, 25)),  # levels 19-24 8x8
        (9, 9, range(25, 31)),  # levels 25-30 9x9
        (10, 10, range(31, 33)),# levels 31-32 expert 10x10
    ]
    piece_counts = {
        5: [5, 6, 6, 7, 7, 8],
        6: [7, 8, 8, 9, 9, 10],
        7: [9, 10, 10, 11, 11, 12],
        8: [11, 12, 12, 13, 13, 14],
        9: [13, 14, 14, 15, 15, 16],
        10: [17, 18],
    }
    for rows, cols, levels in tiers:
        counts = piece_counts[rows]
        for idx, lvl in enumerate(levels):
            plan.append((lvl, rows, cols, counts[idx % len(counts)]))
    return plan


def main():
    levels = []
    for lvl, rows, cols, pieces in level_plan():
        # retry a few times if generation collapses to too few pieces
        seed = 1000 + lvl * 7
        data = generate_level(rows, cols, pieces, seed)
        tries = 0
        while data["pieces"] < pieces - 1 and tries < 5:
            seed += 999
            data = generate_level(rows, cols, pieces, seed)
            tries += 1
        levels.append({
            "id": lvl,
            "title": f"Level {lvl}",
            "rows": data["rows"],
            "cols": data["cols"],
            "clues": data["clues"],
        })

    with open("levels_data.json", "w") as f:
        json.dump(levels, f, indent=2)

    js = "// Auto-generated puzzle data for the Patches game.\n"
    js += "const PATCHES_LEVELS = "
    js += json.dumps(levels, indent=2)
    js += ";\n"
    with open("levels.js", "w") as f:
        f.write(js)

    print(f"Generated {len(levels)} levels")
    for lvl in levels:
        print(lvl["id"], lvl["rows"], lvl["cols"], len(lvl["clues"]))


if __name__ == "__main__":
    main()
