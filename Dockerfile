FROM openjdk:8-jdk-alpine
LABEL maintainer="National Institute of Standards and Technology"

COPY VERSION /

ENV DEBIAN_FRONTEND noninteractive
ARG EXEC_DIR="/opt/executables"
ARG DATA_DIR="/data"

#Create folders
RUN mkdir -p ${EXEC_DIR} \
    && mkdir -p ${DATA_DIR}/inputs \
    && mkdir ${DATA_DIR}/outputs

# Copy wipp-thresholding-plugin JAR
COPY target/wipp-time-seq-fov-plugin*.jar ${EXEC_DIR}/wipp-time-seq-fov-plugin.jar

#Set working dir
WORKDIR ${EXEC_DIR}

# Default command. Additional arguments are provided through the command line
ENTRYPOINT ["java", "-jar", "wipp-time-seq-fov-plugin.jar"]