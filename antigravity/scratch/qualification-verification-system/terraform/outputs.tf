output "vpc_id" {
  description = "ID of the Provisioned VPC"
  value       = aws_vpc.qvs_vpc.id
}

output "database_endpoint" {
  description = "PostgreSQL RDS connection endpoint"
  value       = aws_db_instance.qvs_postgres_db.endpoint
}

output "ecs_cluster_name" {
  description = "ECS Cluster Name"
  value       = aws_ecs_cluster.qvs_cluster.name
}
