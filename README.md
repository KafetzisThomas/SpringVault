<div align="center">
    <img src="src/main/resources/static/favicon.ico" width="150"/>
    <p><strong>SpringVault: </strong>Securely upload your documents to your own vault.<br>Written in Java/Spring Boot</p>
</div>

## Features

- **AES-256 GCM** encryption for file storage
- Support for common formats: `PDF`, `DOCX`, `XLSX`, `TXT`, etc
- Upload, view, download and delete documents

## Tech Stack

Built with Java 25, Spring Boot, PostgreSQL, Thymeleaf and Bootstrap 5.

## Database Schema

![Database Schema](/assets/db_schema.png)

## Usage

Start the PostgreSQL database:

```bash
docker compose up -d
```

Run the Spring Boot application:

```bash
# RECOMMENDED: PowerShell/Linux/Mac
./mvnw spring-boot:run

# Windows CMD
.\mvnw.cmd spring-boot:run
```

Access web application at http://127.0.0.1:8080 or http://localhost:8080.

## Run Tests

```bash
./mvnw test
```

## Demo Images

![Documents List](/assets/documents_list.png)

![Upload Form](/assets/upload_form.png)
