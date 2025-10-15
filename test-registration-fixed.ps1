$body = @{
    firstName = "John"
    lastName = "Doe"
    email = "john.doe@example.com"
    password = "password123"
} | ConvertTo-Json

Write-Host "Testing registration with correct field names..."
Write-Host "Request body: $body"
Write-Host ""

Write-Host "Testing through API Gateway..."
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8765/user-management/auth/register" -Method POST -ContentType "application/json" -Body $body
    Write-Host "✅ SUCCESS: Registration successful!"
    Write-Host "Response: $($response | ConvertTo-Json -Depth 3)"
} catch {
    Write-Host "❌ ERROR: Registration failed"
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
    }
}

Write-Host "`nTesting direct user service..."
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8091/auth/register" -Method POST -ContentType "application/json" -Body $body
    Write-Host "✅ SUCCESS: Direct service registration successful!"
    Write-Host "Response: $($response | ConvertTo-Json -Depth 3)"
} catch {
    Write-Host "❌ ERROR: Direct service registration failed"
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
    }
}
