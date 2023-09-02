![build](https://github.com/atomatus/java-util/actions/workflows/gradle-ci.yml/badge.svg)

# ☕ java-util
Set of utilities to help in project development.

## 🌐 Http Connection
Fully class for http or https connections.
A simple way to do REST actions like methods get, post, put patch or delete.

Simple example of action GET with basic auth.

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

`` application/json ``

```json
 { "id": 123, "name": "Example" } 
```

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

Example of action GET with URL Parameter, Query Parameter

```
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

Example of action POST with URL Parameter, Query Parameter and Body Parameter

```
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

Example of action GET Cached

```
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
Simple way for socket Client/Server connection.

Example socket client/server for a serializable object.

```
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
The Fastest way to find vendors by macAddress

```
//example how to find a vendor (Apple Inc) informations from macAddress.
Vendor v = MacVendors.getInstance().find("BC:92:6B:FF:FF:FF");
```

## 🔒 Security

### Encryptors  
Encrypt and decrypt data ysing CIPHER, NUMERIC or NUMERIC_MATRIX.

```
//example encryptor build.
Encryptor e = Encryptor.builder()
          		.type(Type.CIPHER)//CIPHER, NUMERIC, NUMERIC_MATRIX
          		.key("01234567890QWERTYUIOP1") //if numeric, key must be numbers only.
          		.build();

String result   = e.encrypt("target");
String original = e.decrypt(result);

```

### Key Generator
Generate random key decimal, hexadecimal or alpha numeric. 

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

### Sensitive Bytes
Write data ciphered in memory to protect againt access violation,
using SensitiveBytes, SentitiveChars or SensitiveData classes.
Keeping ciphered data in memory or storing in temp file to free memory.

```
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

Storing large SensitiveBytes in temp file to free memory

```
 byte[] script = new HttpConnection()
                .getContent("https://raw.githubusercontent.com/chcmatos/nanodegree_py_analyze_srag/main/app/analyze.py")
                .getContentBytes();
 SensitiveBytes sb = SensitiveBytes.of(script);
 File tmp = sb.store(); //create temp file in system temp dir.  
```

```
 //or naming file
 File tmp = sb.store("filepath");
```

```
 //or inputing file
 File file = new File("filepath");
 File tmp = sb.store(file);
```
```
 //to recover data back
 sb.stored();
 //or inputing file
 sb.stored(tmp);
```

To read content stored in temp file, but without reload back in memory.

```
   byte[] bytes = sb.peekStored(); //deciphered   
   //or as stream
   InputStream stream = sb.streamStored(); //deciphering stream when read
```

## 📝 Serializer
Serialize and deserialize objects for Object Base64, BSON, JSON or XML.

```
Example ex1 = new Example();
Serializer s = Serializer.getInstance(Serializer.Type.XML);
String xmlEx = s.serialize(ex1);
ex1 = s.deserialize(xmlEx);
``` 

Whether desire apply some particular rule for target class, use below code

```
Serializer.setupDefaultConfigurationXml(Example.class, x -> {
    //do not serialize to password field.
    x.omitField(Example.class, "password");
});
```

## ⛓ 👊 ArrayHelper
Helper to analyze, manipulate and convert array objects.

```
//insert a new element at first index.

Integer[] arr = new Integer[] {1, 2, 3};
ArrayHelper.push(arr, 0);
//arr = [0, 1, 2, 3]
```

```
//insert a new element at last index.

Integer[] arr = new Integer[] {1, 2, 3};
arr = ArrayHelper.add(arr, 0);
//arr = [1, 2, 3, 0]
```

```
//select array elements converting to new values.

Integer[] arr = new Integer[] {1, 2, 3};
arr = ArrayHelper.select(arr, e -> e * 2);
//arr = [2, 4, 6]
```

```
//filter array elements.

Integer[] arr = new Integer[] {1, 2, 3};
arr = ArrayHelper.filter(arr, e -> e < 3);
//arr = [1, 2]

```
```
//first element by condition.

Integer[] arr = new Integer[] {1, 2, 3};
Integer i = ArrayHelper.first(arr, e -> e % 2 == 0);
//i = 2
```

```
//distinct array elements.

Integer[] arr = new Integer[] {1, 1, 2, 2, 3};
Integer result = ArrayHelper.distinct(arr);
//result = [1, 2, 3]
```

```
//reduce array elements.

Integer[] arr = new Integer[] {1, 2, 3};
Integer result = ArrayHelper.reduce(arr, (acc, curr) -> acc + curr);
//result = 6
```

```
//Take a count of elements.

Integer[] arr = new Integer[] {1, 2, 3};
arr = ArrayHelper.take(arr, 2);
//arr = [1, 2]
```

```
//"Jump" (Ignore) a count of element.

Integer[] arr = new Integer[] {1, 2, 3};
arr = ArrayHelper.jump(arr, 2);
//arr = [3]
```

```
//Resize array

Integer[] arr = new Integer[] {1, 2, 3};
arr = ArrayHelper.resize(arr, arr.length + 1);
//arr = [1, 2, 3, null]

arr = ArrayHelper.resize(arr, arr.length - 1);
//arr = [1, 2]
```

```
//Reverse array

Integer[] arr0 = new Integer[] {1, 2, 3};
ArrayHelper.reverse(arr0);
//arr0 = [3, 2, 1]
```

```
//Join array
Integer[] arr0 = new Integer[] {0, 1};
Integer[] arr1 = new Integer[] {2, 3, 4, 5};
Integer[] arr2 = ArrayHelper.join(arr0, arr1);
//arr2 = [0, 1, 2, 3, 4, 5]
```

```
//All condition array

Integer[] arr = new Integer[] {1, 2, 3};
boolean b = ArrayHelper.all(arr, i -> i % 2 == 0);
//b = false
```

```
//Any condition array

Integer[] arr = new Integer[] {1, 2, 3};
boolean b = ArrayHelper.any(arr, i -> i % 2 == 0);
//b = true
```

```
//Contains condition array

Integer[] arr = new Integer[] {1, 2, 3};
boolean b = ArrayHelper.contains(arr, 2);
//b = true
```

```
//SequenceEquals condition array

Integer[] arr0 = new Integer[] {1, 2, 3};
Integer[] arr1 = new Integer[] {1, 2, 3};
boolean b = ArrayHelper.sequenceEquals(arr0, arr1);
//b = true
```

```
//and more...
```

## ∑ $ 👊 Decimal Helper
Helper to analyze and convert wraper or decimal types to BigDecimal, currency or decimal
by discover Locale automatically or locale set it.

```
//all examples will be converted to a bigDecimal of 12.40
BigDecimal bd = DecimalHelper.toBigDecimal(12.4D);
BigDecimal bd = DecimalHelper.toBigDecimal(12.4F);
BigDecimal bd = DecimalHelper.toBigDecimal("12.4");
BigDecimal bd = DecimalHelper.toBigDecimal("12,4");
BigDecimal bd = DecimalHelper.toBigDecimal("R$12,40");
BigDecimal bd = DecimalHelper.toBigDecimal("$12.40");
```

```
String currency = DecimalHelper.toCurrency(bd);//default locale pt-BR
//currency = R$ 12,40

String decimal = Decimal.Helper.toDecimal(bd);
//decimal = 12,40
```

```
String currency = DecimalHelper.toCurrency(bd, Locale.US);
//currency = $12.40

String decimal = Decimal.Helper.toDecimal(bd, Locale.US);
//decimal = 12.40
```

## 📅 👊 Date Helper
Helper to parse and convert String date formatted to Date or Calendar.

```
Date date = DateHelper.getInstance().parseDate("19/11/2020 11:46");
Date date = DateHelper.getInstance().parseDate("19/11/2020");
Date date = DateHelper.getInstance().parseDate("11:46");
```
```
//and more...
```

## 🧠 Memory Info
Information about memory, free, allocated, total...

```
MemoryInfo mi = MemoryInfo.getInstance();
mi.getBytes(MemoryInfo.Amount.FREE); //free memory in bytes.
mi.getKBytes(MemoryInfo.Amount.FREE); //free memory in KB.
mi.getMBytes(MemoryInfo.Amount.FREE); //free memory in MB.
```

## 🕑 SNTP Client
Discovery current date time online for your location

```
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

```
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

```
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

```
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

```
Debug.isDebugMode()
```
