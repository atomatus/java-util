# java-util
Set of utilities to help in project development.

## HttpConnection
Fully class for http or https connections.
A simple way to do REST actions like methods get, post, put patch or delete.

Simple example of action GET with basic auth.

```
try(Response resp = new HttpConnection()
    .useBasicAuth()
    .setCredentials("username", "password")
    .getContent("http://test.com/client")) {

    //response content type
    System.out.println(resp.getContentType());

    //response content as string
    System.out.println(resp.getContent());

    //or parse and convert serialized data (json/bson/xml) to object
    Client c = resp.parse(Client.class);
} catch (URLConnectionException ex) {
    e.printStackTrace();
}
```

`` application/json ``

`` { "id": 123, "name": "Example" } ``