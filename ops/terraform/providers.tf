terraform {
  required_version = ">= 1.0"

  cloud {
    organization = "xently"

    workspaces {
      name = "dt-bank"
    }
  }

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = ">= 5.0"
    }
  }
}

provider "google" {
  project = var.gcp_project_id
  region  = var.gcp_region
}