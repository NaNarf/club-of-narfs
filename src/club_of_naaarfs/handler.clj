(ns club-of-naaarfs.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes]]
            [club-of-naaarfs.layout :refer [error-page]]
            [club-of-naaarfs.routes.home :refer [home-routes]]
            [club-of-naaarfs.routes.users :refer [user-api]]
            [club-of-naaarfs.middleware :as middleware]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.3rd-party.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [club-of-naaarfs.config :refer [defaults]]
            [mount.core :as mount]))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []

  (timbre/merge-config!
    {:level     ((fnil keyword :info) (env :log-level))
     :appenders {:rotor (rotor/rotor-appender
                          {:path (or (env :log-path) "club_of_naaarfs.log")
                           :max-size (* 512 1024)
                           :backlog 10})}})
  (doseq [component (:started (mount/start))]
    (timbre/info component "started"))
  ((:init defaults)))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "club_of_naaarfs is shutting down...")
  (doseq [component (:stopped (mount/stop))]
    (timbre/info component "stopped"))
  (timbre/info "shutdown complete!"))

(def app-routes
  (routes
    (wrap-routes #'home-routes middleware/wrap-csrf)
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))

(def app 
  (routes
    user-api ;; TODO: add swagger docs
    (middleware/wrap-base #'app-routes)))
