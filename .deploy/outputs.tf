output "ec2_ip" {
  value = aws_instance.app_instance.public_ip
}

output "rds_endpoint" {
  value = aws_db_instance.default.endpoint
}
