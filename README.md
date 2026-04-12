SE_Teamwork

Data File Format

Path
`data/data.txt`

Structure
```text
[COURSES]
courseCode|budget

[PTT_MEMBERS]
guid|name|skills|available

[REQUESTS]
courseCode|requiredSkills|status|matchedMember
```

Examples
```text
[COURSES]
CS101|1000
CS102|1200

[PTT_MEMBERS]
M001|Alice|Java,SQL|true
M002|Bob|Python|false

[REQUESTS]
CS101|Java|MATCHED|M001
CS102|Python|PENDING|
```

Rules
- Sections are identified by the header lines in brackets.
- Fields are separated by `|`.
- Skills are comma-separated within the skills field.
- Empty lines are allowed.
- Lines with the wrong number of fields are ignored during load.
- `available` must be `true` or `false`.
- `status` must be `PENDING` or `MATCHED`.
- `matchedMember` may be empty.
