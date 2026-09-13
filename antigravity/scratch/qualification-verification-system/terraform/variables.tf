variable "aws_region" {
  description = "AWS Deployment Region"
  type        = string
  default     = "eu-west-1"
}

variable "environment" {
  description = "Target environment (dev, staging, prod)"
  type        = string
  default     = "production"
}

variable "vpc_cidr" {
  description = "CIDR block for QVS VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "app_docker_image" {
  description = "Docker image repository tag for QVS"
  type        = string
  default     = "devops-qvs:latest"
}

variable "db_username" {
  description = "PostgreSQL administrator username"
  type        = string
  default     = "qvsadmin"
}

variable "db_password" {
  description = "PostgreSQL administrator password"
  type        = string
  sensitive   = true
  default     = "QvsSecureProd2026!"
}
