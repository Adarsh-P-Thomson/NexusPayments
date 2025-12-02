# Apinexus Package Structure

This is the main package for the NexusPay backend application. Here's what each folder contains:

## 📁 Folders

### `controller/`
Contains REST API endpoints - these handle incoming HTTP requests from the frontend and return responses.

### `model/`
Contains database entities - these are Java classes that map directly to database tables (like User, Bill, Transaction).

### `service/`
Contains business logic - these classes handle the complex operations and processing between controllers and repositories.

### `repository/`
Contains database access code - these interfaces let the app read/write data to PostgreSQL and MongoDB.

### `dto/`
Contains Data Transfer Objects - these are simple objects used to transfer data between the frontend and backend (like requests and responses).

### `config/`
Contains configuration classes - these set up how the application connects to databases and other services.

### `initializer/`
Contains data initialization classes - these populate the database with initial/sample data when the app starts.

### `util/`
Contains utility classes - these are helper tools used throughout the application for common tasks.

## 📄 Main File

### `ApinexusApplication.java`
The main entry point of the application - this is where Spring Boot starts the entire backend server.
