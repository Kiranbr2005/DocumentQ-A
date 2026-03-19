🚀 Document Q&A System (AI + RAG)
An AI-powered full-stack application that allows users to upload documents and ask natural language questions, with answers generated strictly from the document content using Retrieval-Augmented Generation (RAG).

🔍 Problem Statement
Traditional AI models often generate generic answers that are not grounded in actual data.
This project solves that by:
Retrieving relevant document content
Feeding it into the AI model
Generating context-aware, accurate answers

💡 Solution Overview
This system implements a RAG (Retrieval-Augmented Generation) pipeline:
Documents are uploaded and parsed
Content is split into smaller chunks
Chunks are stored in PostgreSQL
User query is matched using Full-Text Search
Relevant chunks are sent to the LLM (Groq - Llama 3.3 70B)
AI generates an answer based only on retrieved content

🏗️ Architecture
User Query
   ↓
Spring Boot Backend
   ↓
PostgreSQL (Full-Text Search)
   ↓
Relevant Document Chunks
   ↓
Groq API (LLM)
   ↓
Final Answer (Grounded Response)

🛠 Tech Stack
Backend-
Java 17+
Spring Boot 3.3
Spring AI

Frontend-
Angular 17+ (Standalone Components)

AI / LLM
Groq API (Llama 3.3 70B)

Database
PostgreSQL (Full-Text Search)

Other Tools-
Apache PDFBox (PDF Parsing)

✨ Features
📄 Upload documents (PDF, DOCX, TXT)
❓ Ask questions in natural language
🧠 AI answers based only on document content
👥 Multi-user support (session-based isolation)
📚 Manage multiple documents
🖱️ Drag & drop upload UI

🔑 Key Highlights
Implemented RAG pipeline to reduce generic answers
Optimized document chunking & retrieval strategy
Used PostgreSQL Full-Text Search
Built fully functional end-to-end AI system (frontend + backend)

⚙️ Setup & Installation
1. Clone Repository
git clone [https://github.com/your-username/document-qa.git](https://github.com/Kiranbr2005/DocumentQ-A.git)

2. Backend Setup(document-qa)
mvn clean install
Update application.properties:
spring.datasource.url=jdbc:postgresql://localhost:5432/document_db
spring.datasource.username=your_username
spring.datasource.password=your_password
groq.api.key=your_api_key

3. Frontend Setup(document-qa-ui)
npm install
ng serve


🧠 Learning Outcomes
Designed and implemented a real-world RAG system
Integrated LLM APIs into backend workflows
Built a scalable full-stack architecture
Improved understanding of AI system design patterns


Feel free to fork this repo and submit pull requests.

Contact
If you have any questions or feedback, feel free to reach out.
kiranbr2005@gmail.com
https://www.linkedin.com/in/kirankumarbr2005
