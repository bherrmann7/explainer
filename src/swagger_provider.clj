
(ns swagger-provider
  (:require [chunk-provider :refer [ChunkProviderProtocol]]
            [clojure.java.io :as io]
            [utils]))

(defn update-file [ {:keys [input-dir output-dir say say-debug]} file ]
  (io/copy (io/file (str input-dir "/" file)) (io/file (str output-dir "/" file))))

(defn is-newer  [ {:keys [input-dir output-dir say say-debug]} file ]
  (utils/is-newer (str input-dir "/" file) (str output-dir "/" file)))


(def swagger-header "
<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/4.1.2/swagger-ui.css' integrity='sha512-sFCybMLlCEgtHSq/iUUG/HL4PKfg5l/qlA2scyRpDWTZU8hWOomj/CrOTxpi9+w8rODDy+crxi2VxhLZ+gehWg==' crossorigin='anonymous' referrerpolicy='no-referrer' />
<script src='https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/4.1.2/swagger-ui-bundle.js' integrity='sha512-qoOARXHXcSln7mnJQDGGnPYheEGFOPjlO+cV3KaHlLnTcJJZCZTqyPv7fMX269MuWe3nwQWEilTuuUGjDB4wSA==' crossorigin='anonymous' referrerpolicy='no-referrer'></script>
<script src='https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/4.1.2/swagger-ui-standalone-preset.js' integrity='sha512-Gk8awVM/iz6LZyVlo/5stCZnZvawpFWhrOHDYy5pLkQoCYrVw26PO9WBuVCObRdcUSlddvYmzHv/lHrrgn1N4Q==' crossorigin='anonymous' referrerpolicy='no-referrer'></script>")


(defn is-dirty
  "esnure file files are in the ouput dir and up to date"
  [context  filename]
  (let [ 
        yml-file (str filename ".yml")
        updated-files (filter #(is-newer context %) [yml-file])
        ]
    (if (empty? updated-files)
      false
      (do 
        (doall (map #(update-file context %) updated-files))
        true))))

(defn create-swagger-html [ base-name ]
  (str
   ;; ideally we would only mention the header once
   swagger-header
  "<style>
      /* suppress the yml link */
      .swagger-ui .info hgroup.main a {
        display: none
      }
    </style>
   <script>
     window.addEventListener('load', (event) => {
      // Begin Swagger UI call region
      const ui = SwaggerUIBundle({
          url: '" base-name ".yml',
        dom_id: '#" base-name "-swagger-ui',
        deepLinking: true,
        presets: [
          SwaggerUIBundle.presets.apis,
          // using .slice(1) gets rid of banner
          SwaggerUIStandalonePreset.slice(1) 
        ],
        plugins: [
          SwaggerUIBundle.plugins.DownloadUrl
        ],
        layout: 'StandaloneLayout'
      });
      // End Swagger UI call region

      // I'm not entirely sure if this is needed
      window.ui_" base-name " = ui;
    });
  </script>
  <div id='" base-name "-swagger-ui'></div>
"))

(deftype Provider [context data]
  ChunkProviderProtocol
  (as-html [_] (create-swagger-html data))
  (is-dirty [_] (is-dirty context data))
  (summary [_] (str "swagger file: " (str data ".yml"))))

