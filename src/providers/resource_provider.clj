

;; Used to provide a resource from the input director to the output directory.
;; A typical example is an image file.    Explainer doenst parse HTML to find
;; images references in the html, so the author must explicity mention the images
;; as resources.

;;  {  :html "<img src=browncow.png>"
;;     :resource "browncow.png"   }


(ns providers.resource-provider
  (:require [providers.chunk-provider :refer [ChunkProviderProtocol]]
            [clojure.java.io :as io]
            [utils]))

(defn update-file [{:keys [input-dir output-dir]} file]
  (io/copy (io/file (str input-dir "/" file)) (io/file (str output-dir "/" file))))

(defn is-filename-newer  [{:keys [input-dir output-dir]} file]
  (utils/is-newer (str input-dir "/" file) (str output-dir "/" file)))

(deftype Provider [context filename]
  ChunkProviderProtocol
  (as-html [_] (do
                 (if (is-filename-newer context filename) (update-file context filename) nil)
                 ""))
  (is-dirty [_] (is-filename-newer context filename))
  (summary [_] (str "resource file: " filename)))
