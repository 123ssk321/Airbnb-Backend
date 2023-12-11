FROM node:8-alpine

VOLUME /artillery
WORKDIR /artillery

COPY . /artillery

RUN npm install -g artillery@1.6.0-2

ENTRYPOINT ["artillery"]
CMD ["artillery run workload-light.yaml"]