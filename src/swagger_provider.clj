
(ns swagger-provider
  (:require [chunk-provider :refer [ChunkProviderProtocol]]
            [clojure.java.io :as io]
            [utils]))

(defn update-file [ {:keys [input-dir output-dir say say-debug]} file ]
  (io/copy (io/file (str input-dir "/" file)) (io/file (str output-dir "/" file))))

(defn is-newer  [ {:keys [input-dir output-dir say say-debug]} file ]
  (utils/is-newer (str input-dir "/" file) (str output-dir "/" file)))

(defn is-dirty
  "esnure file files are in the ouput dir and up to date"
  [context  filename]
  (let [ docs-to-watch-swagger [
                        "swagger-ui-bundle.js"
                        "swagger-ui.css"
                        "swagger-ui.js"
                        "swagger-ui-standalone-preset.js" ]
        yml-file (str filename ".yml")
        all-files (conj docs-to-watch-swagger yml-file)
        updated-files (filter #(is-newer context %) all-files)
        ]
    (if (empty? updated-files)
      false
      (do 
        (doall (map #(update-file context %) updated-files))
        true))))

(defn create-swagger-html [ base-name ]
  (str "<link rel='stylesheet' type='text/css' href='swagger-ui.css' />
  <style>
      /* suppress the yml link */
      .swagger-ui .info hgroup.main a {
        display: none
      }
    </style>
  <script src='swagger-ui-bundle.js' charset='UTF-8'> </script>
  <script src='swagger-ui-standalone-preset.js' charset='UTF-8'> </script>
  <script>
    window.onload = function() {
      // Begin Swagger UI call region
      const ui = SwaggerUIBundle({
          url: '" base-name ".yml',
        dom_id: '#" base-name "-swagger-ui',
        deepLinking: true,
        presets: [
          SwaggerUIBundle.presets.apis,
          // slice(1) gets rid of banner
          SwaggerUIStandalonePreset.slice(1)
        ],
        plugins: [
          SwaggerUIBundle.plugins.DownloadUrl
        ],
        layout: 'StandaloneLayout'
      });
      // End Swagger UI call region

      window.ui = ui; 
    };
  </script>
  <div id='" base-name "-swagger-ui'></div>
"))

(deftype Provider [context data]
  ChunkProviderProtocol
  (as-html [_] (create-swagger-html data))
  (is-dirty [_] (is-dirty context data))
  (summary [_] (str "swagger file: " (str data ".yml"))))

