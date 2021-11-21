
(ns render-hiccup
  (:require [hiccup.core]))

(defn render [data]
  (hiccup.core/html data))
