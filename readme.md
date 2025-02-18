# BPMN SERVICE BACKEND

This is the backend service for the BPMN SERVICE project, built with Spring Boot and Camunda BPM.

## Prerequisites

Before you begin, ensure you have the following installed:
 - Docker and Docker Compose

## Getting Started

### 1. Clone the Repository

HTTPS
```bash
git clone https://github.com/MPTCDITR/Domnerka-BPMN-Backend-Service.git
cd Domnerka-BPMN-Backend-Service
```

SSH
```bash
git clone git@github.com:MPTCDITR/Domnerka-BPMN-Backend-Service.git
cd Domnerka-BPMN-Backend-Service
```

### 2. Set up env

Create the .env and copy the template in the .env.example

### 3. Start Docker Services

The project uses Docker to run MySQL databases for BPMN_DB. To start these services:

```bash
docker-compose up -d
```

This command will start the following services:

- MySQL database for BPMN_DB (accessible on port 3307)

## Configuration

- The main configuration file is `src/main/resources/application.yaml`

### Camunda 7 Database Schema

Since we have disabled the automatic schema update for Camunda 7 by setting `camunda.bpm.database.schema-update: false` in our configuration, you need to manually initialize the Camunda database schema.

Please follow the official Camunda documentation for setting up the [database schema](https://docs.camunda.org/manual/7.21/installation/database-schema/#manual-installation):

Make sure to use the scripts corresponding to your specific database system (MySQL in our case ,Version 7.20) and follow the steps carefully.

Remember to run these scripts only once when setting up a new environment or after clearing your database. Running them multiple times may cause errors.

After executing the necessary scripts as per the Camunda documentation, your Camunda database schema will be initialized and ready for use with the application.
