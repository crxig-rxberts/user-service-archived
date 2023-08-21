#!/bin/bash

# Read variables from TF output.json
export EC2_IP=$(jq -r '.ec2_ip.value' output.json)
export RDS_ENDPOINT=$(jq -r '.rds_endpoint.value' output.json)
