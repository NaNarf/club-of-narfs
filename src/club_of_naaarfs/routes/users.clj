(ns club-of-naaarfs.routes.users
  (:require [club-of-naaarfs.domain.users :as domain]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [ring.util.http-response :refer :all]))

(defapi user-api
  {:formats [:json-kw]}
  (GET* "/users/" []
       :name :get-users
       :query-params [ { limit :- s/Int 100 } 
                       { offset :- s/Int 0  } :as request]
       :return domain/Users
       :summary "get a list of users"
       (domain/get-users limit offset))
  (POST* "/users/" []
       :name :create-user
       :return domain/User
       :body [payload (describe domain/UserPayload "new user to be created")]
       :summary "create a new user"
       (ok (domain/create-user! payload))) ;; TODO: maybe return a 201
  (GET* "/user/:id" [] 
       :name :get-user
       :path-params [ id :- s/Int ]
       :return domain/User
       :summary "fetch and return a single user by id"
       (let [user (domain/get-user id)]
         (if (nil? user) 
           (not-found)
           (ok user))))
  (PUT* "/user/:id" []
        :name :update-user
        :path-params [ id :- s/Int ]
        :body [payload (describe domain/UserPayload "stuff to update a user with")]
        :return domain/User
        :summary "update some fields of a user"
        (ok (domain/update-user! id payload)))
  (DELETE* "/user/:id" []
          :name :delete-user
          :path-params [ id :- s/Int ]
          :summary "delete a single user by id"
          (domain/delete-user! id)
          (no-content)))
