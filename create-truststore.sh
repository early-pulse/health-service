#!/bin/bash

# Download Redpanda certificate
echo "Downloading Redpanda certificate..."
openssl s_client -connect d0st9ctag219ed02mod0.any.ap-south-1.mpx.prd.cloud.redpanda.com:9092 -showcerts </dev/null 2>/dev/null | openssl x509 -outform PEM > redpanda.crt

# Create truststore
echo "Creating truststore..."
keytool -import -alias redpanda -file redpanda.crt -keystore redpanda.truststore.p12 -storetype PKCS12 -storepass redpanda -noprompt

# Move truststore to resources directory
echo "Moving truststore to resources directory..."
mv redpanda.truststore.p12 src/main/resources/

# Clean up
echo "Cleaning up..."
rm redpanda.crt

echo "Done!" 