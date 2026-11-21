#!/usr/bin/env python3
"""许可来源审计 —— 判定仓库里每个 .kt 的相对独立性。

用法:
  python3 scripts/license_audit.py --template <解包后的上游模板 java 目录> [--example <miuix 仓库>/example]

退出码:
  0  全部通过
  1  存在未处理文件（CI 应在此失败）
"""
import argparse, difflib, json, os, sys


def read_files(root):
    out = {}
    for dp, _, fs in os.walk(root):
        for f in fs:
            if f.endswith(".kt"):
                p = os.path.join(dp, f)
                try:
                    src = open(p, encoding="utf-8", errors="ignore").read()
                except OSError:
                    continue
                out[os.path.relpath(p, root)] = [l.strip() for l in src.splitlines() if l.strip()]
    return out


def best_sim(lines, pool_items):
    best, near = 0.0, ""
    for name, other in pool_items:
        if difflib.SequenceMatcher(None, lines, other).quick_ratio() <= best + 0.05:
            continue
        r = difflib.SequenceMatcher(None, lines, other).ratio()
        if r > best:
            best, near = r, name
    return round(best, 3), near


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--src", default="app/src/main/java")
    ap.add_argument("--template", required=True)
    ap.add_argument("--example")
    ap.add_argument("--threshold", type=float, default=0.85)
    ap.add_argument("--json-out")
    args = ap.parse_args()

    local = read_files(args.src)
    tpl = list(read_files(args.template).items())
    exm = list(read_files(args.example).items()) if args.example else []
    rows, fail = [], 0
    for rel, lines in sorted(local.items()):
        s_tpl, n_tpl = best_sim(lines, tpl)
        s_exm, n_exm = (best_sim(lines, exm) if exm else (0.0, ""))
        if s_tpl >= args.threshold and s_exm < args.threshold:
            verdict = "DERIVED-AGPL"
            fail += 1
        elif s_exm >= args.threshold:
            verdict = "DERIVED-APACHE-OK"
        elif s_tpl >= 0.60:
            verdict = "REVIEW"
        else:
            verdict = "INDEPENDENT"
        rows.append(dict(file=rel, sim_template=s_tpl, near_template=n_tpl,
                         sim_example=s_exm, near_example=n_exm, verdict=verdict))
    if args.json_out:
        json.dump(rows, open(args.json_out, "w", encoding="utf-8"), indent=1, ensure_ascii=False)
    counts = {}
    for r in rows:
        counts[r["verdict"]] = counts.get(r["verdict"], 0) + 1
    for k in sorted(counts):
        print("%-20s %3d" % (k, counts[k]))
    for r in rows:
        if r["verdict"] in ("DERIVED-AGPL", "REVIEW"):
            print("  %-13s %.2f  %s" % (r["verdict"], r["sim_template"], r["file"]))
    print("TOTAL %d files, blocking=%d" % (len(rows), fail))
    return 1 if fail else 0


if __name__ == "__main__":
    sys.exit(main())

