# Momentum

A personal task manager to keep you focused on whats important.

## About

Momentum is a hobby/learning project that lets you add, complete, and remove tasks, with a focus on hour-by-hour deadline ordering.
The app sorts tasks by their closest upcoming deadline so the next thing you need to work on is always front and center.

As a university student, procrastination is a problem that I see almost every student face for exams, assignments, and projects.
I wanted to build an app to learn and experience full-stack developing, but I also wanted this app to be useful to myself and my peers.

Personally, typing out homework lists or making clunky excel sheets felt impractical, and I wanted a simple, yet modern, way of keeping track of my deadlines.
My solution was Momentum -  an effortless way to ensure you never miss a deadline and keep you motivated on your tasks.

## Features

- Add, complete, and delete tasks
- Tasks are organized by hour buckets and surfaced in chronological order
- Persists tasks to disk between sessions

## Tech stack

- **Backend:** Java 21, Spring Boot 4
- **Persistence:** JSON file on disk
- **Frontend:** Vanilla HTML, CSS, and JavaScript
- **Build tool:** Maven

## Getting started

### Prerequisites

- JDK 21 or higher
- Maven (or use the bundled wrapper `./mvnw`)

### Run the app

From the project root:

```bash
./mvnw spring-boot:run
```

Then open [http://localhost:8080](http://localhost:8080) in your browser.

## Roadmap

- [ ] Live countdown to deadlines (frontend)
- [ ] Visual urgency colors based on time remaining
- [ ] Edit existing tasks
- [ ] Filter by category
- [ ] Online sync / multi-device support

## Notes for self
- Figure out possible AI implementations
