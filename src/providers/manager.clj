
;; BOBH yea...
(ns providers.manager
  (:require
   [clojure.string :as str]
   [utils]
   [providers.chunk-provider]
   [providers.html-provider]
   [providers.html-file-provider]
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
  [context [chunk-type chunk-data] output-filename]
  (case chunk-type
    :html (providers.html-provider/->Provider chunk-data)
    :html-file (providers.html-file-provider/->Provider context chunk-data output-filename)
    :html-hiccup (providers.hiccup-provider/->Provider chunk-data)
    :plantuml-file (providers.plantuml-file-provider/->Provider context chunk-data)
    :dot-file (providers.dot-file-provider/->Provider context chunk-data)
    :js-file (providers.js-file-provider/->Provider context chunk-data)
    :swagger-file (providers.swagger-provider/->Provider context chunk-data output-filename)
    :resource (providers.resource-provider/->Provider context chunk-data)
    :markdown   (providers.markdown-provider/->Provider chunk-data)
    :markdown-file (providers.markdown-file-provider/->Provider context chunk-data output-filename)
    (providers.unknown-provider/->Provider chunk-type chunk-data)))

(def input-schema [:+ [:catn [:s keyword?] [:n any?]]])

(defn load-chunks
  "load edn data into memory"
  [input-filename]
  (try
    (let [input-as-edn (read-string (slurp input-filename))
          _ (if (m/validate input-schema input-as-edn)
              nil
              (utils/die "input file, " input-filename ", must contain pairs of chunk-type (edn keyword) and chunk-data."))
          chunks (partition 2 input-as-edn)]
      chunks)
    (catch Throwable _
      [[:html (str "Unable to load edn file: " input-filename)]])))

(defn prepend-watch-reload-provider [edn-file-providers]
  (conj edn-file-providers (providers.watch-reloader-provider/->Provider)))

(defn build-providers
  "load inital edn file, and break into collection of providers.  Handle special watch provider also."
  [{:keys [watch-flag], :as context} input-filename output-filename]
  (let [edn-file-providers (map #(get-chunk-providers context % output-filename) (load-chunks input-filename))
        providers (if watch-flag (prepend-watch-reload-provider edn-file-providers) edn-file-providers)]
    providers))

(defn write-page
  "Loop through the providers and output the page by extracting each provider's content"
  [_ page-output-filename providers]
  (let [web-page-content (str/join "\n\n" (map providers.chunk-provider/as-html providers))]
    (spit page-output-filename web-page-content)))



