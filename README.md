# Crypto Wallet Management

A Spring Boot application for managing cryptocurrency wallets with real-time price tracking and portfolio performance analysis.

## Overview

This application helps users track their cryptocurrency holdings by integrating with the CoinCap API for real-time pricing data. Users can create wallets, add assets, view portfolio values, and simulate historical performance.

## Features

### 1. Real-time Price Updates
- Automatic price fetching from CoinCap API at configurable intervals
- Concurrent price updates for up to 3 tokens simultaneously using multi-threading
- Price history stored in database for analysis

### 2. Wallet Management
- Create wallet with unique email address
- Add cryptocurrency assets to wallet (symbol, quantity, price)
- Price validation against CoinCap API when adding assets
- View complete wallet details with current valuations

### 3. Portfolio Performance Simulation
- Calculate profit/loss from historical date to present
- Identify best and worst performing assets
- Show percentage performance for each asset
- Simulate hypothetical portfolio scenarios

### 4. Persistent Storage
- SQL database for wallets, assets, and price history
- Secure data persistence across application restarts

## API Documentation

The application uses **SpringDoc OpenAPI 3** for interactive API documentation.

**Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Available Endpoints

#### Wallet Management
| Method | Endpoint                                      | Description |
|--------|-----------------------------------------------|-------------|
| `POST` | `/api/v1/wallets`                             | Create a new wallet with email |
| `GET` | `/api/v1/wallets/{walletId}`                  | Get wallet details with all assets |
| `POST` | `/api/v1/wallets/{walletId}/assets`           | Add asset to wallet |
| `PUT` | `/api/v1/wallets/{walletId}/assets/{assetId}` | Update asset quantity |

#### Portfolio Simulation
| Method | Endpoint                        | Description |
|--------|---------------------------------|-------------|
| `POST` | `/api/v1/simulations/portfolio` | Simulate portfolio performance from historical date |

#### Asset Discovery
| Method | Endpoint                           | Description |
|--------|------------------------------------|-------------|
| `GET` | `/api/v1/assets/available/symbols` | Get all available cryptocurrency symbols |
| `GET` | `/api/v1/assets/available`         | Search and browse available cryptocurrencies (supports search, limit, offset params) |

## Domain Model
![Domain Model](doc/er_model.drawio.svg)

- **User**: One wallet per user (identified by unique email)
- **Wallet**: Contains 0 to many assets
- **Asset**: Token holdings (symbol, quantity, current price)
- **PriceHistory**: Historical price data for tokens

## Configuration

Key configuration properties in `application.yaml`:

```yaml
# Server Configuration
server:
  port: 8080

# Database Configuration
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/crypto_wallet
    username: # You can fetch it from docker compose file
    password: # You can fetch it from docker compose file
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog_master.xml

# CoinCap API Configuration
coincap:
  api:
    base-url: https://rest.coincap.io/v3
    key: ${COINCAP_API_KEY}  # Generate you own

# Price Update Scheduler
wallet:
  price-update:
    enable: true           # Enable/disable scheduled price updates
    interval: 60000        # Update interval in milliseconds (60 seconds)
    max-threads: 3         # Max concurrent price update threads

# Portfolio Simulation
simulation:
  use-market-price:
    enable: true           # true = fetch historical prices, false = use user-provided values

# Caching Configuration
spring:
  cache:
    caffeine:
      spec: maximumSize=500,expireAfterWrite=5m
```

##  Setup Steps

1. **Clone the repository:**
```bash
git clone <repository-url>
cd crypto-wallet-management
```

2. **Start PostgreSQL with Docker Compose:**
```bash
docker compose build
docker compose up -d
```

This will start PostgreSQL in the background. Database credentials are configured in `docker-compose.yml`.


3. **Set environment variables:**
```bash
export COINCAP_API_KEY=your_api_key_here
```

**Note**: Database credentials are already configured in `application.yaml` to match the Docker Compose setup. You can find them in the `docker-compose.yml` file.

4. **Build the project:**
```bash
mvn clean install
```

5. **Run database migrations:**
Liquibase migrations run automatically on application startup.

6. **Start the application:**
```bash
mvn spring-boot:run
```

7. **Access the application:**
- **API Base URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health
