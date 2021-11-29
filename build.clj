(ns build
  (:refer-clojure :exclude [test])
  (:require [org.corfield.build :as bb]))

(defn jar [opts]
  (-> opts
      (assoc :main 'explainer :lib 'net.clojars.top/explainer)
      (bb/clean)
      (bb/uber)))
