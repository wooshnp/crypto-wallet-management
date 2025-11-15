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

## Technical Stack
TBA
## API Endpoints
TBA
## Domain Model
![Domain Model](doc/er_model.drawio.svg)


- **User**: One wallet per user (identified by unique email)
- **Wallet**: Contains 0 to many assets
- **Asset**: Token holdings (symbol, quantity, current price)
- **PriceHistory**: Historical price data for tokens

## Configuration

Key configuration properties in `application.yaml`:
TBA

## Setup Instructions
TBA

### Prerequisites
- Java 21
- Maven 3.6+
- TBA

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd crypto-wallet-management
```
TBA

### Running Tests
TBA

