# Sample convention server for Tanzu Application Platform

A Java implementation of https://github.com/vmware-tanzu/cartographer-conventions/blob/main/samples/convention-server

### How to deploy the convention server

```
kbld -f k8s/server-for-tap1.1.yaml | kapp deploy -a inspect-image-convention -f - -c -y
```

### How to build the image of the convention server

The image of sample-convention-server is managed by Tanzu Build Service, which was created
as follows:

```
kp image save inspect-image-convention-server \
  --tag ghcr.io/making/inspect-image-convention-server \
  --git https://github.com/making/sample-convention-server \
  --git-revision inspect-image \
  --env BP_JVM_VERSION=17 \
  --wait
```

You should be also able to build your own image using Maven plugin as follows:

```
IMAGE_NAME=...
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=${IMAGE_NAME}
docker push ${IMAGE_NAME}
```

### Trying out

```
curl -sL https://github.com/vmware-tanzu/cartographer-conventions/raw/main/samples/spring-convention-server/workload.yaml | sed 's/carto.run/apps.tanzu.vmware.com/g' | kubectl apply -f-
```

```
$ kubectl get podintent spring-sample -oyaml
apiVersion: conventions.apps.tanzu.vmware.com/v1alpha1
kind: PodIntent
metadata:
  annotations:
    kubectl.kubernetes.io/last-applied-configuration: |
      {"apiVersion":"conventions.apps.tanzu.vmware.com/v1alpha1","kind":"PodIntent","metadata":{"annotations":{},"name":"spring-sample","namespace":"default"},"spec":{"template":{"spec":{"containers":[{"image":"scothis/petclinic:sbom-20211210@sha256:8b517f21f283229e855e316e2753396239884eb9c4009ab6c797bdf2a041140f","name":"workload"}]}}}}
  creationTimestamp: "2022-07-05T16:24:37Z"
  generation: 1
  name: spring-sample
  namespace: default
  resourceVersion: "259630652"
  uid: a7629af8-d1a0-48b6-9694-afa60495b8c0
spec:
  serviceAccountName: default
  template:
    metadata: {}
    spec:
      containers:
      - image: scothis/petclinic:sbom-20211210@sha256:8b517f21f283229e855e316e2753396239884eb9c4009ab6c797bdf2a041140f
        name: workload
        resources: {}
status:
  conditions:
  - lastTransitionTime: "2022-07-05T16:26:11Z"
    message: ""
    reason: Applied
    status: "True"
    type: ConventionsApplied
  - lastTransitionTime: "2022-07-05T16:26:11Z"
    message: ""
    reason: ConventionsApplied
    status: "True"
    type: Ready
  observedGeneration: 1
  template:
    metadata:
      annotations:
        boot.spring.io/actuator: http://:8081/actuator
        boot.spring.io/version: 2.5.6
        conventions.apps.tanzu.vmware.com/applied-conventions: |-
          sample-convention/add-env-var
          spring-boot-convention/spring-boot
          spring-boot-convention/spring-boot-graceful-shutdown
          spring-boot-convention/spring-boot-web
          spring-boot-convention/spring-boot-actuator
          spring-boot-convention/service-intent-mysql
          spring-boot-convention/service-intent-postgres
          appliveview-sample/app-live-view-connector
          appliveview-sample/app-live-view-appflavours
          appliveview-sample/app-live-view-systemproperties
        services.conventions.apps.tanzu.vmware.com/mysql: mysql-connector-java/8.0.27
        services.conventions.apps.tanzu.vmware.com/postgres: postgresql/42.2.24
      labels:
        conventions.apps.tanzu.vmware.com/framework: spring-boot
        services.conventions.apps.tanzu.vmware.com/mysql: workload
        services.conventions.apps.tanzu.vmware.com/postgres: workload
        tanzu.app.live.view: "true"
        tanzu.app.live.view.application.actuator.port: "8081"
        tanzu.app.live.view.application.flavours: spring-boot
        tanzu.app.live.view.application.name: petclinic
    spec:
      containers:
      - env:
        - name: CONVENTION_SERVER # <------------------------------ ⭐️⭐️⭐️⭐️⭐️
          value: HELLO FROM CONVENTION
        - name: JAVA_TOOL_OPTIONS
          value: -Dmanagement.endpoint.health.probes.add-additional-paths="true" -Dmanagement.endpoint.health.show-details=always
            -Dmanagement.endpoints.web.base-path="/actuator" -Dmanagement.endpoints.web.exposure.include=*
            -Dmanagement.health.probes.enabled="true" -Dmanagement.server.port="8081"
            -Dserver.port="8080" -Dserver.shutdown.grace-period="24s"
        image: index.docker.io/scothis/petclinic@sha256:8b517f21f283229e855e316e2753396239884eb9c4009ab6c797bdf2a041140f
        name: workload
        ports:
        - containerPort: 8080
          protocol: TCP
        resources: {}
```