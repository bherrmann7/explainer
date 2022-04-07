

(ns web-server
  (:require [ring.adapter.jetty]
            [clojure.java.io]))

(defn respond-with [content-type body]
  {:status 200
   :headers {"Content-Type" content-type}
   :body body})

(defn mimetype-for-file [filename]
  (let [last-dot (.lastIndexOf filename ".")
        extension (subs filename last-dot)]
    (case extension
      ".css" "text/css"
      ".js" "text/javascript"
      ".yml" "text/x-yaml"
      ".html" "text/html"
      ".json" "application/json"
      "image/jpeg")))

(defn handler [serve-from-dir version request]
  (let [uri-raw (:uri request)]
    (if (= uri-raw "/version")
      (respond-with "application/json" (str @version))
      (let [uri (if (= uri-raw "/") "/index.html" uri-raw)
            file (clojure.java.io/file (str serve-from-dir "/" uri))
            content-type (mimetype-for-file uri)]
        (respond-with content-type file)))))

(defn start [serve-from-dir version]
  (println "Listenting for web connections at :3000  serving from" serve-from-dir)
  (ring.adapter.jetty/run-jetty #(handler serve-from-dir version %1) {:port 3000 :join? false}))

