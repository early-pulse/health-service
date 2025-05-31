# Download Redpanda certificate
Write-Host "Downloading Redpanda certificate..."
$cert = New-Object System.Security.Cryptography.X509Certificates.X509Certificate2
$cert.Import("d0st9ctag219ed02mod0.any.ap-south-1.mpx.prd.cloud.redpanda.com:9092")
$certBytes = $cert.Export([System.Security.Cryptography.X509Certificates.X509ContentType]::Cert)
[System.IO.File]::WriteAllBytes("redpanda.crt", $certBytes)

# Create truststore
Write-Host "Creating truststore..."
keytool -import -alias redpanda -file redpanda.crt -keystore redpanda.truststore.p12 -storetype PKCS12 -storepass redpanda -noprompt

# Move truststore to resources directory
Write-Host "Moving truststore to resources directory..."
Move-Item -Path "redpanda.truststore.p12" -Destination "src/main/resources/"

# Clean up
Write-Host "Cleaning up..."
Remove-Item "redpanda.crt"

Write-Host "Done!" 