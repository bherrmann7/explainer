
(ns providers.swagger-provider
  (:require [providers.chunk-provider :refer [ChunkProviderProtocol]]
            [clojure.java.io :as io]
            [clojure.string]
            [utils]))

(defn update-file [{:keys [input-dir output-dir]} file]
  (io/copy (io/file (str input-dir "/" file)) (io/file (str output-dir "/" file))))

(defn is-newer  [{:keys [input-dir output-dir]} file]
  (utils/is-newer (str input-dir "/" file) (str output-dir "/" file)))

(def swagger-header "
<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/4.1.2/swagger-ui.css' integrity='sha512-sFCybMLlCEgtHSq/iUUG/HL4PKfg5l/qlA2scyRpDWTZU8hWOomj/CrOTxpi9+w8rODDy+crxi2VxhLZ+gehWg==' crossorigin='anonymous' referrerpolicy='no-referrer' />
<script src='https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/4.1.2/swagger-ui-bundle.js' integrity='sha512-qoOARXHXcSln7mnJQDGGnPYheEGFOPjlO+cV3KaHlLnTcJJZCZTqyPv7fMX269MuWe3nwQWEilTuuUGjDB4wSA==' crossorigin='anonymous' referrerpolicy='no-referrer'></script>
<script src='https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/4.1.2/swagger-ui-standalone-preset.js' integrity='sha512-Gk8awVM/iz6LZyVlo/5stCZnZvawpFWhrOHDYy5pLkQoCYrVw26PO9WBuVCObRdcUSlddvYmzHv/lHrrgn1N4Q==' crossorigin='anonymous' referrerpolicy='no-referrer'></script>")

(defn is-dirty
  "If the source file is newer thatn the output, then return true"
  [{:keys [input-dir]} filename output-filename]
  (let [
    input-filename (str input-dir "/" filename)]
    (utils/is-newer input-filename output-filename)))

(defn create-swagger-html [file-name]
  (let [dom-id-name (str (clojure.string/replace file-name "." "") "-swagger-ui")]
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
          url: '" file-name "',
        dom_id: '#" dom-id-name "',
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
    });
  </script>
  <div id='" dom-id-name "'></div>
")))

(deftype Provider [context yml-file output-filename]
  ChunkProviderProtocol
  (as-html [_] (create-swagger-html yml-file))
  (is-dirty [_] (is-dirty context yml-file output-filename))
  (summary [_] (str "swagger file: " yml-file)))

