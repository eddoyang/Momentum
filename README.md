# Momentum

A personal task manager to keep you focused on what's important.

[**Live Demo**](https://momentum-f0p6.onrender.com)

Momentum lets you add, complete, and remove tasks, and pins the next incomplete task by deadline. The app sorts tasks by their closest upcoming deadline so the next thing you need to work on is always front and center.

Personally, typing out homework lists or making clunky Excel sheets felt impractical. I wanted a simple, yet modern, way of keeping track of my deadlines and battling procrastination at the same time.

My solution was Momentum — an effortless way to ensure you never miss a deadline.

## Features

- **Deadline-first ordering:** tasks sorted by when they're due, with the most urgent one pinned and counting down
- **Natural-language task entry:** plain English in, a filled-in form out
- **Categories with drag-and-drop ordering:** reorder tabs and the order persists
- **Safe category edits:** renaming a category updates every task that uses it; deleting one leaves its tasks intact and uncategorized, enforced by foreign keys
- **Persistent across restarts:** MySQL, not a file on disk

## Tech stack

| Layer | Choice |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 4 |
| Database | MySQL 8.4, via Spring's JdbcClient |
| LLM | Claude Haiku 4.5 |
| Frontend | HTML / CSS / JS, Flatpickr, SortableJS |
| Build | Maven |
| Local infra | Docker Compose |
| Deploy | Dockerfile on Render |

## Running it locally

**Prerequisites:** JDK 21+, Docker, and Maven (or the bundled `./mvnw` wrapper).

**1. Start MySQL**

```bash
docker compose up -d
```

**2. Set the environment**

Create a `.env` file in the project root:

```bash
MYSQL_PASSWORD=your_password
ANTHROPIC_API_KEY=sk-ant-...
```

Spring doesn't read `.env` on its own, so you have to hand the variables to the process yourself.

**Without an Anthropic key:** set `momentum.parser.enabled=false` in `application.properties`. Everything except natural-language entry works normally.

**3. Run**

```bash
./mvnw spring-boot:run
```

Then open <http://localhost:8080>.

## Project history

Momentum started as a JSON-file-backed app with three in-memory indexes: a map by ID, a map of category to task IDs, and a TreeMap keyed by deadline. Those became a primary key and two MySQL indexes.

The migration removed 611 lines and added 273. The service class went from 264 lines to 79. It also allowed me to build a natural-language task parser. The category foreign key forces the natural-language parser to validate its output instead of trusting it, since an invented category is rejected at insert.
