Steps

#### Create a kube (Kind) service - user-service.yml

- This is to define the type of service (loadbalancer, nodePort, clusterIP), also add a protocol and port

#### Create a kube (Kind) deployment - user-deployment.yml

- This is to define the type of service (loadbalancer, nodePort, clusterIP), also add a protocol and port

## Apply the configuration changes

```
- kubectl apply -f user-service.yml
- kubectl apply -f user-deployment.yml
```

#### Either ForwardPort or get Url

## ForwardPort the svc (service) to external host and path

```
 - kubectl port-forward svc/user-service 8082:80
```

### Get service url

```
- minikube service user-service --url
```
