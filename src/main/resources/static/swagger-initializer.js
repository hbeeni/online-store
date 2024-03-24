window.onload = function () {
    //<editor-fold desc="Changeable Configuration Block">

    // the following lines will be replaced by docker/configurator, when it runs in a docker-container
    window.ui = SwaggerUIBundle({
        url: "/docs/openapi3.yaml",
        dom_id: '#swagger-ui',
        deepLinking: true,
        presets: [
            SwaggerUIBundle.presets.apis,
            SwaggerUIStandalonePreset
        ],
        plugins: [
            SwaggerUIBundle.plugins.DownloadUrl
        ],
        layout: "StandaloneLayout",
        tagsSorter: "alpha",
        operationsSorter: function (a, b) {
            const order = {'get': '0', 'post': '1', 'put': '2', 'delete': '3'};
            return order[a.get("method")].localeCompare(order[b.get("method")]);
        },
        displayRequestDuration: true
    });

    //</editor-fold>
};
