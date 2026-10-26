Check rate limiting for creating orders
set "TOKEN=Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYWh1bCIsImlhdCI6MTc5MTUzNDA3NywiZXhwIjoxNzkxNjIwNDc3fQ.qfMKT4WrpEXPeganmnHxLCy9AfQ-js9qhzYctl9vCp4"
for /L %i in (1,1,10) do curl.exe --location "http://localhost:8080/api/v1/orders" --header "Content-Type: application/json" --header "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYWh1bCIsImlhdCI6MTc5MTUzNDA3NywiZXhwIjoxNzkxNjIwNDc3fQ.qfMKT4WrpEXPeganmnHxLCy9AfQ-js9qhzYctl9vCp4" --data "{\"productId\":\"348d0f72-ef7c-4d1b-977e-511c2aa1fa31\",\"quantity\":1}"


