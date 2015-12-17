(ns club-of-naaarfs.app
  (:require [club-of-naaarfs.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
