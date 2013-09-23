# Featured Mock

The idea of this small project is to be able to use files (in the test classpath) to mock
either method return or http requests.

## Mock interfaces

Use `com.github.rmannibucau.featuredmock.mock.FeaturedMock#mock` method to generate a proxy. The rule to find
the content are simple: it matches a file in the classloader built from the qualified class name (interface)
and method name. For instance `org.superbiz.MyInterface#myMethod` will look for `org/superbiz/MyInterface/myMethod[extension]`
file.

By default json, yml and xml files are searched.

When creating a mock you can specify a custom `com.github.rmannibucau.featuredmock.mock.unmarshaller.Unmarshaller`.
The unmarshaller will be used to convert the file to objects (jackson for json and jaxb for xml by default).

Sample (Mock creation, json feature and result after calling a method):

```json
{ "attr1": "uno", "attr2": "due" }
```

```java
// DTO
public class Value {
    private String attr1;
    private String attr2;

    public String getAttr1() {
        return attr1;
    }

    public void setAttr1(final String attr1) {
        this.attr1 = attr1;
    }

    public String getAttr2() {
        return attr2;
    }

    public void setAttr2(final String attr2) {
        this.attr2 = attr2;
    }
}

// Service
public interface API {
    Value foo();
}

// mock usage
final API api = api = FeaturedMock.mock(API.class);
final Value value = api.foo();
// value.getAttr1() == "uno"
// value.getAttr2() == "due"
```


## HTTP mock

You need to create a server. Here is a sample:

```java
// the builder API supports host, ssl etc...
final FeaturedHttpServer server = new FeaturedHttpServerBuilder().port(1234).build().start();
// do some work
server.stop();
```

The idea is the same as with featured mocks excepted the file used to respond is found from the request uri.
For instance a request on `http://localhost:1234/foo/bar` will match a file `/foo/bar[ext]`.

Extension can be the same as for featured mock or no extension at all. In this last case the response type
will be `text/plain`.

Note: if you need to match the same url for two http methods you can prefix your file name with the http method and `-`.
For instance `GET-foo/bar`.
