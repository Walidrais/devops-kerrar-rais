# Projet DevOps : location de voitures sur Kubernetes

Binôme : **Cerine KERRAR** et **Walid RAIS**, Miage groupe 1, Université Paris Cité, 2026-2027.

Deux microservices Spring Boot (Java 21), une base MySQL, des images Docker publiées automatiquement sur Docker Hub et un déploiement Kubernetes avec gateway, RBAC et Istio (mTLS).

```
                 Ingress NGINX / Gateway Istio
                 /cars  /rentals      /customers
                        |                  |
                  +------------+     +------------------+
                  | car-rental | --> | customer-service |
                  +------------+     +------------------+
                        |
                    +-------+
                    | MySQL |
                    +-------+
```

| Barème | Ce qui est fait | Fichiers |
|---|---|---|
| 10/20 | Service REST, Dockerfile, image sur Docker Hub, Deployment + Service | `car-rental/`, `docker/ubuntu-java/`, `k8s/03-car-rental.yaml` |
| 12/20 | Gateway (Ingress) | `k8s/04-ingress.yaml` |
| 14/20 | Deuxième service appelé par le premier | `customer-service/`, `k8s/02-customer-service.yaml` |
| 16/20 | Base de données | `k8s/01-mysql.yaml` |
| 18/20 | Sécurité : RBAC, mTLS et autorisations Istio | `k8s/05-rbac.yaml`, `k8s/istio/istio.yaml` |
| 20/20 | Déploiement dans le cloud | à faire (voir l'étape 6) |

## L'API

`car-rental` (port 8080) :

| Méthode | URL | Rôle |
|---|---|---|
| GET | `/cars` | liste des voitures |
| GET | `/cars/{plaque}` | une voiture |
| POST | `/cars` | ajouter `{"plateNumber":"DD-444-DD","brand":"Fiat","model":"500","pricePerDay":30}` |
| DELETE | `/cars/{plaque}` | supprimer une voiture |
| POST | `/cars/{plaque}/rent` | louer `{"customerId":1,"begin":"2026-12-01","end":"2026-12-03"}` : vérifie le client auprès de customer-service |
| POST | `/cars/{plaque}/return` | rendre la voiture |
| GET | `/rentals` | historique des locations |

`customer-service` (8081 en local, 8080 dans Kubernetes) : `GET /customers`, `GET /customers/{id}`, `POST /customers`.

## 1. Lancer en local (Java 21 suffit)

Dans deux terminaux :

```bash
cd customer-service && ./mvnw spring-boot:run
```

```bash
cd car-rental && ./mvnw spring-boot:run
```

Ensuite :

```bash
curl http://localhost:8080/cars
curl -X POST http://localhost:8080/cars/AA-111-AA/rent -H "Content-Type: application/json" -d '{"customerId":1,"begin":"2026-12-01","end":"2026-12-03"}'
```

Les tests se lancent avec `./mvnw verify` dans chaque dossier.

## 2. Publier les images sur Docker Hub (automatique)

1. Sur Docker Hub : Account Settings → Personal access tokens → Generate (droit « Read & Write »).
2. Sur GitHub : Settings → Secrets and variables → Actions → New repository secret :
   - `DOCKERHUB_USERNAME` : votre identifiant Docker Hub
   - `DOCKERHUB_TOKEN` : le token
3. À chaque push sur `main`, le workflow `.github/workflows/docker-publish.yml` lance les tests, puis publie :
   - `<user>/ubuntu-java` : Ubuntu 24.04 + Java 21
   - `<user>/car-rental` : construite au-dessus de `ubuntu-java`
   - `<user>/customer-service` : construite au-dessus de `ubuntu-java`

Pour construire à la main avec Docker Desktop :

```bash
docker build -t ubuntu-java docker/ubuntu-java
docker build -t car-rental car-rental
docker run -p 8080:8080 car-rental
```

## 3. Déployer sur minikube

Dans `k8s/kustomization.yaml`, remplacez `VOTRE_USER_DOCKERHUB` par votre identifiant Docker Hub (2 lignes). Ensuite :

```bash
minikube start --memory=4096
minikube addons enable ingress
kubectl apply -k k8s/
kubectl get pods -w
```

Attendez que les 3 pods soient `Running` et `1/1` (MySQL met environ une minute à démarrer). Ensuite :

```bash
minikube service car-rental --url
kubectl get ingress
```

Sous Windows ou macOS avec le driver Docker, l'Ingress n'est pas joignable directement : lancez `minikube tunnel` dans un autre terminal et utilisez `http://127.0.0.1/cars` et `http://127.0.0.1/customers`.

## 4. RBAC

```bash
kubectl auth can-i list pods --as=system:serviceaccount:default:observateur
kubectl auth can-i delete pods --as=system:serviceaccount:default:observateur
```

La première commande répond `yes`, la deuxième `no`. Les pods applicatifs n'ont pas de token pour l'API Kubernetes (`automountServiceAccountToken: false`).

## 5. Istio : mTLS et autorisations

```bash
istioctl install --set profile=demo -y
kubectl label namespace default istio-injection=enabled
kubectl rollout restart deployment
kubectl apply -f k8s/istio/istio.yaml
kubectl get pods
```

Chaque pod doit afficher `2/2` (application + sidecar Envoy). Ensuite, ouvrez la gateway Istio avec `minikube tunnel`, puis utilisez `kubectl get svc istio-ingressgateway -n istio-system` pour trouver son adresse.

Pour montrer que les autorisations fonctionnent : un pod qui n'est pas car-rental ne peut pas appeler customer-service.

```bash
kubectl run test --rm -it --image=curlimages/curl -- curl -s http://customer-service:8080/customers
```

Réponse attendue : `RBAC: access denied`.

## 6. Pour 20/20 : déploiement dans le cloud

Les mêmes fichiers `k8s/` s'appliquent sur un cluster GKE (Google Cloud, crédits des Google labs) :

```bash
gcloud container clusters create-auto rental --region europe-west1
gcloud container clusters get-credentials rental --region europe-west1
kubectl apply -k k8s/
```

## Captures à mettre dans le rapport

1. Le workflow GitHub Actions en vert, et les 3 images sur Docker Hub.
2. `kubectl get all` et `kubectl get ingress`.
3. `curl` sur `/cars`, puis une location réussie via la gateway (preuve que car-rental appelle customer-service).
4. Une location avec un client inconnu, qui renvoie 404 « Client inconnu ».
5. Les deux commandes `kubectl auth can-i`.
6. Les pods en `2/2` avec Istio, et le `RBAC: access denied` du pod de test.
7. Les captures Google Skills Boost de chacun.
