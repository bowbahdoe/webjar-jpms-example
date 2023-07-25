package dev.mccue.basic;

import dev.mccue.rosie.Body;
import dev.mccue.rosie.Request;
import dev.mccue.rosie.Response;
import dev.mccue.rosie.microhttp.MicrohttpAdapter;
import org.microhttp.Options;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    static final AtomicInteger COUNTER = new AtomicInteger();
    static Response handleRequest(Request request) {
        if (request.uri().equals("/htmx.min.js")) {
            try {
                return new Response(
                        200,
                        Map.of("content-type", "application/json"),
                        Body.fromInputStream(
                                Objects.requireNonNull(
                                        ModuleLayer.boot()
                                                .findModule("htmx.org")
                                                .orElseThrow()
                                                .getResourceAsStream("/META-INF/resources/webjars/htmx.org/1.9.3/dist/htmx.min.js")
                                )
                        )
                );
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

        }
        else if (request.uri().equals("/increment")) {
            return new Response(
                    200,
                    Map.of("content-type", "text/html"),
                    Body.fromString("%d".formatted(COUNTER.incrementAndGet()))
            );
        }
        else {
            return new Response(
                    200,
                    Map.of("content-type", "text/html"),
                    Body.fromString("""
                                    <html>
                                        <head>
                                            <script src="/htmx.min.js" type="text/javascript"></script>
                                        </head>
                                        <body>
                                            <h1> Counter </h1>
                                            <h2 id="counter"> %d </h2>
                                            <button hx-get="/increment" hx-target="#counter"> increment </h2>
                                        </body>
                                    </html>
                                    """.formatted(COUNTER.get()))
            );
        }
    }

    public static void main(String[] args) throws Exception {
        var options = new Options()
                .withHost("0.0.0.0")
                .withPort(9000);

        MicrohttpAdapter.runServer(
                Main::handleRequest,
                options,
                Executors.newSingleThreadExecutor()
        );
    }
}
