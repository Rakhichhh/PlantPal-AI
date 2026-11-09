# Acceptance scenarios — run locally and record observed output

| # | Scenario | Inputs | Expected result | Actual result |
|---|---|---|---|---|
| 1 | Create plant | Name Monstera, room Living Room, 7 days | Plant saved, initial care tasks appear | To verify |
| 2 | Edit plant | Change room to Bedroom | Room persists after page refresh | To verify |
| 3 | Strategy swap | Standard -> Conservative | Next soil-check date changes, event notification created | To verify |
| 4 | Observer event | Complete pending soil check | Task completed, notification generated, stats updated | To verify |
| 5 | Persistence | Restart app after saving | Previous plants, tasks and settings remain | To verify |
| 6 | Invalid name | Empty nickname | Validation error; no plant created | To verify |
| 7 | Invalid interval | 0 or 1000 days | Validation error; no plant created | To verify |
| 8 | Missing plant | Open /api/plants/999999 | 404 error with meaningful response | To verify |
| 9 | AI failure | Scan without GEMINI_API_KEY | Error shown; manual add still works | To verify |
| 10 | CSV export | Download after task completion | Valid CSV with summary and saved tasks | To verify |
| 11 | Visitor empty case | No report entries | Zero tasks, 0% completion, no divide-by-zero | Automated test |
| 12 | Security of CSV | Name starts with =SUM(1,1) | Serialized as data, not spreadsheet formula | Automated test |

Automated tests are in `src/test/java`. Run `mvn clean test` and attach terminal screenshots to the final report. Replace 'To verify' only after observing each test.
