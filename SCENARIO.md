Scenario
===

### Access protected method without being authenticated

```
curl -i -H "Accept: application/json" -X GET http://localhost:8080/users
```

You should get a ```401 Unauthorized``` response status.

### Log-in

```
curl -i -c cookie.txt -H "Accept: application/json" -H "Content-type: application/json" -X POST -d '{"username":"admin@example.com","password":"admin"}' http://localhost:8080/users/auth
```

You should get a ```200 OK``` response status and have a valid cookie stored in ```cookie.txt```.

### Access protected method again

```
curl -i -b cookie.txt -H "Accept: application/json" -X GET http://localhost:8080/users
```

You should get a ```200 OK``` response status and some JSON representing existing users.