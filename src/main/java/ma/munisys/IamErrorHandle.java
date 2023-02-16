package ma.munisys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;

@SpringBootApplication
public class IamErrorHandle extends RouteBuilder {

    public static void main(String[] args) {
        SpringApplication.run(IamErrorHandle.class, args);
    }

    @Override
    public void configure() {

        //errorHandler(noErrorHandler());

        from("netty4-http:proxy://0.0.0.0:8088")
        //from("netty4-http:http:0.0.0.0:8088")
        //.onCompletion().onWhen(httpcode .contains("Hello"))
        .doTry()
            .log(LoggingLevel.INFO, "===> in.headers: \n${in.headers}")
            .log(LoggingLevel.INFO, "===> body: \n${body}")
            //.setHeader("CamelHttpMethod", constant("POST"))
            //.toD("netty4-http:http:130.24.31.210:8090" + "${headers." + Exchange.HTTP_PATH + "}");
            //.to("netty4-http:http:130.24.31.210:8090")
            .log(LoggingLevel.INFO, "===> Routing request to : ${headers." + Exchange.HTTP_SCHEME + "}://${headers." + Exchange.HTTP_HOST + "}:${headers." + Exchange.HTTP_PORT + "}${headers." + Exchange.HTTP_PATH + "}")
            .toD("netty4-http:"
                + "${headers." + Exchange.HTTP_SCHEME + "}://"
                + "${headers." + Exchange.HTTP_HOST + "}:"
                + "${headers." + Exchange.HTTP_PORT + "}"
                + "${headers." + Exchange.HTTP_PATH + "}")
           // .convertBodyTo(String.class)
            
       .doCatch(Exception.class)
       //.onWhen(exceptionMessage().contains("someCamelError"))
       //.onWhen(body().contains("Pencil"))
            .log(LoggingLevel.INFO, "==> body : \n ${body}")
            .log(LoggingLevel.ERROR, "==> exceptionMessage : \n" + exceptionMessage())
            .log(LoggingLevel.ERROR, "==> header.CamelHttpResponseCode : ${header.CamelHttpResponseCode}")
            .choice()
                .when(simple("${header.CamelHttpResponseCode} == 401"))
                    .process(IamErrorHandle::sendCustomError2)
                    .endChoice()
                .when(simple("${header.CamelHttpResponseCode} < '200'"))
                .when(simple("${header.CamelHttpResponseCode} > '299'"))
                    .process(IamErrorHandle::sendCustomError)
                    .endChoice()
                
                /*
                .when(header("someKey").isEqualTo("someValue"))
                .when(body().contains("Hello, world!"))
                .when(header("CamelFileName").endsWith(".xml"))
                .when(xpath("/customer/@status = 'gold'"))
                .otherwise().to("...") */
            .end();
            //.to("netty4-http:http:130.24.31.210:8090?bridgeErrorHandler=true")
            //.onException(Exception.class).handled(true).log("Error muis")
            
      
        /* .doFinally()
            .convertBodyTo(String.class)
            .log(LoggingLevel.INFO, "Backend response in.headers: \n${in.headers}")
            .log(LoggingLevel.INFO, "Backend response body: \n${body}")
        .end(); */
           
    }

    public static void sendCustomError(final Exchange exchange) {
        final Message message = exchange.getIn();
       // final String body = message.getBody(String.class);
        message.setBody("<CustomError>This is a custom error set by Camel</CustomError>");
    }

    public static void sendCustomError2(final Exchange exchange) {
        final Message message = exchange.getIn();
       // final String body = message.getBody(String.class);
        message.setBody("<CustomError2>This is a custom error set by Camel</CustomError2>");
    }
}
