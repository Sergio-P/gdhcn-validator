# entomo GDHCN Validator

## Overview

The **entomo GDHCN Validator** is a microservice designed for validating Verifiable Smart Health Certificate (VSHC). This application uses Spring Boot and various dependencies to perform certificate validation and related operations.

## Features

- **GDHCN Certificate Issuance:** Endpoint to issue certificates based on QR code requests.
- **GDHCN Certificate Validation:** Endpoint to validate certificates.
- **Manifest Retrieval:** Endpoint to retrieve manifests in JSON format.
- **File Operations:** Upload and download files to/from AWS S3 bucket.
- **Certificate and Key Management:** Handles private keys and certificates for secure communications.

## Technologies

- **Spring Boot:** Framework for building and running the application.
- **Java 17:** Programming language used for development.
- **PostgreSQL:** Database for storing application data.
- **AWS SDK:** Interact with AWS services such as S3.
- **ModelMapper:** Object mapping for data transformation.
- **BouncyCastle:** Cryptographic operations for secure communications.
- **Apache HttpClient:** HTTP client for making requests.
- **COSE and Base45:** For encoding and decoding operations.
- **Docker:** Container to run the app without installing dependencies.

## Dependencies

- **Spring Boot Starter Web:** For web development.
- **Spring Boot Starter Data JPA:** For JPA data access.
- **Spring Boot Starter Test:** For testing purposes.
- **Authlete CBOR:** For CBOR encoding/decoding.
- **Lombok:** For reducing boilerplate code.
- **AWS SDK:** For AWS service interactions.
- **PostgreSQL Driver:** For PostgreSQL database connectivity.
- **ModelMapper:** For object mapping.
- **BouncyCastle:** For cryptographic operations.
- **Apache HttpClient:** For HTTP operations.
- **Base45:** For Base45 encoding/decoding.
- **COSE:** For COSE operations.
- **Commons Compress:** For file compression operations.

## Run project using Maven

### Configuration

Ensure you configure the following properties in your `application.properties` or `application.yml`:

```properties
# Profile Configuration, for Local use 'fileSystem', for AWS use 'aws'
spring.profiles.active = fileSystem

# Spring JPA Configuration
spring.datasource.url=your-database-url
spring.datasource.username=your-database-username
spring.datasource.password=yourdatabase-password

# AWS S3 configuration
aws.bucket.s3.bucket.name=your-bucket-name
aws.bucket.access.key=your-access-key
aws.bucket.access.secret=your-secret-key

# TLS configuration
tng.tls.pem=path/to/your/certificate.pem
tng.tls.key=path/to/your/private-key.pem
tng.country = XA
tng.dsc.privateKey =path/to/your/dsc-privaye-key
tng.dsc.privateKey.kid =dsc kid id


```
### Build and Run

To build and run the application, follow these steps:

#### 1. Clone the repository:

```properties
git clone https://github.com/gdhcnentomo/entomo-gdhcn-validator.git
cd entomo-gdhcn-validator
```
#### 2. Create new folder to contain the generated QR files.
```shell
mkdir json
```
#### 3. Build the project using Maven:
```properties
mvn clean install
```
#### 4. Run the application:
```properties
mvn spring-boot:run
```

## Run project using Docker

### Configuration

Ensure you add the following files inside the `secret` folder (you need to create the folder first):

- **DSCpriv.key:** DSC private key file.
- **TLS.key:** TLS private key file.
- **TLS.pem:** TLS certificate pem file.

Additionally edit the configuration values inside the `docker-compose.yml`:

- **TNG_DSC_PRIVATEKEY_KID:** DSC privatekey KID value
- **GDHCN_BASEURL:** National Server base URL 

### Build and Run

To build and run the application, follow these steps:

#### 1. Clone the repository:

```properties
git clone https://github.com/gdhcnentomo/entomo-gdhcn-validator.git
cd entomo-gdhcn-validator
```

#### 2. Copy the correct files into the secret folder
#### 3. Create new folder to contain the generated QR files.
```shell
mkdir json
```
#### 4. Build and run the project using Docker:
```shell
docker-compose up -d
```

#### 4. Re Build and run the project using Docker:
```shell
docker-compose up --build --force-recreate --no-deps -d
```

## API Endpoints

- POST /v2/vshcIssuance: Issues a GDHCN certificate.
- POST /v2/vshcValidation: Validates a GDHCN certificate.
- GET /v2/ips-json/{jsonId}: Retrieves JSON data for the given ID.
- POST /v2/manifests/{jsonId}: Retrieves a manifest for the given ID.

### Swagger

More documentation can be found in the `/swagger-ui/index.html#` endpoint. Example: `http://localhost:8080/swagger-ui/index.html#/`


## Exception Handling

- **GdhcnValidationException:** Handles validation exceptions and returns an appropriate error response.
- **MethodArgumentNotValidException:** Handles validation errors in request arguments.

## License

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0


## Contact
For any questions or support, please reach out to <a href="">gdhcn@entomo.co</a>


### Notes:
```properties
- Replace placeholders such as `your-database-url`,`your-database-usernmae`,`your-database-password`,`your-bucket-name`, `your-access-key` and `your-secret-key` with actual values relevant to your setup.
- Update the repository URL with the actual URL where the code is hosted.
- Adjust any configuration or details based on your actual implementation and environment.
```

## Acknowledgements

- **HCERT Java Library:** This project utilizes the HCERT Java library for handling health certificate operations. Special thanks to the developers of HCERT Java for their valuable contribution.
