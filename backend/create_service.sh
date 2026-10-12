#!/bin/bash

# Check if a service name argument was provided
if [ -z "$1" ]; then
    echo "❌ Error: Please provide a service name."
    echo "💡 Usage: ./create_service.sh <service-name>"
    echo "📋 Example: ./create_service.sh product"
    exit 1
fi

# Clean the input to lowercase and append -service for the folder name
SERVICE_NAME=$(echo "$1" | tr '[:upper:]' '[:lower:]')
FOLDER_NAME="${SERVICE_NAME}-service"
BASE_PACKAGE="src/main/java/org/ecommerce/${SERVICE_NAME}"

# Array of your requested internal layers
LAYERS=(
    "controller"
    "data"
    "service"
    "dto"
    "model"
    "util"
    "client"
    "kafka"
)

echo "🚀 Generating infrastructure for: $FOLDER_NAME"

# Create the standard structure loops
for LAYER in "${LAYERS[@]}"
do
    TARGET_DIR="$FOLDER_NAME/$BASE_PACKAGE/$LAYER"
    mkdir -p "$TARGET_DIR"
done

# Generate resources directory
mkdir -p "$FOLDER_NAME/src/main/resources"

echo "✅ Successfully created folder structure for $FOLDER_NAME!"
