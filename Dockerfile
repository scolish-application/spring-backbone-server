FROM gradle:8.13-jdk-21-and-23-alpine AS builder

WORKDIR /usr/app

COPY . .
