## Task Board App

Kanban-style web application. Frontend is developed with React + Vite build tool. Backend is done mainly with Java 17 and Spring Boot. MySQL serves as the app DB.

Features:
 - "Login" (set username)
 - Browse public task boards
 - Create task boards
   - These can be either public or private
 - Create tasks
 - Edit/move tasks
 - Board-specific chat

### Running app

Running app requires only `docker-compose`. Run `docker-compose up` in the root directory. Application will run at `http://localhost:8080`.

### Development

Both front- and backend include hot reload when running with docker. In order to start developing, run the containers with `docker-compose up`. Any code changes will be reflected to running application shortly.

**Note**: Some change may require container rebuild/restart, e.g. adding dependencies.

#### Running backend tests

Test have to be run locally, with e.g. IntelliJ IDEA. Container doesn't currently support this.