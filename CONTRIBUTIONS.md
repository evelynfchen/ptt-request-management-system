# Contribution Map and Git Evidence

This document records exactly who wrote what in this codebase, so my own contribution is verifiable rather than asserted. It was produced by a read-only audit of the original team repository's Git history (`git log`, `git blame --line-porcelain`, `git branch --contains`), cross-checked against this showcase copy. Teammates' names are taken directly from their Git author identities as recorded in that history.

This was a **six-person team project** for a University of Glasgow MSc Software Development module. The team repository itself is private; this repository is my personal portfolio copy of the submitted application.

## Authorship by file

| File | Lines | Author breakdown (by `git blame`) | Mine |
|---|---|---|---|
| `src/entity/PTTMember.java` | ~70 | 100% Eleanor Chen (me) | ✅ 100% |
| `src/entity/TeachingRequest.java` | ~70 | 100% Eleanor Chen (me) | ✅ 100% |
| `src/entity/TrainingRecord.java` | ~26 | 100% Eleanor Chen (me) | ✅ 100% |
| `src/entity/RequestStatus.java` | ~6 | 100% Eleanor Chen (me) | ✅ 100% |
| `src/entity/Course.java` | ~26 | 100% Eleanor Chen (me) | ✅ 100% |
| `src/entity/User.java` | ~27 | 100% Eleanor Chen (me) | ✅ 100% |
| `src/entity/Administrator.java` | ~7 | 100% Eleanor Chen (me) | ✅ 100% |
| `src/entity/CourseDirector.java` | ~8 | 100% Eleanor Chen (me) | ✅ 100% |
| `src/EntityTest.java` | 60 | 100% Eleanor Chen (me) | ✅ 100% |
| `docs/progress_checklist.md` | ~89 | 100% Eleanor Chen (me) | ✅ 100% |
| `src/service/SkillMatchingStrategy.java` | 32 | 31 lines teammate (`jiahuigao9`), 1 line me | 🟡 1 line |
| `src/Main.java` | 175 | 155 lines teammate (`FanYang`), 16 lines me, 4 lines `KERONG HUA` | 🟡 16 lines |
| `src/persistence/SimpleDatabase.java` | 419 | 100% `KERONG HUA` | ❌ 0% |
| `src/service/MatchingEngine.java` | 18 | 100% `jiahuigao9` | ❌ 0% |
| `src/service/MatchingStrategy.java` | 9 | 100% `jiahuigao9` | ❌ 0% |
| `src/service/RequestManager.java` | 35 | 100% `jiahuigao9` | ❌ 0% |
| `src/service/TrainingManager.java` | 14 | 100% `jiahuigao9` | ❌ 0% |
| `docs/class_responsibilities.md` | — | 100% `felixyangio` | ❌ 0% |
| `docs/design_explanation_outline.md` | — | 100% `huit960789-cmyk` | ❌ 0% |
| `docs/report.md` | — | 100% `huit960789-cmyk` | ❌ 0% |
| `docs/user_stories.md` | — | 100% `KAIYUAN ZHANG` | ❌ 0% |

Across the whole committed codebase (~1,000 lines of `src/`), my own commits account for roughly 170 lines — the full entity layer plus the test harness, plus small, specific additions to two files owned by teammates. The remainder (CLI menu loop, persistence, matching algorithm/Strategy Pattern plumbing) was built by teammates, as shown above.

## What I actually did, by commit

**`5bdf95d` — "Add entity layer skeleton classes"** and **`1948f5c` — "Improve entity layer implementation"**
Initial design and implementation of the entity layer: `PTTMember`, `TeachingRequest`, `TrainingRecord`, `RequestStatus`, `Course`, `User`, `Administrator`, `CourseDirector` — fields, constructors, getters/setters, and the first versions of the validation methods.

**`0e8a82f` — "Finish Saturday system flow: request, matching, training, persistence"**
- In `Main.java`: added course-budget input with `NumberFormatException` handling (defaults to 0 on bad input instead of crashing); removed a redundant direct call into the persistence layer that duplicated what `RequestManager` already did; wired the "match candidates" menu option to set the request's status to `MATCHED` when matches are found; minor local-variable renames for clarity.
- In `TeachingRequest.java`: added `isMatched()`.
- In `SkillMatchingStrategy.java` (1 line, in a file otherwise written by a teammate): replaced an inline skill check (`staff.getSkills() != null && staff.getSkills().containsAll(required)`) with a call to my own `staff.hasAllSkills(required)` method — i.e. the teammate's matching algorithm calls into my validation logic rather than re-implementing the check inline.

