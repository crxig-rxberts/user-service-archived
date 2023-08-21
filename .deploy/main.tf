provider "aws" {
  region = "eu-west-1"
}

// Create a new key pair
resource "aws_key_pair" "deployer" {
  key_name   = "deployer-key"
  public_key = file("~/.ssh/deployer-key.pub")
}

// Create EC2 instance
resource "aws_instance" "app_instance" {
  ami           = "ami-0ed752ea0f62749af"
  instance_type = "t2.micro"
  key_name      = aws_key_pair.deployer.key_name

  tags = {
    Name = "user-service"
  }
}

// Create RDS instance
resource "aws_db_instance" "default" {
  allocated_storage    = 20
  storage_type         = "gp2"
  engine               = "postgres"
  engine_version       = "12"
  instance_class       = "db.t2.micro"
  identifier           = "user-service"
  username             = var.DB_USERNAME
  password             = var.DB_PASSWORD
  parameter_group_name = "default.postgres12"
  skip_final_snapshot  = true
}
