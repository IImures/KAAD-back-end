# KancelariaAdwokacka

This is a backend part of a project created for a Law Firm, made in order to help the firm advertise their services, publish posts, and easily manage client requests.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
  - [Backend](#backend)
  - [Frontend](#frontend)
- [Installation](#installation)
- [Endpoints](#endpoints)

## Features

Blog Posting: Client can write and publish posts to share legal insights and updates.
Client Management: A system for handling and organizing client and requests.
Advertising: Display the firm’s services to attract potential clients.
Role-Based Authentication: Access based on user roles (Admin, Lawyer, Writer).

## Technology Stack
### Backend
- Framework: Spring
  - Language: Java
- Libraries:
  - Lombok for easy bean creating
  - Hibernate for easy DB use.
### Frontend 
  Frontend is located in separate repository: [KAAD-frontend](https://github.com/IImures/KAAD-back-end](https://github.com/IImures/KAAD-front-end))

## Installation
  1. **Clone Repository**:
     ```sh
       git clone git@github.com:IImures/KAAD-back-end.git
       cd KAAD-back-end
     ```
  2. **Configure**
      ```sh
        cd src/main/resources/application.yml
      ```
  3. **Run the Backend**:
      Use your preferred method to build and run the application.
   
## Endpoints
Postman collection of endpoints: [KAAD.postman_collection.json](https://github.com/user-attachments/files/17269898/KAAD.postman_collection.json)
