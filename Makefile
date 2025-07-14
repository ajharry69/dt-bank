.PHONY: help destroy update-hosts keycloak docker compose build test build-images build-and-push-images k8s-dev k8s-staging k8s-production k8s

help: ## Display this help message.
	@echo "Please use \`make <target>\` where <target> is one of:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; \
	{printf "\033[36m%-40s\033[0m %s\n", $$1, $$2}'

destroy: ## Stop docker compose services.
	docker compose down --volumes --remove-orphans

update-hosts: ## Update hosts file.
	@echo "\033[0;33mUpdating hosts file if necessary...\e[0m"
	@if ! grep -q -F "host.docker.internal" /etc/hosts; then\
	  echo "127.0.0.1	host.docker.internal" | sudo tee -a /etc/hosts > /dev/null && echo "\033[0;32m✅ hosts file updated.\e[0m" || echo "\033[0;31m❌ Failed to update hosts file.\e[0m";\
	else\
	  echo "\033[0;32m✅ Entry already exists. No changes made.\e[0m";\
	fi

keycloak: update-hosts destroy ## Start only the keycloak Docker Compose service and its direct dependencies.
	@until docker compose up --detach --build --remove-orphans keycloak; do\
	    echo "\033[0;33mFailed to start container services! Retrying in 10s...\e[0m";\
    	sleep 10;\
    done

docker: update-hosts destroy ## Start docker compose services.
	@until docker compose up --detach --build --remove-orphans; do\
	    echo "\033[0;33mFailed to start container services! Retrying in 10s...\e[0m";\
    	sleep 10;\
    done

compose: ## Build images and start docker compose services.
	IMAGE_VERSION=current $(MAKE) build-images
	$(MAKE) docker

build: ## Build project.
	./gradlew build --no-daemon --exclude-task test --continue --build-cache --parallel

test: ## Run tests for all modules.
	./gradlew test --no-daemon --exclude-task :gateway:test --continue --build-cache --info

build-images: ## Build Docker images.
	./gradlew bootBuildImage --no-daemon --continue --build-cache --parallel

build-and-push-images: ## Build and push Docker images.
	./gradlew bootBuildImage --no-daemon --continue --build-cache --parallel --publishImage

k8s-dev: ## Start kubernetes development cluster.
	kubectl apply -k ./ops/k8s/overlays/development/

k8s-dev-delete: ## Delete kubernetes development cluster.
	kubectl delete -k ./ops/k8s/overlays/development/

k8s-staging: ## Start kubernetes staging cluster.
	kubectl apply -k ./ops/k8s/overlays/staging/

k8s-staging-delete: ## Delete kubernetes staging cluster.
	kubectl delete -k ./ops/k8s/overlays/staging/

k8s-production: ## Start kubernetes production cluster.
	kubectl apply -k ./ops/k8s/overlays/production/

k8s-production-delete: ## Delete kubernetes production cluster.
	kubectl delete -k ./ops/k8s/overlays/production/

k8s: k8s-production ## Start kubernetes production cluster.

k8s-delete: k8s-production-delete ## Start kubernetes production cluster.
