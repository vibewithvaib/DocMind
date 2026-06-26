# DocMind

## Description

DocMind is an internal system that allows administrators to upload company documents and employees to ask questions from those documents using natural language.

The system extracts text from uploaded PDF files, stores the content in smaller chunks, generates embeddings, and stores them in Redis. Whenever a user asks a question, the system retrieves the most relevant chunks and uses OpenAI to generate an answer based on the retrieved content.

Conversation history is also maintained using Redis so that follow-up questions can be answered with context from previous interactions.

---

## Features

### Authentication

* JWT Authentication
* Role Based Access Control
* Admin and Employee Roles
* Secured APIs using Spring Security

### Document Management

* Upload PDF documents
* Store document details
* Delete uploaded documents
* Admin-only document operations

### Document Processing

When a document is uploaded:

* Text is extracted using Apache PDFBox
* Text is divided into chunks
* Chunks are stored in MySQL
* Embeddings are generated for each chunk
* Embeddings are stored in Redis

### Question Answering

When a user asks a question:

* Relevant chunks are retrieved from Redis using similarity search
* Conversation history is retrieved from Redis
* Retrieved content is sent to OpenAI
* The generated answer is returned to the user

### Conversation Memory

Recent conversation history is stored in Redis.

This allows the system to understand follow-up questions and maintain context during a conversation.

---

## Workflow

Document Upload

```text
PDF
 ↓
Text Extraction
 ↓
Chunking
 ↓
Embedding Generation
 ↓
Redis Vector Store
```

Question Answering

```text
Question
 ↓
Similarity Search
 ↓
Relevant Chunks
 ↓
Conversation History
 ↓
OpenAI
 ↓
Answer
```

---

## Technologies Used

Backend

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* Spring AI

Database

* MySQL

Caching and Vector Storage

* Redis Cloud
* Redis Vector Store

Document Processing

* Apache PDFBox

AI Models

* OpenAI Chat Model
* OpenAI Embedding Model

Build Tool

* Maven

---

## Database Tables

### User

Stores user information.

Fields:

* id
* name
* email
* password
* role

### Document

Stores uploaded document information.

Fields:

* id
* fileName
* filePath
* uploadedAt
* uploadedBy

### DocumentChunk

Stores chunks generated from uploaded documents.

Fields:

* id
* chunkText
* document

---

## API Endpoints

Authentication

```http
POST /api/auth/register
POST /api/auth/login
```

Document Management

```http
POST /api/documents/upload
DELETE /api/documents/{id}
```

Chat

```http
POST /api/chat/ask
```

Sample Request

```json
{
  "question": "What is the leave policy?"
}
```

Sample Response

```json
{
  "answer": "Employees are entitled to 20 annual leaves."
}
```

---
* Analytics Dashboard