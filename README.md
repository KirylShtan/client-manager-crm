# Client Manager CRM

Internal CRM for a **law firm**: manage active clients, case status, archives, files, and staff notifications.

Real business tool used in daily operations — not a tutorial demo.  
Deployed on **AWS** with Docker, Jenkins CI/CD, and HashiCorp Vault for secrets.

---

## Tech stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17, Spring Boot, Spring Data JPA / Hibernate |
| Database | MySQL 8 |
| Security | Spring Security + JWT |
| Secrets | HashiCorp Vault (Spring Cloud Vault) |
| Frontend | React, Tailwind CSS, Framer Motion |
| API docs | Swagger / OpenAPI |
| Notifications | Email + Telegram bot |
| CI/CD | Jenkins |
| Containers | Docker Compose (dev + production) |
| Hosting | AWS |

---

## Features

### Clients & cases

- Add and edit clients
- Track case status for active clients
- Archive completed cases (positive / negative outcomes)
- Search and filter clients
- Notes on client records
- Concurrent update handling (`ConcurrentUpdateException`)

### Files

- Upload / download files linked to a client
- Shared (common) files for the office
- File storage with dedicated exception handling

### Auth & security

- Login with JWT
- Spring Security configuration
- Secrets and sensitive config via **Vault** (not hardcoded in production)

### Notifications

- Email notifications (templates)
- Telegram bot / subscribers / webhooks

### Frontend

- React SPA with Tailwind UI
- Smooth animations (Framer Motion)
- Login screen and protected CRM views
- Optional Chrome extension for gov autofill (`gov-autofill-extension`)

### Quality

- Backend unit / controller tests
- Frontend component tests
- Global exception handling (`@ControllerAdvice`-style handler)
- Swagger UI for API exploration

---

## Architecture

```
Browser (React :3000)
    │  JWT
    ▼
Spring API (:8080) ──► MySQL
    │
    ├── Vault (:8200)     secrets
    ├── Email             notifications
    └── Telegram bot      alerts / webhooks
```

Production stack (AWS host):

```
Docker Compose (production)
  ├── frontend (nginx)
  ├── backend (Spring Boot)
  ├── mysqlServer
  ├── vault (+ TLS)
  └── phpMyAdmin (localhost-only bind)
```

---

## Ports (typical)

| Service | Port |
|---------|------|
| React UI | `3000` |
| Spring API | `8080` |
| Swagger UI | `8080/swagger-ui/index.html` |
| MySQL | `3306` (internal in Docker) |
| Vault | `8200` |
| phpMyAdmin (prod compose) | `127.0.0.1:8082` |

---

## Live demo

> Add your public AWS URL here, for example:  
> **https://your-domain-or-ec2-ip**

Also set the same URL as the repository **Homepage** on GitHub.

---

## How to run locally

### Prerequisites

- Java 17+
- Maven
- Node.js + npm
- Docker Desktop (recommended)  
  or local MySQL (e.g. XAMPP)

### Option A — Docker Compose

```powershell
cd client-manager-crm
docker compose up -d --build
```

- Frontend: `http://localhost:3000`  
- API: `http://localhost:8080`  
- Swagger: `http://localhost:8080/swagger-ui/index.html`

Configure env vars / `.env` for DB passwords, JWT secret, Vault token, mail and Telegram as needed (see `docker-compose.yml` and production compose).

### Option B — Manual (dev)

**1. Database**

Create MySQL database `client_manager` (or your name) and set credentials in:

`backend/src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/client_manager
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

**2. Backend**

```powershell
cd backend
mvn spring-boot:run
```

API: `http://localhost:8080`

**3. Frontend**

```powershell
cd frontend
npm install
npm start
```

UI: `http://localhost:3000` (talks to API on `:8080`)

---

## Production (AWS)

Production layout is defined in `docker-compose.production.yml`:

- MySQL with persistent volume
- Backend with env-based DB, Vault, JWT, mail, Telegram, CORS
- Frontend nginx image on port `3000`
- Vault with TLS config under `vault_config/`
- phpMyAdmin bound to **localhost only** (`127.0.0.1:8082`)

Helper script: `scripts/jenkins/vault-unseal.ps1`  
CI pipeline: `JenkinsFile`

Typical flow:

1. Build & test in Jenkins  
2. Deploy compose stack on the AWS instance  
3. Unseal / configure Vault as required  
4. Serve frontend + API behind the host firewall / reverse proxy  

Do **not** commit real production secrets. Use Vault + env files outside git.

---

## Main API areas

| Area | Controllers (examples) |
|------|-------------------------|
| Auth | `AuthController` — login / JWT |
| Active clients | `ActualClientController` |
| Base clients | `BasicClientController` |
| Completed / archive | `CompletedClientController` |
| Client files | `ClientFileController` |
| Common files | `CommonFileController` |
| Notifications | `NotificationController` |
| Telegram | `TelegramController` |

Explore full contracts in Swagger when the API is running.

---

## Project structure

```
client-manager-crm/
  backend/                 # Spring Boot API
    src/main/java/.../
      controller/
      service/
      repository/
      model/
      dto/
      security/            # JWT filter, JwtService, SecurityConfig
      exception/
      swagger/
    Dockerfile
  frontend/                # React SPA
    src/components/
    src/api/
    gov-autofill-extension/
    Dockerfile
    nginx.conf
  vault_config/            # Vault HCL + TLS material layout
  scripts/jenkins/         # Vault unseal helper
  docker-compose.yml
  docker-compose.production.yml
  JenkinsFile
  README.md
```

---

## Tests

### Backend

```powershell
cd backend
mvn test
```

### Frontend

```powershell
cd frontend
npm test
```

---

## Screenshots

Screenshots live under `frontend/public/screenshots/`:

- Active clients  
- Positive archive  
- Negative archive  

---

## Author

**Kiryl Arlou**  
Greater Poland Voivodeship, Poland  

- GitHub: [KirylShtan](https://github.com/KirylShtan)  
- Related portfolio: [education-trips-crm](https://github.com/KirylShtan/education-trips-crm) (Spring Boot + Angular + Keycloak + Redis + Jenkins)

---

## License

Private / internal business tool · portfolio showcase.
