FROM ubuntu:latest
LABEL authors="simaodiazz"

ENTRYPOINT ["top", "-b"]