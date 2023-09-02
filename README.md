![build](https://github.com/atomatus/java-util/actions/workflows/gradle-ci.yml/badge.svg)

# ☕ java-util: A Java Utility Library
The java-util library is a comprehensive set of Java utilities designed to simplify common tasks in application development. It provides a wide range of features, including:

- HTTP Connection: Simplifies HTTP and HTTPS operations, such as GET, POST, PUT, PATCH, and DELETE requests. It supports basic authentication and automatic parsing of response data into Java objects.

- Socket Connection: Facilitates client-server communication via sockets, making it easy to create socket servers and clients.

- MacVendors: Quickly identifies device manufacturers based on MAC addresses, aiding in device recognition.

- Security: Offers encryption and random key generation for data protection and security.

- Serialization: Streamlines object serialization and deserialization in formats like Object Base64, BSON, JSON, and XML.

- ArrayHelper: Provides powerful tools for analyzing, manipulating, and converting Java arrays.

- Decimal Helper: Helps with the conversion and manipulation of floating-point and numeric data types, simplifying decimal and monetary value formatting.

- Date Helper: Simplifies date manipulation and formatting tasks in Java.

- Memory Info: Provides memory usage information, including free, allocated, and total memory.

- SNTP Client: Synchronizes the system clock with online time servers to obtain accurate date and time information.

- String Utils: Offers utilities for string manipulation, including checks for nullity, capitalization, string padding, and string joining.

- Reflection: Enables dynamic access and manipulation of Java objects at runtime, allowing for class inflation and method/field access.

- Debug: Provides a means to check if code is running in debug mode.

The java-util library serves as a versatile toolset to accelerate Java application development by simplifying common tasks and offering advanced features for data manipulation and network communication.

----

## 🌐 Http Connection

### Performing HTTP Requests

The `HttpConnection` class simplifies `HTTP` and `HTTPS` connections, enabling you to perform RESTful actions such as `GET`, `POST`, `PUT`, `PATCH`, and `DELETE`. Below is an example of a GET request with basic authentication:

#### Java Example:

```java
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

#### Response Content (JSON):

```json
 { "id": 123, "name": "Example" } 
```

### GET Request with URL Parameters
You can also perform GET requests with URL parameters:

#### Java Example:

```java
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

### GET Request with URL Parameters
You can also perform GET requests with URL parameters:

#### Java Example:
```java
 try (Response resp = new HttpConnection()
        .getContent("https://test.com/api/{0}/json",            
            Parameter.buildQuery("urlParamExample"),
            Parameter.buildQuery("param", "queryParamExample"))) {
                String result = resp.getContent();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//url: https://test.com/api/urlParamExample/json?param=queryParamExample
```

### POST Request with Parameters
Performing a POST request with URL parameters, query parameters, and a request body:

#### Java Example:

```java
 try (Response resp = new HttpConnection()
        .postContent("https://test.com/api/{0}/json",            
            Parameter.buildQuery("urlParamExample"),
            Parameter.buildQuery("param", "queryParamExample"),
            Parameter.buildBody("data", "data to send"))) {
                String result = resp.getContent();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//url: https://test.com/api/urlParamExample/json?param=queryParamExample
```

### Caching Responses
You can enable response caching to improve performance:

#### Java Example:

```java
 try (Response resp = new HttpConnection()
        .useCache()//enable cache
        .setCacheMaxAge(60L, TimeUnit.SECONDS)//cache data max age
        .setCacheId(cacheId)//if defined, create an isolated cache, otherwise use global cache.
        .getContent("https://test.com/api/{0}/json",            
            Parameter.buildQuery("urlParamExample"))) {
                String result = resp.getContent();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//url: https://test.com/api/urlParamExample/json
//all other requests (using cacheId) will use cached data into 60 seconds, then request again.
```


## 🔌 Socket Connection

### Simplifying Socket Communication
The java-util library provides a straightforward way to establish socket connections for both clients and servers.

#### Java Example (Socket Client/Server for Serializable Object):

```java
final Server s = new Server();
            s.setServerObjectAdapter(new ServerObjectAdapter() {
                @Override
                public void onInputObjectAction(InputObjectEvent evt) {
                    try {
                        Vendor v = evt.readObject();
                        assertEquals(v.getCompany(), "Client Inc");
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onOutputObjectAction(OutputObjectEvent evt) {
                    try {
                        Vendor v = new Vendor();
                        v.setCompany("Server Inc");
                        evt.writeObject(v);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            s.open();

            try(Client c = new Client(s.getPort())) {
                Vendor v = new Vendor();
                v.setCompany("Client Inc");
                c.writeObject(v);
                Vendor resp = c.readObject();
                assertEquals(resp.getCompany(), "Server Inc");
            }

            s.close();
```


## 🤝 MacVendors

### Find Vendors by Mac Address
MacVendors offers a fast and efficient way to retrieve vendor information based on a MAC address.

#### Java Example:

```java
//example how to find a vendor (Apple Inc) informations from macAddress.
Vendor v = MacVendors.getInstance().find("BC:92:6B:FF:FF:FF");
```

## 🔒 Security

### Data Encryption and Protection
The `java-util` library provides various tools for data encryption and protection.

#### Encryptors
Encrypt and decrypt data using CIPHER, NUMERIC, or NUMERIC_MATRIX encryption methods.

#### Java Example:

```java
//example encryptor build.
Encryptor e = Encryptor.builder()
          		.type(Type.CIPHER)//CIPHER, NUMERIC, NUMERIC_MATRIX
          		.key("01234567890QWERTYUIOP1") //if numeric, key must be numbers only.
          		.build();

String result   = e.encrypt("target");
String original = e.decrypt(result);

```

### Key Generator
Generate random keys in decimal, hexadecimal, alphabetic, or alphanumeric formats.

#### Java Examples:

```java
//random decimal.
System.out.println(KeyGenerator.generateRandomKey(10 /*length, if not set, default value is 6.*/))

//random hexdecimal.
System.out.println(KeyGenerator.generateRandomKeyHex())

//random alphabetic chars.
System.out.println(KeyGenerator.generateRandomKeyAlpha())

//random alpha numeric chars.
System.out.println(KeyGenerator.generateRandomKeyAlphaNumeric())
```

``2984751263``

``5E8F3AB1CD``

``JYUSDFLBOR``

``Q8E5W2G8BP``

### Sensitive Bytes
Write data ciphered in memory to protect againt access violation,
using `SensitiveBytes`, `SentitiveChars` or `SensitiveData` classes.
Keeping ciphered data in memory or storing in temp file to free memory.

#### Java Example (Sensitive Bytes):

```java
//Sensitive bytes
byte[] str = "This is a sensitive string!".getBytes();
SensitiveBytes sb0 = SensitiveBytes.of(str);//instance inputing bytes

// and/or

//append data
SensitiveBytes sb0 = new SensitiveBytes()
                .append((byte) 'A')
                .append((byte) 'B')
                .append((byte) 'C')
                .append((byte) 'D');

//data is ciphered
byte[] res = sb0.readAll();//now, deciphering back to read.

//reading by index
byte b = sb0.read(0);
//b = 'A' (byte)

//reading by range
byte[] range = sb0.read(0, 2);
//range = ['A', 'B'] (byte)

//reading by iterator loop
for (Byte b : sb0) {
  //todo
}

//reading as stream
InputStream stream = sb0.stream();

```

#### Java Example (Storing SensitiveBytes):
Storing large SensitiveBytes in temp file to free memory.

```java
byte[] script = new HttpConnection()
        .getContent("https://raw.githubusercontent.com/chcmatos/nanodegree_py_analyze_srag/main/app/analyze.py")
        .getContentBytes();
SensitiveBytes sb = SensitiveBytes.of(script);
File tmp = sb.store(); // Creates a temporary file in the system's temporary directory
```

```java
 //or naming file
 File tmp = sb.store("filepath");
```

```java
 //or inputing file
 File file = new File("filepath");
 File tmp = sb.store(file);
```
```java
 //to recover data back
 sb.stored();
 //or inputing file
 sb.stored(tmp);
```

#### Java Example (Reading Stored Data without Reloading into Memory):
To read content stored in temp file, but without reload back in memory.

```java
   byte[] bytes = sb.peekStored(); //deciphered   
   //or as stream
   InputStream stream = sb.streamStored(); //deciphering stream when read
```

## 📝 Serializer

### Serialize and Deserialize Objects
The Serializer class enables serialization and deserialization of objects into various formats, including Object Base64, BSON, JSON, or XML.

#### Java Example:
```java
Example ex1 = new Example();
Serializer s = Serializer.getInstance(Serializer.Type.XML);
String xmlEx = s.serialize(ex1);
ex1 = s.deserialize(xmlEx);
``` 

You can also apply custom serialization rules to specific classes using the Serializer.setupDefaultConfigurationXml() method.

```java
Serializer.setupDefaultConfigurationXml(Example.class, x -> {
    //do not serialize to password field.
    x.omitField(Example.class, "password");
});
```

## ⛓ 👊 ArrayHelper

### Array Manipulation and Analysis
The `ArrayHelper` class provides a range of functions for manipulating and analyzing arrays.

#### Java Examples:

```java
// Insert a new element at the first index
Integer[] arr = new Integer[] {1, 2, 3};
ArrayHelper.push(arr, 0);

// Insert a new element at the last index
Integer[] arr = new Integer[] {1, 2, 3};
arr = ArrayHelper.add(arr, 0);

// Select array elements and convert them to new values
Integer[] arr = new Integer[] {1, 2, 3};
arr = ArrayHelper.select(arr, e -> e * 2);

// Filter array elements
Integer[] arr = new Integer[] {1, 2, 3};
arr = ArrayHelper.filter(arr, e -> e < 3);

// Find the first element by condition
Integer[] arr = new Integer[] {1, 2, 3};
Integer i = ArrayHelper.first(arr, e -> e % 2 == 0);

// Remove duplicate elements
Integer[] arr = new Integer[] {1, 1, 2, 2, 3};
Integer[] result = ArrayHelper.distinct(arr);

// Reduce array elements
Integer[] arr = new Integer[] {1, 2, 3};
Integer result = ArrayHelper.reduce(arr, (acc, curr) -> acc + curr);

// Take a count of elements
Integer[] arr = new Integer[] {1, 2, 3};
arr = ArrayHelper.take(arr, 2);

// Ignore a count of elements
Integer[] arr = new Integer[] {1, 2, 3};
arr = ArrayHelper.jump(arr, 2);

// Resize array
Integer[] arr = new Integer[] {1, 2, 3};
arr = ArrayHelper.resize(arr, arr.length + 1);

// Reverse array
Integer[] arr0 = new Integer[] {1, 2, 3};
ArrayHelper.reverse(arr0);

// Join arrays
Integer[] arr0 = new Integer[] {0, 1};
Integer[] arr1 = new Integer[] {2, 3, 4, 5};
Integer[] arr2 = ArrayHelper.join(arr0, arr1);

// Check conditions on arrays
Integer[] arr = new Integer[] {1, 2, 3};
boolean b1 = ArrayHelper.all(arr, i -> i % 2 == 0);
boolean b2 = ArrayHelper.any(arr, i -> i % 2 == 0);
boolean b3 = ArrayHelper.contains(arr, 2);
boolean b4 = ArrayHelper.sequenceEquals(arr0, arr1);
```

## ∑ $ 👊 Decimal Helper

The DecimalHelper class assists in analyzing and converting wrapper or decimal types to BigDecimal, currency, or decimal format, automatically detecting the locale.

### Java Examples:

```java
//all examples will be converted to a bigDecimal of 12.40
BigDecimal bd = DecimalHelper.toBigDecimal(12.4D);
BigDecimal bd = DecimalHelper.toBigDecimal(12.4F);
BigDecimal bd = DecimalHelper.toBigDecimal("12.4");
BigDecimal bd = DecimalHelper.toBigDecimal("12,4");
BigDecimal bd = DecimalHelper.toBigDecimal("R$12,40");
BigDecimal bd = DecimalHelper.toBigDecimal("$12.40");
```

```java
String currency = DecimalHelper.toCurrency(bd);//default locale pt-BR
//currency = R$ 12,40

String decimal = Decimal.Helper.toDecimal(bd);
//decimal = 12,40
```

```java
String currency = DecimalHelper.toCurrency(bd, Locale.US);
//currency = $12.40

String decimal = Decimal.Helper.toDecimal(bd, Locale.US);
//decimal = 12.40
```

## 📅 👊 Date Helper
Helper to parse and convert String date formatted to Date or Calendar.

```java
Date date = DateHelper.getInstance().parseDate("19/11/2020 11:46");
Date date = DateHelper.getInstance().parseDate("19/11/2020");
Date date = DateHelper.getInstance().parseDate("11:46");
```
```
//and more...
```

## 🧠 Memory Info
Information about memory, free, allocated, total...

```java
MemoryInfo mi = MemoryInfo.getInstance();
mi.getBytes(MemoryInfo.Amount.FREE); //free memory in bytes.
mi.getKBytes(MemoryInfo.Amount.FREE); //free memory in KB.
mi.getMBytes(MemoryInfo.Amount.FREE); //free memory in MB.
```

## 🕑 SNTP Client
Discovery current date time online for your location

```java
SNTPClient sntp = new SNTPClient();
//attempt to get global date time online.
//default timeout is 5 seconds.
if(sntp.request(/*timeout in millis*/)) {
    long l = sntp.getNtpTime();
    //or
    Date d = sntp.getNtpDate();
    //or
    Calendar c = sntp.getNtpCalendar();
}
```

## ⛓ String Utils
Join strings, check and request string input not empty or not whitespace.

```java
//requires
String str = null;
str = StringUtils.requireNonNull(str);//throws exception
str = StringUtils.requireNonNull(str, "custom message");//throws exception with custom message

str = StringUtils.requireNonNullOrEmpty(str);//throws exception
str = StringUtils.requireNonNullOrEmpty(str, "custom message");//throws exception with custom message

str = StringUtils.requireNonNullOrWhitespace(str);//throws exception
str = StringUtils.requireNonNullOrWhitespace(str, "custom message");//throws exception with custom message

//check values
StringUtils.isNullOrEmpty(str);
StringUtils.isNullOrWhitespace(str);

//capitalize
StringUtils.capitalize("AbC", StringUtils.Capitalize.NONE); 
//abc

StringUtils.capitalize("AbC", StringUtils.Capitalize.ALL); 
//ABC

StringUtils.capitalize("Aa bbb ccc", StringUtils.Capitalize.FIRST_EACH_WORD);
//Aa Bbb Ccc

StringUtils.capitalize("Aa bbb ccc", StringUtils.Capitalize.ONLY_FIRST);
//Aa bbb ccc

//padLeft
StringUtils.padLeft("1", 3, '0');
//001

//padRight
StringUtils.padRight("1", 3, '0');
//100

//join
str = StringUtils.join("[", "]", ", ", 0, 1, 2, 3);
//str = "[0, 1, 2, 3]"

str = StringUtils.join(",", 0, 1, 2, 3);
//str = "0, 1, 2, 3"
```

```
//and more...
```

## 🔥 Reflection
Access objects by reflection.<br>
Inflate (create an instance or load access for static class) a class by fullName (included package path) 
to try to manipulate and access it.

```java
//if not found, throws exception.
//inflating static class, set only full path.
Reflection r = Reflection.inflate("android.os.Debug");

//it was found? check inflated() to know.
Reflection r = Reflection.tryInflate("android.os.Debug");
if(r.inflated()) {
    //r.configDeflateAfterReturns() //after return new data dispose objects loaded.
    r = r.method("isDebuggerConnected");
    //method result value as boolean.
    boolean b = r.valueBoolean();    
}

//The Fastest way to do samething
boolean b = Reflection.tryInflate("android.os.Debug")
    .configDeflateAfterReturns()
    .method("isDebuggerConnected");
```

```java
//cast object to access method and fields
Object obj      = "simple test";
Reflection r    = Reflection.cast(obj, "java.lang.String");
boolean found   = r.method("indexOf", "test").valueInt() > 0;
```

```
//and more...
```

## 🔍 📋 Debug
Check if current code is running in debug mode

```java
Debug.isDebugMode()
```
----
(c) Atomaus - All Rights Reserveds.
