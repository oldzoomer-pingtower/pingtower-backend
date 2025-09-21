#!/bin/bash

# PingTower Keycloak External Configuration Helper
# This script helps configure Keycloak for external access

set -e

echo "PingTower Keycloak External Configuration Helper"
echo "=============================================="

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo "Error: .env file not found. Please create it by copying .env.example"
    exit 1
fi

# Get domain from .env file
DOMAIN=$(grep "^DOMAIN=" .env | cut -d '=' -f2)

if [ -z "$DOMAIN" ] || [ "$DOMAIN" = "localhost" ]; then
    echo "Warning: DOMAIN is set to localhost or not set. For external access, please set it to your public domain."
    echo "Example: DOMAIN=your-public-domain.com"
    exit 1
fi

echo "Configuring Keycloak for domain: $DOMAIN"
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker first."
    exit 1
fi

echo "Restarting Keycloak with new domain configuration..."
docker compose restart keycloak

echo ""
echo "Keycloak is now configured for external access."
echo "You can access it at:"
echo "  - http://$DOMAIN:8080"
echo ""
echo "External clients can use:"
echo "  - Client ID: pingtower-frontend"
echo "  - Client Secret: pingtower-frontend-secret"
echo ""
echo "Configuration completed successfully!"