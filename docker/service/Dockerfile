FROM debian:bullseye-slim

MAINTAINER LE Nhu Chu Hiep <hieplnc.m20ict@st.usth.edu.vn>

ENV DEBIAN_FRONTEND noninteractive
ENV DEBIAN_FRONTEND newt

RUN apt-get update && apt-get install -yq --no-install-recommends \
        openjdk-17-jre-headless \
        git aha vim procps tar \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64/

COPY ulake.tar /home
COPY setup.sh /home
RUN /bin/bash /home/setup.sh

ADD entrypoint.sh /entrypoint.sh
RUN chmod a+x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
