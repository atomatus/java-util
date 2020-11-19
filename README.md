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

Example of action GET with URL parameter

```
 try (Response resp = new HttpConnection()
        .changeReadTimeOut(8000/*8s*/)
        .setSecureProtocol(HttpConnection.SecureProtocols.SSL)
        .getContent("https://macvendors.co/api/{0}/json",
            /*url parameter maybe passed by name or index*/
            Parameter.buildQuery(macAddress.toUpperCase()))) {
                //response format contains a rootElement:
                //{"result": {...} }
                Vendor v = resp.parse("result", Vendor.class);                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
```

## MacVendors
The Fastest way to find vendors by macAddress

```
//example how to find a vendor (Apple Inc) informations from macAddress.
Vendor v = MacVendors.getInstance().find("BC:92:6B:FF:FF:FF");
```

## Security
Encryptors types and helper for random key generator.

```
//example encryptor build.
Encryptor e = Encryptor.builder()
          		.type(Type.CIPHER)//CIPHER, NUMERIC, NUMERIC_MATRIX
          		.key("01234567890QWERTYUIOP1") //if numeric, key have to be numbers only.
          		.build();

String result   = e.encrypt("target");
String original = e.decrypt(result);

```

````
//random decimal.
System.out.println(KeyGenerator.generateRandomKey(10 /*length, if not set, default value is 6.*/))

//random hexdecimal.
System.out.println(KeyGenerator.generateRandomKeyHex())

//random alphabetic chars.
System.out.println(KeyGenerator.generateRandomKeyAlpha())

//random alpha numeric chars.
System.out.println(KeyGenerator.generateRandomKeyAlphaNumeric())
````

``2984751263``

``5E8F3AB1CD``

``JYUSDFLBOR``

``Q8E5W2G8BP``
