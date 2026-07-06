# E-Commerce Modular Monolith
## Overview

This project is an evolution of a previous microservices-based e-commerce platform. The original system was designed using a distributed architecture with independent services for customers, orders, payments, products, notifications, and shipments.

While the microservices approach provided strong service isolation and independent scalability, it introduced significant operational complexity, longer deployment cycles, and increased infrastructure costs that were difficult to justify for the application's size and traffic requirements.

To address these challenges, the platform was redesigned as a Modular Monolith using Spring Modulith, preserving clear module boundaries while dramatically simplifying deployment and operations.

## Why Move Away From Microservices?

The original microservices architecture included multiple independently deployed services:

* Customer Service
* Product Service
* Order Service
* Payment Service
* Shipment Service
* Notification Service
* API Gateway
* Service Discovery
* Distributed Messaging Infrastructure

As the system evolved, several challenges emerged:

### Operational Complexity

Each service required:

* Individual deployment pipelines
* Container images
* Configuration management
* Monitoring and logging
* Networking and service discovery
* Health checks and orchestration

Even small feature changes often required coordinating multiple services.


### Increased Infrastructure Costs

Running several services introduced costs associated with:

* Multiple compute instances
* Container orchestration
* Network communication
* Observability tooling
* Service management infrastructure

For many business scenarios, the infrastructure overhead exceeded the actual application workload.

### Longer Development Cycles

Frequently had to:

* Start multiple services locally
* Maintain service contracts
* Handle inter-service communication failures
* Debug distributed transactions
* Manage version compatibility

This slowed feature delivery and increased development effort.


### Why Spring Modulith?

Spring Modulith provides a middle ground between a traditional monolith and a distributed microservices architecture.

The application remains a single deployable unit while enforcing strong modular boundaries.

Benefits include:

* Single application deployment
* Reduced infrastructure footprint
* Faster startup times
* Simpler local development
* Strong module isolation
* Explicit application boundaries
* Event-driven communication between modules
* Easier testing and maintenance

Modules can evolve independently while remaining part of a cohesive application.

### Architecture

The application is organized into business-focused modules:

```
com.derocode.EcommApp 
│ 
├── customer 
├── order 
├── payment 
├── product 
├── shipment 
├── notification 
└── security
```
Each module contains its own:

* Domain model
* Repositories
* Services
* API layer
* Internal implementation details

Spring Modulith helps ensure modules communicate through well-defined


## Benefits Achieved
### Reduced Deployment Time

Instead of deploying multiple services:

* Customer Service
* Order Service
* Payment Service
* Shipment Service
* Notification Service

the platform is deployed as a single application.

This significantly reduces:

* CI/CD complexity
* Release coordination
* Environment management
* Lower Infrastructure Costs

The modular monolith eliminates the need for maintaining multiple runtime environments while still preserving logical separation of business domains.

### Benefits include:

* Fewer running containers
* Reduced cloud resource consumption
* Lower networking overhead
* Simpler monitoring requirements
* Improved Developer Experience

Developers can:

* Run the entire platform locally
* Debug across module boundaries
* Test business workflows end-to-end
* Develop features without managing multiple services
* Preserved Architectural Boundaries

Unlike a traditional monolith, module boundaries remain explicit and enforceable.

This prevents the application from degrading into a tightly coupled codebase.

## Technology Stack
### Backend
* Java 21
* Spring Boot
* Spring Modulith
* Spring Security
* Spring Data JPA
* Spring Data MongoDB
### Databases
* PostgreSQL
* MongoDB / DocumentDB
### Infrastructure
* Docker
* AWS
* GitHub Actions