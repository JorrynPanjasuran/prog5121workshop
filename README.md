# SwiftDeliver

PROG5121 Portfolio of Evidence — Food Delivery Coordination Application

**Student:** Jorryn Panjasuran (ST10448822)
**Module:** PROG5121/p/w
**Language:** Java 17 | Tests: JUnit 5 | Build: Maven

---

## What it does

SwiftDeliver is a console-based messaging app for a food delivery company. Kitchen managers, branch supervisors, and delivery riders use it to coordinate orders in real time. There is no GUI — all interaction is through typed input and printed output.

## Structure

| Branch | Contents | Tests |
|--------|----------|-------|
| `part1` | Account setup and sign-in only | 22 |
| `part2` | Part 1 + message dispatching + JSON persistence | 62 |
| `main`  | Full application including arrays and reports | 132 |

## Source files

| File | Purpose |
|------|---------|
| `SwiftDeliver.java` | Main entry point — registration, sign-in, menu loop |
| `Login.java` | Handle / passkey / contact number validation and auth |
| `Message.java` | Message ID generation, hash creation, JSON persistence |
| `MessageManager.java` | Five message arrays, search, delete, and report operations |

## Running the app

```
mvn clean compile exec:java
```

## Running tests

```
mvn clean test
```

All 132 tests must pass before submission.

## Scenario document

`SwiftDeliver_Scenario.html` — open in any browser for the full POE scenario, test data, rubric, and attribution.
