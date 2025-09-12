# --- VPC Network ---
resource "google_compute_network" "vpc_network" {
  name                    = "dt-bank-vpc"
  auto_create_subnetworks = false
}

resource "google_compute_subnetwork" "subnet" {
  name          = "dt-bank-subnet"
  ip_cidr_range = "10.10.0.0/24"
  network       = google_compute_network.vpc_network.id
  region        = var.gcp_region
}

# --- GKE Cluster (using Autopilot for simplicity and cost-effectiveness) ---
resource "google_container_cluster" "primary" {
  name       = "dt-bank-cluster"
  location   = var.gcp_region
  network    = google_compute_network.vpc_network.self_link
  subnetwork = google_compute_subnetwork.subnet.self_link

  # Autopilot automatically manages nodes and scaling.
  enable_autopilot    = true
  deletion_protection = false
}

# --- Google Artifact Registry for Docker Images ---
resource "google_artifact_registry_repository" "docker_repo" {
  location      = var.gcp_region
  repository_id = "dt-bank-images"
  description   = "Docker repository for dt-bank microservices"
  format        = "DOCKER"
}

# --- Service Account for GitHub Actions to use for deployment ---
resource "google_service_account" "github_actions_sa" {
  account_id   = "github-actions-deployer"
  display_name = "GitHub Actions Deployer SA"
}

# --- IAM Permissions for the Service Account ---
# Allow SA to push to Artifact Registry
resource "google_project_iam_member" "artifact_writer" {
  project = var.gcp_project_id
  role    = "roles/artifactregistry.writer"
  member  = "serviceAccount:${google_service_account.github_actions_sa.email}"
}

# Allow SA to manage the GKE cluster
resource "google_project_iam_member" "gke_developer" {
  project = var.gcp_project_id
  role    = "roles/container.developer" # Provides necessary access to deploy to GKE
  member  = "serviceAccount:${google_service_account.github_actions_sa.email}"
}

# --- Workload Identity Federation (Securely connect GitHub Actions to GCP) ---
resource "google_iam_workload_identity_pool" "github" {
  workload_identity_pool_id = "github-actions-pool"
  display_name              = "GitHub Actions Pool"
}

resource "google_iam_workload_identity_pool_provider" "github_provider" {
  workload_identity_pool_id          = google_iam_workload_identity_pool.github.workload_identity_pool_id
  workload_identity_pool_provider_id = "github-provider"
  display_name                       = "GitHub Actions Provider"
  attribute_condition                = "assertion.repository=='${var.github_repo}'"
  attribute_mapping = {
    "google.subject"       = "assertion.sub"
    "attribute.actor"      = "assertion.actor"
    "attribute.repository" = "assertion.repository"
  }
  oidc {
    issuer_uri = "https://token.actions.githubusercontent.com"
  }
}

resource "google_service_account_iam_member" "workload_identity_user" {
  service_account_id = google_service_account.github_actions_sa.name
  role               = "roles/iam.workloadIdentityUser"
  member             = "principalSet://iam.googleapis.com/${google_iam_workload_identity_pool.github.name}/attribute.repository/${var.github_repo}"
}
