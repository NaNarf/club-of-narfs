(ns club-of-naaarfs.config
  (:require [taoensso.timbre :as timbre]))

(def defaults
  {:init
   (fn []
     (timbre/info "\n-=[club_of_naaarfs started successfully]=-"))
   :middleware identity})
