# ==============================================================================
# Infrastructure-as-Code (IaC) - Terraform Module for DevOps QVS
# Deploys Qualification Verification System on Cloud Container Infrastructure
# ==============================================================================

terraform {
  required_version = ">= 1.5.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# 1. Virtual Private Cloud (VPC) & Subnets
resource "aws_vpc" "qvs_vpc" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name        = "qvs-production-vpc"
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

resource "aws_subnet" "qvs_public_subnet_a" {
  vpc_id                  = aws_vpc.qvs_vpc.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "${var.aws_region}a"
  map_public_ip_on_launch = true

  tags = {
    Name = "qvs-public-subnet-a"
  }
}

resource "aws_subnet" "qvs_public_subnet_b" {
  vpc_id                  = aws_vpc.qvs_vpc.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = "${var.aws_region}b"
  map_public_ip_on_launch = true

  tags = {
    Name = "qvs-public-subnet-b"
  }
}

resource "aws_internet_gateway" "qvs_igw" {
  vpc_id = aws_vpc.qvs_vpc.id

  tags = {
    Name = "qvs-internet-gateway"
  }
}

resource "aws_route_table" "qvs_public_rt" {
  vpc_id = aws_vpc.qvs_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.qvs_igw.id
  }

  tags = {
    Name = "qvs-public-route-table"
  }
}

resource "aws_route_table_association" "qvs_rta_a" {
  subnet_id      = aws_subnet.qvs_public_subnet_a.id
  route_table_id = aws_route_table.qvs_public_rt.id
}

resource "aws_route_table_association" "qvs_rta_b" {
  subnet_id      = aws_subnet.qvs_public_subnet_b.id
  route_table_id = aws_route_table.qvs_public_rt.id
}

# 2. Security Groups
resource "aws_security_group" "qvs_alb_sg" {
  name        = "qvs-alb-security-group"
  description = "Allows incoming HTTP/HTTPS traffic to QVS Application Load Balancer"
  vpc_id      = aws_vpc.qvs_vpc.id

  ingress {
    description = "HTTP Public Access"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTPS Public Access"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "qvs_app_sg" {
  name        = "qvs-ecs-app-security-group"
  description = "Allows ingress from ALB to Spring Boot containers"
  vpc_id      = aws_vpc.qvs_vpc.id

  ingress {
    description     = "Allow port 8080 from ALB"
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.qvs_alb_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# 3. PostgreSQL Database Instance (RDS)
resource "aws_db_subnet_group" "qvs_db_subnet_group" {
  name       = "qvs-db-subnet-group"
  subnet_ids = [aws_subnet.qvs_public_subnet_a.id, aws_subnet.qvs_public_subnet_b.id]

  tags = {
    Name = "qvs-db-subnets"
  }
}

resource "aws_db_instance" "qvs_postgres_db" {
  identifier             = "qvs-postgres-prod"
  allocated_storage      = 20
  engine                 = "postgres"
  engine_version         = "15.4"
  instance_class         = "db.t3.micro"
  db_name                = "qvsdb"
  username               = var.db_username
  password               = var.db_password
  db_subnet_group_name   = aws_db_subnet_group.qvs_db_subnet_group.name
  vpc_security_group_ids = [aws_security_group.qvs_app_sg.id]
  skip_final_snapshot    = true
  publicly_accessible    = false

  tags = {
    Name = "qvs-production-db"
  }
}

# 4. AWS ECS Cluster & Fargate Service Definition
resource "aws_ecs_cluster" "qvs_cluster" {
  name = "qvs-production-cluster"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }
}

resource "aws_ecs_task_definition" "qvs_task" {
  family                   = "qvs-spring-boot-app"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"

  container_definitions = jsonencode([
    {
      name      = "qvs-app"
      image     = var.app_docker_image
      essential = true
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
        }
      ]
      environment = [
        { name = "SPRING_PROFILES_ACTIVE", value = "prod" },
        { name = "SPRING_DATASOURCE_URL", value = "jdbc:postgresql://${aws_db_instance.qvs_postgres_db.endpoint}/qvsdb" },
        { name = "SPRING_DATASOURCE_USERNAME", value = var.db_username },
        { name = "SPRING_DATASOURCE_PASSWORD", value = var.db_password }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/ecs/qvs-app"
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "ecs"
        }
      }
    }
  ])
}
