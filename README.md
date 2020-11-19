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

## Serializer
Serialize and deserialize objects for Object Base64, BSON, JSON or XML.

```
Example ex1 = new Example();
Serializer s = Serializer.getInstance(Serializer.Type.XML);
String xmlEx = s.serialize(ex1);
ex1 = s.deserialize(xmlEx);
``` 

Whether needs apply some particular rule for target class, use below code

```
Serializer.setupDefaultConfigurationXml(Example.class, x -> {
    //do not serialize to password field.
    x.omitField(Example.class, "password");
});
```

## ArrayHelper
Helper to analyze, manipulate and convert array objects.

```
//insert a new element at first index.
Integer[] arr = new Integer[] {1, 2, 3}
ArrayHelper.push(arr, 0);
//arr = [0, 1, 2, 3]
```

```
//insert a new element at last index.
Integer[] arr = new Integer[] {1, 2, 3}
arr = ArrayHelper.add(arr, 0);
//arr = [1, 2, 3, 0]
```

```
//select array elements converting to new values.
Integer[] arr = new Integer[] {1, 2, 3}
arr = ArrayHelper.select(arr, e -> e * 2);
//arr = [2, 4, 6]
```

```
//filter array elements.
Integer[] arr = new Integer[] {1, 2, 3}
arr = ArrayHelper.filter(arr, e -> e < 3);
//arr = [1, 2]
```

```
//reduce array elements.
Integer[] arr = new Integer[] {1, 2, 3}
Integer result = ArrayHelper.reduce(arr, (acc, curr) -> acc + curr);
//result = 6
```

```
//Take a count of elements.
Integer[] arr = new Integer[] {1, 2, 3}
arr = ArrayHelper.take(arr, 2);
//arr = [1, 2]
```

```
//"Jump" (Ignore) a count of element.
Integer[] arr = new Integer[] {1, 2, 3}
arr = ArrayHelper.jump(arr, 2);
//arr = [3]
```

## Decimal Helper
Helper to analyze and convert wrapper or decimal types to BigDecimal, currency or decimal
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

## Date Helper
Helper to parse and convert String date formatted to Date or Calendar.

```
Date date = DateHelper.getInstance().parseDate("19/11/2020 11:46");
Date date = DateHelper.getInstance().parseDate("19/11/2020");
Date date = DateHelper.getInstance().parseDate("11:46");
```

## Memory Info
Information about memory, free, allocated, total...

```
MemoryInfo mi = MemoryInfo.getInstance();
mi.getBytes(MemoryInfo.Amount.FREE); //free memory in bytes.
mi.getKBytes(MemoryInfo.Amount.FREE); //free memory in KB.
mi.getMBytes(MemoryInfo.Amount.FREE); //free memory in MB.
```

## SNTP Client
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

## String Utils
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

//join
str = StringUtils.join("[", "]", ", ", 0, 1, 2, 3);
//str = "[0, 1, 2, 3]"

str = StringUtils.join(",", 0, 1, 2, 3);
//str = "0, 1, 2, 3"
```

## Reflection
Access objects by reflection.<br/>
Inflate (create an instance or load access for static class) a class by fullName (included package path) 
to try manipulate and access it.

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
Reflection r    = Reflection.cast(obj, java.lang.String");
boolean found   = r.method("indexOf", "test").valueInt() > 0;
```

## Debug
Check if current code is running in debug mode

```
Debug.isDebugMode()
```