**`7fe1628` — "Complete Sunday entity improvements: status auto update and prevent duplicate skills"** (merged via PR #5, merge commit `76cc8d4`)
- `PTTMember.java`: changed the constructor and `setSkills()`/`setTrainingRecords()` to copy incoming lists (`new ArrayList<>(skills)`) instead of storing the caller's list reference directly; added a duplicate guard to `addSkill()`; added a null/blank guard to `hasSkill()`.
- `TeachingRequest.java`: changed the constructor and `setRequiredSkills()`/`setMatchedCandidates()` to copy incoming lists the same way; **added the duplicate-candidate guard to `addMatchedCandidate()`** (`!this.matchedCandidates.contains(candidate)`) and made it set `status = RequestStatus.MATCHED` as part of the same call, so a request can't end up with a duplicate candidate or a stale `PENDING` status after a successful match.
- Added `EntityTest.java` (60 lines) — a standalone manual test harness exercising this behaviour directly (create a request, match a candidate by skill, confirm the status flips to `MATCHED`, record training, confirm the trained flag flips).

## Defensive copying — exact scope (please read before citing this as "immutability")

Constructors and setters on `PTTMember` and `TeachingRequest` copy any `List` passed in, so the object doesn't end up holding — and silently sharing — the exact list reference a caller passed in. That's real, and it's the change made in `7fe1628` above.

**However, the getters on both classes (`getSkills()`, `getTrainingRecords()`, `getRequiredSkills()`, `getMatchedCandidates()`) return the live internal list reference, not a copy or an unmodifiable view.** A caller can do `request.getMatchedCandidates().clear()` and mutate the entity's internal state directly. So the accurate claim is: *"constructors and setters defensively copy incoming list parameters so the object isn't left sharing the caller's original list"* — not that the class is immutable, or that its internal state is fully protected from external mutation. I have not claimed the latter anywhere in the README.

## Skill-matching: precise division of responsibility

- **Mine:** `PTTMember.hasAllSkills(List<String> requiredSkills)` — the reusable method that checks whether a staff member's skill set covers everything a request needs (`skills.containsAll(requiredSkills)`, with a null guard).
- **Teammate's (`jiahuigao9`):** `SkillMatchingStrategy.match()` — the Strategy Pattern implementation that loops over all staff and calls `hasAllSkills()` on each one to build the list of matches, and the `MatchingStrategy` interface / `MatchingEngine` wrapper around it.
- The one line I changed in `SkillMatchingStrategy.java` (commit `0e8a82f`) swapped a duplicated inline skill-check for a call to my `hasAllSkills()` method, so the teammate's algorithm uses my validation logic rather than its own copy of the same check.

I did not design or implement the Strategy Pattern itself (the interface, the engine that holds a strategy and delegates to it, or the decision to use that pattern) — that was the teammate's work. My contribution is the validation method it calls.

## Duplicate-candidate prevention and status transition

`TeachingRequest.addMatchedCandidate(PTTMember candidate)`:
```java
public void addMatchedCandidate(PTTMember candidate) {
    if (candidate != null && !this.matchedCandidates.contains(candidate)) {
        this.matchedCandidates.add(candidate);
        this.status = RequestStatus.MATCHED;
    }
}
```
Before commit `7fe1628`, this method had no duplicate check and did not update `status`, so a request could accumulate the same candidate more than once and could stay `PENDING` even after a candidate had been added. I added the `contains()` guard (so adding the same candidate twice is a no-op) and the automatic transition to `MATCHED` in the same method, so the two pieces of state — "who's matched" and "what status is this request" — can't drift out of sync.

## CLI integration

The CLI itself — the menu loop, option dispatch, and overall `Main` structure — was built by a teammate (`FanYang`, 155 of 175 lines). My 16 lines (commit `0e8a82f`, detailed above) added input validation for the course-budget field and wired the status-update behaviour into the "match candidates" option. I did not design the CLI's architecture or menu flow.

## Testing

`EntityTest.java` (60 lines, 100% mine) is a manual test harness — a `main()` method that runs through a scenario (create request → match by skill → check status → record training → check trained flag) and prints the results, rather than a JUnit test suite. It verifies the entity-layer logic described above, including the status auto-update and the matching call into `hasAllSkills()`.

## Verification methodology

- `git log --author="Eleanor Chen"` and `git log --all` on the original team repository (private; not included here) to identify relevant commits.
- `git blame --line-porcelain <file> | grep "^author " | sort | uniq -c` per file, to get per-line authorship rather than trusting commit-level authorship (a single commit can touch files written by other people).
- `git branch --contains <commit>` and `git log --merges` to confirm which commits were actually merged into `main` (and via which PR).
- Line counts taken directly from the files in this repository (`wc -l`), cross-checked against the blame output above.
