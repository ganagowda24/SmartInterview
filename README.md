🎯 SmartInterview – Spring Boot Web Application

SmartInterview is a Spring Boot–based practice and learning platform designed to help users prepare for interviews efficiently. It includes user management, topic pages, learning modules, and a clean UI built using HTML/CSS/JS.

🚀 Features 🔐 User Features

User Registration & Login

Session-based Authentication

User Dashboard

Learning Pages

Static Pages (login, register, home, etc.)

🛠 Backend Features

Spring Boot MVC

REST API Ready

JPA + Hibernate

MySQL Database Integration

Robust Logging (stored in /logs/)

🎨 Frontend Features

HTML Templates

Styled with CSS

JavaScript for UI interactions

Static Assets (images, backgrounds, etc.)

📁 Project Structure SmartInterview/ │── src/ │ ├── main/ │ │ ├── java/... (Controllers, Services, Models) │ │ ├── resources/ │ │ │ ├── static/ (CSS, JS, Images) │ │ │ ├── templates/ (HTML pages) │ │ │ └── application.properties │── logs/ │── pom.xml │── mvnw / mvnw.cmd │── .gitignore │── .gitattributes

⚙ Setup Instructions 1️⃣ Clone the Repository git clone https://github.com/ganagowda24/SmartInterview.git 
                          cd SmartInterview

2️⃣ Configure the Database

Edit src/main/resources/application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/smart_interview spring.datasource.username=root spring.datasource.password=yourpassword spring.jpa.hibernate.ddl-auto=update

3️⃣ Install Dependencies mvn clean install

4️⃣ Run the App mvn spring-boot:run

Application starts at:

👉 http://localhost:8080
