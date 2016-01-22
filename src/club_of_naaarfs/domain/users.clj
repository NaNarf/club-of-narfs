(ns club-of-naaarfs.domain.users
 (:require [schema.core :as s]
           [ring.swagger.schema :refer [coerce!]]
           [taoensso.timbre :as timbre]
           [club-of-naaarfs.db.core :as db]))

;; user schemas
(s/defschema User {:id s/Int
                        :nickname s/Str
                        :email s/Str
                        :avatar_url s/Str
                        :created s/Inst})
(s/defschema UserPayload (dissoc User :created :id))
(s/defschema Users [User])

(defn get-users 
  "get a list of users"
  [limit offset]
    (db/get-users {:limit limit
                   :offset offset}))

(defn get-user
  "get a specific user by id"
  [id]
  (db/get-user {:id id} {:result-set-fn first}))

(defn create-user!
  "create a new user"
  [user-payload]
  (let [result (db/create-user<! user-payload) 
        id ((keyword "scope_identity()") result)]
        (get-user id)))

(defn update-user!
  "update some fields of a given user"
  [id payload]
  (db/update-user! (assoc payload :id id))
  (get-user id))

(defn delete-user!
  "delete a user given its id"
  [id]
  (db/delete-user! {:id id}))

