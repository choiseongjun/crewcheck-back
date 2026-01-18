#!/bin/bash

# Base URL
BASE_URL="http://localhost:8080/api/v1"

# --- Authentication ---
echo "1. Registering a new user..."
curl -X POST "$BASE_URL/users/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "password123",
    "nickname": "TestUser",
    "bio": "This is a test user.",
    "profileImageUrl": "http://example.com/profile.jpg",
    "birthDate": "1990-01-01",
    "gender": "MALE"
  }'
echo -e "\n"

echo "2. Logging in..."
# Login to get the JWT token. Copy the 'accessToken' from the response to the TOKEN variable below.
curl -X POST "$BASE_URL/users/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "password123"
  }'
echo -e "\n"

# --- Configuration ---
# REPLACE THIS WITH YOUR ACTUAL TOKEN AFTER LOGIN
TOKEN="YOUR_ACCESS_TOKEN_HERE"

# --- Team APIs ---
echo "3. Creating a Team..."
# Copy the 'id' from the response to use as TEAM_ID
curl -X POST "$BASE_URL/teams" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My Awesome Team",
    "description": "A team for awesome people",
    "memberLimit": 10,
    "category": "Development",
    "leaderId": "user-uuid-placeholder",
    "frequency": "WEEKLY",
    "durationDays": 30
  }'
echo -e "\n"

echo "4. Get All Teams..."
curl -X GET "$BASE_URL/teams?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

# REPLACE WITH ACTUAL TEAM ID
TEAM_ID="REPLACE_WITH_TEAM_ID"

echo "5. Get Team by ID..."
curl -X GET "$BASE_URL/teams/$TEAM_ID" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

echo "6. Update Team..."
curl -X PUT "$BASE_URL/teams/$TEAM_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Team Name",
    "description": "Updated description",
    "memberLimit": 15,
    "category": "Research",
    "frequency": "DAILY"
  }'
echo -e "\n"

echo "7. Delete Team..."
# curl -X DELETE "$BASE_URL/teams/$TEAM_ID" \
#   -H "Authorization: Bearer $TOKEN"
echo -e "Delete Team command commented out to prevent accidental deletion.\n"


# --- CheckIn APIs ---
echo "8. Create CheckIn..."
# Copy the 'id' from the response to use as CHECKIN_ID
curl -X POST "$BASE_URL/check-ins" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"teamId\": \"$TEAM_ID\",
    \"content\": \"Today I did some coding.\",
    \"imageUrl\": \"http://example.com/checkin.jpg\",
    \"routineTitle\": \"Morning Coding\"
  }"
echo -e "\n"

echo "9. Get CheckIns for Team..."
curl -X GET "$BASE_URL/check-ins?teamId=$TEAM_ID&page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

# REPLACE WITH ACTUAL CHECKIN ID
CHECKIN_ID="REPLACE_WITH_CHECKIN_ID"

echo "10. Get CheckIn by ID..."
curl -X GET "$BASE_URL/check-ins/$CHECKIN_ID" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

echo "11. Update CheckIn..."
curl -X PUT "$BASE_URL/check-ins/$CHECKIN_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Updated content: I did A LOT of coding.",
    "imageUrl": "http://example.com/checkin_updated.jpg",
    "routineTitle": "Intense Coding"
  }'
echo -e "\n"


# --- CheckIn Approval APIs ---
echo "12. Approve CheckIn..."
# Copy the 'id' from the response to use as APPROVAL_ID
curl -X POST "$BASE_URL/check-in-approvals/$CHECKIN_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "approved"
  }'
echo -e "\n"

# REPLACE WITH ACTUAL APPROVAL ID
APPROVAL_ID="REPLACE_WITH_APPROVAL_ID"

echo "13. Delete Approval (Revert)..."
curl -X DELETE "$BASE_URL/check-in-approvals/$APPROVAL_ID" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

echo "14. Delete CheckIn..."
# curl -X DELETE "$BASE_URL/check-ins/$CHECKIN_ID" \
#   -H "Authorization: Bearer $TOKEN"
echo -e "Delete CheckIn command commented out.\n"
