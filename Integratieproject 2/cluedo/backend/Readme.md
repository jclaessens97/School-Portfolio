Instructies uitvoeren projecten:

4 services runnen:
- Auth service
- Gateway: npm i && npm start
- Backend
- Front-end: ng serve --open
- Docker (neo4j & rabbitmq) (back-end folder): docker-compose up

Alvorens front-end werkt eerst inloggen:
- username: test
- password: password

Api testen via postman:
- eerst inloggen:
http://localhost:9000/auth/authenticate

body:

{
	"username": "test",
	"password": "password"
}
- bearer token kopieren
- Api call maken

Authorization -> bearer token -> token pasten