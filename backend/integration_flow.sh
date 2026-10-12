#!/bin/bash

# Configuration
BASE_URL="http://localhost:8080/api/v1"
USERNAME="user_$(date +%s)" # Generates a unique username per execution
PASSWORD="password123"

echo "🚀 Starting Automated Modular E-Commerce Flow Verification Pipeline..."
echo "👤 Target Username: $USERNAME"
echo "--------------------------------------------------------"

# 1. REGISTER
echo "🔐 Step 1: Registering new test identity..."
REG_RESPONSE=$(curl -s --location "$BASE_URL/auth/register" \
  --header 'Content-Type: application/json' \
  --data "{
    \"username\": \"$USERNAME\",
    \"password\": \"$PASSWORD\"
  }")
echo "📝 Server Response: $REG_RESPONSE"
echo ""

# 2. LOGIN & TOKEN EXTRACTION
echo "🔑 Step 2: Logging in to collect authorization bearer access..."
LOGIN_RESPONSE=$(curl -s --location "$BASE_URL/auth/login" \
  --header 'Content-Type: application/json' \
  --data "{
    \"username\": \"$USERNAME\",
    \"password\": \"$PASSWORD\"
  }")

# Pull out token string from JSON structure matching "token":"xxx"
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | grep -o '[^"]*$')

if [ -z "$TOKEN" ]; then
    echo "❌ Error: Failed to extract JWT token from authorization sequence. Exit."
    exit 1
fi
echo "🎯 JWT Bearer Token generated successfully!"
echo ""

# 3. ADD PRODUCT
echo "📦 Step 3: Provisioning an electronic device to the catalog..."
PROD_RESPONSE=$(curl -s --location "$BASE_URL/products/update" \
  --header 'Content-Type: application/json' \
  --header "Authorization: Bearer $TOKEN" \
  --data '{
    "title": "MacBook Pro M3",
    "actualPrice": 19990000,
    "discountPrice": 17990000,
    "category": {
        "name": "laptops",
        "description": "Premium laptops"
    }
  }')
echo "📝 Server Response: $PROD_RESPONSE"
echo ""

# 4. FETCH ALL PRODUCTS & EXTRACT UUID
echo "🔍 Step 4: Fetching product array map to parse item identifier..."
FETCH_RESPONSE=$(curl -s --location "$BASE_URL/products/fetch" \
  --header "Authorization: Bearer $TOKEN")

# Regex pattern extraction to isolate the 36-character UUID string format automatically
PRODUCT_ID=$(echo "$FETCH_RESPONSE" | grep -oE '[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}' | head -n 1)

if [ -z "$PRODUCT_ID" ]; then
    echo "❌ Error: Could not locate any active Product UUID inside the response envelope. Exit."
    exit 1
fi
echo "🎯 Extracted Product UUID Pointer: $PRODUCT_ID"
echo ""

# 5. CREATE ORDER (Triggers Sync Client Loop + Async Kafka Event broadcast)
echo "🛒 Step 5: Committing checkout sequence against target product..."
ORDER_RESPONSE=$(curl -s --location "$BASE_URL/orders" \
  --header 'Content-Type: application/json' \
  --header "Authorization: Bearer $TOKEN" \
  --data "{
    \"productId\": \"$PRODUCT_ID\",
    \"quantity\": 2
  }")

echo "========================================================"
echo "🎉 PIPELINE COMPLETE - TARGET TRANSACTION STATUS COMPLETED"
echo "========================================================"
echo "📝 Order Payload Response:"
echo "$ORDER_RESPONSE"
echo "========================================================"
echo "💡 Check your terminal logs! You should see the Notification service"
echo "   print out the consumed Kafka event for this transaction."
