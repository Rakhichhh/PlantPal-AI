# PlantPal AI — integrated project (Students 1–4)

Java 17 + Spring Boot 3.3.5 + H2 file storage + HTML/CSS/JS.

## Run
Rakhat,Tair,Nurtileu,Aibar
```bash
mvn clean test
mvn spring-boot:run
```

Visit http://localhost:8080. Runs without AI key (manual plant entry). For photo recognition set `GEMINI_API_KEY` on the backend, restart, and try the AI upload. Photo analysis requires internet and a valid Gemini account/key; do not commit credentials.

## 8 implemented patterns and owners

| Student | Pattern | Classes | App demonstration |
|---|---|---|---|
| 1 | Builder | `PlantBuilder` | Add plant with validated fields |
| 1 | Factory Method | `CareTaskCreator` / concrete creators | Initial plant tasks |
| 2 | Strategy | `CareSchedulingStrategy`, strategies | Change Standard/Conservative |
| 2 | Observer | `CareEventPublisher`, observers | New notification and reschedule |
| 3 | Adapter | `GeminiPlantAdapter`, `GeminiGateway` | Photo analysis and review |
| 3 | Decorator | `NotificationDecorator`, concrete decorators | Enriched activity notifications |
| 4 | Facade | `PlantCareFacade` | Add via workflow; choose mode; analytics |
| 4 | Visitor | `CareReportElement`, `CareReportVisitor`, `CareStatisticsVisitor` | Stats and CSV export |

## User journeys (live)
1. Add a plant manually or review AI suggestion, then save.
2. Browse/edit/delete saved plant and tasks.
3. Choose alternative care scheduling Strategy and inspect the changed task date.
4. Mark soil check completed and see Observer notification.
5. Read and acknowledge activity notifications.
6. Visit Care Analytics and export the persistent care report as CSV.

## Endpoints from Student 4
- `POST /api/workflows/plants`: validates request, delegates through facade
- `PUT /api/workflows/plants/{id}/care-mode` (JSON `{"mode":"CONSERVATIVE"}`)
- `PATCH /api/workflows/tasks/{id}/complete`
- `GET /api/workflows/summary`
- `GET /api/workflows/report.csv`

Existing `/api/plants`, `/api/tasks`, `/api/notifications` and `/api/plants/analyze` endpoints remain compatible.

## Persistent data and sample content
H2 file DB at `./data/plantpal.mv.db` (created at first run), demo data seeded on an empty database. Do not delete `data` if you want to retain changes. Test screenshots with UI and browser network panel. The CSV is compatible with spreadsheet apps.

## Validation and failure handling
Plant text fields, intervals, image upload file type/size, missing objects and API failures are checked. AI recognition is a suggestion, not a diagnosis. The CSV exporter quotes fields and prefixes spreadsheet-formula-looking values.

## Notes / boundaries
The notification center is in-app, not browser Push while tab is closed. No scheduling emails/SMS. System depends on externally configured Gemini API for AI features. The PlantPal application can be demonstrated without a key using manual entry.

## Team report checklist
Introduction; user scenarios; application layers; 8-pattern table including problem/participants/demo/owner; class diagram; main-flow sequence diagram; screenshots; at least 8 test scenarios with expected and observed results; automated test evidence; individual contribution; conclusion; future work; AI disclosure. Complete the report from actual observed results, not hypothetical tests.
