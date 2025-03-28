#!/bin/bash

# Employee Management System Database Setup Script
# This script initializes the MySQL database for the EMS application by:
# 1. Creating the database and tables using schema.sql
# 2. Populating initial data using init_data.sql

# MySQL connection credentials
# Note: In a production environment, these should be stored securely
# and possibly passed as environment variables
MYSQL_USER="root"
MYSQL_PASSWORD="root"

# Step 1: Create database schema
echo "Creating database schema..."
echo "This will create the database and all required tables..."
mysql -u $MYSQL_USER -p$MYSQL_PASSWORD < docs/database/schema.sql

# Step 2: Insert initial data
echo "Inserting initial data..."
echo "This will populate the database with:"
echo "- Geographic data (states and cities)"
echo "- Organizational structure (divisions and job titles)"
echo "- Admin user account"
mysql -u $MYSQL_USER -p$MYSQL_PASSWORD < docs/database/init_data.sql

# Confirm completion
echo "Database setup complete!"
echo "You can now log in to the application with:"
echo "Username: admin"
echo "Password: admin123"
echo "Role: ADMIN" 