(ns club-of-naaarfs.config
  (:require [selmer.parser :as parser]
            [taoensso.timbre :as timbre]
            [club-of-naaarfs.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (timbre/info "\n-=[club_of_naaarfs started successfully using the development profile]=-"))
   :middleware wrap-dev})
