# Momentum
[Live Demo](https://momentum-f0p6.onrender.com)

A personal task manager to keep you focused on whats important.

## About

Momentum is a project that lets you add, complete, and remove tasks, with a focus on hour-by-hour deadline ordering.
The app sorts tasks by their closest upcoming deadline so the next thing you need to work on is always front and center.

As a university student, procrastination is a problem that I see almost every student face for exams, assignments, and projects.
I wanted to build an app to learn and experience full-stack developing. However, I also wanted this app to be useful to myself and my peers.

Personally, typing out homework lists or making clunky excel sheets felt impractical. I wanted a simple, yet modern, way of keeping track of my deadlines.
My solution was Momentum -  an effortless way to ensure you never miss a deadline.

## Features

- Add, complete, and delete tasks
- Tasks surfaced in chronological order, with the most urgent one pinned at the top with a live countdown
- Custom categories with drag-and-drop tab ordering that persists across restarts
- Deleting a category leaves its tasks intact and uncategorized; renaming one carries every task with it
- All data stored in MySQL, so state survives restarts and redeploys

## Tech stack

- **Backend:** Java 21, Spring Boot 4
- **Persistence:** MySQL 8.4, accessed with Spring's JdbcClient
- **Frontend:** Vanilla HTML, CSS, and JavaScript, with Flatpickr for date selection and SortableJS for drag-and-drop
- **Build tool:** Maven
- **Local infrastructure:** Docker Compose

## Getting started

#### Prerequisites

- JDK 21 or higher
- Maven (or use the bundled wrapper `./mvnw`)
- Docker, to run MySQL locally, or an existing MySQL 8.4 server


1. **Set your database passwords**
```bash
cp .env.example .env
```
Fill in MYSQL_ROOT_PASSWORD and MYSQL_PASSWORD. .env is gitignored and should stay that way.

2. **Start MySQL**
```bash
docker compose up -d db
```

This creates the momentum database and user, and keeps the data in a named Docker volume so it survives container restarts.

MySQL only reads those environment variables the first time it starts against an empty volume. If you change the password afterwards, run docker compose down -v to reset it — that deletes the data.

3. **Run the app**

Docker Compose reads .env on its own, but Spring Boot reads the shell environment, so export the password before starting:

```bash
export MYSQL_PASSWORD=<the value from .env>
./mvnw spring-boot:run
```

The schema is created automatically on first startup. Then open http://localhost:8080.



