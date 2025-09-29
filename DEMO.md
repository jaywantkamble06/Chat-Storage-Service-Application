Demo Scenarios: Chat Storage Service

Prereqs
- Java 17, Postgres running (per `application.yml`), API keys configured via `API_KEYS`.
- Optional: `OPENAI_API_KEY` for agent replies.

Run
1) `mvn spring-boot:run`
2) Import Postman files:
   - `postman/Chat-Storage-Service.postman_environment.json`
   - `postman/Chat-Storage-Service.postman_collection.json`
3) Select the environment and set `apiKey` to one of your configured keys.

Scenario Flow (sequential)
1) Create Session
   - POST /api/sessions
   - Saves `sessionId` to environment
2) Add User Message
   - POST /api/sessions/{{sessionId}}/messages?userId={{userId}}
3) List Messages
   - GET /api/sessions/{{sessionId}}/messages?userId={{userId}}
4) Agent Reply (optional needs OPENAI_API_KEY)
   - POST /api/sessions/{{sessionId}}/agent/reply?userId={{userId}}&systemPrompt=...
5) Rename Session
   - PATCH /api/sessions/{{sessionId}}/title?userId={{userId}}
6) Favorite Session
   - PATCH /api/sessions/{{sessionId}}/favorite?userId={{userId}}
7) Delete Session
   - DELETE /api/sessions/{{sessionId}}?userId={{userId}}

Notes
- All calls require header `X-API-KEY`.
- Logs are JSON-structured; correlation ID is echoed in `X-Correlation-ID`.
- For multiple keys, set `API_KEYS="key1,key2"`.


