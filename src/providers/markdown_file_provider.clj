
(ns providers.markdown-file-provider
  (:require [providers.chunk-provider :refer [ChunkProviderProtocol]]
            [utils]
            [clojure.java.io]
            [markdown.core]))

(defn create-html [context filename]
  (let [{:keys [input-dir]} context
        input-contents (slurp (str input-dir "/" filename))]
    (markdown.core/md-to-html-string input-contents)))

(defn compute-input-filename [context filename]
  (let [{:keys [input-dir]} context]
    (str input-dir "/" filename)))

(defn load-input-filename [context filename]
  (slurp (compute-input-filename context filename)))

(defn is-dirty
  "If the dot source file has changed, rebuild the output file"
  [context filename output-filename]
  (let
   [{:keys [input-dir]} context
    input-filename (str input-dir "/" filename)]
    (utils/is-newer input-filename output-filename)))

(deftype Provider [context filename output-filename]
  ChunkProviderProtocol
  (as-html [_] (create-html context filename))
  (is-dirty [_] (is-dirty context filename output-filename))
  (summary [_] (str "markdown file" (.length (clojure.java.io/file (compute-input-filename context filename)) " bytes"))))





