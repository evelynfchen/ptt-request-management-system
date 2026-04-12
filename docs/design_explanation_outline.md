# Design Commentary


This system manages the assignment of Part-Time Teaching (PTT) staff to courses, matching members to teaching requests based on their skills. The design prioritises clarity, extensibility, and low coupling through a combination of well-established patterns and deliberate structural choices.


## Inheritance Hierarchy and the Abstract `User`

The `User` class serves as an abstract base for `Administrator`, `CourseDirector`, and `PTTMember`. This is a straightforward application of **generalisation**, capturing shared identity attributes (`guid`, `name`) in one place and avoiding repetition across subclasses. Only `PTTMember` carries additional domain-specific state — skills, training records, and trained status — keeping the hierarchy shallow and purposeful rather than over-engineered.


## Strategy Pattern for Matching

The most deliberate design decision is the application of the **Strategy Pattern** to candidate matching. The `MatchingStrategy` interface declares a single `match(TeachingRequest, List<PTTMember>)` method, and `SkillMatchingStrategy` provides the concrete implementation. A `MatchingEngine` holds a reference to whichever strategy is injected at construction time.

This decouples the *algorithm* from the *context that uses it*. Adding a new matching rule — say, ranking by availability, budget fit, or training history — requires only a new class implementing `MatchingStrategy`, with zero changes to `MatchingEngine` or any calling code. This directly satisfies the **Open/Closed Principle**: the system is open for extension but closed for modification.


## Singleton for the Database

`SimpleDatabase` uses the **Singleton Pattern** with `synchronized` access to ensure only one in-memory store exists across the application lifetime. This is appropriate here because the flat-file persistence model (`data/data.txt`) would produce race conditions or duplicate state with multiple instances. The trade-off is reduced testability — a singleton is hard to swap for a mock — but for the scale of this system, the simplicity outweighs that concern.


## Enum for Request Status

Using a `RequestStatus` enum (`PENDING`, `MATCHED`) rather than a plain string or boolean provides type safety and makes invalid states unrepresentable at compile time. It also makes the persistence layer's `parseStatus` method straightforward and resilient: unknown values gracefully fall back to `PENDING` rather than crashing.


## Reducing Coupling via the `TeachingRequest` Aggregate

`TeachingRequest` bundles a `Course`, a list of required skills, matched candidates, and a status into a single cohesive aggregate. Services interact with this one object rather than passing multiple loose parameters. This reduces the surface area of inter-component dependencies and means that extending a request (e.g., adding a deadline or priority) is localised to one class.


## Flat-File Persistence with Section Parsing

`SimpleDatabase` reads and writes a structured plain-text file divided into `[COURSES]`, `[PTT_MEMBERS]`, and `[REQUESTS]` sections. The parsing is handled by an inner `Section` enum with a `fromHeader` factory method, keeping section-detection logic encapsulated and easy to extend. Invalid lines are skipped gracefully with a console warning rather than aborting the load — a pragmatic choice for a lightweight persistence layer.


## Separation of Concerns

The three packages — `entity`, `service`, and `persistence` — enforce a clear layered structure. Entities carry state and domain logic (e.g., `hasAllSkills`). Services encapsulate algorithmic behaviour. Persistence handles I/O. Neither the entity layer nor the service layer has any dependency on `SimpleDatabase`, meaning matching logic can be unit-tested in isolation without touching the file system.


## Summary

The design makes targeted, justified use of Strategy, Singleton, and Enum patterns, combined with a clean package structure to minimise coupling. The result is a system that is easy to extend — particularly in its matching logic — without requiring widespread changes, and where each component has a single, well-defined responsibility.

