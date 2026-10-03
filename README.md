# Spring Boot Todo List Application

A RESTful Todo List API built with **Spring Boot** and **Java 21**, featuring **in-memory storage using a thread-safe `List`**, **pre-populated sample data**, comprehensive validation, exception handling, full test coverage, and an **interactive Web UI dashboard**.

---

## 🌟 Key Features

- **No Database Needed**: Uses a thread-safe `CopyOnWriteArrayList<Todo>` in `TodoService` with `AtomicLong` for ID generation.
- **Pre-populated Data**: Loaded automatically on startup with 5 realistic todo items with varied priorities, due dates, and completion statuses.
- **Complete REST API**:
  - `GET /api/todos`: Fetch all todos (with filtering by completion status, priority, keyword search, and sorting).
  - `GET /api/todos/{id}`: Fetch single todo by ID.
  - `POST /api/todos`: Create a new todo with validation.
  - `PUT /api/todos/{id}`: Full update of a todo.
  - `PATCH /api/todos/{id}`: Partial update of a todo.
  - `PATCH /api/todos/{id}/toggle`: Toggle completion status (`true`/`false`).
  - `DELETE /api/todos/{id}`: Delete a todo by ID.
  - `DELETE /api/todos/completed`: Bulk delete all completed todos.
  - `DELETE /api/todos`: Clear all todos.
  - `GET /api/todos/stats`: Aggregate metrics (total, completed, pending, completion %).
  - `POST /api/todos/reset`: Reset back to initial populated data.
- **Modern Interactive Dashboard**: Accessible directly at `http://localhost:8080/` with instant filters, live search, edit modal, and toast alerts.
- **Error Handling & Validation**: Centralized `@RestControllerAdvice` returning structured JSON error payloads with field-level validation errors.
- **Fully Tested**: 26 unit and integration test cases covering services and controllers.

---

## 🚀 Getting Started

### Prerequisites
- **Java 21** or higher (`java -version`)
- Included Maven Wrapper (`mvnw.cmd` on Windows, `./mvnw` on Linux/macOS)

### Running the Application

You can run the project using the Maven wrapper:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Or run the pre-built JAR:

```bash
java -jar target/todo-list-0.0.1-SNAPSHOT.jar
```

Once started, open your browser to:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 📖 REST API Endpoints

Base URL: `http://localhost:8080/api/todos`

| HTTP Method | Endpoint | Description | Status Code |
|-------------|----------|-------------|-------------|
| `GET` | `/api/todos` | List todos (supports filters & sorting) | `200 OK` |
| `GET` | `/api/todos/{id}` | Get single todo by ID | `200 OK` / `404 Not Found` |
| `POST` | `/api/todos` | Create a new todo | `201 Created` |
| `PUT` | `/api/todos/{id}` | Update all fields of an existing todo | `200 OK` / `404 Not Found` |
| `PATCH` | `/api/todos/{id}` | Partially update fields of an existing todo | `200 OK` / `404 Not Found` |
| `PATCH` | `/api/todos/{id}/toggle` | Toggle completion status | `200 OK` / `404 Not Found` |
| `DELETE` | `/api/todos/{id}` | Delete a single todo by ID | `204 No Content` / `404 Not Found` |
| `DELETE` | `/api/todos/completed` | Bulk delete all completed todos | `200 OK` |
| `DELETE` | `/api/todos` | Clear all todos | `204 No Content` |
| `GET` | `/api/todos/stats` | Get aggregate statistics | `200 OK` |
| `POST` | `/api/todos/reset` | Reset to initial populated sample data | `200 OK` |

### Query Parameters for `GET /api/todos`:
- `completed`: `true` or `false`
- `priority`: `LOW`, `MEDIUM`, or `HIGH`
- `search`: Keyword string (searches title and description)
- `sortBy`: `id` (default), `title`, `dueDate`, `priority`, or `createdAt`
- `order`: `asc` (default) or `desc`

---

## 📝 Example Requests

### 1. Create a Todo (`POST /api/todos`)
```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Prepare Demo Presentation",
    "description": "Showcase Spring Boot features and endpoints",
    "priority": "HIGH",
    "dueDate": "2026-10-15"
  }'
```

### 2. Toggle Todo Completion (`PATCH /api/todos/{id}/toggle`)
```bash
curl -X PATCH http://localhost:8080/api/todos/3/toggle
```

### 3. Filter & Sort (`GET /api/todos`)
```bash
curl "http://localhost:8080/api/todos?completed=false&priority=HIGH&sortBy=dueDate&order=asc"
```

### 4. Get Statistics (`GET /api/todos/stats`)
```bash
curl http://localhost:8080/api/todos/stats
```
**Response:**
```json
{
  "total": 5,
  "completed": 2,
  "pending": 3,
  "completionPercentage": 40.0
}
```

---

## 📁 Project Structure

```
D:\todo-list
├── pom.xml
├── mvnw / mvnw.cmd
├── src
│   ├── main
│   │   ├── java/com/example/todo
│   │   │   ├── TodoApplication.java
│   │   │   ├── controller
│   │   │   │   └── TodoController.java
│   │   │   ├── service
│   │   │   │   └── TodoService.java
│   │   │   ├── model
│   │   │   │   ├── Todo.java
│   │   │   │   └── Priority.java
│   │   │   ├── dto
│   │   │   │   ├── CreateTodoRequest.java
│   │   │   │   ├── UpdateTodoRequest.java
│   │   │   │   ├── TodoStats.java
│   │   │   │   └── ErrorResponse.java
│   │   │   └── exception
│   │   │       ├── ResourceNotFoundException.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources
│   │       ├── application.properties
│   │       └── static
│   │           └── index.html
│   └── test
│       └── java/com/example/todo
│           ├── TodoApplicationTests.java
│           ├── controller
│           │   └── TodoControllerTest.java
│           └── service
│               └── TodoServiceTest.java
```

---

## 🧪 Running Tests

Execute all 26 unit and integration tests with:

```bash
.\mvnw.cmd test
```
