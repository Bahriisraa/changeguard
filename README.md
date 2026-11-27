# ChangeGuard

Déploiement par vagues (1 %, 10 %, 100 %) avec surveillance de la santé et rollback automatique.
Mini-projet Architecture SOA et Services Web.

## Services
change-service (REST), health-service (gRPC), approval-service (SOAP), api-gateway, target-app.

## Lancement
cp .env.example .env
docker compose up -d --build
