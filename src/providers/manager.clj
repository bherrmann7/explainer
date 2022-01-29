
;; BOBH yea...
(ns providers.manager
  (:require
   [clojure.string :as str]
   [utils]
   [providers.chunk-provider]
   [providers.html-provider]
   [providers.hiccup-provider]
   [providers.plantuml-file-provider]
   [providers.dot-file-provider]
   [providers.js-file-provider]
   [providers.swagger-provider]
   [providers.resource-provider]
   [providers.watch-reloader-provider]
   [providers.markdown-provider]
   [providers.markdown-file-provider]
   [providers.unknown-provider]
   [malli.core :as m]
   [malli.dev.pretty]))

(defn get-chunk-providers
  "given a type and data, return the appropriate provider.  A bit like a factory."
  [context [chunk-type chunk-data]]
  (case chunk-type
    :html (providers.html-provider/->Provider chunk-data)
    :html-hiccup (providers.hiccup-provider/->Provider chunk-data)
    :plantuml-file (providers.plantuml-file-provider/->Provider context chunk-data)
    :dot-file (providers.dot-file-provider/->Provider context chunk-data)
    :js-file (providers.js-file-provider/->Provider context chunk-data)
    :swagger-file (providers.swagger-provider/->Provider context chunk-data)
    :resource (providers.resource-provider/->Provider context chunk-data)
    :markdown   (providers.markdown-provider/->Provider chunk-data)
    :markdown-file (providers.markdown-file-provider/->Provider context chunk-data)
    (providers.unknown-provider/->Provider chunk-type chunk-data)))

(def input-schema [:+ [:catn [:s keyword?] [:n any?]]])

(defn load-chunks
  "load edn data into memory"
  [input-filename]
  (let [input-as-edn (read-string (slurp input-filename))
        _ (if (m/validate input-schema input-as-edn)
            nil
            (utils/die "input file, " input-filename ", must contain pairs of chunk-type (edn keyword) and chunk-data."))
        chunks (partition 2 input-as-edn)]
    chunks))

(defn prepend-watch-reload-provider [edn-file-providers]
  (conj edn-file-providers (providers.watch-reloader-provider/->Provider)))

(defn build-providers
  "load inital edn file, and break into collection of providers.  Handle special watch provider also."
  [{:keys [input-dir input-filename watch-flag], :as context}]
  (let [input-edn-file (str input-dir "/" input-filename)
        _ (if (not (.exists (java.io.File. input-edn-file))) (utils/die "Input file does not exist: " input-edn-file) nil)
        edn-file-providers (map #(get-chunk-providers context %) (load-chunks input-edn-file))
        providers (if watch-flag (prepend-watch-reload-provider edn-file-providers) edn-file-providers)]
    providers))

(defn write-page
  "Loop through the providers and output the page by extracting each provider's content"
  [context providers]
  (let [{:keys [output-web-page verbose]} context
        web-page-content (str/join "\n\n" (map providers.chunk-provider/as-html providers))]
    (spit output-web-page web-page-content)
    (verbose "Wrote " output-web-page)))

(defn is-edn-dirty [context]
  (let [{:keys [input-edn-file  output-web-page]} context]
    (utils/is-newer input-edn-file output-web-page)))



