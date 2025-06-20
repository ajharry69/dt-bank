.PHONY: help docker compose build test build-images build-and-push-images

help: ## Display this help message.
	@echo "Please use \`make <target>\` where <target> is one of:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; \
	{printf "\033[36m%-40s\033[0m %s\n", $$1, $$2}'

docker: ## Start docker compose services.
	docker compose down --volumes --remove-orphans
	@until docker compose up --detach --build --remove-orphans; do\
	    echo "\033[0;33mFailed to start container services! Retrying in 10s...\e[0m";\
    	sleep 10;\
    done

compose: ## Build images and start docker compose services.
	COMPOSE_IMAGE=true $(MAKE) build-images
	$(MAKE) docker

build: ## Build project.
	./gradlew build --no-daemon --exclude-task test --continue --build-cache --parallel

test: ## Run tests.
	./gradlew test --no-daemon --exclude-task :gateway:test --continue --build-cache --info

build-images: ## Build Docker images.
	./gradlew bootBuildImage --no-daemon --continue --build-cache --parallel

build-and-push-images: ## Build and push Docker images.
	./gradlew bootBuildImage --no-daemon --continue --build-cache --parallel --publishImage
