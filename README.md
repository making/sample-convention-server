# inspect-image convention server for Tanzu Application Platform

### How to deploy the convention server

For TAP 1.3+

```
kbld -f https://github.com/making/sample-convention-server/raw/inspect-image/k8s/server.yaml  | kapp deploy -a inspect-image-convention -f - -c -y
```

For prior to TAP 1.3

```
kbld -f https://github.com/making/sample-convention-server/raw/inspect-image/k8s/server-legacy.yaml | kapp deploy -a inspect-image-convention -f - -c -y
```

> To apply conventions into existing `PodIntent`s, restart conventions-controller-manager
>
> ```
> # TAP 1.3+
> kubectl rollout restart deploy -n cartographer-system cartographer-conventions-controller-manager
> # prior to TAP 1.3
> kubectl rollout restart deploy -n conventions-system conventions-controller-manager 
> ```

### Trying out

```
curl -sL https://github.com/vmware-tanzu/cartographer-conventions/raw/main/samples/spring-convention-server/workload.yaml | kubectl apply -f-
```

```
$ kubectl get podintent.conventions.carto.run spring-sample -oyaml
# or kubectl get podintent.conventions.apps.tanzu.vmware.com spring-sample -oyaml (prior to TAP 1.3)
apiVersion: conventions.carto.run/v1alpha1
kind: PodIntent
metadata:
  annotations:
    kubectl.kubernetes.io/last-applied-configuration: |
      {"apiVersion":"conventions.carto.run/v1alpha1","kind":"PodIntent","metadata":{"annotations":{},"name":"spring-sample","namespace":"default"},"spec":{"template":{"spec":{"containers":[{"image":"scothis/petclinic:sbom-20211210@sha256:8b517f21f283229e855e316e2753396239884eb9c4009ab6c797bdf2a041140f","name":"workload"}]}}}}
  creationTimestamp: "2022-10-13T04:17:16Z"
  generation: 1
  name: spring-sample
  namespace: default
  resourceVersion: "744667"
  selfLink: /apis/conventions.carto.run/v1alpha1/namespaces/default/podintents/spring-sample
  uid: 256e89c0-00c5-485c-9598-7e5bb40dfc54
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
  - lastTransitionTime: "2022-10-13T04:17:24Z"
    message: ""
    reason: Applied
    status: "True"
    type: ConventionsApplied
  - lastTransitionTime: "2022-10-13T04:17:24Z"
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
        conventions.carto.run/applied-conventions: |-
          inspect-image-convention/buildpacks
          inspect-image-convention/base-image
          inspect-image-convention/run-image
          spring-boot-convention/spring-boot
          spring-boot-convention/spring-boot-graceful-shutdown
          spring-boot-convention/spring-boot-web
          spring-boot-convention/spring-boot-actuator
          spring-boot-convention/service-intent-mysql
          spring-boot-convention/service-intent-postgres
          appliveview-sample/app-live-view-appflavour-check
          appliveview-sample/app-live-view-connector-boot
          appliveview-sample/app-live-view-appflavours-boot
          appliveview-sample/app-live-view-systemproperties
        inspect-image.buildpacks.io/base-image: |-
          reference: 0d382d05205978c348b29b35331bbbe0200b93a4b55f33934cf6131dbaa86337
          top_layer: sha256:d7467baf869b85ae4a6df9a7f06f008bf1e41c4d5f4916c399e602a94d0b7cc4
        inspect-image.buildpacks.io/buildpacks: |-
          - id: paketo-buildpacks/ca-certificates
            version: 3.0.1
          - id: paketo-buildpacks/bellsoft-liberica
            version: 9.0.1
          - id: paketo-buildpacks/syft
            version: 1.2.0
          - id: paketo-buildpacks/maven
            version: 6.0.1
          - id: paketo-buildpacks/executable-jar
            version: 6.0.1
          - id: paketo-buildpacks/apache-tomcat
            version: 7.0.2
          - id: paketo-buildpacks/dist-zip
            version: 5.0.1
          - id: paketo-buildpacks/spring-boot
            version: 5.2.0
        inspect-image.buildpacks.io/run-image: index.docker.io/paketobuildpacks/run:tiny-cnb
        services.conventions.apps.tanzu.vmware.com/mysql: mysql-connector-java/8.0.27
        services.conventions.apps.tanzu.vmware.com/postgres: postgresql/42.2.24
      labels:
        conventions.apps.tanzu.vmware.com/framework: spring-boot
        services.conventions.apps.tanzu.vmware.com/mysql: workload
        services.conventions.apps.tanzu.vmware.com/postgres: workload
        tanzu.app.live.view: "true"
        tanzu.app.live.view.application.actuator.port: "8081"
        tanzu.app.live.view.application.flavours: spring-boot
        tanzu.app.live.view.application.name: unknown-app
    spec:
      containers:
      - env:
        - name: JAVA_TOOL_OPTIONS
          value: -Dmanagement.endpoint.health.show-details=always -Dmanagement.endpoints.web.base-path="/actuator"
            -Dmanagement.endpoints.web.exposure.include=* -Dmanagement.server.port="8081"
            -Dserver.port="8080" -Dserver.shutdown.grace-period="24s"
        image: index.docker.io/scothis/petclinic@sha256:8b517f21f283229e855e316e2753396239884eb9c4009ab6c797bdf2a041140f
        name: workload
        ports:
        - containerPort: 8080
          protocol: TCP
        resources: {}
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